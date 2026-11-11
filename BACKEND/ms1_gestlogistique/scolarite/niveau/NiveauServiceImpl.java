package com.groupeisi.ms1_gestschool.scolarite.niveau;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NiveauServiceImpl implements NiveauService {

    private final NiveauRepository repository;
    private final NiveauMapper mapper;

    @Override
    @Transactional
    public NiveauDtoResponse save(NiveauDtoRequest request) {
        NiveauEntity entity = mapper.toNiveauEntity(request);
        entity.setActif(true); // Par défaut à la création
        return mapper.toNiveauDtoResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NiveauDtoResponse> findAll() {
        return mapper.toListNiveauDtoResponse(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NiveauDtoResponse> findById(String id) {
        return repository.findById(id).map(mapper::toNiveauDtoResponse);
    }

    @Override
    @Transactional
    public NiveauDtoResponse update(String id, NiveauDtoRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setLibelle(request.getLibelle());
                    // Ajoutez d'autres champs si nécessaire
                    return mapper.toNiveauDtoResponse(repository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Niveau non trouvé"));
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }
}