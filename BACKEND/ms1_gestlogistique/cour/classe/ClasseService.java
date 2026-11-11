package com.groupeisi.ms1_gestschool.cour.classe;

import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClasseService {
 Optional<List<ClasseDtoResponse>> getAllClasses();
 Optional<Page<ClasseDtoResponse>> getAllClassesPaged(Pageable pageable);
 Page<ClasseDtoResponse> filterClasses(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable);
 Optional<ClasseDtoResponse> getClasseById(String id);
  Optional<ClasseDtoResponse> addClasse(ClasseDtoRequest classe);
   Optional<ClasseDtoResponse> updateClasse(String id, ClasseDtoRequest classeDtoRequest);
   Optional<?> deleteClasse(String id);
}
