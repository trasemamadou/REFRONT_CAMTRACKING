package com.groupeisi.ms1_gestschool.examen.note;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereRepository;
import com.groupeisi.ms1_gestschool.examen.bulletin.BulletinEntity;
import com.groupeisi.ms1_gestschool.examen.bulletin.BulletinRepository;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final BulletinRepository bulletinRepository;
    private final MatiereRepository matiereRepository;
    private final NoteMapper noteMapper;
    private final EpreuveRepository epreuveRepository;

    @Override
    public NoteDtoResponse createNote(NoteDtoRequest request) {
        BulletinEntity bulletin = bulletinRepository.findById(request.getBulletinId())
                .orElseThrow(() ->
                        new RuntimeException("Bulletin non trouvé avec l'ID : " + request.getBulletinId()));

        NoteEntity entity = noteMapper.toEntity(request);
        entity.setBulletin(bulletin);
        entity.setSessionmatiereId(request.getSessionmatiereId()); // Assure que sessionmatiereId est bien défini

        NoteEntity saved = noteRepository.save(entity);
        return enrichResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public NoteDtoResponse getNoteById(String id) {
        NoteEntity entity = noteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Note non trouvée avec l'ID : " + id));
        return enrichResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDtoResponse> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NoteDtoResponse updateNote(String id, NoteDtoRequest request) {
        NoteEntity entity = noteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Note non trouvée avec l'ID : " + id));

        if (request.getBulletinId() != null &&
                !request.getBulletinId().equals(entity.getBulletin().getId())) {
            BulletinEntity bulletin = bulletinRepository.findById(request.getBulletinId())
                    .orElseThrow(() ->
                            new RuntimeException("Bulletin non trouvé avec l'ID : " + request.getBulletinId()));
            entity.setBulletin(bulletin);
        }

        if (request.getSessionmatiereId() != null) {
            entity.setSessionmatiereId(request.getSessionmatiereId());
        }

        noteMapper.updateEntityFromDto(request, entity);
        NoteEntity updated = noteRepository.save(entity);
        return enrichResponse(updated);
    }

    @Override
    public void deleteNote(String id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Note non trouvée avec l'ID : " + id);
        }
        noteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDtoResponse> getNotesByBulletin(String bulletinId) {
        return noteRepository.findByBulletinId(bulletinId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDtoResponse> getNotesByMatiere(String sessionmatiereId) { // Changé
        return noteRepository.findBySessionmatiereId(sessionmatiereId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDtoResponse> getNotesByEleve(String eleveId) {
        return noteRepository.findByEleveId(eleveId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateMoyenneByEleveAndMatiere(String eleveId, String sessionmatiereId) { // Changé
        Double moyenne = noteRepository.findMoyenneByEleveAndMatiere(eleveId, sessionmatiereId);
        return moyenne != null ? Math.round(moyenne * 100.0) / 100.0 : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoteDtoResponse> getNotesByTypeEvaluation(String type) {
        return noteRepository.findByTypeEvaluation(type)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    // ===========================
    // Méthode utilitaire enrichie
    // ===========================
    private NoteDtoResponse enrichResponse(NoteEntity entity) {
        NoteDtoResponse response = noteMapper.toDto(entity);

        // Récupérer le nom de la matière via sessionmatiereId si nécessaire
        // Note: Vous devrez peut-être créer un repository pour SessionMatiereEntity
        // Pour l'instant, je garde le même mécanisme
//        response.setMatiereNom(
//                sessionmatiereRepository.findById(entity.getSessionmatiereId()).get().getMatiereName()
//                        .map(MatiereEntity::getName)
//                        .orElse("Matière inconnue (ID: " + entity.getSessionmatiereId() + ")")
//        );

        return response;
    }
}