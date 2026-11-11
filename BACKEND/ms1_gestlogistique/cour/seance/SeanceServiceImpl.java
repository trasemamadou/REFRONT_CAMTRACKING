package com.groupeisi.ms1_gestschool.cour.seance;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereRepository;
import com.groupeisi.ms1_gestschool.config.exception.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class SeanceServiceImpl implements SeanceService {
    private final SeanceRepository sessionRepository;
    private final MatiereRepository matiereRepository;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(SeanceServiceImpl.class);

    private final SeanceMapper sessionMapper;
    @Override
    public SeanceDtoResponse createSession(SeanceDtoRequest request) {
        logger.info("Tentative de création de session pour le cours avec l'id : {}", request.getCourseId());
        if (!matiereRepository.findById(request.getCourseId()).isPresent()) {
            logger.error("Le cours avec l'id : {} n'a pas été trouvé", request.getCourseId());
            throw new EntityExistsException(messageSource.getMessage("course.notfound", new Object[]{request.getCourseId()}, Locale.getDefault()));
        }
        MatiereEntity course = matiereRepository.getReferenceById(request.getCourseId());
        logger.info("Cours trouvé avec l'id : {}, création de la session en cours", request.getCourseId());
        SeanceEntity session = sessionMapper.toSessionEntity(request);
        session.setMatiere(course);
        session.setBeginDate(LocalDateTime.now());
        session = sessionRepository.save(session);
        logger.info("Session créée avec succès pour le cours avec l'id : {}", request.getCourseId());
        return sessionMapper.toSessionDtoResponse(session);
    }


    @Override
    public List<SeanceDtoResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toSessionDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceDtoResponse> getSessionsByCourseId(String name) {
        logger.info("Tentative de récupération des sessions pour le cours avec l'id : {}", name);

        List<SeanceEntity> sessions = sessionRepository.getSessionsByMatiereName(name);

        if (sessions.isEmpty()) {
            logger.warn("Aucune session trouvée pour le cours avec l'id : {}", name);
        } else {
            logger.info("Nombre de sessions récupérées pour le cours avec l'id {} : {}", name, sessions.size());
        }

        return sessions.stream()
                .map(sessionMapper::toSessionDtoResponse)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<SeanceDtoResponse> getSessionById(Long id) {
        logger.info("Tentative de récupération de la session avec l'id : {}", id);

        return sessionRepository.findById(id)
                .map(session -> {
                    logger.info("Session trouvée avec l'id : {}", id);
                    return sessionMapper.toSessionDtoResponse(session);
                })
                .or(() -> {
                    logger.error("Session non trouvée avec l'id : {}", id);
                    throw new com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException(
                            messageSource.getMessage("session.notfound", new Object[]{id}, Locale.getDefault())
                    );
                });
    }

    @Override
    public Optional<SeanceDtoResponse> updateSession(Long id, SeanceDtoRequest request) {
        logger.info("Tentative de mise à jour de la session avec l'id : {}", id);

        return Optional.ofNullable(sessionRepository.findById(id)
                .map(session -> {
                    logger.info("Session trouvée avec l'id : {}", id);
                    session.setName(request.getName());
                    session.setDescription(request.getDescription());
                    session.setArchive(false);

                    SeanceEntity updatedSession = sessionRepository.save(session);
                    logger.info("Session mise à jour avec succès : {}", updatedSession.getId());
                    return sessionMapper.toSessionDtoResponse(updatedSession);
                })
                .orElseThrow(() -> {
                    logger.error("Session non trouvée avec l'id : {}", id);
                    return new com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException(
                            messageSource.getMessage("session.notfound", new Object[]{id}, Locale.getDefault())
                    );
                }));
    }
    @Override
    public boolean deleteSession(Long id) {
        logger.info("Tentative de suppression de la session avec l'id : {}", id);
        if (!sessionRepository.existsById(id)) {
            logger.error("Session non trouvée pour l'id : {}", id);
            throw new EntityNotFoundException(
                    messageSource.getMessage("session.notfound", new Object[]{id}, Locale.getDefault())
            );
        }
        sessionRepository.deleteById(id);
        logger.info("Session avec l'id : {} supprimée avec succès", id);
        return true;
    }

    @Override
    public SeanceDtoResponse endSession(Long id) {
        logger.info("Tentative de fin de la session avec l'id : {}", id);
        return sessionRepository.findById(id).map(session -> {
            logger.info("Session trouvée, mise à jour de la date de fin pour l'id : {}", id);
            session.setEndDate(LocalDateTime.now());
            sessionRepository.save(session);
            logger.info("Session terminée avec succès, id : {}", id);
            return sessionMapper.toSessionDtoResponse(session);
        }).orElseThrow(() -> {
            logger.error("Session non trouvée pour l'id : {}", id);
            return new EntityNotFoundException("La session n'existe pas");
        });
    }

}
