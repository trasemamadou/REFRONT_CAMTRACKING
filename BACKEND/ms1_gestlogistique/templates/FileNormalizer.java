package com.groupeisi.ms1_gestschool.templates;

public class FileNormalizer {

    /**
     * Normalise une base64 en ajoutant le bon préfixe data:mime;base64,
     */
    public static String normalize(String file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        // Si c'est déjà une URL
        if (file.startsWith("http")) {
            return file;
        }

        // Si déjà normalisé
        if (file.startsWith("data:")) {
            return file;
        }

        // PDF
        if (file.startsWith("JVBERi0")) {
            return "data:application/pdf;base64," + file;
        }

        // Excel (.xlsx)
        if (file.startsWith("UEsDB")) {
            return "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + file;
        }

        // Word (.docx)
        if (file.startsWith("UEsDB")) {
            return "data:application/vnd.openxmlformats-officedocument.wordprocessingml.document;base64," + file;
        }

        // Image PNG
        if (file.startsWith("iVBOR")) {
            return "data:image/png;base64," + file;
        }

        // Image JPEG
        if (file.startsWith("/9j/")) {
            return "data:image/jpeg;base64," + file;
        }

        // Par défaut image jpeg
        return "data:image/jpeg;base64," + file;
    }


    /**
     * Supprime le préfixe data:mime;base64,
     */
    public static String denormalize(String file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.startsWith("data:")) {
            return file.substring(file.indexOf(",") + 1);
        }

        return file;
    }
}