package jsf.managedbean;

import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import util.enumeration.AccessRightEnum;



@Named(value = "utilManagedBean")
@ApplicationScoped

public class UtilManagedBean 
{    
    public UtilManagedBean() 
    {
    }



    public AccessRightEnum[] getAccessRightEnumValues()
    {
        return AccessRightEnum.values();
    }
}
