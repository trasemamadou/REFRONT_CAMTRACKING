package com.groupeisi.ms1_gestschool.examen.participation;

import com.groupeisi.ms1_gestschool.cour.classe.ClasseEntity;
import com.groupeisi.ms1_gestschool.cour.classe.ClasseRepository;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionInstance;
import com.groupeisi.ms1_gestschool.examen.parametrage.CompositionInstanceRepository;
import com.groupeisi.ms1_gestschool.examen.session.SessionEntity;
import com.groupeisi.ms1_gestschool.examen.session.SessionRepository;
import com.groupeisi.ms1_gestschool.examen.epreuve.EpreuveService;
import com.groupeisi.ms1_gestschool.personne.student.EleveDtoResponse;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageDtoResponse;
import com.groupeisi.ms1_gestschool.scolarite.apprentissage.ApprentissageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final ClasseRepository classeRepository;
    private final SessionRepository sessionRepository;
    private final ParticipationMapper participationMapper;
    private final EpreuveService epreuveService;
    private final ApprentissageService apprentissageService;
    private final CompositionInstanceRepository compositionInstanceRepository;

    @Override
    @Transactional
    public ParticipationDtoResponse create(ParticipationDtoRequest request) {
        // Vérifier si la session-classe existe déjà
        if (participationRepository.existsByClasseIdAndSessionId(request.getClasseId(), request.getSessionId())) {
            throw new RuntimeException("Cette session-classe existe déjà");
        }

        // Vérifier l'existence de la compositionInstance si fournie
        if (request.getCompositionInstanceId() != null) {
            compositionInstanceRepository.findById(request.getCompositionInstanceId())
                    .orElseThrow(() -> new RuntimeException("Instance de composition non trouvée"));
        }

        // Récupérer la classe et la session
        ClasseEntity classe = classeRepository.findById(request.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        SessionEntity session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session non trouvée"));

        // Créer l'entité via le mapper
        ParticipationEntity sessionclasse = participationMapper.toEntity(request);
        sessionclasse.setClasse(classe);
        sessionclasse.setSession(session);

        ParticipationEntity saved = participationRepository.save(sessionclasse);
        log.info("Session-classe créée avec succès: {}", saved.getId());

        return participationMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ParticipationDtoResponse update(String id, ParticipationDtoRequest request) {
        ParticipationEntity entity = participationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session-classe non trouvée"));

        // Mise à jour des relations
        if (!entity.getClasse().getId().equals(request.getClasseId())) {
            ClasseEntity classe = classeRepository.findById(request.getClasseId())
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
            entity.setClasse(classe);
        }

        if (!entity.getSession().getId().equals(request.getSessionId())) {
            SessionEntity session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session non trouvée"));
            entity.setSession(session);
        }

        // Mise à jour de l'instance de composition
        if (request.getCompositionInstanceId() != null) {
            CompositionInstance instance = compositionInstanceRepository.findById(request.getCompositionInstanceId())
                    .orElseThrow(() -> new RuntimeException("Instance de composition non trouvée"));
            entity.setCompositionInstance(instance);
        } else {
            entity.setCompositionInstance(null);
        }

        ParticipationEntity updated = participationRepository.save(entity);
        log.info("Session-classe mise à jour avec succès: {}", id);

        return participationMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!participationRepository.existsById(id)) {
            throw new RuntimeException("Session-classe non trouvée");
        }

        participationRepository.deleteById(id);
        log.info("Session-classe supprimée avec succès: {}", id);
    }

    @Override
    public ParticipationDtoResponse getById(String id) {
        ParticipationEntity sessionclasse = participationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session-classe non trouvée"));

        ParticipationDtoResponse dto = participationMapper.toDto(sessionclasse);
        dto.setMatieres(epreuveService.getByParticipationId(sessionclasse.getId()));
        return dto;
    }

    @Override
    public List<ParticipationDtoResponse> getAll() {
        return participationRepository.findAll()
                .stream()
                .map(participationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationDtoResponse> getByClasseId(String classeId) {
        // Récupérer les entités Sessionclasse
        System.out.println("L'id class est là : " + classeId);
        List<ParticipationEntity> entities = participationRepository.findByClasseId(classeId);

        // Pour chaque entité, construire le DTO en allant chercher les matières
        return entities.stream()
                .map(entity -> {
                    ParticipationDtoResponse dto = participationMapper.toDto(entity); // On aurait une méthode sans matieres
                    // Ou on utilise le mapper normal et on écrase la liste

                    dto.setMatieres(epreuveService.getByParticipationId(entity.getId()));
                    System.out.println("L'id entity  est là : " + entity.getId());
                    System.out.println("Le dto  est là : " + dto.getMatieres());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationDtoResponse> getBySessionId(String sessionId) {
        return participationRepository.findBySessionId(sessionId)
                .stream()
                .map(participationMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public Resource generateSaisieNotesExcel(String sessionClasseId) {

        ParticipationDtoResponse sessionclasse = getById(sessionClasseId);

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Saisie Notes");

            // =============================
            // 🎨 STYLES
            // =============================

            // Police en gras
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            // Style header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Style cellules normales
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Style infos
            CellStyle infoStyle = workbook.createCellStyle();
            infoStyle.setFont(boldFont);

            int rowIndex = 0;

            // =============================
            // INFOS CLASSE
            // =============================
            Row infoRow1 = sheet.createRow(rowIndex++);
            Cell c1 = infoRow1.createCell(0);
            c1.setCellValue("Classe : ");
            c1.setCellStyle(infoStyle);

            infoRow1.createCell(1)
                    .setCellValue(sessionclasse.getClasse().getNom());

            Row infoRow2 = sheet.createRow(rowIndex++);
            Cell c2 = infoRow2.createCell(0);
            c2.setCellValue("Session : ");
            c2.setCellStyle(infoStyle);

            infoRow2.createCell(1)
                    .setCellValue(sessionclasse.getSession().getLibelle());

            rowIndex++; // ligne vide

            // =============================
            // HEADER
            // =============================
            Row header = sheet.createRow(rowIndex++);
            int colIndex = 0;

            String[] fixedHeaders = {"Matricule", "Nom", "Prénom"};

            for (String title : fixedHeaders) {
                Cell cell = header.createCell(colIndex++);
                cell.setCellValue(title);
                cell.setCellStyle(headerStyle);
            }


            // =============================
            // LISTE ETUDIANTS
            // =============================
            List<ApprentissageDtoResponse> apprentissages =
                    apprentissageService.getAllByClasseId(sessionclasse.getClasse().getId());

            for (ApprentissageDtoResponse apprentissage : apprentissages) {

                EleveDtoResponse etudiant = apprentissage.getEleve();
                Row row = sheet.createRow(rowIndex++);
                int c = 0;

                Cell cell1 = row.createCell(c++);
                cell1.setCellValue(etudiant.getMatricule());
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(c++);
                cell2.setCellValue(etudiant.getNom());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(c++);
                cell3.setCellValue(etudiant.getPrenom());
                cell3.setCellStyle(dataStyle);
            }

            // Auto-size colonnes
            for (int i = 0; i < colIndex; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayResource(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération Excel saisie notes", e);
        }
    }
}