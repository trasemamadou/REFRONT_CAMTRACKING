package com.groupeisi.ms1_gestschool.templates;

public enum TemplateType {
    FACTURE("Facture", "fct", DocumentFormat.PDF),
    BULLETIN("Bulletin scolaire", "blt", DocumentFormat.EXCEL),
    FICHE_SALAIRE("Fiche de salaire", "sal", DocumentFormat.EXCEL),
    CERTIFICAT("Certificat", "cert", DocumentFormat.PDF),
    ATTESTATION("Attestation", "att", DocumentFormat.PDF),
    RELEVE_NOTE("Relevé de notes", "rln", DocumentFormat.EXCEL),
    CONVENTION("Convention", "conv", DocumentFormat.PDF),
    CONTRAT("Contrat", "ctr", DocumentFormat.PDF),
    RAPPORT("Rapport", "rpt", DocumentFormat.EXCEL);

    private final String label;
    private final String prefix;
    private final DocumentFormat defaultFormat;

    TemplateType(String label, String prefix, DocumentFormat defaultFormat) {
        this.label = label;
        this.prefix = prefix;
        this.defaultFormat = defaultFormat;
    }

    public String getLabel() { return label; }
    public String getPrefix() { return prefix; }
    public DocumentFormat getDefaultFormat() { return defaultFormat; }

    public static String getDefaultExtension(TemplateType type) {
        return switch (type.getDefaultFormat()) {
            case PDF -> "pdf";
            case EXCEL -> "xlsx";
            case WORD -> "docx";
            case HTML -> "html";
            case TEXT -> "txt";
            case CSV -> "csv";
        };
    }
}