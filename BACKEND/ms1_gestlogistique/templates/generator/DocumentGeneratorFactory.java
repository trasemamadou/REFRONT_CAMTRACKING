// DocumentGeneratorFactory.java
package com.groupeisi.ms1_gestschool.templates.generator;

import com.groupeisi.ms1_gestschool.templates.DocumentFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class DocumentGeneratorFactory {

    private final Map<DocumentFormat, DocumentGenerator> generators = new EnumMap<>(DocumentFormat.class);

    @Autowired
    public DocumentGeneratorFactory(List<DocumentGenerator> generatorList) {
        generatorList.forEach(generator -> generators.put(generator.getFormat(), generator));
    }

    public DocumentGenerator getGenerator(DocumentFormat format) {
        DocumentGenerator generator = generators.get(format);
        if (generator == null) {
            throw new IllegalArgumentException("No generator found for format: " + format);
        }
        return generator;
    }
}