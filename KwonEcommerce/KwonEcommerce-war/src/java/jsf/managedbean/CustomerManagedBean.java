/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;


import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.CustomerEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.BanCustomerException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.StaffNotFoundException;
import util.exception.UpdateStaffException;

@Named
@ViewScoped
public class CustomerManagedBean implements Serializable {

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private List<CustomerEntity> customerEntities;
    private List<CustomerEntity> filteredCustomerEntities;

    private CustomerEntity customerEntityToBan;

    public CustomerManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        customerEntities = customerSessionBeanLocal.retrieveAllCustomers();
    }

    public void viewCustomerDetails(ActionEvent actionEvent) throws IOException {
        Long customerIdToView = (Long) actionEvent.getComponent().getAttributes().get("customerId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("customerIdToView", customerIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewCustomerDetails.xhtml");
    }
    
    public void banStaff(ActionEvent actionEvent) {
        customerEntityToBan = (CustomerEntity) actionEvent.getComponent().getAttributes().get("customerEntityToBan");
        try {
            customerSessionBeanLocal.banCustomer(customerEntityToBan.getCustomerId());
        } catch (BanCustomerException | CustomerNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while banning customer: " + ex.getMessage(), null));
        }
    }

//    public void deleteCustomer(ActionEvent actionEvent) {
//        customerEntityToDelete = (StaffEntity) actionEvent.getComponent().getAttributes().get("customerEntityToDelete");
//        try {
//            customerEntitySessionBeanLocal.deleteStaff(staffEntityToDelete.getStaffId());
//            customerEntities.remove(staffEntityToDelete);
//            if(filteredCustomerEntities != null) {
//                filteredCustomerEntities.remove(customerEntityToDelete);
//            }
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Customer " + customerEntityToDelete.getFirstName() + " " + customerEntityToDelete.getLastName() + " has successfully been deleted.", null));
//        } catch (DeleteCustomerException | CustomerNotFoundException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting customer: " + ex.getMessage(), null));
//        }
//    }
    public List<CustomerEntity> getCustomerEntities() {
        return customerEntities;
    }

    public void setCustomerEntities(List<CustomerEntity> customerEntities) {
        this.customerEntities = customerEntities;
    }

    public List<CustomerEntity> getFilteredCustomerEntities() {
        return filteredCustomerEntities;
    }

    public void setFilteredCustomerEntities(List<CustomerEntity> filteredCustomerEntities) {
        this.filteredCustomerEntities = filteredCustomerEntities;
    }

    public CustomerEntity getCustomerEntityToBan() {
        return customerEntityToBan;
    }

    public void setCustomerEntityToBan(CustomerEntity customerEntityToBan) {
        this.customerEntityToBan = customerEntityToBan;
    }
}
