package entity;

import javafx.beans.property.*;

public class Saleitem {
    private final StringProperty productID = new SimpleStringProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();

    public Saleitem() {}

    public Saleitem(String productID, String productName, double price, int quantity) {
        setProductId(productID);
        setProductName(productName);
        setPrice(price);
        setQuantity(quantity);
    }

    // Getters and Setters
    public StringProperty productIdProperty() { return productID; }
    public String getProductId() { return productID.get(); }
    public void setProductId(String value) { productID.set(value); }

    public StringProperty productNameProperty() { return productName; }
    public String getProductName() { return productName.get(); }
    public void setProductName(String value) { productName.set(value); }

    public DoubleProperty priceProperty() { return price; }
    public double getPrice() { return price.get(); }
    public void setPrice(double value) { price.set(value); }

    public IntegerProperty quantityProperty() { return quantity; }
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }

    public double getTotal() {
        return getPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%.2f,%d",
                getProductId(), getProductName(), getPrice(), getQuantity());
    }
}