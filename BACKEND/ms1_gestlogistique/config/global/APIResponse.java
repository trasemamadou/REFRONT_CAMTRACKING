package com.groupeisi.ms1_gestschool.config.global;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
 @NoArgsConstructor
@Builder
public class APIResponse<T> {

    private String status;
    private Integer httpStatus;
    private String message;
    private T data;
//

    public APIResponse(T success, T value, T inscriptionRéussie, T keycloakId) {
    }
}
