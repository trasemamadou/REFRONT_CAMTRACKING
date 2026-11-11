package com.groupeisi.ms1_gestschool.personne.student;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoRequest;
import com.groupeisi.ms1_gestschool.scolarite.tutorat.TutoratDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EleveService {
    Optional<List<EleveDtoResponse>> getAllStudents();
    Optional<Page<EleveDtoResponse>> getAllElevePaged(Pageable pageable);
    Optional<EleveDtoResponse> getStudentById(String id);
    Optional<EleveDtoResponse> saveStudent(EleveDtoRequest eleveDto);
    Optional<EleveDtoResponse> updateStudent(String id, EleveDtoRequest student);
    Optional<EleveDtoResponse> deleteStudentById(String id);
    Optional<TutoratDtoResponse> saveTutorat(TutoratDtoRequest request);
    RegistrationWraperResponse registerStudent(RegistrationWraperRequest request);
    Page<EleveDtoResponse> filterStudents(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
}