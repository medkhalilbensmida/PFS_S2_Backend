package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.repository.SessionExamenRepository;

import java.util.List;

@Service
public class SessionExamenService {

    @Autowired
    private SessionExamenRepository sessionExamenRepository;

    public List<SessionExamen> getAllSessions() {
        return sessionExamenRepository.findAll();
    }

    public SessionExamen getSessionById(Long id) {
        return sessionExamenRepository.findById(id).orElse(null);
    }

    public SessionExamen createSession(SessionExamen sessionExamen) {
        return sessionExamenRepository.save(sessionExamen);
    }

    public SessionExamen updateSession(Long id, SessionExamen sessionExamen) {
        if (sessionExamenRepository.existsById(id)) {
            sessionExamen.setId(id);
            return sessionExamenRepository.save(sessionExamen);
        }
        return null;
    }

    public void deleteSession(Long id) {
        sessionExamenRepository.deleteById(id);
    }
}
