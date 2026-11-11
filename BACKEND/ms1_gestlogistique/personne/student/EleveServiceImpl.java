package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.config.exception.EntityExistsException;
import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoRequest;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationDtoResponse;
import com.groupeisi.ms1_gestschool.facturation.facturation.FacturationService;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import com.groupeisi.ms1_gestschool.personne.Role;
import com.groupeisi.ms1_gestschool.personne.dossier.*;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageEntity;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageRepository;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageService;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoRequest;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoResponse;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EleveServiceImpl implements EleveService {

    private final PersonneRepository personneRepository;
    private final DossierRepository dossierRepository;
    private final ApprentissageRepository apprentissageRepository;
    private final EleveMapper studentMapper;
    private final ModelMapper modelMapper;
    private final TutoratService tutoratService;
    private final FacturationService facturationService;
    private final DossierService dossierService;
    private final ApprentissageService apprentissageService;
    private final MessageSource messageSource;
    private final GenericFilterServiceFactory filterServiceFactory;
    private static final int MAX_RETRIES = 5;
    private static final String MATRICULE_PREFIX = "STU";
    private static final SecureRandom random = new SecureRandom();

    @Override
    public Optional<List<EleveDtoResponse>> getAllStudents() {
        List<PersonneEntity> students = personneRepository.findAll();

        // Filtrer pour ne garder que les étudiants (rôle ELEVE)
        List<EleveDtoResponse> responseList = students.stream()
                .filter(student -> student.getRole() != null && "ELEVE".equals(student.getRole().name()))
                .map(studentMapper::toStudentDtoResponse)
                .collect(Collectors.toList());

        return responseList.isEmpty() ? Optional.empty() : Optional.of(responseList);
    }

    @Override
    public Optional<Page<EleveDtoResponse>> getAllElevePaged(Pageable pageable) {
        Page<PersonneEntity> eleves = personneRepository.findByRole(Role.ELEVE, pageable);
        // Filtrer pour ne garder que les étudiants
        Page<EleveDtoResponse> response = eleves.map(studentMapper::toStudentDtoResponse);

        return response.isEmpty() ? Optional.empty() : Optional.of(response);
    }

    @Override
    public Optional<TutoratDtoResponse> saveTutorat(TutoratDtoRequest request) {
        return Optional.of(tutoratService.createTutorat(request));
    }

    @Override
    @Transactional
    public Optional<EleveDtoResponse> saveStudent(EleveDtoRequest eleveDto) {
        // Valider l'unicité des champs
        validateStudentUniqueness(eleveDto);

        // Configurer le rôle et le statut
        eleveDto.setRole("ELEVE");
        eleveDto.setArchive(false);

        try {
            // Générer un matricule unique
            String matricule = generateUniqueMatricule();
            eleveDto.setMatricule(matricule);

            // Mapping DTO -> Entity
            PersonneEntity studentEntity = studentMapper.toStudentEntity(eleveDto);

            // Sauvegarde en base
            PersonneEntity saved = personneRepository.save(studentEntity);

            // Mapping Entity -> DTO
            EleveDtoResponse response = studentMapper.toStudentDtoResponse(saved);
            log.info("Étudiant créé avec succès - ID: {}, Matricule: {}", saved.getId(), saved.getMatricule());

            return Optional.of(response);

        } catch (DataIntegrityViolationException e) {
            log.error("Violation de contrainte d'unicité: {}", e.getMessage());
            throw new EntityExistsException("Un étudiant avec ces informations existe déjà");
        }
    }
    @Override
    @Transactional
    public RegistrationWraperResponse registerStudent(RegistrationWraperRequest request) {
        log.info("Début de l'inscription complète d'un étudiant");

        RegistrationWraperResponse response = new RegistrationWraperResponse();

        try {
            // Étape 1: Vérifications préalables
            EleveDtoRequest eleveRequest = request.getEleveDtoRequest();

            // Vérifier l'email
            if (eleveRequest.getEmail() != null && !eleveRequest.getEmail().isEmpty()) {
                personneRepository.findByEmail(eleveRequest.getEmail()).ifPresent(existing -> {
                    throw new EntityExistsException("Un étudiant avec l'email '" + eleveRequest.getEmail() + "' existe déjà");
                });
            }

            // Vérifier le téléphone
            if (eleveRequest.getTelephone() != null && !eleveRequest.getTelephone().isEmpty()) {
                personneRepository.findByTelephone(eleveRequest.getTelephone()).ifPresent(existing -> {
                    throw new EntityExistsException("Un étudiant avec le téléphone '" + eleveRequest.getTelephone() + "' existe déjà");
                });
            }

            // Étape 2: Créer le dossier (sans ID élève)
            DossierEleveDtoRequest dossierRequest = request.getDossierEleveDtoRequest();
            if (dossierRequest.getType() == null || dossierRequest.getType().isEmpty()) {
                dossierRequest.setType(TypeDossier.ELEVE.name());
            }
            dossierRequest.setEleveId(null); // Important: pas d'ID pour éviter la liaison automatique
            DossierEleveDtoResponse savedDossier = dossierService.createEleveDossier(dossierRequest);
            log.info("Dossier créé avec ID: {}", savedDossier.getId());

            // Étape 3: Créer l'étudiant (sans liaison dossier)
            eleveRequest.setMatricule(null);
            eleveRequest.setDossierId(null); // ← Important: PAS de dossierId ici
            EleveDtoResponse savedStudent = this.saveStudent(eleveRequest)
                    .orElseThrow(() -> new RuntimeException("Erreur lors de la création de l'étudiant"));
            log.info("Étudiant créé avec matricule: {}", savedStudent.getMatricule());

            // 🔥 Étape 4: LIER MANUELLEMENT le dossier à l'étudiant
            PersonneEntity eleveEntity = personneRepository.findById(savedStudent.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Élève non trouvé avec l'id: " + savedStudent.getId()));

            DossierEntity dossierEntity = dossierRepository.findById(savedDossier.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dossier non trouvé avec l'id: " + savedDossier.getId()));

            eleveEntity.setDossier(dossierEntity);
            personneRepository.save(eleveEntity);

            log.info("✅ Dossier {} lié manuellement à l'élève {}", savedDossier.getId(), savedStudent.getId());

            // Étape 5: Créer la facturation
            FacturationDtoRequest facturationRequest = request.getFacturationDtoRequest();
            facturationRequest.setStudentId(savedStudent.getId());

            FacturationDtoResponse savedFacture = facturationService.addFacturation(facturationRequest)
                    .orElseThrow(() -> new RuntimeException("Erreur lors de la création de la facturation"));

            // Étape 6: Récupérer l'étudiant avec son dossier pour la réponse
            EleveDtoResponse studentWithDossier = this.getStudentById(savedStudent.getId())
                    .orElseThrow(() -> new RuntimeException("Erreur lors de la récupération de l'étudiant avec son dossier"));

            // Étape 7: Préparer la réponse
            response.setEleveDtoResponse(studentWithDossier); // ← Utiliser l'étudiant avec dossier
            response.setDossierEleveDtoResponse(savedDossier);
            response.setFacturationDtoResponse(savedFacture);

            log.info("Inscription complète réussie pour l'étudiant ID: {}, Matricule: {}",
                    savedStudent.getId(), savedStudent.getMatricule());

        } catch (EntityExistsException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription complète: {}", e.getMessage(), e);
            throw new RuntimeException("Échec de l'inscription: " + e.getMessage(), e);
        }

        return response;
    }

    @Override
    public Optional<EleveDtoResponse> getStudentById(String id) {

        PersonneEntity student = personneRepository.findPersonneWithDossier(id)
                .filter(s -> s.getRole() != null && "ELEVE".equals(s.getRole().name()))
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("student.notfound", new Object[]{id}, Locale.getDefault())
                ));

        return Optional.of(modelMapper.map(student, EleveDtoResponse.class));
    }


    @Override
    public Page<EleveDtoResponse> filterStudents(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {

        log.info("Filtrage des étudiants avec {} critères", filters != null ? filters.size() : 0);

        try {

            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            // Vérifier si un filtre idClasse existe
            Optional<FilterRequest> classeFilter = allFilters.stream()
                    .filter(f -> "idClasse".equals(f.getChamp()))
                    .findFirst();

            List<String> studentIds;

            if (classeFilter.isPresent()) {

                String idClasse = classeFilter.get().getKeyword();

                studentIds = apprentissageRepository
                        .getAllByClasseId(idClasse)
                        .stream()
                        .map(a -> a.getStudent().getId())
                        .toList();

                // retirer le filtre idClasse
                allFilters.removeIf(f -> "idClasse".equals(f.getChamp()));

                if (studentIds.isEmpty()) {
                    return Page.empty(pageable);
                }
            } else {
                studentIds = null;
            }

            // Ajouter automatiquement le filtre role ELEVE
            FilterRequest roleFilter = new FilterRequest();
            roleFilter.setChamp("role");
            roleFilter.setKeyword("ELEVE");
            roleFilter.setMode("equals");

            allFilters.add(roleFilter);

            GenericFilterService<PersonneEntity> filterService =
                    filterServiceFactory.createGenericFilterService(PersonneEntity.class);

            Page<PersonneEntity> filteredEntities;

            if (sorter != null && sorter.getChamp() != null) {
                filteredEntities = filterService.searchAndSortByFilters(
                        personneRepository,
                        allFilters,
                        sorter,
                        pageable
                );
            } else {
                filteredEntities = filterService.searchByFilters(
                        personneRepository,
                        allFilters,
                        pageable
                );
            }

            // Si un filtre classe était présent → filtrer par les ids récupérés
            if (studentIds != null) {
                List<PersonneEntity> filteredList = filteredEntities
                        .stream()
                        .filter(p -> studentIds.contains(p.getId()))
                        .toList();

                return new PageImpl<>(
                        filteredList.stream()
                                .map(studentMapper::toStudentDtoResponse)
                                .toList(),
                        pageable,
                        filteredList.size()
                );
            }

            return filteredEntities.map(studentMapper::toStudentDtoResponse);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des étudiants", e);
            throw new RuntimeException("Erreur lors du filtrage des étudiants", e);
        }
    }
    @Override
    @Transactional
    public Optional<EleveDtoResponse> updateStudent(String id, EleveDtoRequest studentRequest) {
        PersonneEntity existingStudent = personneRepository.findById(id)
                .filter(student -> student.getRole() != null && "ELEVE".equals(student.getRole().name()))
                .orElseThrow(() -> {
                    log.error("L'étudiant avec l'ID : {} n'a pas été trouvé", id);
                    return new EntityNotFoundException(
                            messageSource.getMessage("student.notfound", new Object[]{id}, Locale.getDefault())
                    );
                });

        // Ne pas permettre la modification du matricule
        if (studentRequest.getMatricule() != null &&
                !studentRequest.getMatricule().equals(existingStudent.getMatricule())) {
            throw new IllegalArgumentException("Le matricule ne peut pas être modifié");
        }

        // Mise à jour des champs autorisés
        updateAllowedFields(existingStudent, studentRequest);

        PersonneEntity updatedStudent = personneRepository.save(existingStudent);
        log.info("Les informations de l'étudiant avec l'ID : {} ont été mises à jour avec succès", id);

        return Optional.of(studentMapper.toStudentDtoResponse(updatedStudent));
    }

    @Override
    @Transactional
    public Optional<EleveDtoResponse> deleteStudentById(String id) {
        PersonneEntity student = personneRepository.findById(id)
                .filter(s -> s.getRole() != null && "ELEVE".equals(s.getRole().name()))
                .orElseThrow(() -> {
                    log.error("L'étudiant avec l'ID : {} n'a pas été trouvé", id);
                    return new EntityNotFoundException(
                            messageSource.getMessage("student.notfound", new Object[]{id}, Locale.getDefault())
                    );
                });

        log.info("Suppression de l'étudiant avec l'ID : {}", id);
        personneRepository.deleteById(student.getId());
        log.info("L'étudiant avec l'ID : {} a été supprimé avec succès", id);

        return Optional.of(studentMapper.toStudentDtoResponse(student));
    }

    private void validateStudentUniqueness(EleveDtoRequest eleveDto) {
        // Utiliser les méthodes du repository qui existent réellement
        if (eleveDto.getEmail() != null && !eleveDto.getEmail().isEmpty()) {
            if (personneRepository.existsByEmail(eleveDto.getEmail())) {
                throw new EntityExistsException("Un étudiant avec l'email '" + eleveDto.getEmail() + "' existe déjà");
            }
        }

        if (eleveDto.getTelephone() != null && !eleveDto.getTelephone().isEmpty()) {
            if (personneRepository.existsByTelephone(eleveDto.getTelephone())) {
                throw new EntityExistsException("Un étudiant avec le téléphone '" + eleveDto.getTelephone() + "' existe déjà");
            }
        }

//        if (eleveDto.getNumeroCNI() != null && !eleveDto.getNumeroCNI().isEmpty()) {
//            if (personneRepository.existsByNumeroCNI(eleveDto.getNumeroCNI())) {
//                throw new EntityExistsException("Un étudiant avec le numéro CNI '" + eleveDto.getNumeroCNI() + "' existe déjà");
//            }
//        }
    }

    private String generateUniqueMatricule() {
        String matricule;
        int attempts = 0;

        do {
            if (attempts >= MAX_RETRIES) {
                // Si trop de tentatives, utiliser timestamp
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                matricule = MATRICULE_PREFIX + timestamp;
                log.warn("Génération de matricule avec timestamp après {} tentatives", attempts);
                return matricule;
            }

            // Générer un nombre aléatoire entre 10000 et 99999
            int number = 10000 + random.nextInt(90000);
            matricule = MATRICULE_PREFIX + number;
            attempts++;

        } while (personneRepository.existsByMatricule(matricule));

        log.debug("Matricule généré après {} tentative(s): {}", attempts, matricule);
        return matricule;
    }

    private void updateAllowedFields(PersonneEntity existing, EleveDtoRequest request) {
        if (request.getPrenom() != null) {
            existing.setPrenom(request.getPrenom());
        }
        if (request.getNom() != null) {
            existing.setNom(request.getNom());
        }
        if (request.getEmail() != null) {
            // Vérifier que le nouvel email n'est pas déjà utilisé par un autre étudiant
            if (!request.getEmail().equals(existing.getEmail()) &&
                    personneRepository.existsByEmail(request.getEmail())) {
                throw new EntityExistsException("L'email '" + request.getEmail() + "' est déjà utilisé");
            }
            existing.setEmail(request.getEmail());
        }
        if (request.getTelephone() != null) {
            if (!request.getTelephone().equals(existing.getTelephone()) &&
                    personneRepository.existsByTelephone(request.getTelephone())) {
                throw new EntityExistsException("Le téléphone '" + request.getTelephone() + "' est déjà utilisé");
            }
            existing.setTelephone(request.getTelephone());
        }
        if (request.getAdresse() != null) {
            existing.setAdresse(request.getAdresse());
        }
        if (request.getDateNaissance() != null) {
            existing.setDateNaissance(request.getDateNaissance());
        }
        if (request.getLieuNaissance() != null) {
            existing.setLieuNaissance(request.getLieuNaissance());
        }
//        if (request.getSexe() != null) {
//            existing.setSexe(request.getSexe());
//        }
        if (request.getNationalite() != null) {
            existing.setNationalite(request.getNationalite());
        }
//        if (request.getSituationMatrimoniale() != null) {
//            existing.setSituationMatricule(request.getSituationMatrimoniale());
//        }
//        if (request.getNumeroCNI() != null) {
//            if (!request.getNumeroCNI().equals(existing.getNumeroCNI()) &&
//                    personneRepository.existsByNumeroCNI(request.getNumeroCNI())) {
//                throw new EntityExistsException("Le numéro CNI '" + request.getNumeroCNI() + "' est déjà utilisé");
//            }
//            existing.setNumeroCNI(request.getNumeroCNI());
//        }
        if (request.getDossierId() != null) {
            // La logique pour lier le dossier sera gérée ailleurs
        }
    }
}