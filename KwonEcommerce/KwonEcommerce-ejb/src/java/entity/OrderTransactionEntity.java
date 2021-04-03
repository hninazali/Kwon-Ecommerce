package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import util.enumeration.ShippingStatusEnum;
import util.exception.EntityInstanceExistsInCollectionException;
import util.exception.EntityInstanceMissingInCollectionException;



@Entity

public class OrderTransactionEntity implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderTransactionId;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer totalLineItem;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer totalQuantity;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2) // 11 - 2 digits to the left of the decimal point
    private BigDecimal totalAmount;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date transactionDateTime;    
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderLineItemEntity> orderLineItemEntities;    
    @Column(nullable = false)
    @NotNull
    private Boolean voidRefund;
    
    @Column(nullable = false)
    @NotNull
    private ShippingStatusEnum shippingStatus;
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private StaffEntity staffEntity;
    
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    private CustomerEntity customerEntity;

    
    
    public OrderTransactionEntity()
    {
        orderLineItemEntities = new ArrayList<>();
        voidRefund = false;
        this.shippingStatus = ShippingStatusEnum.NOT_SHIPPED;
    }
    
    
    
    public OrderTransactionEntity(Integer totalLineItem, Integer totalQuantity, BigDecimal totalAmount, Date transactionDateTime, Boolean voidRefund)
    {
        this();
        
        this.totalLineItem = totalLineItem;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.transactionDateTime = transactionDateTime;
        this.voidRefund = voidRefund;
        this.shippingStatus = ShippingStatusEnum.NOT_SHIPPED;
    }

    
    
    public OrderTransactionEntity(Integer totalLineItem, Integer totalQuantity, BigDecimal totalAmount, Date transactionDateTime, List<OrderLineItemEntity> orderLineItemEntities, Boolean voidRefund)
    {
        this.totalLineItem = totalLineItem;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.transactionDateTime = transactionDateTime;
        this.orderLineItemEntities = orderLineItemEntities;        
        this.voidRefund = voidRefund; 
        this.shippingStatus = ShippingStatusEnum.NOT_SHIPPED;
    }
    
    public OrderTransactionEntity(Integer totalLineItem, Integer totalQuantity, BigDecimal totalAmount, Date transactionDateTime, List<OrderLineItemEntity> orderLineItemEntities, Boolean voidRefund, ShippingStatusEnum shipStatus)
    {
        this.totalLineItem = totalLineItem;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.transactionDateTime = transactionDateTime;
        this.orderLineItemEntities = orderLineItemEntities;        
        this.voidRefund = voidRefund; 
        this.shippingStatus = shipStatus;
    }

    
    
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (this.orderTransactionId != null ? this.orderTransactionId.hashCode() : 0);
        
        return hash;
    }

    
    
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof OrderTransactionEntity)) 
        {
            return false;
        }
        
        OrderTransactionEntity other = (OrderTransactionEntity) object;
        
        if ((this.orderTransactionId == null && other.orderTransactionId != null) || (this.orderTransactionId != null && !this.orderTransactionId.equals(other.orderTransactionId))) 
        {
            return false;
        }
        
        return true;
    }

    
    
    @Override
    public String toString() 
    {
        return "entity.OrderTransactionEntity[ orderTransactionId=" + this.orderTransactionId + " ]";
    }
    
    
    
    public void addOrderLineItemEntity(OrderLineItemEntity orderTransactionLineItemEntity) throws EntityInstanceExistsInCollectionException
    {
        if(!this.orderLineItemEntities.contains(orderTransactionLineItemEntity))
        {
            this.orderLineItemEntities.add(orderTransactionLineItemEntity);
        }
        else
        {
            throw new EntityInstanceExistsInCollectionException("Sale Transaction Line Item already exist");
        }
    }
    
    
    
    public void removeOrderLineItemEntity(OrderLineItemEntity orderTransactionLineItemEntity) throws EntityInstanceMissingInCollectionException
    {
        if(this.orderLineItemEntities.contains(orderTransactionLineItemEntity))
        {
            this.orderLineItemEntities.remove(orderTransactionLineItemEntity);
        }
        else
        {
            throw new EntityInstanceMissingInCollectionException("Sale Transaction Line Item missing");
        }
    }
    
    
    
    public Long getOrderTransactionId() {
        return orderTransactionId;
    }

    public void setOrderTransactionId(Long orderTransactionId) {
        this.orderTransactionId = orderTransactionId;
    }

    public Integer getTotalLineItem() {
        return totalLineItem;
    }

    public void setTotalLineItem(Integer totalLineItem) {
        this.totalLineItem = totalLineItem;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(Date transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public List<OrderLineItemEntity> getOrderLineItemEntities() {
        return orderLineItemEntities;
    }

    public void setOrderLineItemEntities(List<OrderLineItemEntity> orderLineItemEntities) {
        this.orderLineItemEntities = orderLineItemEntities;
    }    

    public Boolean getVoidRefund() {
        return voidRefund;
    }

    public void setVoidRefund(Boolean voidRefund) {
        this.voidRefund = voidRefund;
    }
    
    public StaffEntity getStaffEntity() {
        return staffEntity;
    }

    public void setStaffEntity(StaffEntity staffEntity) 
    {
        if(this.staffEntity != null)
        {
            this.staffEntity.getOrderTransactionEntities().remove(this);
        }
        
        this.staffEntity = staffEntity;
        
        if(this.staffEntity != null)
        {
            if(!this.staffEntity.getOrderTransactionEntities().contains(this))
            {
                this.staffEntity.getOrderTransactionEntities().add(this);
            }
        }
    }

    public ShippingStatusEnum getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(ShippingStatusEnum shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }
}