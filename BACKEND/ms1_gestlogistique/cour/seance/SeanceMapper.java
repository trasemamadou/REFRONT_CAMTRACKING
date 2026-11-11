package com.groupeisi.ms1_gestschool.cour.seance;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SeanceMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "archive", target = "archive")
    //@Mapping(source = "courseId", target = "course.id" )
    SeanceEntity toSessionEntity(SeanceDtoRequest session);
    SeanceDtoResponse toSessionDtoResponse(SeanceEntity session);
    List<SeanceDtoResponse> toSessionDtoResponseList(List<SeanceEntity> listeSessions);
}