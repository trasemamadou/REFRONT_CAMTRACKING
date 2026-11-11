package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {

    private final PersonneRepository tuteurRepository;
    private final AgentMapper tuteurMapper;
    private final MessageSource messageSource;



    @Override
    public Optional<List<AgentDtoResponse>> getAllTuteurs() {
        List<PersonneEntity> tuteurs = tuteurRepository.findAll();
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        List<AgentDtoResponse> dtos = tuteurMapper.toDtoList(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<Page<AgentDtoResponse>> getAllTuteursPaged(Pageable pageable) {
        Page<PersonneEntity> tuteurs = tuteurRepository.findAll(pageable);
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        Page<AgentDtoResponse> dtos = tuteurMapper.toDtoPage(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<Page<AgentDtoResponse>> searchTuteurs(String search, Pageable pageable) {
        Page<PersonneEntity> tuteurs = tuteurRepository.searchByNomOrPrenom(search, pageable);
        if (tuteurs.isEmpty()) {
            return Optional.empty();
        }
        Page<AgentDtoResponse> dtos = tuteurMapper.toDtoPage(tuteurs);
        return Optional.of(dtos);
    }

    @Override
    public Optional<AgentDtoResponse> saveTuteur(AgentDtoRequest request) {
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
    public Optional<AgentDtoResponse> getTuteurById(String id) {
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
    public Optional<AgentDtoResponse> getTuteurByMatricule(String matricule) {
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
    public Optional<AgentDtoResponse> updateTuteur(String id, AgentDtoRequest request) {
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
    public Optional<AgentDtoResponse> changeStatus(String id, StatutPerson statut) {
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