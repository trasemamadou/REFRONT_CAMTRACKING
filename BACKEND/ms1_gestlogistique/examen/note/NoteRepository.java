package com.groupeisi.ms1_gestschool.examen.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, String> {

    List<NoteEntity> findByBulletinId(String bulletinId);

    List<NoteEntity> findBySessionmatiereId(String sessionmatiereId);

    List<NoteEntity> findByBulletinIdAndSessionmatiereId(String bulletinId, String sessionmatiereId);

    List<NoteEntity> findByTypeEvaluation(String type);

    // Correction: eleveId est Long, pas String
    @Query("""
        SELECT n
        FROM NoteEntity n
        JOIN n.bulletin b
        WHERE b.eleveId = :eleveId
    """)
    List<NoteEntity> findByEleveId(@Param("eleveId") String eleveId);

    // Nouvelle méthode pour trouver par élève ET sessionclasse
    @Query("""
        SELECT n
        FROM NoteEntity n
        JOIN n.bulletin b
        WHERE b.eleveId = :eleveId 
          AND b.sessionclasseId = :sessionclasseId
    """)
    List<NoteEntity> findByEleveIdAndSessionclasseId(
            @Param("eleveId") String eleveId,
            @Param("sessionclasseId") String sessionclasseId
    );

    @Query("""
        SELECT AVG(n.valeur * n.coefficient) / AVG(n.coefficient)
        FROM NoteEntity n
        JOIN n.bulletin b
        WHERE b.eleveId = :eleveId
          AND n.sessionmatiereId = :sessionmatiereId
    """)
    Double findMoyenneByEleveAndMatiere(
            @Param("eleveId") String eleveId,
            @Param("sessionmatiereId") String sessionmatiereId
    );

    @Query("""
        SELECT n
        FROM NoteEntity n
        JOIN n.bulletin b
        WHERE n.sessionmatiereId = :sessionmatiereId
          AND b.eleveId = :eleveId
    """)
    List<NoteEntity> findBySessionmatiereIdAndEleveId(
            @Param("sessionmatiereId") String sessionmatiereId,
            @Param("eleveId") String eleveId
    );
}