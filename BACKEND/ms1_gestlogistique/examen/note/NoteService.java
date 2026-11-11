package com.groupeisi.ms1_gestschool.examen.note;

import java.util.List;

public interface NoteService {

    NoteDtoResponse createNote(NoteDtoRequest request);

    NoteDtoResponse getNoteById(String id);

    List<NoteDtoResponse> getAllNotes();

    NoteDtoResponse updateNote(String id, NoteDtoRequest request);

    void deleteNote(String id);

    // =========================
    // Filtres métier
    // =========================

    List<NoteDtoResponse> getNotesByBulletin(String bulletinId);

    List<NoteDtoResponse> getNotesByMatiere(String sessionmatiereId); // Changé de String à Long

    List<NoteDtoResponse> getNotesByEleve(String eleveId);

    List<NoteDtoResponse> getNotesByTypeEvaluation(String type);

    // =========================
    // Calculs
    // =========================

    Double calculateMoyenneByEleveAndMatiere(String eleveId, String sessionmatiereId); // Changé de Long matiereId
}