package com.groupeisi.ms1_gestschool.templates.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateInitializationRunner {

    private final List<TemplateInitializer> initializers;

    @PostConstruct
    public void run() {
        log.info("🚀 Début de l'initialisation des templates...");
        log.info("📋 {} initializers trouvés", initializers.size());

        int successCount = 0;
        int errorCount = 0;

        for (TemplateInitializer initializer : initializers) {
            try {
                log.info("➡ Initialisation du template: {}", initializer.getCode());
                initializer.initialize();
                successCount++;
                log.info("✅ Template {} initialisé avec succès", initializer.getCode());
            } catch (Exception e) {
                errorCount++;
                log.error("❌ Erreur lors de l'initialisation du template {}",
                        initializer.getCode(), e);
            }
        }

        log.info("🎯 Initialisation terminée - Succès: {}, Échecs: {}",
                successCount, errorCount);
    }
}