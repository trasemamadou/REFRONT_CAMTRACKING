package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseRepository;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationObject;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseMapper;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParametrageServiceImpl implements ParametrageService {

    private final ParametrageRepository repository;
    private final ClasseRepository classeRepository;
    private final ParametrageMapper mapper;
    private final GenericFilterServiceFactory filterServiceFactory;
    private final ParametrageclasseMapper mapperParamClasse;
    private final ParametrageclasseRepository paramclasseRepository;
    @Override
    public ParametrageDtoResponse create(ParametrageDtoRequest request) {
        ParametrageEntity entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public ParametrageDtoResponse createWithParamSalaire(ParametragesalaireWrapper request) {

        if (request.getParametrage() == null || request.getParametrageclasse() == null) {
            throw new IllegalArgumentException("La requête ou le paramétrage est null");
        }

        try {
            System.out.println("DEBUG: Parametrage reçu: " + request.getParametrage());
            System.out.println("DEBUG: Parametrageclasse reçu: " + request.getParametrageclasse());

            request.getParametrage().setObject("SALAIRE");
            ParametrageEntity entity = mapper.toEntity(request.getParametrage());
            System.out.println("DEBUG: Entity parametrage: " + entity);

            ParametrageEntity savedEntity = repository.save(entity);
            System.out.println("DEBUG: Saved entity: " + savedEntity);

            request.getParametrageclasse().setParametrageId(savedEntity.getId());
            ParametrageclasseEntity paramSalaire = mapperParamClasse.toEntity(request.getParametrageclasse());
            System.out.println("DEBUG: Entity parametrageclasse: " + paramSalaire);

            paramSalaire.setParametrage(savedEntity);
            paramclasseRepository.save(paramSalaire);
            System.out.println("DEBUG: Parametrageclasse saved successfully");

            return mapper.toDto(savedEntity);

        } catch (Exception e) {
            e.printStackTrace(); // <- pour voir l’exception réelle
            throw new RuntimeException("Erreur lors de la création du paramétrage avec salaire", e);
        }
    }

    @Override
    public ParametrageDtoResponse update(String id, ParametrageDtoRequest request) {
        ParametrageEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramétrage introuvable !"));
        mapper.updateEntityFromRequest(request, entity);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public ParametrageDtoResponse getById(String id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Paramétrage introuvable !"));
    }

    @Override
    public List<ParametrageDtoResponse> getAll() {
        return mapper.toDtoList(repository.findAll());
    }


    public Optional<ParametrageDtoResponse> getByObject(FacturationObject object) {
        return Optional.of(repository.findByObject( object)
                .map(entity -> mapper.toDto(entity))
                .orElseThrow(() -> new RuntimeException("Paramétrage non trouvé")));
    }

    @Override
    public Page<ParametrageDtoResponse> filterParametrages(
            List<FilterRequest> filters,
            SorterRequest sorter,
            Pageable pageable) {

        try {
            // Initialiser la liste de filtres pour éviter les nulls
            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            // Vérifier si un filtre "ecole" existe
            Optional<FilterRequest> ecoleFilter = allFilters.stream()
                    .filter(f -> "ecole".equals(f.getChamp()))
                    .findFirst();

            Page<ParametrageEntity> filteredEntities;

            if (ecoleFilter.isPresent()) {
                String ecoleNom = ecoleFilter.get().getKeyword();

                // Retirer le filtre ecole pour le GenericFilterService
                allFilters.removeIf(f -> "ecole".equals(f.getChamp()));

                // Récupérer directement la page paginée via le repository
                filteredEntities = repository.findAllByEcole_Nom(ecoleNom, pageable);

            } else {
                // Pas de filtre école → filtrer avec GenericFilterService
                GenericFilterService<ParametrageEntity> filterService =
                        filterServiceFactory.createGenericFilterService(ParametrageEntity.class);

                if (sorter != null && sorter.getChamp() != null) {
                    filteredEntities = filterService.searchAndSortByFilters(
                            repository,       // ✅ ici on passe le repository
                            allFilters,
                            sorter,
                            pageable
                    );
                } else {
                    filteredEntities = filterService.searchByFilters(
                            repository,
                            allFilters,
                            pageable
                    );
                }
            }

            // Mapper vers DTO
            return filteredEntities.map(mapper::toDto);

        } catch (Exception e) {
           // log.error("Erreur lors du filtrage des paramètres", e);
            throw new RuntimeException("Erreur lors du filtrage des paramètres", e);
        }
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id))
            throw new RuntimeException("Paramétrage introuvable !");
        repository.deleteById(id);
    }
}
