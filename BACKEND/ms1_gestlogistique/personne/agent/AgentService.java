package com.groupeisi.ms1_gestschool.personne.agent;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AgentService {

     // Récupérer tous les tuteurs
     Optional<List<AgentDtoResponse>> getAllTuteurs();

     // Récupérer tous les tuteurs avec pagination
     Optional<Page<AgentDtoResponse>> getAllTuteursPaged(Pageable pageable);

     // Rechercher des tuteurs par nom ou prénom
     Optional<Page<AgentDtoResponse>> searchTuteurs(String search, Pageable pageable);

     // Créer un nouveau tuteur
     Optional<AgentDtoResponse> saveTuteur(AgentDtoRequest tuteur);

     // Récupérer un tuteur par son ID
     Optional<AgentDtoResponse> getTuteurById(String id);

     // Récupérer un tuteur par son matricule
     Optional<AgentDtoResponse> getTuteurByMatricule(String matricule);

     // Mettre à jour un tuteur
     Optional<AgentDtoResponse> updateTuteur(String id, AgentDtoRequest tuteur);

     // Supprimer un tuteur
     void deleteTuteurById(String id);


     // Changer le statut d'un tuteur
     Optional<AgentDtoResponse> changeStatus(String id, StatutPerson statut);

     // Rechercher par spécialité

}