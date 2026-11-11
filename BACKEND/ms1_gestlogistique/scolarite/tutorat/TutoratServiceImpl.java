package com.groupeisi.ms1_gestschool.scolarite.tutorat;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TutoratServiceImpl implements TutoratService {

    private final TutoratRepository tutoratRepository;
    private final PersonneRepository personneRepository;
    private final TutoratMapper tutoratMapper;

    @Override
    public TutoratDtoResponse createTutorat(TutoratDtoRequest request) {
        log.info("Création d'un nouveau tutorat");

        // Validation des personnes
        PersonneEntity eleve = personneRepository.findById(request.getEleveId())
                .orElseThrow(() -> new RuntimeException("Élève non trouvé avec l'ID: " + request.getEleveId()));

        PersonneEntity tuteur = personneRepository.findById(request.getTuteurId())
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé avec l'ID: " + request.getTuteurId()));

        // Vérifier que l'élève et le tuteur sont différents
        if (eleve.getId().equals(tuteur.getId())) {
            throw new RuntimeException("L'élève et le tuteur ne peuvent pas être la même personne");
        }

        // Créer l'entité
        TutoratEntity entity = tutoratMapper.toEntity(request, eleve, tuteur);
        TutoratEntity savedEntity = tutoratRepository.save(entity);

        log.info("Tutorat créé avec succès avec l'ID: {}", savedEntity.getId());
        return tutoratMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TutoratDtoResponse getTutoratById(Long id) {
        log.info("Récupération du tutorat avec l'ID: {}", id);

        TutoratEntity entity = tutoratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutorat non trouvé avec l'ID: " + id));

        return tutoratMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoratDtoResponse> getAllTutorats() {
        log.info("Récupération de tous les tutorats");

        List<TutoratEntity> entities = tutoratRepository.findAll();

        return entities.stream()
                .map(tutoratMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TutoratDtoResponse updateTutorat(Long id, TutoratDtoRequest request) {
        log.info("Mise à jour du tutorat avec l'ID: {}", id);

        TutoratEntity entity = tutoratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutorat non trouvé avec l'ID: " + id));

        PersonneEntity eleve = personneRepository.findById(request.getEleveId())
                .orElseThrow(() -> new RuntimeException("Élève non trouvé avec l'ID: " + request.getEleveId()));

        PersonneEntity tuteur = personneRepository.findById(request.getTuteurId())
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé avec l'ID: " + request.getTuteurId()));

        // Mettre à jour l'entité
        tutoratMapper.updateEntity(entity, request, eleve, tuteur);
        TutoratEntity updatedEntity = tutoratRepository.save(entity);

        log.info("Tutorat mis à jour avec succès avec l'ID: {}", id);
        return tutoratMapper.toResponse(updatedEntity);
    }

    @Override
    public void deleteTutorat(Long id) {
        log.info("Suppression du tutorat avec l'ID: {}", id);

        if (!tutoratRepository.existsById(id)) {
            throw new RuntimeException("Tutorat non trouvé avec l'ID: " + id);
        }

        tutoratRepository.deleteById(id);
        log.info("Tutorat supprimé avec succès avec l'ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoratDtoResponse> getTutoratsByEleve(Long eleveId) {
        log.info("Récupération des tutorats pour l'élève avec l'ID: {}", eleveId);

        List<TutoratEntity> entities = tutoratRepository.findAll().stream()
                .filter(t -> t.getEleve().getId().equals(eleveId))
                .collect(Collectors.toList());

        return entities.stream()
                .map(tutoratMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutoratDtoResponse> getTutoratsByTuteur(Long tuteurId) {
        log.info("Récupération des tutorats pour le tuteur avec l'ID: {}", tuteurId);

        List<TutoratEntity> entities = tutoratRepository.findAll().stream()
                .filter(t -> t.getTuteur().getId().equals(tuteurId))
                .collect(Collectors.toList());

        return entities.stream()
                .map(tutoratMapper::toResponse)
                .collect(Collectors.toList());
    }
}