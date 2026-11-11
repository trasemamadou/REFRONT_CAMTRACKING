package com.groupeisi.ms1_gestschool.examen.bulletin;

import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationRequestDTO;
import com.groupeisi.ms1_gestschool.examen.evaluation.EvaluationResponseDTO;
import com.groupeisi.ms1_gestschool.examen.note.NoteEntity;
import com.groupeisi.ms1_gestschool.examen.note.NoteRepository;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionService;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationEntity;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationRepository;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveEntity;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BulletinServiceImpl implements BulletinService {

    private final BulletinRepository bulletinRepository;
    private final BulletinMapper bulletinMapper;
    private final NoteRepository noteRepository;
    private final ParticipationRepository participationRepository;
    private final EpreuveRepository epreuveRepository;
    private final CompositionService compositionService;

    @Override
    public BulletinDtoResponse createBulletin(BulletinDtoRequest request) {
        // Vérifier que la sessionclasse existe
        ParticipationEntity sessionclasse = participationRepository.findById(request.getSessionclasseId())
                .orElseThrow(() -> new RuntimeException("Session-classe non trouvée avec l'ID: " + request.getSessionclasseId()));

        // Récupérer toutes les notes de l'élève pour cette sessionclasse
        List<NoteEntity> notes = noteRepository.findByEleveIdAndSessionclasseId(
                request.getEleveId(),
                request.getSessionclasseId()
        );

        // Calculer la moyenne générale
        Double moyenneGenerale = calculerMoyenneAvecComposition(notes, sessionclasse);

        // Créer le bulletin
        BulletinEntity bulletin = new BulletinEntity();
        bulletin.setEleveId(request.getEleveId());
        bulletin.setSessionclasseId(request.getSessionclasseId());
        bulletin.setTrimestre(request.getTrimestre());
        bulletin.setMoyenneGenerale(moyenneGenerale);
        bulletin.setAppreciation(request.getAppreciation());

        BulletinEntity saved = bulletinRepository.save(bulletin);
        return bulletinMapper.toDto(saved);
    }

    /**
     * Calcule la moyenne en utilisant l'instance de composition si disponible
     */
    private Double calculerMoyenneAvecComposition(List<NoteEntity> notes, ParticipationEntity sessionclasse) {
        if (notes.isEmpty()) {
            return 0.0;
        }

        // Si une instance de composition est associée, l'utiliser
        if (sessionclasse.getCompositionInstance() != null) {
            try {
                // Construire la map d'entrée pour l'évaluation
                Map<String, Double> inputs = new HashMap<>();

                // Pour chaque note, on utilise le nom de la matière comme clé
                for (NoteEntity note : notes) {
                    // Récupérer le nom de la matière à partir de sessionmatiereId
                    // Note: sessionmatiereId est un Long mais c'est peut-être l'ID de la table sessionmatiere
                    // Si c'est une clé composite, il faut utiliser findByMatiereNameAndSessionclasseId

                    // Option 1: Si sessionmatiereId est l'ID auto-généré (recommandé)
                    EpreuveEntity sessionmatiere = epreuveRepository.findById(note.getSessionmatiereId())
                            .orElse(null);

                    // Option 2: Si vous n'avez pas d'ID auto-généré, il faut utiliser une autre méthode
                    // SessionmatiereEntity sessionmatiere = sessionmatiereRepository
                    //     .findByMatiereNameAndSessionclasseId(matiereName, sessionclasse.getId());

                    if (sessionmatiere != null) {
                        // Utiliser le nom de la matière comme clé
                        String matiereKey = sessionmatiere.getId();
                        inputs.put(matiereKey, note.getValeur());
                    } else {
                        // Fallback: utiliser l'ID comme clé
                        inputs.put("matiere_" + note.getSessionmatiereId(), note.getValeur());
                    }
                }

                // Créer la requête d'évaluation
                EvaluationRequestDTO evaluationRequest = new EvaluationRequestDTO();
                evaluationRequest.setInputs(inputs);

                // Appeler le service de composition
                EvaluationResponseDTO result = compositionService.evaluate(
                        sessionclasse.getCompositionInstance().getId(),
                        evaluationRequest
                );

                // Récupérer la valeur calculée
                if (result != null && result.getValues() != null) {
                    if (result.getValues().containsKey("reference")) {
                        return result.getValues().get("reference");
                    } else if (!result.getValues().isEmpty()) {
                        return result.getValues().values().iterator().next();
                    }
                }

                return calculerMoyennePonderee(notes);

            } catch (Exception e) {
                // Log l'erreur si nécessaire
                System.err.println("Erreur lors du calcul avec composition: " + e.getMessage());
                return calculerMoyennePonderee(notes);
            }
        } else {
            return calculerMoyennePonderee(notes);
        }
    }

    /**
     * Calcule la moyenne pondérée par les coefficients
     */
    private Double calculerMoyennePonderee(List<NoteEntity> notes) {
        double sommePonderee = 0.0;
        double sommeCoefficients = 0.0;

        for (NoteEntity note : notes) {
            sommePonderee += note.getValeur() * note.getCoefficient();
            sommeCoefficients += note.getCoefficient();
        }

        return sommeCoefficients > 0 ? sommePonderee / sommeCoefficients : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public BulletinDtoResponse getBulletinById(String id) {
        BulletinEntity entity = bulletinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bulletin non trouvé avec l'ID: " + id));
        return bulletinMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BulletinDtoResponse> getAllBulletins() {
        return bulletinRepository.findAll()
                .stream()
                .map(bulletinMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BulletinDtoResponse updateBulletin(String id, BulletinDtoRequest request) {
        BulletinEntity entity = bulletinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bulletin non trouvé avec l'ID: " + id));

        if (request.getEleveId() != null) {
            entity.setEleveId(request.getEleveId());
        }
        if (request.getSessionclasseId() != null) {
            entity.setSessionclasseId(request.getSessionclasseId());
        }
        if (request.getTrimestre() != null) {
            entity.setTrimestre(request.getTrimestre());
        }
        if (request.getMoyenneGenerale() != null) {
            entity.setMoyenneGenerale(request.getMoyenneGenerale());
        }
        if (request.getAppreciation() != null) {
            entity.setAppreciation(request.getAppreciation());
        }

        BulletinEntity updatedEntity = bulletinRepository.save(entity);
        return bulletinMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteBulletin(String id) {
        if (!bulletinRepository.existsById(id)) {
            throw new RuntimeException("Bulletin non trouvé avec l'ID: " + id);
        }
        bulletinRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BulletinDtoResponse> getBulletinsByEleve(String eleveId) {
        return bulletinRepository.findByEleveId(eleveId)
                .stream()
                .map(bulletinMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BulletinDtoResponse> getBulletinsByClasseId(String sessionclasseId) {
        return bulletinRepository.findBySessionclasseId(sessionclasseId)
                .stream()
                .map(bulletinMapper::toDto)
                .collect(Collectors.toList());
    }
}