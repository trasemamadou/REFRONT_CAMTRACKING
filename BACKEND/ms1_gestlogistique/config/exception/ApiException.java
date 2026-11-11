package com.groupeisi.ms1_gestschool.config.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiException {
    private String message;
    private LocalDateTime timestamp;
}
