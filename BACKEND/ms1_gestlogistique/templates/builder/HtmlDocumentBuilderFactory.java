// HtmlDocumentBuilderFactory.java
package com.groupeisi.ms1_gestschool.templates.builder;

import com.groupeisi.ms1_gestschool.templates.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class HtmlDocumentBuilderFactory {

    private final Map<DocumentType, HtmlDocumentBuilder> builders = new EnumMap<>(DocumentType.class);

    @Autowired
    public HtmlDocumentBuilderFactory(List<HtmlDocumentBuilder> builderList) {
        builderList.forEach(builder -> builders.put(builder.getType(), builder));
        // Vérifier que tous les types ont un builder
        for (DocumentType type : DocumentType.values()) {
            if (!builders.containsKey(type)) {
                // Optionnel: logger un warning
                System.out.println("Warning: No builder found for document type: " + type);
            }
        }
    }

    public HtmlDocumentBuilder getBuilder(DocumentType type) {
        HtmlDocumentBuilder builder = builders.get(type);
        if (builder == null) {
            throw new IllegalArgumentException("No HTML builder found for document type: " + type);
        }
        return builder;
    }
}