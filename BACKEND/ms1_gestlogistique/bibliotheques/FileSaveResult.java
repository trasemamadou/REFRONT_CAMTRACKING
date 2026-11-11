package com.groupeisi.ms1_gestschool.bibliotheques;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileSaveResult {
    private String filePath;
    private String extension;
    private String contentType;
}