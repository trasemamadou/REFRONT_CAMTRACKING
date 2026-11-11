package com.groupeisi.ms1_gestschool.templates.builder;


import com.groupeisi.ms1_gestschool.templates.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FactureHtmlBuilder implements HtmlDocumentBuilder {

    private final SpringTemplateEngine templateEngine;

    @Override
    public String build(Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process("pdf/facture_template", context);
    }

    @Override
    public DocumentType getType() {
        return DocumentType.FACTURE;
    }
}