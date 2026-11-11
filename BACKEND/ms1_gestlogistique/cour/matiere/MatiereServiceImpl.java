package com.groupeisi.ms1_gestschool.cour.matiere;
import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseRepository;
import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatiereServiceImpl implements MatiereService {

    private final   MatiereRepository matiereRepository;
    private final ClasseRepository classeRepository;
    private  final MatiereMapper courseMapper;
    private final GenericFilterServiceFactory filterServiceFactory;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(MatiereServiceImpl.class);

    @Override
    public MatiereDtoResponse createMatiere(MatiereDtoRequest request) {
        MatiereEntity course = courseMapper.toCourseEntity(request);
//        if (!classeRepository.findById(request.getClasseId()).isPresent()){
//            throw new EntityNotFoundException(messageSource.getMessage("classe.notfound", new Object[]{request.getClasseId()}, Locale.getDefault()));
//        }
        logger.info("Engregistrement du cours: {}", course.getName());
        course = matiereRepository.save(course);
        return courseMapper.toCourseDtoResponse(course);
    }

    @Override
    public List<MatiereDtoResponse> getAllMatieres() {
        return matiereRepository.findAll().stream()
                .map(courseMapper::toCourseDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MatiereDtoResponse> filterMatieres(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {
        log.info("Filtrage des matières avec {} critères", filters != null ? filters.size() : 0);

        try {

            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            // Vérifier si un filtre ecole existe
            Optional<FilterRequest> ecoleFilter = allFilters.stream()
                    .filter(f -> "ecole".equals(f.getChamp()))
                    .findFirst();

            Page<MatiereEntity> filteredEntities;

            if (ecoleFilter.isPresent()) {
                String ecoleNom = ecoleFilter.get().getKeyword();

                // retirer le filtre ecole pour le GenericFilterService
                allFilters.removeIf(f -> "ecole".equals(f.getChamp()));

                // Récupérer directement la page paginée via le repository
                filteredEntities = matiereRepository.findAllByEcole_Nom(ecoleNom, pageable);

            } else {
                // Pas de filtre école → filtrer avec GenericFilterService
                GenericFilterService<MatiereEntity> filterService =
                        filterServiceFactory.createGenericFilterService(MatiereEntity.class);

                if (sorter != null && sorter.getChamp() != null) {
                    filteredEntities = filterService.searchAndSortByFilters(
                            matiereRepository,
                            allFilters,
                            sorter,
                            pageable
                    );
                } else {
                    filteredEntities = filterService.searchByFilters(
                            matiereRepository,
                            allFilters,
                            pageable
                    );
                }
            }

            return filteredEntities.map(courseMapper::toCourseDtoResponse);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des matières", e);
            throw new RuntimeException("Erreur lors du filtrage des matières", e);
        }
    }

    @Override
    public Optional<MatiereDtoResponse> getMatiereByName(String name) {
        if (!matiereRepository.findById(name).isPresent()) {
            logger.info("L'ID du cours demandé n'existe pas : {}", name);
            throw new EntityNotFoundException(messageSource.getMessage("course.notfound", new Object[]{name}, Locale.getDefault()));
        }
        return matiereRepository.findById(name)
                .map(courseMapper::toCourseDtoResponse);
    }

    @Override
    public Optional<MatiereDtoResponse> updateMatiere(String name, MatiereDtoRequest request) {
        if (!matiereRepository.findById(name).isPresent()){
            logger.info("Cours avec l'id {} est introuvable", name);
            throw new EntityNotFoundException(messageSource.getMessage("course.notfound", new Object[]{name}, Locale.getDefault()));
        }
        return matiereRepository.findById(name).map(course -> {
            course.setName(request.getName());
            course.setDescription(request.getDescription());
            course.setArchive(request.isArchive());
            MatiereEntity updatedCourse = matiereRepository.save(course);
            return courseMapper.toCourseDtoResponse(updatedCourse);
        });
    }

    @Override
    public boolean deleteMatiere(String name) {
        if (!matiereRepository.findById(name).isPresent()){
            logger.info("Le cours avec l'id {} est introuvable", name);
            throw new EntityNotFoundException(messageSource.getMessage("course.notfound", new Object[]{name}, Locale.getDefault()));
        }
        try{
        matiereRepository.deleteById(name);
        return true;
        }catch (Exception e) {
            System.err.println("Error " + e);
            return false;
        }
    }
}
