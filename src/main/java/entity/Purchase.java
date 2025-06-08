package entity;

import java.util.Date;

import javafx.beans.property.*;
import util.DateUtil;

public class Purchase {
 private final StringProperty orderID = new SimpleStringProperty();
 private final StringProperty productID = new SimpleStringProperty();
 private final IntegerProperty quantity = new SimpleIntegerProperty();
 private final DoubleProperty cost = new SimpleDoubleProperty();
 private final ObjectProperty<Date> purchaseTime = new SimpleObjectProperty<>();
 private final StringProperty purchaser = new SimpleStringProperty();
 private final StringProperty supplier = new SimpleStringProperty();

    public Purchase() {
    }

    public Purchase(String orderID, String productID, Integer quantity, Double cost, Date purchaseTime, String purchaser, String supplier) {
        setOrderId(orderID);
        setProductId(productID);
        setQuantity(quantity);
        setCost(cost);
        setPurchaseTime(purchaseTime);
        setPurchaser(purchaser);
        setSupplier(supplier);
    }

    public StringProperty orderIdProperty() { return orderID; }
    public String getOrderID() { return orderID.get(); }
    public void setOrderId(String value) { orderID.set(value); }

    public StringProperty productIdProperty() { return productID; }
    public String getProductID() { return productID.get(); }
    public void setProductId(String value) { productID.set(value); }

    public IntegerProperty quantityProperty() { return quantity; }
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }

    public DoubleProperty costProperty() { return cost; }
    public double getCost() { return cost.get(); }
    public void setCost(double value) { cost.set(value); }

    public ObjectProperty<Date> purchaseTimeProperty() { return purchaseTime; }
    public Date getPurchaseTime() { return purchaseTime.get(); }
    public void setPurchaseTime(Date value) { purchaseTime.set(value); }

    public StringProperty purchaserProperty() { return purchaser; }
    public String getPurchaser() { return purchaser.get(); }
    public void setPurchaser(String value) { purchaser.set(value); }

    public StringProperty supplierProperty() { return supplier; }
    public String getSupplier() { return supplier.get(); }
    public void setSupplier(String value) { supplier.set(value); }

    @Override
    public String toString() {
        return String.format("%s,%s,%d,%.2f,%s,%s,%s",
                getOrderID(), getProductID(), getQuantity(), getCost(),
                DateUtil.format(getPurchaseTime()), getPurchaser(), getSupplier());
    }
}



