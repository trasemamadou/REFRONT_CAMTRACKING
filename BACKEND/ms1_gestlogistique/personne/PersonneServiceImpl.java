package com.groupeisi.ms1_gestschool.personne;

import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoRequest;
import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantDtoResponse;
import com.groupeisi.ms1_gestschool.personne.enseignant.EnseignantMapper;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoRequest;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import com.groupeisi.ms1_gestschool.personne.student.EleveMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonneServiceImpl implements PersonneService {

    private final PersonneRepository eleveRepository;

    private final EleveMapper eleveMapper;
    private final EnseignantMapper enseignantMapper;

    /* =======================
       LISTING
       ======================= */

    @Override
    public List<?> getAllPersonnesByRole(String role) {
        if ("ELEVE".equalsIgnoreCase(role)) {
            return eleveRepository.findAll()
                    .stream()
                    .map(eleveMapper::toStudentDtoResponse)
                    .toList();
        }
        if ("ENSEIGNANT".equalsIgnoreCase(role)) {
            return eleveRepository.findAll()
                    .stream()
                    .map(enseignantMapper::toEnseignantDtoResponse)
                    .toList();
        }
        throw new IllegalArgumentException("Rôle non reconnu : " + role);
    }

    @Override
    public Page<?> getAllPersonnesByRole(Pageable pageable, String role) {
        if ("ELEVE".equalsIgnoreCase(role)) {
            return eleveRepository.findAll(pageable)
                    .map(eleveMapper::toStudentDtoResponse);
        }
        if ("ENSEIGNANT".equalsIgnoreCase(role)) {
            return eleveRepository.findAll(pageable)
                    .map(enseignantMapper::toEnseignantDtoResponse);
        }
        throw new IllegalArgumentException("Rôle non reconnu : " + role);
    }

    @Override
    public Page<?> getAllPersonnes(Pageable pageable) {
        throw new UnsupportedOperationException("Utiliser getAllPersonnesByRole");
    }

    /* =======================
       ELEVE
       ======================= */

    @Override
    @Transactional
    public EleveDtoResponse saveStudent(EleveDtoRequest dto, String role) {

        if (!"ELEVE".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Rôle invalide pour un élève");
        }

        // 1️⃣ Mapping DTO -> Entity
        PersonneEntity entity = eleveMapper.toStudentEntity(dto);

        // 2️⃣ Affectation du rôle (ENUM)
        entity.setRole(Role.ELEVE);

        // 🔍 Debug ultra utile
        System.out.println("ID AVANT SAVE = {}" + entity.getId()); // doit être null

        // 3️⃣ Sauvegarde
        PersonneEntity saved = eleveRepository.save(entity);

       // log.info("ID APRES SAVE = {}", saved.getId()); // doit être NON null

        // 4️⃣ Retour DTO
        return eleveMapper.toStudentDtoResponse(saved);
    }


    @Override
    public Optional<EleveDtoResponse> getStudentById(String id) {
        return eleveRepository.findById(id)
                .map(eleveMapper::toStudentDtoResponse);
    }

    @Override
    public EleveDtoResponse updateStudent(String id, EleveDtoRequest eleve) {
        return eleveRepository.findById(id)
                .map(existing -> {
                    eleveMapper.toStudentEntity(eleve);
                    return eleveMapper.toStudentDtoResponse(
                            eleveRepository.save(existing)
                    );
                })
                .orElseThrow(() -> new RuntimeException("Élève non trouvé"));
    }

    @Override
    public void deleteStudentById(String id) {
        eleveRepository.deleteById(id);
    }

    /* =======================
       ENSEIGNANT
       ======================= */

    @Override
    public EnseignantDtoResponse saveTeacher(EnseignantDtoRequest enseignant, String role) {
        if (!"ENSEIGNANT".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Rôle invalide pour un enseignant");
        }

        return enseignantMapper.toEnseignantDtoResponse(
                eleveRepository.save(
                        enseignantMapper.toEnseignantEntity(enseignant)
                )
        );
    }

    @Override
    public Optional<EnseignantDtoResponse> getTeacherById(String id) {
        return eleveRepository.findById(id)
                .map(enseignantMapper::toEnseignantDtoResponse);
    }

    @Override
    public EnseignantDtoResponse updateTeacher(String id, EnseignantDtoRequest enseignant) {
        return eleveRepository.findById(id)
                .map(existing -> {
                    enseignantMapper.toEnseignantEntity(enseignant);
                    return enseignantMapper.toEnseignantDtoResponse(
                            eleveRepository.save(existing)
                    );
                })
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));
    }

    @Override
    public void deleteTeacherById(String id) {
        eleveRepository.deleteById(id);
    }
}
