// File: CompositionServiceImpl.java
package com.groupeisi.ms1_gestschool.examen.parametrage;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationRequestDTO;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompositionServiceImpl implements CompositionService {

    private final CompositionInstanceRepository instanceRepository;
    private final CompositionMapper mapper;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final CalculationUtils fn = new CalculationUtils();

    // regex pour trouver les références de champs: #nom
    private static final Pattern REF_PATTERN = Pattern.compile("#([A-Za-z_][A-Za-z0-9_]*)");

    @Override
    public CompositionInstanceResponseDTO saveInstance(CompositionInstanceRequestDTO requestDTO) {
        // Convertir DTO en entité
        CompositionInstance instance = mapper.toEntity(requestDTO);

        // Validation de l'instance avant sauvegarde
        validateInstance(instance);

        // Sauvegarder l'instance
        CompositionInstance savedInstance = instanceRepository.save(instance);

        // Retourner la réponse DTO
        return mapper.toDto(savedInstance);
    }

    @Override
    public Optional<CompositionInstanceResponseDTO> findById(String id) {
        return instanceRepository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public List<CompositionInstanceResponseDTO> findAll() {
        return mapper.toInstanceDtoList(instanceRepository.findAll());
    }


    @Override
    public EvaluationResponseDTO evaluate(String instanceId, EvaluationRequestDTO request) {
        CompositionInstance instance = instanceRepository.findById(instanceId)
                .orElseThrow(() -> new IllegalArgumentException("Instance introuvable : " + instanceId));

        Map<String, Double> inputs = request.getInputs();
        Map<String, CompositionField> byName = instance.getFields().stream()
                .collect(Collectors.toMap(CompositionField::getName, f -> f, (a, b) -> a));

        Map<String, Double> values = new HashMap<>();

        // Initialiser les champs SAISIR avec les inputs
        for (CompositionField f : instance.getFields()) {
            if (f.getType().equals(FieldType.SAISIR) && f.isActif()) {
                Double v = inputs.get(f.getName());
                if (v == null) {
                    throw new IllegalArgumentException("Valeur manquante pour le champ SAISIR: " + f.getName());
                }
                values.put(f.getName(), v);
            }
        }

        // Construire le graphe de dépendances pour les champs CALCULE
        Map<String, Set<String>> deps = new HashMap<>();
        for (CompositionField f : instance.getFields()) {
            if (f.getType().equals(FieldType.CALCULE) && f.isActif()) {
                Set<String> refs = extractReferences(f.getFormule());
                // Ne garder que les références vers des champs existants
                deps.put(f.getName(), refs.stream().filter(byName::containsKey).collect(Collectors.toSet()));
            } else {
                deps.put(f.getName(), Collections.emptySet());
            }
        }

        // Tri topologique
        List<String> order;
        try {
            order = topologicalSort(deps);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Cycle détecté dans les formules : " + ex.getMessage());
        }

        // Contexte SpEL
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("fn", fn);
        values.forEach(context::setVariable);

        // Évaluation des champs calculés
        for (String fieldName : order) {
            CompositionField f = byName.get(fieldName);
            if (f == null || !f.isActif()) continue;

            if (f.getType().equals(FieldType.CALCULE)) {
                String expr = f.getFormule();
                if (expr == null || expr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Formule manquante pour le champ calculé: " + f.getName());
                }
                Double computed = parser.parseExpression(expr).getValue(context, Double.class);
                if (computed == null) computed = 0d;
                values.put(f.getName(), computed);
                context.setVariable(f.getName(), computed);
            }
        }

        // Calcul de la référence
        if (instance.getReferenceFieldName() != null && !instance.getReferenceFieldName().isEmpty()) {
            Double referenceValue;

            // Si une formule de référence est fournie, l'utiliser
            if (instance.getReferenceFormula() != null && !instance.getReferenceFormula().isEmpty()) {
                referenceValue = parser.parseExpression(instance.getReferenceFormula()).getValue(context, Double.class);
            }
            // Sinon, calculer automatiquement la moyenne simple
            else {
                // Récupérer les champs actifs uniquement
                List<CompositionField> activeFields = instance.getFields().stream()
                        .filter(CompositionField::isActif)
                        .collect(Collectors.toList());

                if (activeFields.isEmpty()) {
                    referenceValue = 0d;
                } else {
                    // Calculer la moyenne simple
                    double sum = 0;
                    int count = 0;

                    for (CompositionField field : activeFields) {
                        Double v = values.get(field.getName());
                        if (v != null) {
                            sum += v;
                            count++;
                        }
                    }

                    referenceValue = count == 0 ? 0 : sum / count;
                }
            }

            values.put(instance.getReferenceFieldName(), referenceValue);
        }

        // Construire la réponse
        EvaluationResponseDTO response = new EvaluationResponseDTO();
        response.setInstanceId(instance.getId());
        response.setValues(values);
        response.setOrder(order);

        return response;
    }

    @Override
    public void validateInstance(CompositionInstance instance) {
        if (instance.getFields() == null || instance.getFields().isEmpty()) {
            throw new IllegalArgumentException("L'instance doit contenir au moins un champ");
        }

        // Vérifier que les noms de champs sont uniques
        Set<String> names = new HashSet<>();
        for (CompositionField field : instance.getFields()) {
            if (!names.add(field.getName())) {
                throw new IllegalArgumentException("Nom de champ en double : " + field.getName());
            }
        }

        // Valider les formules des champs calculés
        for (CompositionField field : instance.getFields()) {
            if (field.getType() == FieldType.CALCULE) {
                if (field.getFormule() == null || field.getFormule().trim().isEmpty()) {
                    throw new IllegalArgumentException("Formule manquante pour le champ : " + field.getName());
                }

                // Vérifier que la formule ne référence que des champs existants
                Set<String> refs = extractReferences(field.getFormule());
                for (String ref : refs) {
                    if (!names.contains(ref)) {
                        throw new IllegalArgumentException("La formule du champ '" + field.getName() +
                                "' référence un champ inconnu : " + ref);
                    }
                }
            }
        }
    }

    private Set<String> extractReferences(String formule) {
        if (formule == null) return Collections.emptySet();
        Matcher m = REF_PATTERN.matcher(formule);
        Set<String> refs = new HashSet<>();
        while (m.find()) {
            refs.add(m.group(1));
        }
        return refs;
    }

    private List<String> topologicalSort(Map<String, Set<String>> deps) {
        Map<String, Set<String>> incoming = new HashMap<>();
        Map<String, Set<String>> outgoing = new HashMap<>();

        for (String node : deps.keySet()) {
            outgoing.put(node, new HashSet<>());
            incoming.put(node, new HashSet<>(deps.get(node)));
        }

        for (String node : deps.keySet()) {
            for (String dep : deps.get(node)) {
                outgoing.computeIfAbsent(dep, k -> new HashSet<>()).add(node);
            }
        }

        Deque<String> q = new ArrayDeque<>();
        for (Map.Entry<String, Set<String>> e : incoming.entrySet()) {
            if (e.getValue().isEmpty()) q.add(e.getKey());
        }

        List<String> result = new ArrayList<>();
        while (!q.isEmpty()) {
            String n = q.removeFirst();
            result.add(n);
            for (String m : outgoing.getOrDefault(n, Collections.emptySet())) {
                incoming.get(m).remove(n);
                if (incoming.get(m).isEmpty()) q.add(m);
            }
        }

        boolean cycle = incoming.values().stream().anyMatch(s -> !s.isEmpty());
        if (cycle) {
            List<String> cyc = incoming.entrySet().stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            throw new RuntimeException("Cycle detected among nodes: " + cyc);
        }

        return result;
    }
}