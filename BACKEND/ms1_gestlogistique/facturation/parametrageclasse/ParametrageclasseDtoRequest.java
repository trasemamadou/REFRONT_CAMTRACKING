package com.groupeisi.ms1_gestschool.facturation.parametrageclasse;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametrageclasseDtoRequest {

    // Peut être null ou rempli selon le cas
    private String classeId;
    private String parametrageId;

    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0.0")
    private Double montant = 0.0;

    private boolean actif = true;
}