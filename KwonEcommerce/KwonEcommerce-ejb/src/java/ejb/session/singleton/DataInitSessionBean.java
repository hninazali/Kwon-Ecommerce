/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import entity.AccountEntity;
import entity.StaffEntity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.exception.AccountNotFoundException;
import util.exception.AccountUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author winyfebriny
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;
    
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;

    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        try {
            accountSessionBeanLocal.retrieveAccountByUsername("account");
        } catch (AccountNotFoundException ex) {
            initializeData();
        }
    }

    private void initializeData() {
        try {
            accountSessionBeanLocal.createNewAccount(new AccountEntity("Default", "Account", AccessRightEnum.MANAGER, "account", "password", "account@gmail.com"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Account", AccessRightEnum.MANAGER, "manager", "password"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Staff One", AccessRightEnum.WAREHOUSESTAFF, "staff1", "password"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Staff Two", AccessRightEnum.WAREHOUSESTAFF, "staff2", "password"));

        } catch (AccountUsernameExistException | StaffUsernameExistException | UnknownPersistenceException | InputDataValidationException ex) {
            ex.printStackTrace();
        }
    }
}
