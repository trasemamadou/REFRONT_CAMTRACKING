package com.groupeisi.ms1_gestschool.scolarite.enseignement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnseignementServiceImpl implements EnseignementService {

    private final EnseignementRepository enseignementRepository;
    private final EnseignementMapper enseignementMapper;

    @Override
    public Optional<EnseignementDtoResponse> save(EnseignementDtoRequest dtoRequest) {
        try {
            EnseignementEntity entity = enseignementMapper.toEntity(dtoRequest);
            EnseignementEntity saved = enseignementRepository.save(entity);
            return Optional.of(enseignementMapper.toDtoResponse(saved));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<EnseignementDtoResponse> update(String id, EnseignementDtoRequest dtoRequest) {
        return enseignementRepository.findById(id)
                .map(existing -> {
                    EnseignementEntity updated = enseignementMapper.toEntity(dtoRequest);
                    updated.setId(existing.getId());
                    EnseignementEntity saved = enseignementRepository.save(updated);
                    return enseignementMapper.toDtoResponse(saved);
                });
    }

    @Override
    public Optional<EnseignementDtoResponse> getById(String id) {
        return enseignementRepository.findById(id)
                .map(enseignementMapper::toDtoResponse);
    }

    @Override
    public List<EnseignementDtoResponse> getAll() {
        return enseignementMapper.toDtoResponseList(enseignementRepository.findAll());
    }

    @Override
    public List<EnseignementDtoResponse> getAllByClasseId(String idClasse) {
         return enseignementMapper.toDtoResponseList(enseignementRepository.findAll());
    }

    @Override
    public Optional<Boolean> delete(String id) {
        return enseignementRepository.findById(id)
                .map(entity -> {
                    enseignementRepository.delete(entity);
                    return true;
                });
    }
}
