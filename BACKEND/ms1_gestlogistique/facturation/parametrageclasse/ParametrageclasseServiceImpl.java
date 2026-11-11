package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseRepository;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrage.ParametrageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParametrageclasseServiceImpl implements ParametrageclasseService {

    private final ParametrageclasseRepository repository;
    private final ClasseRepository classeRepository;
    private final ParametrageRepository parametrageRepository;
    private final ParametrageclasseMapper mapper;

    @Override
    public ParametrageclasseDtoResponse create(ParametrageclasseDtoRequest request) {
        // Vérifier si la combinaison existe déjà
        if (repository.existsByClasseIdAndParametrageId(request.getClasseId(), request.getParametrageId())) {
            throw new RuntimeException("Ce paramétrage existe déjà pour cette classe");
        }

        ClasseEntity classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID: " + request.getClasseId()));

        ParametrageEntity parametrage = parametrageRepository.findById(request.getParametrageId())
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé avec l'ID: " + request.getParametrageId()));

        ParametrageclasseEntity entity = new ParametrageclasseEntity();
        entity.setClasse(classe);
        entity.setParametrage(parametrage);
        entity.setMontant(request.getMontant());
        entity.setActif(request.isActif());

        ParametrageclasseEntity savedEntity = repository.save(entity);
        return mapper.toDtoResponse(savedEntity);
    }

    @Override
    public ParametrageclasseDtoResponse update(String id, ParametrageclasseDtoRequest request) {
        ParametrageclasseEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramétrage classe non trouvé avec l'ID: " + id));

        // Vérifier si on change la combinaison classe/paramétrage
        if (!entity.getClasse().getId().equals(request.getClasseId()) ||
                !entity.getParametrage().getId().equals(request.getParametrageId())) {
            if (repository.existsByClasseIdAndParametrageId(request.getClasseId(), request.getParametrageId())) {
                throw new RuntimeException("Ce paramétrage existe déjà pour cette classe");
            }
        }

        ClasseEntity classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'ID: " + request.getClasseId()));

        ParametrageEntity parametrage = parametrageRepository.findById(request.getParametrageId())
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé avec l'ID: " + request.getParametrageId()));

        entity.setClasse(classe);
        entity.setParametrage(parametrage);
        entity.setMontant(request.getMontant());
        entity.setActif(request.isActif());

        ParametrageclasseEntity updatedEntity = repository.save(entity);
        return mapper.toDtoResponse(updatedEntity);
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Paramétrage classe non trouvé avec l'ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ParametrageclasseDtoResponse getById(String id) {
        ParametrageclasseEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramétrage classe non trouvé avec l'ID: " + id));
        return mapper.toDtoResponse(entity);
    }
    @Override
    @Transactional(readOnly = true)
    public ParametrageclasseDtoResponse getByClasseIdAndObject(String classeId, FacturationObject parametrageObject) {
        // Appel du repository avec l'enum
        ParametrageclasseEntity entity = repository.findByClasseIdAndParametrage_Object(classeId, parametrageObject);

        return mapper.toDtoResponse(entity);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParametrageclasseDtoResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametrageclasseDtoResponse> getByClasseId(String classeId) {
        return repository.findByClasseId(classeId)
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametrageclasseDtoResponse> getByParametrageId(String parametrageId) {
        return repository.findByParametrageId(parametrageId)
                .stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByClasseAndParametrage(String classeId, String parametrageId) {
        return repository.existsByClasseIdAndParametrageId(classeId, parametrageId);
    }
}