package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.Role;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {LocalDateTime.class})
public interface AgentMapper {

    // Conversion de DTO Request vers Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricule", source = "matricule")
    @Mapping(target = "prenom", source = "prenom")
    @Mapping(target = "nom", source = "nom")
    @Mapping(target = "sexe", source = "sexe")
    @Mapping(target = "adresse", source = "adresse")
    @Mapping(target = "telephone", source = "telephone")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "photoProfil", source = "photoProfil")
    @Mapping(target = "statut", source = "statut")
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(source = "role", target = "role")
    PersonneEntity toEntity(AgentDtoRequest dto);

    // Conversion de Entity vers DTO Response
    AgentDtoResponse toDto(PersonneEntity entity);

    // Conversion de liste d'Entity vers liste de DTO Response
    List<AgentDtoResponse> toDtoList(List<PersonneEntity> entities);

    // Conversion de Page d'Entity vers Page de DTO Response
    default Page<AgentDtoResponse> toDtoPage(Page<PersonneEntity> page) {
        List<AgentDtoResponse> dtos = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    // Mise à jour d'une Entity à partir d'un DTO Request
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricule", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    void updateEntityFromDto(AgentDtoRequest dto, @MappingTarget PersonneEntity entity);
    default String map(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.get(0).name(); // on prend le premier rôle
    }


}