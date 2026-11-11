package com.groupeisi.ms1_gestschool.templates.builder;

import com.groupeisi.ms1_gestschool.templates.DocumentType;

import java.util.Map;

public interface HtmlDocumentBuilder {
    String build(Map<String, Object> data);
    DocumentType getType();
}