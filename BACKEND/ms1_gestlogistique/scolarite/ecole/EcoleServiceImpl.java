package com.groupeisi.ms1_gestschool.scolarite.ecole;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EcoleServiceImpl implements EcoleService {

    private final EcoleRepository ecoleRepository;
    private final EcoleMapper ecoleMapper;

    @Override
    public EcoleDtoResponse createEcole(EcoleDtoRequest ecoleDtoRequest) {
        log.info("Création d'une nouvelle école: {}", ecoleDtoRequest.getNom());

        // Vérifier si l'école existe déjà
        if (ecoleRepository.existsByNom(ecoleDtoRequest.getNom())) {
            throw new RuntimeException("Une école avec ce nom existe déjà");
        }

//        if (ecoleRepository.existsByEmail(ecoleDtoRequest.getEmail())) {
//            throw new RuntimeException("Une école avec cet email existe déjà");
//        }

        EcoleEntity ecoleEntity = ecoleMapper.toEntity(ecoleDtoRequest);
        EcoleEntity savedEntity = ecoleRepository.save(ecoleEntity);

        log.info("École créée avec succès: {}", savedEntity.getNom());
        return ecoleMapper.toDtoResponse(savedEntity);
    }

    @Override
    public EcoleDtoResponse updateEcole(String nom, EcoleDtoRequest ecoleDtoRequest) {
        log.info("Mise à jour de l'école: {}", nom);

        EcoleEntity ecoleEntity = ecoleRepository.findById(nom)
                .orElseThrow(() -> new RuntimeException("École non trouvée avec le nom: " + nom));

        // Vérifier si le nouvel email n'est pas déjà utilisé par une autre école
        if (!ecoleEntity.getEmailPrescripteur().equals(ecoleDtoRequest.getEmailPrescripteur())
                && ecoleRepository.existsByEmailPrescripteur(ecoleDtoRequest.getEmailPrescripteur())) {
            throw new RuntimeException("Cet email est déjà utilisé par une autre école");
        }

        ecoleMapper.updateEntityFromDto(ecoleDtoRequest, ecoleEntity);
        EcoleEntity updatedEntity = ecoleRepository.save(ecoleEntity);

        log.info("École mise à jour avec succès: {}", nom);
        return ecoleMapper.toDtoResponse(updatedEntity);
    }

    @Override
    public void deleteEcole(String nom) {
        log.info("Suppression de l'école: {}", nom);

        if (!ecoleRepository.existsById(nom)) {
            throw new RuntimeException("École non trouvée avec le nom: " + nom);
        }

        ecoleRepository.deleteById(nom);
        log.info("École supprimée avec succès: {}", nom);
    }

    @Override
    public EcoleDtoResponse getEcoleByNom(String nom) {
        log.info("Récupération de l'école: {}", nom);

        EcoleEntity ecoleEntity = ecoleRepository.findById(nom)
                .orElseThrow(() -> new RuntimeException("École non trouvée avec le nom: " + nom));

        return ecoleMapper.toDtoResponse(ecoleEntity);
    }

    @Override
    public List<EcoleDtoResponse> getAllEcoles() {
        log.info("Récupération de toutes les écoles");

        return ecoleRepository.findAll().stream()
                .map(ecoleMapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EcoleDtoResponse> getActiveEcoles() {
        log.info("Récupération des écoles actives");

        return ecoleRepository.findAll().stream()
                .filter(EcoleEntity::isActive)
                .map(ecoleMapper::toDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EcoleDtoResponse toggleEcoleStatus(String nom, boolean active) {
        log.info("Changement du statut de l'école {} à: {}", nom, active ? "actif" : "inactif");

        EcoleEntity ecoleEntity = ecoleRepository.findById(nom)
                .orElseThrow(() -> new RuntimeException("École non trouvée avec le nom: " + nom));

        ecoleEntity.setActive(active);
        EcoleEntity updatedEntity = ecoleRepository.save(ecoleEntity);

        log.info("Statut de l'école {} mis à jour avec succès", nom);
        return ecoleMapper.toDtoResponse(updatedEntity);
    }
}