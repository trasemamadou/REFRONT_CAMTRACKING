package com.groupeisi.ms1_gestschool.templates;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TemplateType type;

    //@Lob
    @Column(name = "template_file", columnDefinition = "BYTEA")
    private byte[] templateFile; // ✅ Type correct pour bytea

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    //@Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentFormat format;

    @Column(name = "file_extension", nullable = false)
    private String fileExtension;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ⏱ Remplit createdAt, updatedAt et format/fileExtension par défaut
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (format == null && type != null) {
            format = type.getDefaultFormat();
        }

        if (fileExtension == null && format != null) {
            fileExtension = switch (format) {
                case PDF -> "pdf";
                case EXCEL -> "xlsx";
                case WORD -> "docx";
                case HTML -> "html";
                case TEXT -> "txt";
                case CSV -> "csv";
            };
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
