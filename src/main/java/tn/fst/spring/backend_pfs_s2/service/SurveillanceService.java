package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.repository.SurveillanceRepository;

import java.util.List;

@Service
public class SurveillanceService {

    @Autowired
    private SurveillanceRepository surveillanceRepository;

    public List<Surveillance> getAllSurveillances() {
        return surveillanceRepository.findAll();
    }

    public Surveillance getSurveillanceById(Long id) {
        return surveillanceRepository.findById(id).orElse(null);
    }

    public Surveillance createSurveillance(Surveillance surveillance) {
        return surveillanceRepository.save(surveillance);
    }

    public Surveillance updateSurveillance(Long id, Surveillance surveillance) {
        if (surveillanceRepository.existsById(id)) {
            surveillance.setId(id);
            return surveillanceRepository.save(surveillance);
        }
        return null;
    }

    public void deleteSurveillance(Long id) {
        surveillanceRepository.deleteById(id);
    }
}