package com.groupeisi.ms1_gestschool.cour.seance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeanceDtoRequest{
        private  Long id;
        private String name;
        private String description;
        private boolean archive;
        private String courseId;
}

