package com.groupeisi.ms1_gestschool.examen.epreuve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionmatiereId implements Serializable {
    private String matiereName;
    private Long sessionclasseId;
}