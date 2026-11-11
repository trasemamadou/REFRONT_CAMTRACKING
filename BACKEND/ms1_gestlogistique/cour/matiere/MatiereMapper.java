package com.groupeisi.ms1_gestschool.cour.matiere;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MatiereMapper {
     @Mapping(source = "id", target = "id")
     @Mapping(source = "name", target = "name")
     @Mapping(source = "description", target = "description")
     @Mapping(source = "archive", target = "archive")
     @Mapping(source = "ecole", target = "ecole.nom")
     MatiereEntity toCourseEntity(MatiereDtoRequest course);
     MatiereDtoResponse toCourseDtoResponse(MatiereEntity course);
     List<MatiereDtoResponse> toCourseDtoResponseList(List<MatiereEntity> listeCourses);
}
