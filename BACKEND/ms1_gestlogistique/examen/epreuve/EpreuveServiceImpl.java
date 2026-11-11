package com.groupeisi.ms1_gestschool.examen.epreuve;

import com.groupeisi.ms1_gestschool.cour.matiere.MatiereEntity;
import com.groupeisi.ms1_gestschool.cour.matiere.MatiereRepository;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationEntity;
import com.groupeisi.ms1_gestschool.examen.participation.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EpreuveServiceImpl implements EpreuveService {

    private final EpreuveRepository epreuveRepository;
    private final ParticipationRepository participationRepository;
    private final MatiereRepository matiereRepository;
    private final EpreuveMapper epreuveMapper;

    @Override
    public EpreuveDtoResponse create(EpreuveDtoRequest dtoRequest) {

        MatiereEntity matiere = matiereRepository.findById(dtoRequest.getMatiereId())
                .orElseThrow(() -> new RuntimeException("Matière introuvable"));

        ParticipationEntity participation = participationRepository.findById(dtoRequest.getParticipationId())
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        if (epreuveRepository.existsByMatiere_IdAndParticipation_Id(
                matiere.getId(), participation.getId())) {
            throw new RuntimeException("Cette épreuve existe déjà");
        }

        EpreuveEntity entity = new EpreuveEntity();

        entity.setMatiere(matiere);
        entity.setParticipation(participation);
        entity.setCoef(dtoRequest.getCoef());

        return epreuveMapper.toDtoResponse(epreuveRepository.save(entity));
    }

    @Override
    public List<EpreuveDtoResponse> createList(String participationId, List<EpreuveDtoRequest> dtoRequestList) {

        ParticipationEntity participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        List<EpreuveEntity> entities = dtoRequestList.stream()
                .map(dto -> {

                    MatiereEntity matiere = matiereRepository.findById(dto.getMatiereId())
                            .orElseThrow(() -> new RuntimeException("Matière introuvable"));

                    if (epreuveRepository.existsByMatiere_IdAndParticipation_Id(
                            matiere.getId(), participationId)) {
                        throw new RuntimeException("La matière existe déjà dans cette participation");
                    }

                    EpreuveEntity entity = new EpreuveEntity();
                    entity.setMatiere(matiere);
                    entity.setParticipation(participation);
                    entity.setCoef(dto.getCoef());

                    return entity;

                }).toList();

        return epreuveRepository.saveAll(entities)
                .stream()
                .map(epreuveMapper::toDtoResponse)
                .toList();
    }

    @Override
    public EpreuveDtoResponse update(String id, EpreuveDtoRequest dtoRequest) {

        EpreuveEntity entity = epreuveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Epreuve introuvable"));

        entity.setCoef(dtoRequest.getCoef());

        return epreuveMapper.toDtoResponse(epreuveRepository.save(entity));
    }

    @Override
    public void delete(String id) {
        epreuveRepository.deleteById(id);
    }

    @Override
    public EpreuveDtoResponse getById(String id) {
        return epreuveMapper.toDtoResponse(
                epreuveRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Epreuve introuvable")));
    }

    @Override
    public List<EpreuveDtoResponse> getAll() {
        return epreuveRepository.findAll()
                .stream()
                .map(epreuveMapper::toDtoResponse)
                .toList();
    }

    @Override
    public List<EpreuveDtoResponse> getByMatiereId(String matiereId) {
        return epreuveRepository.findByMatiere_Id(matiereId)
                .stream()
                .map(epreuveMapper::toDtoResponse)
                .toList();
    }

    @Override
    public List<EpreuveDtoResponse> getByParticipationId(String participationId) {
        return epreuveRepository.findByParticipation_Id(participationId)
                .stream()
                .map(epreuveMapper::toDtoResponse)
                .toList();
    }
}