package com.groupeisi.ms1_gestschool.scolarite.annee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnneeServiceImpl implements AnneeService {

    private final AnneeRepository repository;
    private final AnneeMapper mapper;

    @Override
    @Transactional
    public AnneeDtoResponse save(AnneeDtoRequest request) {
        AnneeEntity entity = mapper.toAnneeEntity(request);
        entity.setActif(true); // Par défaut à la création
        return mapper.toAnneeDtoResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnneeDtoResponse> findAll() {
        return mapper.toListAnneeDtoResponse(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnneeDtoResponse> findById(String id) {
        return repository.findById(id).map(mapper::toAnneeDtoResponse);
    }

    @Override
    @Transactional
    public AnneeDtoResponse update(String id, AnneeDtoRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setLibelle(request.getLibelle());
                    // Ajoutez d'autres champs si nécessaire
                    return mapper.toAnneeDtoResponse(repository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Année non trouvée"));
    }

    @Override
    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }
}