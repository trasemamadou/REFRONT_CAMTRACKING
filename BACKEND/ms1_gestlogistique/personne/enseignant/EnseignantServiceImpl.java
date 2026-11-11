package com.groupeisi.ms1_gestschool.personne.enseignant;

import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.personne.*;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierEntity;
import com.groupeisi.ms1_gestschool.personne.dossier.DossierRepository;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EnseignantServiceImpl implements EnseignantService {

    private final PersonneRepository enseignantRepository;
    private final DossierRepository dossierRepository;
    private final EnseignantMapper enseignantMapper;
    private final MessageSource messageSource;
    private final GenericFilterServiceFactory filterServiceFactory;
    @Override
    @Transactional(readOnly = true)
    public Optional<List<EnseignantDtoResponse>> getAllEnseignants() {
        List<PersonneEntity> enseignants = enseignantRepository.findByRole(Role.PROFESSEUR);
        if (enseignants.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(enseignantMapper.toEnseignantDtoResponseList(enseignants));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Page<EnseignantDtoResponse>> getAllEnseignantsPaged(Pageable pageable) {
        Page<PersonneEntity> enseignants = enseignantRepository.findByRole(Role.PROFESSEUR, pageable);
        if (enseignants.isEmpty()) {
            return Optional.empty();
        }
        Page<EnseignantDtoResponse> dtoPage = enseignants.map(enseignantMapper::toEnseignantDtoResponse);
        return Optional.of(dtoPage);
    }

    @Override
    public Optional<EnseignantDtoResponse> saveEnseignant(EnseignantDtoRequest request) {
        try {
            // Vérifier l'unicité des données
            if (enseignantRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Un enseignant avec cet email existe déjà");
            }

            if (enseignantRepository.existsByTelephone(request.getTelephone())) {
                throw new IllegalArgumentException("Un enseignant avec ce téléphone existe déjà");
            }

            if (enseignantRepository.existsByNumeroCNI(request.getNumeroCNI())) {
                throw new IllegalArgumentException("Un enseignant avec ce numéro CNI existe déjà");
            }

            // Générer le matricule
            String matricule = generateMatricule();
            request.setMatricule(matricule);

            // Définir le statut par défaut si non fourni
            if (request.getStatut() == null) {
                request.setStatut(StatutPerson.ACTIF);
            }

            // Convertir et sauvegarder
            PersonneEntity enseignant = enseignantMapper.toEnseignantEntity(request);
            enseignant.setDateCreation(LocalDateTime.now());
            enseignant.setRole(Role.PROFESSEUR);

            PersonneEntity saved = enseignantRepository.save(enseignant);
            log.info("Enseignant créé avec succès - Matricule: {}, ID: {}", matricule, saved.getId());

            return Optional.of(enseignantMapper.toEnseignantDtoResponse(saved));
        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation lors de la création de l'enseignant: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création de l'enseignant", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<EnseignantDtoResponse> saveEnseignantWithDossier(EnseignantWrapperDtoRequest wrapperRequest) {
        try {
            // 1. Sauvegarder l'enseignant
            EnseignantDtoRequest enseignantRequest = wrapperRequest.getEnseignantRequest();

            // Vérifications d'unicité
            if (enseignantRepository.existsByEmail(enseignantRequest.getEmail())) {
                throw new IllegalArgumentException("Un enseignant avec cet email existe déjà");
            }
            if (enseignantRepository.existsByTelephone(enseignantRequest.getTelephone())) {
                throw new IllegalArgumentException("Un enseignant avec ce téléphone existe déjà");
            }
            if (enseignantRepository.existsByNumeroCNI(enseignantRequest.getNumeroCNI())) {
                throw new IllegalArgumentException("Un enseignant avec ce numéro CNI existe déjà");
            }

            // Générer le matricule
            String matricule = generateMatricule();
            enseignantRequest.setMatricule(matricule);

            if (enseignantRequest.getStatut() == null) {
                enseignantRequest.setStatut(StatutPerson.ACTIF);
            }

            // Créer l'enseignant
            PersonneEntity enseignant = enseignantMapper.toEnseignantEntity(enseignantRequest);
            enseignant.setDateCreation(LocalDateTime.now());
            if (enseignant.getRole()==null || enseignant.getRole().equals("PROFESSEUR")) {
                enseignant.setRole(Role.PROFESSEUR);
            }

            PersonneEntity savedEnseignant = enseignantRepository.save(enseignant);

            // 2. Créer le dossier si fourni
            if (wrapperRequest.getDossierRequest() != null) {
                DossierEntity dossier = DossierEntity.builder()
                        .photoProfil(wrapperRequest.getDossierRequest().getPhotoProfil())
                        .fileCni(wrapperRequest.getDossierRequest().getFileCni())
                        .nomUrgence(wrapperRequest.getDossierRequest().getNomUrgence())
                        .telUrgence(wrapperRequest.getDossierRequest().getTelUrgence())
                        .lienUrgence(wrapperRequest.getDossierRequest().getLienUrgence())
                        .antecedents(wrapperRequest.getDossierRequest().getAntecedents())
                        .allergies(wrapperRequest.getDossierRequest().getAllergies())
                        .experience(wrapperRequest.getDossierRequest().getExperience())
                        .adresse(wrapperRequest.getDossierRequest().getAdresse())
                        .diplome(wrapperRequest.getDossierRequest().getDiplome())
                        .dateEmbauche(wrapperRequest.getDossierRequest().getDateEmbauche())
                        .typeContrat(wrapperRequest.getDossierRequest().getTypeContrat())
                        .salaire(wrapperRequest.getDossierRequest().getSalaire())
                        .banque(wrapperRequest.getDossierRequest().getBanque())
                        .numeroCompte(wrapperRequest.getDossierRequest().getNumeroCompte())
                        .specialite(wrapperRequest.getDossierRequest().getSpecialite())
                        .personne(savedEnseignant)
                        .build();

                dossierRepository.save(dossier);
                savedEnseignant.setDossier(dossier);
            }

            log.info("Enseignant créé avec dossier - Matricule: {}, ID: {}", matricule, savedEnseignant.getId());
            return Optional.of(enseignantMapper.toEnseignantDtoResponse(savedEnseignant));

        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création", e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnseignantDtoResponse> getEnseignantById(String id) {
        PersonneEntity enseignant = enseignantRepository.findByIdAndRole(id, Role.PROFESSEUR)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("enseignant.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        return Optional.of(enseignantMapper.toEnseignantDtoResponse(enseignant));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnseignantDtoResponse> getEnseignantByMatricule(String matricule) {
        PersonneEntity enseignant = enseignantRepository.findByMatriculeAndRole(matricule, Role.PROFESSEUR)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("enseignant.notfound.matricule",
                            new Object[]{matricule}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        return Optional.of(enseignantMapper.toEnseignantDtoResponse(enseignant));
    }

    @Override
    public Optional<EnseignantDtoResponse> updateEnseignant(String id, EnseignantDtoRequest request) {
        PersonneEntity existing = enseignantRepository.findByIdAndRole(id, Role.PROFESSEUR)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("enseignant.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });

        // Vérifier l'unicité de l'email (sauf pour l'utilisateur actuel)
        if (!existing.getEmail().equals(request.getEmail()) &&
                enseignantRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un enseignant avec cet email existe déjà");
        }

        // Vérifier l'unicité du téléphone (sauf pour l'utilisateur actuel)
        if (!existing.getTelephone().equals(request.getTelephone()) &&
                enseignantRepository.existsByTelephone(request.getTelephone())) {
            throw new IllegalArgumentException("Un enseignant avec ce téléphone existe déjà");
        }

        // Vérifier l'unicité du numéro CNI (sauf pour l'utilisateur actuel)
        if (!existing.getNumeroCNI().equals(request.getNumeroCNI()) &&
                enseignantRepository.existsByNumeroCNI(request.getNumeroCNI())) {
            throw new IllegalArgumentException("Un enseignant avec ce numéro CNI existe déjà");
        }

        // Mise à jour des champs
        existing.setPrenom(request.getPrenom());
        existing.setNom(request.getNom());
        existing.setDateNaissance(request.getDateNaissance());
        existing.setLieuNaissance(request.getLieuNaissance());
        existing.setPhotoProfil(request.getPhotoProfil());
        existing.setSexe(Sexe.valueOf(request.getSexe()));
        existing.setNationalite(request.getNationalite());
        existing.setSituationMatrimoniale(request.getSituationMatrimoniale());
        existing.setNumeroCNI(request.getNumeroCNI());
        existing.setAdresse(request.getAdresse());
        existing.setTelephone(request.getTelephone());
        existing.setEmail(request.getEmail());
        existing.setArchive(request.isArchive());
        existing.setStatut(request.getStatut());

        PersonneEntity updated = enseignantRepository.save(existing);
        log.info("Enseignant mis à jour avec succès - ID: {}", id);

        return Optional.of(enseignantMapper.toEnseignantDtoResponse(updated));
    }

    @Override
    public void deleteEnseignantById(String id) {
        PersonneEntity enseignant = enseignantRepository.findByIdAndRole(id, Role.PROFESSEUR)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("enseignant.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });

        // Supprimer d'abord le dossier s'il existe
        if (enseignant.getDossier() != null) {
            dossierRepository.delete(enseignant.getDossier());
        }

        enseignantRepository.delete(enseignant);
        log.info("Enseignant supprimé avec succès - ID: {}", id);
    }

    @Override
    public Optional<EnseignantDtoResponse> changeStatus(String id, String statut) {
        PersonneEntity enseignant = enseignantRepository.findByIdAndRole(id, Role.PROFESSEUR)
                .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID: " + id));

        try {
            StatutPerson nouveauStatut = StatutPerson.valueOf(statut.toUpperCase());
            enseignant.setStatut(nouveauStatut);
            PersonneEntity updated = enseignantRepository.save(enseignant);
            log.info("Statut de l'enseignant ID: {} changé à: {}", id, nouveauStatut);

            return Optional.of(enseignantMapper.toEnseignantDtoResponse(updated));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut invalide: " + statut + ". Valeurs acceptées: ACTIF, INACTIF, SUSPENDU");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Page<EnseignantDtoResponse>> searchEnseignants(String search, Pageable pageable) {
        Page<PersonneEntity> enseignants = enseignantRepository.searchByNomOrPrenomAndRole(search, Role.PROFESSEUR, pageable);
        if (enseignants.isEmpty()) {
            return Optional.empty();
        }
        Page<EnseignantDtoResponse> dtoPage = enseignants.map(enseignantMapper::toEnseignantDtoResponse);
        return Optional.of(dtoPage);
    }

    @Override
    public Page<EnseignantDtoResponse> filterTeacherss(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {
        log.info("Filtrage des étudiants avec {} critères", filters != null ? filters.size() : 0);

        try {
            // Créer le service de filtrage pour PersonneEntity
            GenericFilterService<PersonneEntity> filterService =
                    filterServiceFactory.createGenericFilterService(PersonneEntity.class);

            // Ajouter automatiquement le filtre pour le rôle ELEVE
            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            FilterRequest roleFilter = new FilterRequest();
            roleFilter.setChamp("role");
            roleFilter.setKeyword("PROFESSEUR");
            roleFilter.setMode("equals");
            allFilters.add(roleFilter);

            Page<PersonneEntity> filteredEntities;
            if (sorter != null && sorter.getChamp() != null) {
                filteredEntities = filterService.searchAndSortByFilters(enseignantRepository, allFilters, sorter, pageable);
            } else {
                filteredEntities = filterService.searchByFilters(enseignantRepository, allFilters, pageable);
            }

            return filteredEntities.map(enseignantMapper::toEnseignantDtoResponse);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des étudiants", e);
            throw new RuntimeException("Erreur lors du filtrage des étudiants", e);
        }
    }

    private String generateMatricule() {
        String matricule;
        String prefix = "ENS";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        do {
            StringBuilder sb = new StringBuilder(prefix);
            for (int i = 0; i < 5; i++) {
                int index = (int) (Math.random() * chars.length());
                sb.append(chars.charAt(index));
            }
            matricule = sb.toString();
        } while (enseignantRepository.existsByMatricule(matricule));

        return matricule;
    }
}