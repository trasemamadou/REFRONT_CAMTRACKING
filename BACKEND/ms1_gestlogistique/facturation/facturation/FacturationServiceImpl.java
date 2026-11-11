package com.groupeisi.ms1_gestschool.facturation.facturation;

import com.groupeisi.ms1_gestschool.config.exception.EntityNotFoundException;
import com.groupeisi.ms1_gestschool.config.filtering.FilterRequest;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterService;
import com.groupeisi.ms1_gestschool.config.filtering.GenericFilterServiceFactory;
import com.groupeisi.ms1_gestschool.config.filtering.SorterRequest;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseEntity;
import com.groupeisi.ms1_gestschool.facturation.parametrageclasse.ParametrageclasseRepository;
import com.groupeisi.ms1_gestschool.personne.PersonneEntity;
import com.groupeisi.ms1_gestschool.personne.PersonneRepository;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageDtoRequest;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageService;
import com.groupeisi.ms1_gestschool.templates.TemplateService;
import com.groupeisi.ms1_gestschool.templates.DocumentType;
import com.groupeisi.ms1_gestschool.templates.DocumentFormat;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturationServiceImpl implements FacturationService {

    private final FacturationRepository facturationRepository;
    private final PersonneRepository studentRepository;
    private final ParametrageclasseRepository parametrageRepository;
    private final FacturationMapper facturationMapper;
    private final MessageSource messageSource;
    private final Logger logger = LoggerFactory.getLogger(FacturationServiceImpl.class);
    private final ApprentissageService apprentissageService;
    private final TemplateService templateService;
    private final GenericFilterServiceFactory filterServiceFactory;

    // ===================== MÉTHODE PRIVÉE DE GÉNÉRATION DE LIBELLÉ =====================

    /**
     * Génère un libellé unique et compréhensible pour la facturation
     * Format: FACT-YYYY-MM-XXXX (ex: FACT-2024-03-0042)
     */
    private String generateLibelle() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());

        // Compter le nombre de facturations pour le mois en cours
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        long count = facturationRepository.countByDateGenerationBetween(startOfMonth, endOfMonth);

        // Générer le libellé avec compteur sur 4 chiffres
        String libelle = String.format("FACT-%s-%s-%04d", year, month, count + 1);

        // Vérifier l'unicité (sécurité)
        while (facturationRepository.existsByLibelle(libelle)) {
            count++;
            libelle = String.format("FACT-%s-%s-%04d", year, month, count + 1);
        }

        return libelle;
    }

    /**
     * Alternative avec le type d'objet
     * Format: FACT-YYYY-MM-TYPE-XXXX (ex: FACT-2024-03-INSC-0042)
     */
    private String generateLibelleWithType(FacturationDtoRequest dto) {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());

        // Déterminer le type (4 premières lettres de l'objet)
        String type = "FRAIS";
        if (dto.getObject() != null && !dto.getObject().isEmpty()) {
            type = dto.getObject().toUpperCase();
            if (type.length() > 4) {
                type = type.substring(0, 4);
            }
        }

        // Compter les facturations du mois
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);

        long count = facturationRepository.countByDateGenerationBetween(startOfMonth, endOfMonth);

        String libelle = String.format("FACT-%s-%s-%s-%04d", year, month, type, count + 1);

        // Vérifier l'unicité
        while (facturationRepository.existsByLibelle(libelle)) {
            count++;
            libelle = String.format("FACT-%s-%s-%s-%04d", year, month, type, count + 1);
        }

        return libelle;
    }

    // ===================== CRUD METHODS =====================

    @Override
    public Optional<List<FacturationDtoResponse>> getAllFacturations() {
        logger.debug("Récupération de toutes les facturations");
        List<FacturationEntity> registrations = facturationRepository.findAll();
        return Optional.of(facturationMapper.toListRegistrationDtoResponse(registrations));
    }

    @Override
    public Optional<FacturationDtoResponse> addFacturation(FacturationDtoRequest dto) {
        logger.info("Ajout d'une nouvelle facturation pour l'étudiant ID: {}", dto.getStudentId());

        // Vérifier l'étudiant
//        if (dto.getObject().equals("MENSUALITE") || dto.getObject().equals("INSCRIPTION")){
//        PersonneEntity student = studentRepository.findById(dto.getStudentId())
//                .orElseThrow(() -> new EntityNotFoundException(
//                        "Étudiant introuvable avec ID = " + dto.getStudentId()
//                ));
//        }else {
            PersonneEntity student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Étudiant introuvable avec ID = " + dto.getStudentId()
                    ));
