package com.groupeisi.ms1_gestschool.config.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperGlobal {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}