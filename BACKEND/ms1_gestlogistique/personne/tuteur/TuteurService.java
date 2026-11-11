package com.groupeisi.ms1_gestschool.personne.tuteur;

import com.groupeisi.ms1_gestschool.personne.StatutPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface TuteurService {

     // Récupérer tous les tuteurs
     Optional<List<TuteurDtoResponse>> getAllTuteurs();

     // Récupérer tous les tuteurs avec pagination
     Optional<Page<TuteurDtoResponse>> getAllTuteursPaged(Pageable pageable);

     // Rechercher des tuteurs par nom ou prénom
     Optional<Page<TuteurDtoResponse>> searchTuteurs(String search, Pageable pageable);

     // Créer un nouveau tuteur
     Optional<TuteurDtoResponse> saveTuteur(TuteurDtoRequest tuteur);

     // Récupérer un tuteur par son ID
     Optional<TuteurDtoResponse> getTuteurById(String id);

     // Récupérer un tuteur par son matricule
     Optional<TuteurDtoResponse> getTuteurByMatricule(String matricule);

     // Mettre à jour un tuteur
     Optional<TuteurDtoResponse> updateTuteur(String id, TuteurDtoRequest tuteur);

     // Supprimer un tuteur
     void deleteTuteurById(String id);


     // Changer le statut d'un tuteur
     Optional<TuteurDtoResponse> changeStatus(String id, StatutPerson statut);

     // Rechercher par spécialité

}