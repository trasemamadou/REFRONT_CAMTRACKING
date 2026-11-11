package com.groupeisi.ms1_gestschool.scolarite.apprentissage;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseDtoResponse;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprentissageServiceImpl implements ApprentissageService {

    private final ApprentissageRepository enseignementRepository;
    private final ApprentissageMapper enseignementMapper;
    private final EleveMapper eleveMapper;

    @Override
    public Optional<ApprentissageDtoResponse> save(ApprentissageDtoRequest dtoRequest) {
        try {
            ApprentissageEntity entity = enseignementMapper.toEntity(dtoRequest);
            ApprentissageEntity saved = enseignementRepository.save(entity);
            return Optional.of(enseignementMapper.toDtoResponse(saved));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ApprentissageDtoResponse> update(String id, ApprentissageDtoRequest dtoRequest) {
        return enseignementRepository.findById(id)
                .map(existing -> {
                    ApprentissageEntity updated = enseignementMapper.toEntity(dtoRequest);
                    updated.setId(existing.getId());
                    ApprentissageEntity saved = enseignementRepository.save(updated);
                    return enseignementMapper.toDtoResponse(saved);
                });
    }

    @Override
    public Optional<ApprentissageDtoResponse> getById(String id) {
        return enseignementRepository.findById(id)
                .map(enseignementMapper::toDtoResponse);
    }

    @Override
    public List<ApprentissageDtoResponse> getAll() {
        return enseignementMapper.toDtoResponseList(enseignementRepository.findAll());
    }

    @Override
    public List<ApprentissageDtoResponse> getAllByClasseId(String idClasse) {
         return enseignementMapper.toDtoResponseList(enseignementRepository.findAll());
    }

    @Override
    public Page<ApprentissageDtoResponse> getAllByClasseIdPaged(String idClasse, Pageable pageable) {
        Page<ApprentissageEntity> classes = enseignementRepository.findAll(pageable);

        Page<ApprentissageDtoResponse> response =
                classes.map(enseignementMapper::toDtoResponse);

        return response;
    }

    @Override
    public Optional<Page<EleveDtoResponse>> getStudentsByClasseIdPaged(String classeId, Pageable pageable) {
        Page<ApprentissageEntity> apprentissagesPage = enseignementRepository.findAllByClasseId(classeId, pageable);

        Page<EleveDtoResponse> response = apprentissagesPage
                .map(apprentissage -> eleveMapper.toStudentDtoResponse(apprentissage.getStudent()));

        return response.isEmpty() ? Optional.empty() : Optional.of(response);
    }

    @Override
    public Optional<List<EleveDtoResponse>> getStudentsByClasseId(String classeId) {
        List<ApprentissageEntity> apprentissages = enseignementRepository.findAllByClasseId(classeId);

        List<EleveDtoResponse> response = apprentissages.stream()
                .map(apprentissage -> eleveMapper.toStudentDtoResponse(apprentissage.getStudent()))
                .toList(); // Java 16+

        return response.isEmpty() ? Optional.empty() : Optional.of(response);
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
