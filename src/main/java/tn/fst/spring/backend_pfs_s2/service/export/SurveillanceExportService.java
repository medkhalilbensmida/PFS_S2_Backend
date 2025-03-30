package tn.fst.spring.backend_pfs_s2.service.export;

/*
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface SurveillanceExportService {
    void export(List<Surveillance> surveillances, OutputStream outputStream) throws IOException;
}
*/


import tn.fst.spring.backend_pfs_s2.service.export.SurveillanceFilterDTO;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface SurveillanceExportService {
    void export(List<Surveillance> surveillances, OutputStream outputStream) throws IOException;
}