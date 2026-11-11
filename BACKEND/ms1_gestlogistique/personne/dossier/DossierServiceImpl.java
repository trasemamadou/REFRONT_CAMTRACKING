package com.groupeisi.ms1_gestschool.personne.dossier;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@Transactional
public class DossierServiceImpl implements DossierService {

    private final DossierRepository dossierRepository;
    private final DossierMapper mapper;

    // ========== ÉLÈVE ==========
    @Override
    public DossierEleveDtoResponse createEleveDossier(DossierEleveDtoRequest request) {
        if (!request.getType().equalsIgnoreCase("ELEVE") || request.getType()==null){
            request.setType("ELEVE");
        }
        DossierEntity entity = mapper.toEntity(request);
        DossierEntity saved = dossierRepository.save(entity);
        return mapper.toEleveResponse(saved);
    }

    @Override
    public DossierEleveDtoResponse getEleveDossierById(String id) {
        DossierEntity entity = dossierRepository.findByIdAndType(id, TypeDossier.ELEVE)
                .orElseThrow(() -> new EntityNotFoundException("Dossier élève non trouvé avec l'id : " + id));
        return mapper.toEleveResponse(entity);
    }

    @Override
    public DossierEleveDtoResponse updateEleveDossier(String id, DossierEleveDtoRequest request) {
        DossierEntity entity = dossierRepository.findByIdAndType(id, TypeDossier.ELEVE)
                .orElseThrow(() -> new EntityNotFoundException("Dossier élève non trouvé avec l'id : " + id));
        mapper.updateEntity(entity, request);
        DossierEntity updated = dossierRepository.save(entity);
        return mapper.toEleveResponse(updated);
    }

    @Override
    public void deleteEleveDossier(String id) {
        if (!dossierRepository.existsByIdAndType(id, TypeDossier.ELEVE)) {
            throw new EntityNotFoundException("Dossier élève non trouvé avec l'id : " + id);
        }
        dossierRepository.deleteById(id);
    }

    @Override
    public List<DossierEleveDtoResponse> getAllEleveDossiers() {
        return dossierRepository.findByType(TypeDossier.ELEVE)
                .stream()
                .map(mapper::toEleveResponse)
                .collect(Collectors.toList());
    }

    // ========== ENSEIGNANT ==========
    @Override
    public DossierEnseignantDtoResponse createEnseignantDossier(DossierEnseignantDtoRequest request) {
        DossierEntity entity = mapper.toEntity(request);
        DossierEntity saved = dossierRepository.save(entity);
        return mapper.toEnseignantResponse(saved);
    }

    @Override
    public DossierEnseignantDtoResponse getEnseignantDossierById(String id) {
        DossierEntity entity = dossierRepository.findByIdAndType(id, TypeDossier.ENSEIGNANT)
                .orElseThrow(() -> new EntityNotFoundException("Dossier enseignant non trouvé avec l'id : " + id));
        return mapper.toEnseignantResponse(entity);
    }

    @Override
    public DossierEnseignantDtoResponse updateEnseignantDossier(String id, DossierEnseignantDtoRequest request) {
        DossierEntity entity = dossierRepository.findByIdAndType(id, TypeDossier.ENSEIGNANT)
                .orElseThrow(() -> new EntityNotFoundException("Dossier enseignant non trouvé avec l'id : " + id));
        mapper.updateEntity(entity, request);
        DossierEntity updated = dossierRepository.save(entity);
        return mapper.toEnseignantResponse(updated);
    }

    @Override
    public void deleteEnseignantDossier(String id) {
        if (!dossierRepository.existsByIdAndType(id, TypeDossier.ENSEIGNANT)) {
            throw new EntityNotFoundException("Dossier enseignant non trouvé avec l'id : " + id);
        }
        dossierRepository.deleteById(id);
    }

    @Override
    public List<DossierEnseignantDtoResponse> getAllEnseignantDossiers() {
        return dossierRepository.findByType(TypeDossier.ENSEIGNANT)
                .stream()
                .map(mapper::toEnseignantResponse)
                .collect(Collectors.toList());
    }
}