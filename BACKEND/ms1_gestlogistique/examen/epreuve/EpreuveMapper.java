package com.groupeisi.ms1_gestschool.examen.epreuve;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereDtoResponse;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereMapper;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationDtoResponse;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {MatiereMapper.class, ParticipationMapper.class})
public interface EpreuveMapper {

    @Mapping(source = "matiere", target = "matiere")
    @Mapping(source = "participation", target = "participation")
    EpreuveDtoResponse toDtoResponse(EpreuveEntity entity);

}