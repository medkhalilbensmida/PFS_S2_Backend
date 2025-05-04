package tn.fst.spring.backend_pfs_s2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMessage(String message);
    List<Notification> findByMessageAndDateEnvoi(String message, Date dateEnvoi);
    List<Notification> findByEnseignantDestinataire(Enseignant enseignant);
    List<Notification> findByAdminDestinataire(Administrateur admin);
    @Modifying
    @Query("UPDATE Notification n SET n.estLue = true WHERE n.id IN :ids")
    void markAsRead(@Param("ids") List<Long> ids);

    @Query("SELECT n FROM Notification n " +
           "LEFT JOIN FETCH n.enseignantDestinataire " +
           "LEFT JOIN FETCH n.adminDestinataire " +
           "WHERE n.enseignantDestinataire.email = :email OR n.adminDestinataire.email = :email")
    List<Notification> findByUserEmailWithFetch(@Param("email") String email);
    
}