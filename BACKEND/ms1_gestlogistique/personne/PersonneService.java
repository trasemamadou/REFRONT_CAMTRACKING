package com.groupeisi.ms1_gestschool.personne;

import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoRequest;
import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoRequest;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonneService {

    /* =======================
       LISTING / PAGINATION
       ======================= */

    List<?> getAllPersonnesByRole(String role);

    Page<?> getAllPersonnesByRole(Pageable pageable, String role);

    Page<?> getAllPersonnes(Pageable pageable);

    /* =======================
       ELEVE
       ======================= */

    EleveDtoResponse saveStudent(EleveDtoRequest eleve, String role);

    Optional<EleveDtoResponse> getStudentById(String id);

    EleveDtoResponse updateStudent(String id, EleveDtoRequest eleve);

    void deleteStudentById(String id);

    /* =======================
       ENSEIGNANT
       ======================= */

    EnseignantDtoResponse saveTeacher(EnseignantDtoRequest enseignant, String role);

    Optional<EnseignantDtoResponse> getTeacherById(String id);

    EnseignantDtoResponse updateTeacher(String id, EnseignantDtoRequest enseignant);

    void deleteTeacherById(String id);
}
