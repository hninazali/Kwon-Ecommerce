/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.StaffEntitySessionBeanLocal;
import entity.StaffEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.AccessRightEnum;
import util.exception.DeleteStaffException;
import util.exception.InputDataValidationException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;

@Named(value = "staffManagementManagedBean")
@ViewScoped
public class StaffManagementManagedBean implements Serializable {

    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;

    private List<StaffEntity> staffEntities;
    private List<StaffEntity> filteredStaffEntities;

    private StaffEntity newStaffEntity;

    private StaffEntity staffEntityToUpdate;

    private StaffEntity staffEntityToDelete;

    public StaffManagementManagedBean() {
        newStaffEntity = new StaffEntity();
    }

    @PostConstruct
    public void postConstruct() {
        staffEntities = staffEntitySessionBeanLocal.retrieveAllStaffs();
    }

    public void viewStaffDetails(ActionEvent actionEvent) throws IOException {
        Long staffIdToView = (Long) actionEvent.getComponent().getAttributes().get("staffId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("staffIdToView", staffIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewStaffDetails.xhtml");
    }

    public void createNewStaff(ActionEvent actionEvent) {
        try {
            newStaffEntity.setAccessRightEnum(AccessRightEnum.MANAGER);
            Long seId = staffEntitySessionBeanLocal.createNewStaff(newStaffEntity);
            StaffEntity createdStaff = staffEntitySessionBeanLocal.retrieveStaffByStaffId(seId);
            staffEntities.add(newStaffEntity);
            if(filteredStaffEntities != null) {
                filteredStaffEntities.add(newStaffEntity);
            }
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New staff " + createdStaff.getFirstName() + " " + createdStaff.getLastName() + " created successfully", null));
        } catch (InputDataValidationException | StaffNotFoundException | StaffUsernameExistException | UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occured while creating the new staff: " + ex.getMessage(), null));
        }
    }

    public void doUpdateStaff(ActionEvent actionEvent) {
        staffEntityToUpdate = (StaffEntity) actionEvent.getComponent().getAttributes().get("staffEntityToUpdate");
    }

    public void updateStaff(ActionEvent actionEvent) {
        try {
            staffEntitySessionBeanLocal.updateStaff(staffEntityToUpdate);
        } catch (InputDataValidationException | StaffNotFoundException | UpdateStaffException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating staff: " + ex.getMessage(), null));
        }
    }

    public void deleteStaff(ActionEvent actionEvent) {
        staffEntityToDelete = (StaffEntity) actionEvent.getComponent().getAttributes().get("staffEntityToDelete");
        try {
            staffEntitySessionBeanLocal.deleteStaff(staffEntityToDelete.getStaffId());
            staffEntities.remove(staffEntityToDelete);
            if(filteredStaffEntities != null) {
                filteredStaffEntities.remove(staffEntityToDelete);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Staff " + staffEntityToDelete.getFirstName() + " " + staffEntityToDelete.getLastName() + " has successfully been deleted.", null));
        } catch (DeleteStaffException | StaffNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting staff: " + ex.getMessage(), null));
        }
    }

    public List<StaffEntity> getStaffEntities() {
        return staffEntities;
    }

    public void setStaffEntities(List<StaffEntity> staffEntities) {
        this.staffEntities = staffEntities;
    }

    public List<StaffEntity> getFilteredStaffEntities() {
        return filteredStaffEntities;
    }

    public void setFilteredStaffEntities(List<StaffEntity> filteredStaffEntities) {
        this.filteredStaffEntities = filteredStaffEntities;
    }

    public StaffEntity getNewStaffEntity() {
        return newStaffEntity;
    }

    public void setNewStaffEntity(StaffEntity newStaffEntity) {
        this.newStaffEntity = newStaffEntity;
    }

    public StaffEntity getStaffEntityToUpdate() {
        return staffEntityToUpdate;
    }

    public void setStaffEntityToUpdate(StaffEntity staffEntityToUpdate) {
        this.staffEntityToUpdate = staffEntityToUpdate;
    }

    public StaffEntity getStaffEntityToDelete() {
        return staffEntityToDelete;
    }

    public void setStaffEntityToDelete(StaffEntity staffEntityToDelete) {
        this.staffEntityToDelete = staffEntityToDelete;
    }

}
