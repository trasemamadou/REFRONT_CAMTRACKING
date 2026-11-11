package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.config.exception.EntityExistsException;
import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClasseServiceImpl implements ClasseService {

    private final ClasseRepository classeRepository;
    private final ClasseMapper classeMapper;
    private final MessageSource messageSource;
    private final GenericFilterServiceFactory filterServiceFactory;
    private final ApprentissageRepository apprentissageRepository;
    private final Logger logger = LoggerFactory.getLogger(ClasseServiceImpl.class);

    // 🔥 Méthode centrale pour enrichir le DTO
    private ClasseDtoResponse enrichirAvecEffectif(ClasseEntity classe) {
        ClasseDtoResponse dto = classeMapper.toClasseDtoResponse(classe);

        // ✅ Nombre d'élèves actifs
        long effectif = apprentissageRepository.countByClasseIdAndActiveTrue(classe.getId());
        dto.setEffectifActuel((int) effectif);

        // ✅ Calcul taux occupation
        if (dto.getCapaciteMax() != null && dto.getCapaciteMax() > 0) {
            double taux = (effectif * 100.0) / dto.getCapaciteMax();
            dto.setTauxOccupation(taux);
        } else {
            dto.setTauxOccupation(0.0);
        }

        return dto;
    }

    @Override
    public Optional<List<ClasseDtoResponse>> getAllClasses() {
        List<ClasseDtoResponse> list = classeRepository.findAll()
                .stream()
                .map(this::enrichirAvecEffectif)
                .toList();

        return Optional.of(list);
    }

    @Override
    public Optional<Page<ClasseDtoResponse>> getAllClassesPaged(Pageable pageable) {

        Page<ClasseEntity> classes = classeRepository.findAll(pageable);

        Page<ClasseDtoResponse> response = classes.map(this::enrichirAvecEffectif);

        return Optional.of(response);
    }

    @Override
    public Optional<ClasseDtoResponse> getClasseById(String id) {

        ClasseEntity classe = classeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("classe.notfound", new Object[]{id}, Locale.getDefault())
                ));

        return Optional.of(enrichirAvecEffectif(classe));
    }

    @Override
    public Optional<ClasseDtoResponse> addClasse(ClasseDtoRequest classe) {

        ClasseEntity saved = classeRepository.save(classeMapper.toClasseEntity(classe));

        return Optional.of(enrichirAvecEffectif(saved));
    }

    @Override
    public Page<ClasseDtoResponse> filterClasses(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {

        log.info("Filtrage des classes avec {} critères", filters != null ? filters.size() : 0);

        try {
            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            Optional<FilterRequest> ecoleFilter = allFilters.stream()
                    .filter(f -> "ecole".equals(f.getChamp()))
                    .findFirst();

            Page<ClasseEntity> filteredEntities;

            if (ecoleFilter.isPresent()) {
                String ecoleNom = ecoleFilter.get().getKeyword();
                allFilters.removeIf(f -> "ecole".equals(f.getChamp()));

                filteredEntities = classeRepository.findAllByEcole_Nom(ecoleNom, pageable);

            } else {
                GenericFilterService<ClasseEntity> filterService =
                        filterServiceFactory.createGenericFilterService(ClasseEntity.class);

                if (sorter != null && sorter.getChamp() != null) {
                    filteredEntities = filterService.searchAndSortByFilters(
                            classeRepository,
                            allFilters,
                            sorter,
                            pageable
                    );
                } else {
                    filteredEntities = filterService.searchByFilters(
                            classeRepository,
                            allFilters,
                            pageable
                    );
                }
            }

            // 🔥 enrichissement ici
            return filteredEntities.map(this::enrichirAvecEffectif);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des classes", e);
            throw new RuntimeException("Erreur lors du filtrage des classes", e);
        }
    }

    @Override
    public Optional<ClasseDtoResponse> updateClasse(String id, ClasseDtoRequest request) {

        ClasseEntity classe = classeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("classe.notfound", new Object[]{request.getId()}, Locale.getDefault())
                ));

        Optional<ClasseEntity> existingClasse = classeRepository.findFirstByNom(request.getNom());

        if (existingClasse.isPresent() && !existingClasse.get().getId().equals(classe.getId())) {
            throw new EntityExistsException(
                    messageSource.getMessage("classeName.exist", new Object[]{request.getNom()}, Locale.getDefault())
            );
        }

        classe.setNom(request.getNom());
        classe.setDescription(request.getDescription());
        classe.setArchive(request.isArchive());

        ClasseEntity updated = classeRepository.save(classe);

        return Optional.of(enrichirAvecEffectif(updated));
    }

    @Override
    public Optional<?> deleteClasse(String id) {
        if (classeRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(
                    messageSource.getMessage("classe.notfound", new Object[]{id}, Locale.getDefault())
            );
        }

        classeRepository.deleteById(id);
        return Optional.empty();
    }
}
