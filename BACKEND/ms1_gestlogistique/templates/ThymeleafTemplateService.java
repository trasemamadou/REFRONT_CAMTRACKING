package com.groupeisi.ms1_gestschool.templates;

import com.groupeisi.ms1_gestschool.templates.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThymeleafTemplateService {

    private final TemplateEngine templateEngine;

    // Templates dans le dossier pdf/ - ne pas mettre "templates/" car c'est le root
    private static final String TEMPLATE_CARTE_SCOLAIRE = "pdf/carte-scolaire";
    private static final String TEMPLATE_CARTES_MULTIPLE = "pdf/cartes-scolaires-multiple";

    /**
     * Prépare le contexte pour une carte scolaire individuelle
     */
    public Map<String, Object> prepareCarteScolaireContext(Map<String, Object> data) {
        Map<String, Object> context = new HashMap<>();

        Map<String, Object> eleve = (Map<String, Object>) data.getOrDefault("eleve", new HashMap<>());
        String photoBase64 = (String) data.getOrDefault("photoBase64", "");
        String qrCodeBase64 = (String) data.getOrDefault("qrCodeBase64", "");

        context.put("schoolName", data.getOrDefault("schoolName", "ÉCOLE INTERNATIONALE"));
        context.put("cardType", data.getOrDefault("cardType", "CARTE SCOLAIRE"));
        context.put("eleve", eleve);
        context.put("photoBase64", photoBase64);
        context.put("qrCodeBase64", qrCodeBase64);
        context.put("footerText", data.getOrDefault("footerText",
                "Carte valide pour l'année scolaire " + eleve.getOrDefault("anneeScolaire", "2024-2025")));

        return context;
    }

    /**
     * Prépare le contexte pour plusieurs cartes scolaires
     */
    public Map<String, Object> prepareMultipleCartesContext(List<Map<String, Object>> cartes, Map<String, Object> data) {
        Map<String, Object> context = new HashMap<>();

        context.put("schoolName", data.getOrDefault("schoolName", "ÉCOLE INTERNATIONALE"));
        context.put("cardType", data.getOrDefault("cardType", "CARTE SCOLAIRE"));
        context.put("cartes", cartes);
        context.put("classe", data.getOrDefault("classe", ""));
        context.put("footerText", data.getOrDefault("footerText", "Carte valide pour l'année scolaire"));

        return context;
    }

    /**
     * Génère le HTML pour une carte scolaire individuelle
     */
    public String generateCarteScolaireHtml(Map<String, Object> data) {
        try {
            Map<String, Object> contextData = prepareCarteScolaireContext(data);
            Context context = new Context(Locale.FRENCH);
            context.setVariables(contextData);

            log.debug("Génération du template: {}", TEMPLATE_CARTE_SCOLAIRE);
            String result = templateEngine.process(TEMPLATE_CARTE_SCOLAIRE, context);
            log.debug("Template généré avec succès, longueur: {}", result != null ? result.length() : 0);
            return result;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du template HTML {}: {}", TEMPLATE_CARTE_SCOLAIRE, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du template HTML", e);
        }
    }

    /**
     * Génère le HTML pour plusieurs cartes scolaires
     */
    public String generateMultipleCartesHtml(List<Map<String, Object>> cartes, Map<String, Object> data) {
        try {
            Map<String, Object> contextData = prepareMultipleCartesContext(cartes, data);
            Context context = new Context(Locale.FRENCH);
            context.setVariables(contextData);

            log.debug("Génération du template multiple: {}", TEMPLATE_CARTES_MULTIPLE);
            String result = templateEngine.process(TEMPLATE_CARTES_MULTIPLE, context);
            log.debug("Template multiple généré avec succès, longueur: {}", result != null ? result.length() : 0);
            return result;
        } catch (Exception e) {
            log.error("Erreur lors de la génération du template HTML multiple {}: {}", TEMPLATE_CARTES_MULTIPLE, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du template HTML multiple", e);
        }
    }
}