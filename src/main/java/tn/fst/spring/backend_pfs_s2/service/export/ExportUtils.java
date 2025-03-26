package tn.fst.spring.backend_pfs_s2.service.export;

import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportUtils {

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    public static String formatEnseignant(Enseignant enseignant) {
        if (enseignant == null) return "";
        return String.format("%s %s", enseignant.getNom(), enseignant.getPrenom());
    }
}