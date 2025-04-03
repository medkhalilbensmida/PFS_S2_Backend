package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.DisponibiliteEnseignant;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.repository.DisponibiliteEnseignantRepository;
import tn.fst.spring.backend_pfs_s2.repository.SurveillanceRepository;

import java.util.List;
import java.util.logging.Logger;

@Service
public class EnseignantService {

    private static final Logger logger = Logger.getLogger(EnseignantService.class.getName());

    private final EnseignantRepository enseignantRepository;
    private final DisponibiliteEnseignantRepository disponibiliteRepository;
    private final SurveillanceRepository surveillanceRepository;

    @Autowired
    public EnseignantService(EnseignantRepository enseignantRepository,
                             DisponibiliteEnseignantRepository disponibiliteRepository,
                             SurveillanceRepository surveillanceRepository) {
        this.enseignantRepository = enseignantRepository;
        this.disponibiliteRepository = disponibiliteRepository;
        this.surveillanceRepository = surveillanceRepository;
    }

    @Transactional
    public Enseignant createEnseignant(Enseignant enseignant) {
        logger.info("Création d'un nouvel enseignant : " + enseignant.getNom());
        Enseignant savedEnseignant = enseignantRepository.save(enseignant);
        enseignantRepository.flush();  // S'assurer que l'enseignant est bien enregistré

        logger.info("Insertion des disponibilités...");
        initializeDisponibilites(savedEnseignant);

        return savedEnseignant;
    }

    @Transactional
    public void initializeDisponibilites(Enseignant enseignant) {
        List<Surveillance> surveillances = surveillanceRepository.findSurveillancesWithoutDisponibiliteForEnseignant(enseignant.getId());

        if (surveillances.isEmpty()) {
            logger.warning("Aucune surveillance trouvée pour l'enseignant " + enseignant.getNom());
            return;
        }

        surveillances.forEach(surveillance -> {
            boolean exists = disponibiliteRepository.existsByEnseignantAndSurveillance(enseignant, surveillance);
            if (!exists) {
                DisponibiliteEnseignant disponibilite = new DisponibiliteEnseignant();
                disponibilite.setEnseignant(enseignant);
                disponibilite.setSurveillance(surveillance);
                disponibilite.setEstDisponible(false);
                disponibiliteRepository.save(disponibilite);
                logger.info("Disponibilité ajoutée pour la surveillance " + surveillance.getId());
            } else {
                logger.info("Disponibilité déjà existante pour la surveillance " + surveillance.getId());
            }
        });

        disponibiliteRepository.flush();
    }

    public List<Enseignant> getAllEnseignants() {
        return enseignantRepository.findAll();
    }

    public Enseignant getEnseignantById(Long id) {
        return enseignantRepository.findById(id).orElse(null);
    }

    @Transactional
    public Enseignant updateEnseignant(Long id, Enseignant enseignant) {
        if (enseignantRepository.existsById(id)) {
            enseignant.setId(id);
            return enseignantRepository.save(enseignant);
        }
        return null;
    }

    @Transactional
    public void deleteEnseignant(Long id) {
        disponibiliteRepository.deleteByEnseignantId(id);
        disponibiliteRepository.flush(); // Forcer l'écriture des suppressions en base
        enseignantRepository.deleteById(id);
    }
}