//        }


        // Vérifier le paramétrage de classe
        ParametrageclasseEntity parametrage = parametrageRepository.findById(dto.getParametrageclasseId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Paramétrage de facturation introuvable avec ID = " + dto.getParametrageclasseId()
                ));

        // Créer l'entité
        FacturationEntity entity = facturationMapper.toRegistrationEntity(dto);
        entity.setStudent(student);
        entity.setParametrageclasse(parametrage);
        entity.setStatut(FacturationStatus.ENATTENTE);

        // *** NOUVEAU : Génération automatique du libellé ***
        // Remplacer le libellé du DTO par un libellé généré automatiquement
        String generatedLibelle = generateLibelleWithType(dto); // ou generateLibelle() selon votre choix
        entity.setLibelle(generatedLibelle);

        // Définir la date de génération
        entity.setDateGeneration(LocalDateTime.now());

        // Conversion du string object en enum
        if (dto.getObject() != null) {
            try {
                entity.setObject(FacturationObject.valueOf(dto.getObject().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Objet de facturation invalide: {}, utilisation de INSCRIPTION par défaut", dto.getObject());
                entity.setObject(FacturationObject.INSCRIPTION);
            }
        }

        // Sauvegarder
        FacturationEntity saved = facturationRepository.save(entity);
         logger.info("Facturation ajoutée avec succès, libellé généré: {}", saved.getLibelle());

        return Optional.of(facturationMapper.toRegistrationDtoResponse(saved));
    }

    @Override
    @Transactional
    public Optional<FacturationDtoResponse> updateFacturation(String libelle, FacturationDtoRequest dto) {
        logger.info("Mise à jour de la facturation: {}", libelle);

        FacturationEntity existing = facturationRepository.findById(libelle)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Facturation introuvable avec libellé = " + libelle
                ));

        // Mise à jour des champs (sans modifier le libellé)
        existing.setDescription(dto.getDescription());
        existing.setMontant(dto.getMontant());
        existing.setResponsableFinancier(dto.getResponsableFinancier());
        existing.setModePaiement(dto.getModePaiement());

        // Mise à jour du statut seulement si fourni
        if (dto.getStatut() != null) {
            try {
                existing.setStatut(FacturationStatus.valueOf(dto.getStatut().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Statut invalide: {}", dto.getStatut());
            }
        }

        // Mise à jour de l'objet
        if (dto.getObject() != null) {
            try {
                existing.setObject(FacturationObject.valueOf(dto.getObject().toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Objet de facturation invalide: {}", dto.getObject());
            }
        }

        // Mise à jour de l'étudiant si fourni
        if (dto.getStudentId() != null) {
            PersonneEntity student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Étudiant introuvable avec ID = " + dto.getStudentId()
                    ));
            existing.setStudent(student);
        }

        // Mise à jour du paramétrage si fourni
        if (dto.getParametrageclasseId() != null) {
            ParametrageclasseEntity parametrage = parametrageRepository.findById(dto.getParametrageclasseId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Paramétrage de facturation introuvable avec ID = " + dto.getParametrageclasseId()
                    ));
            existing.setParametrageclasse(parametrage);
        }

        FacturationEntity saved = facturationRepository.save(existing);
        logger.info("Facturation mise à jour avec succès: {}", libelle);

        return Optional.of(facturationMapper.toRegistrationDtoResponse(saved));
    }

    @Override
    public Optional<FacturationDtoResponse> getFacturationByLibelle(String libelle) {
        logger.debug("Récupération de la facturation: {}", libelle);
        return facturationRepository
                .findById(libelle)
                .map(facturationMapper::toRegistrationDtoResponse);
    }

    @Override
    public Page<FacturationDtoResponse> filterFacturations(List<FilterRequest> filters, SorterRequest sorter, Pageable pageable) {
        log.info("Filtrage des facturations avec {} critères", filters != null ? filters.size() : 0);

        try {
            List<FilterRequest> allFilters = new ArrayList<>();
            if (filters != null) {
                allFilters.addAll(filters);
            }

            Optional<FilterRequest> ecoleFilter = allFilters.stream()
                    .filter(f -> "ecole".equals(f.getChamp()))
                    .findFirst();

            Page<FacturationEntity> filteredEntities;

            if (ecoleFilter.isPresent()) {
                String ecoleNom = ecoleFilter.get().getKeyword();
                allFilters.removeIf(f -> "ecole".equals(f.getChamp()));
                filteredEntities = facturationRepository.findAllByParametrageclasse_Parametrage_Ecole_Nom(ecoleNom, pageable);
            } else {
                GenericFilterService<FacturationEntity> filterService =
                        filterServiceFactory.createGenericFilterService(FacturationEntity.class);

                if (sorter != null && sorter.getChamp() != null) {
                    filteredEntities = filterService.searchAndSortByFilters(
                            facturationRepository,
                            allFilters,
                            sorter,
                            pageable
                    );
                } else {
                    filteredEntities = filterService.searchByFilters(
                            facturationRepository,
                            allFilters,
                            pageable
                    );
                }
            }

            return filteredEntities.map(facturationMapper::toRegistrationDtoResponse);

        } catch (Exception e) {
            log.error("Erreur lors du filtrage des facturations", e);
            throw new RuntimeException("Erreur lors du filtrage des facturations", e);
        }
    }

    @Override
    @Transactional
    public Optional<?> deleteFacturation(String libelle) {
        logger.info("Suppression de la facturation: {}", libelle);

        FacturationEntity registration = facturationRepository.findById(libelle)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("registration.notfound",
                                new Object[]{libelle}, Locale.getDefault()))
                );

        facturationRepository.delete(registration);
        logger.info("Facturation supprimée avec succès: {}", libelle);

        return Optional.of(true);
    }

    // ===================== STATUS MANAGEMENT =====================

    @Override
    @Transactional
    public Optional<FacturationDtoResponse> changeStatusFacturation(String libelle, FacturationStatus status) {
        logger.info("Changement de statut de la facturation {} vers {}", libelle, status);

        try {
            Optional<FacturationEntity> facturationOpt = facturationRepository.findById(libelle);
            if (facturationOpt.isEmpty()) {
                logger.warn("Facturation avec libellé '{}' non trouvée", libelle);
                return Optional.empty();
            }

            FacturationEntity facturation = facturationOpt.get();

            if (facturation.getStatut() == status) {
                logger.info("Facturation '{}' est déjà au statut {}", libelle, status);
                return Optional.of(facturationMapper.toRegistrationDtoResponse(facturation));
            }

            facturation.setStatut(status);
            FacturationEntity savedFacturation = facturationRepository.save(facturation);

            if (FacturationStatus.APPROUVEE.equals(status)) {
                createApprentissageFromFacturation(savedFacturation);
            }

            logger.info("Statut de la facturation {} changé avec succès vers {}", libelle, status);
            return Optional.of(facturationMapper.toRegistrationDtoResponse(savedFacturation));

        } catch (Exception e) {
            logger.error("Erreur lors du changement de statut de la facturation '{}'", libelle, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<FacturationDtoResponse> approveFacturation(String libelle) {
        return changeStatusFacturation(libelle, FacturationStatus.APPROUVEE);
    }

    private void createApprentissageFromFacturation(FacturationEntity facturation) {
        try {
            if (facturation.getParametrageclasse() != null &&
                    facturation.getParametrageclasse().getClasse() != null &&
                    facturation.getStudent() != null) {

                ApprentissageDtoRequest apprentissageDtoRequest = new ApprentissageDtoRequest();
                apprentissageDtoRequest.setClasseId(facturation.getParametrageclasse().getClasse().getId());
                apprentissageDtoRequest.setStudentId(facturation.getStudent().getId());
                apprentissageService.save(apprentissageDtoRequest);

                logger.info("Apprentissage créé pour l'étudiant {} dans la classe {}",
                        facturation.getStudent().getId(),
                        facturation.getParametrageclasse().getClasse().getId());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'apprentissage", e);
        }
    }

    // ===================== DOCUMENT GENERATION =====================

    @Override
    @Transactional
    public Resource generateInvoice(String libelle) {
        logger.info("Génération de facture PDF pour le libellé: {}", libelle);
        return generateInvoiceInternal(libelle, "FACTURE_001", DocumentFormat.PDF);
    }

    @Override
    @Transactional
    public Resource generateInvoicePdf(String libelle) {
        logger.info("Génération de facture PDF pour le libellé: {}", libelle);
        return generateInvoiceInternal(libelle, "FACTURE_001", DocumentFormat.PDF);
    }

    @Override
    @Transactional
    public Resource generateInvoiceExcel(String libelle) {
        logger.info("Génération de facture Excel pour le libellé: {}", libelle);
        return generateInvoiceInternal(libelle, "FACTURE_001", DocumentFormat.EXCEL);
    }

    @Override
    @Transactional
    public String generateInvoiceHtml(String libelle) {
        logger.info("Génération de facture HTML pour le libellé: {}", libelle);

        try {
            FacturationEntity facture = facturationRepository.findById(libelle)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Facturation introuvable avec libellé = " + libelle
                    ));

            Map<String, Object> invoiceData = buildInvoiceData(facture);
            String html = templateService.parseTemplateContent("FACTURE_001", invoiceData);

            logger.info("Facture HTML générée avec succès pour: {}", libelle);
            return html;

        } catch (EntityNotFoundException e) {
            logger.error("Facture non trouvée: {}", libelle);
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors de la génération de la facture HTML {}", libelle, e);
            throw new RuntimeException("Erreur lors de la génération de la facture HTML: " + e.getMessage(), e);
        }
    }

    private Resource generateInvoiceInternal(String libelle, String templateCode, DocumentFormat format) {
        try {
            FacturationEntity facture = facturationRepository.findById(libelle)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Facturation introuvable avec libellé = " + libelle
                    ));

            Map<String, Object> invoiceData = buildInvoiceData(facture);
            Resource document;

            try {
                document = templateService.generateDocument(
                        DocumentType.FACTURE,
                        format,
                        invoiceData
                );
            } catch (Exception e) {
                logger.debug("Fallback vers generateDocument avec 2 paramètres");
                document = templateService.generateDocument(
                        DocumentType.FACTURE,
                        format,
                        invoiceData
                );
            }

            logger.info("Facture {} générée avec succès: {}", format, document.getFilename());
            return document;

        } catch (EntityNotFoundException e) {
            logger.error("Facture non trouvée: {}", libelle);
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors de la génération de la facture {}", libelle, e);
            throw new RuntimeException("Erreur lors de la génération de la facture: " + e.getMessage(), e);
        }
    }

    // ===================== DATA BUILDING METHODS =====================

    private Map<String, Object> buildInvoiceData(FacturationEntity facture) {
        Map<String, Object> data = new HashMap<>();

        data.put("invoiceNumber", facture.getLibelle());
        data.put("invoiceDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        data.put("dueDate", LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        data.put("generationDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        data.put("generationDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        data.put("client", buildClientData(facture.getStudent()));
        data.put("company", buildCompanyData());
        data.put("items", buildInvoiceItems(facture));
        calculateTotals(data, facture);
        data.put("paymentTerms", "Paiement à réception");
        data.put("currency", "EUR");
        data.put("responsiblePerson", facture.getResponsableFinancier() != null ?
                facture.getResponsableFinancier() : "Non spécifié");
        data.put("paymentMethod", facture.getModePaiement() != null ?
                facture.getModePaiement() : "Non spécifié");
        data.put("companySlogan", "École de qualité pour tous");
        data.put("status", facture.getStatut() != null ? facture.getStatut().toString() : "ENATTENTE");

        return data;
    }

    private Map<String, Object> buildClientData(PersonneEntity student) {
        Map<String, Object> client = new HashMap<>();

        String firstName = student.getPrenom() != null ? student.getPrenom() : "";
        String lastName = student.getNom() != null ? student.getNom() : "";
        String fullName = (firstName + " " + lastName).trim();

        client.put("id", student.getId());
        client.put("firstName", firstName);
        client.put("lastName", lastName);
        client.put("fullName", fullName.isEmpty() ? "Non renseigné" : fullName);
        client.put("email", student.getEmail() != null ? student.getEmail() : "non renseigné");
        client.put("phone", student.getTelephone() != null ? student.getTelephone() : "non renseigné");
        client.put("address", buildAddress(student));

        return client;
    }

    private String buildAddress(PersonneEntity student) {
        StringBuilder address = new StringBuilder();

        if (student.getAdresse() != null && !student.getAdresse().isEmpty()) {
            address.append(student.getAdresse());
        } else {
            address.append("Adresse non renseignée");
        }

        if (student.getAdresse() != null && !student.getAdresse().isEmpty()) {
            address.append(", ").append(student.getAdresse());
        }

        return address.toString();
    }

    private Map<String, Object> buildCompanyData() {
        Map<String, Object> company = new HashMap<>();
        company.put("name", "Groupe Scolaire International");
        company.put("siret", "123 456 789 00012");
        company.put("address", "123 Avenue des Écoles, 75000 Paris");
        company.put("phone", "01 23 45 67 89");
        company.put("email", "comptabilite@groupe-scolaire.fr");
        company.put("website", "www.groupe-scolaire.fr");
        return company;
    }

    private List<Map<String, Object>> buildInvoiceItems(FacturationEntity facture) {
        List<Map<String, Object>> items = new ArrayList<>();

        Map<String, Object> mainItem = new HashMap<>();
        mainItem.put("id", 1);
        mainItem.put("description", buildItemDescription(facture));
        mainItem.put("quantity", 1);
        mainItem.put("unitPrice", facture.getMontant() != null ? facture.getMontant() : 0.0);
        mainItem.put("total", facture.getMontant() != null ? facture.getMontant() : 0.0);
        items.add(mainItem);

        return items;
    }

    private String buildItemDescription(FacturationEntity facture) {
        StringBuilder description = new StringBuilder();

        if (facture.getObject() != null) {
            description.append(facture.getObject().toString());
        } else {
            description.append("Frais scolaires");
        }

        if (facture.getDescription() != null && !facture.getDescription().isEmpty()) {
            description.append(" - ").append(facture.getDescription());
        }

        if (facture.getParametrageclasse() != null &&
                facture.getParametrageclasse().getClasse() != null &&
                facture.getParametrageclasse().getClasse().getNom() != null) {
            description.append(" (Classe: ")
                    .append(facture.getParametrageclasse().getClasse().getNom())
                    .append(")");
        }

        return description.toString();
    }

    private void calculateTotals(Map<String, Object> data, FacturationEntity facture) {
        double montant = facture.getMontant() != null ? facture.getMontant() : 0.0;
        double taxRate = 0.0;
        double taxAmount = montant * taxRate / 100;
        double totalWithTax = montant + taxAmount;

        data.put("subTotal", montant);
        data.put("taxRate", taxRate);
        data.put("taxAmount", taxAmount);
        data.put("total", totalWithTax);
        data.put("totalWithTax", totalWithTax);
    }
}