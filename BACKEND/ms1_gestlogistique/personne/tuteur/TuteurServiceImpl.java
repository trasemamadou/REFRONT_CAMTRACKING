package com.groupeisi.ms1_gestschool.personne.tuteur;

import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TuteurServiceImpl implements TuteurService {

    private final PersonneRepository tuteurRepository;
    private final TuteurMapper tuteurMapper;
    private final MessageSource messageSource;



    @Override
    public Optional<List<TuteurDtoResponse>> getAllTuteurs() {
        List<PersonneEntity> tuteurs = tuteurRepository.findAll();
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        List<TuteurDtoResponse> dtos = tuteurMapper.toDtoList(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<Page<TuteurDtoResponse>> getAllTuteursPaged(Pageable pageable) {
        Page<PersonneEntity> tuteurs = tuteurRepository.findAll(pageable);
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        Page<TuteurDtoResponse> dtos = tuteurMapper.toDtoPage(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<Page<TuteurDtoResponse>> searchTuteurs(String search, Pageable pageable) {
        Page<PersonneEntity> tuteurs = tuteurRepository.searchByNomOrPrenom(search, pageable);
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        Page<TuteurDtoResponse> dtos = tuteurMapper.toDtoPage(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<TuteurDtoResponse> saveTuteur(TuteurDtoRequest request) {
        try {
            // Vérifier l'unicité des données
            if (tuteurRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Un tuteur avec cet email existe déjà");
            }

            if (tuteurRepository.existsByTelephone(request.getTelephone())) {
                throw new IllegalArgumentException("Un tuteur avec ce téléphone existe déjà");
            }

            // Générer le matricule
            String matricule = generateMatricule();
            request.setMatricule(matricule);
            request.setRole("TUTEUR");
            // Convertir et sauvegarder
            PersonneEntity tuteur = tuteurMapper.toEntity(request);
            tuteur.setDateCreation(LocalDateTime.now());

            PersonneEntity saved = tuteurRepository.save(tuteur);
            log.info("Tuteur créé avec succès - Matricule: {}", matricule);

            return Optional.of(tuteurMapper.toDto(saved));
        } catch (Exception e) {
            log.error("Erreur lors de la création du tuteur", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<TuteurDtoResponse> getTuteurById(String id) {
        PersonneEntity tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("tuteur.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        return Optional.of(tuteurMapper.toDto(tuteur));
    }

    @Override
    public Optional<TuteurDtoResponse> getTuteurByMatricule(String matricule) {
        PersonneEntity tuteur = tuteurRepository.findByMatricule(matricule)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("tuteur.notfound.matricule",
                            new Object[]{matricule}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        return Optional.of(tuteurMapper.toDto(tuteur));
    }

    @Override
    public Optional<TuteurDtoResponse> updateTuteur(String id, TuteurDtoRequest request) {
        PersonneEntity existing = tuteurRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("tuteur.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });

        // Vérifier l'unicité de l'email (sauf pour l'utilisateur actuel)
        if (!existing.getEmail().equals(request.getEmail()) &&
                tuteurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un tuteur avec cet email existe déjà");
        }

        // Vérifier l'unicité du téléphone (sauf pour l'utilisateur actuel)
        if (!existing.getTelephone().equals(request.getTelephone()) &&
                tuteurRepository.existsByTelephone(request.getTelephone())) {
            throw new IllegalArgumentException("Un tuteur avec ce téléphone existe déjà");
        }

        // Mettre à jour l'entité
        tuteurMapper.updateEntityFromDto(request, existing);

        PersonneEntity updated = tuteurRepository.save(existing);
        log.info("Tuteur mis à jour avec succès - ID: {}", id);

        return Optional.of(tuteurMapper.toDto(updated));
    }

    @Override
    public void deleteTuteurById(String id) {
        PersonneEntity tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("tuteur.notfound.id",
                            new Object[]{id}, Locale.getDefault());
                    log.error(message);
                    return new EntityNotFoundException(message);
                });

        tuteurRepository.delete(tuteur);
        log.info("Tuteur supprimé avec succès - ID: {}", id);
    }


    @Override
    public Optional<TuteurDtoResponse> changeStatus(String id, StatutPerson statut) {
        PersonneEntity tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tuteur non trouvé"));

        tuteur.setStatut(statut);

        PersonneEntity updated = tuteurRepository.save(tuteur);
        log.info("Statut du tuteur ID: {} changé à: {}", id, statut);

        return Optional.of(tuteurMapper.toDto(updated));
    }


    private String generateMatricule() {
        String matricule;
        String prefix = "TUT";
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        do {
            StringBuilder sb = new StringBuilder(prefix);

            for (int i = 0; i < 6; i++) {
                int index = (int) (Math.random() * chars.length());
                sb.append(chars.charAt(index));
            }

            matricule = sb.toString();
        } while (tuteurRepository.existsByMatricule(matricule));

        return matricule;
    }
}