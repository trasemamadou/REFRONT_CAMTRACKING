package com.groupeisi.ms1_gestschool.examen.session;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "composition")
@Getter
@Setter
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String libelle;

    private LocalDate dateDebut;
    private LocalDate datFin;
}
