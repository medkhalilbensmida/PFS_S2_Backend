package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.Salle;
import tn.fst.spring.backend_pfs_s2.repository.SalleRepository;

import java.util.List;

@Service
public class SalleService {

    @Autowired
    private SalleRepository salleRepository;

    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    public Salle getSalleById(Long id) {
        return salleRepository.findById(id).orElse(null);
    }

    public Salle createSalle(Salle salle) {
        return salleRepository.save(salle);
    }

    public Salle updateSalle(Long id, Salle salle) {
        if (salleRepository.existsById(id)) {
            salle.setId(id);
            return salleRepository.save(salle);
        }
        return null;
    }

    public void deleteSalle(Long id) {
        salleRepository.deleteById(id);
    }
}