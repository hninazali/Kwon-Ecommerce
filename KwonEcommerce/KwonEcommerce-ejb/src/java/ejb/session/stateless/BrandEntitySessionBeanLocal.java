package ejb.session.stateless;

import entity.BrandEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.BrandNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.DeleteBrandException;
import util.exception.InputDataValidationException;
import util.exception.UpdateBrandException;

@Local
public interface BrandEntitySessionBeanLocal {

    public BrandEntity createNewBrandEntity(BrandEntity newBrandEntity) throws InputDataValidationException, CreateNewBrandException;

    public List<BrandEntity> retrieveAllBrands();

    public BrandEntity retrieveBrandByBrandId(Long brandId) throws BrandNotFoundException;

    public void updateBrand(BrandEntity brandEntity) throws InputDataValidationException, BrandNotFoundException, UpdateBrandException;

    public void deleteBrand(Long brandId) throws BrandNotFoundException, DeleteBrandException;
    
}
