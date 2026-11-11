// DocumentGenerator.java
package com.groupeisi.ms1_gestschool.templates.generator;

import com.groupeisi.ms1_gestschool.templates.DocumentFormat;

import java.util.Map;

public interface DocumentGenerator {
    byte[] generate(String htmlContent, Map<String, Object> data);
    DocumentFormat getFormat();
}