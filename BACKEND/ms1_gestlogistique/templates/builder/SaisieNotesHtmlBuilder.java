package com.groupeisi.ms1_gestschool.templates.builder;

import com.groupeisi.ms1_gestschool.templates.DocumentType;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SaisieNotesHtmlBuilder implements HtmlDocumentBuilder {

    private final TemplateEngine templateEngine;

    public SaisieNotesHtmlBuilder() {

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/pdf/"); // dossier des templates
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
    }

    @Override
    public String build(Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process("saisie_notes", context);
    }

    @Override
    public DocumentType getType() {
        return DocumentType.SAISIE_NOTES;
    }
}