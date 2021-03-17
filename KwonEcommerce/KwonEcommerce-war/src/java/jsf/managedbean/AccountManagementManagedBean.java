/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.AccountSessionBeanLocal;
import entity.AccountEntity;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.enumeration.AccessRightEnum;
import util.exception.AccountNotFoundException;
import util.exception.AccountUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author winyfebriny
 */
@Named(value = "accountManagementManagedBean")
@ViewScoped
public class AccountManagementManagedBean implements Serializable {

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;

    private AccountEntity newAccountEntity;
    
    public AccountManagementManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        newAccountEntity = new AccountEntity();
    }

    public void createNewAccount(ActionEvent actionEvent) {
        try {
            newAccountEntity.setAccessRightEnum(AccessRightEnum.MANAGER);
            Long accId = accountSessionBeanLocal.createNewAccount(newAccountEntity);
            AccountEntity createdAccount = accountSessionBeanLocal.retrieveAccountByAccountId(accId);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New account " + createdAccount.getUsername() + " created successfully", null));
        } catch (AccountNotFoundException | InputDataValidationException | AccountUsernameExistException |  UnknownPersistenceException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occured while creating the new account: " + ex.getMessage(), null));
        }
    }

    public AccountEntity getNewAccountEntity() {
        return newAccountEntity;
    }

    public void setNewAccountEntity(AccountEntity newAccountEntity) {
        this.newAccountEntity = newAccountEntity;
    }
}
