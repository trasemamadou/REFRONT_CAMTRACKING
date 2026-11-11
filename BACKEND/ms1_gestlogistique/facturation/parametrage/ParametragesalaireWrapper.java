package com.groupeisi.ms1_gestschool.facturation.parametrage;

import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseDtoRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParametragesalaireWrapper {
    private ParametrageclasseDtoRequest parametrageclasse;
    private ParametrageDtoRequest  parametrage;
}
