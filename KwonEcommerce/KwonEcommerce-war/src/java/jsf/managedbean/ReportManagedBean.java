/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 *
 * @author tanwk
 */
@Named(value = "reportManagedBean")
@RequestScoped
public class ReportManagedBean {

    @Resource(name = "kwonEcommerceDataSource")
    private DataSource kwonEcommerceDataSource;

    /**
     * Creates a new instance of RecordReportManagedBean
     */
    public ReportManagedBean() {
    }

    public void generateReport(ActionEvent event) {
        try {
            HashMap parameters = new HashMap();
            parameters.put("Description", "This is a placeholder description");

            InputStream reportStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/jasperreport/recordreport.jasper");
            OutputStream outputStream = FacesContext.getCurrentInstance().getExternalContext().getResponseOutputStream();

            JasperRunManager.runReportToPdfStream(reportStream, outputStream, parameters, kwonEcommerceDataSource.getConnection());
        } catch (JRException | SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
        }
    }
}
