package entity;

import javafx.beans.property.*;

public class Product {
    private final StringProperty ID = new SimpleStringProperty();
    private final StringProperty Name = new SimpleStringProperty();
    private final DoubleProperty salePrice = new SimpleDoubleProperty();
    private final DoubleProperty costPrice = new SimpleDoubleProperty();
    private final IntegerProperty stock = new SimpleIntegerProperty();
    private final StringProperty remark = new SimpleStringProperty();

    public Product() {
    }

    public Product(String ID, String Name, double salePrice, double costPrice, int stock, String remark) {
        setID(ID);
        setName(Name);
        setSalePrice(salePrice);
        setCostPrice(costPrice);
        setStock(stock);
        setRemark(remark);
    }

    public StringProperty IDProperty() { return ID; }
    public String getID() { return ID.get(); }
    public void setID(String value) { ID.set(value); }

    public StringProperty nameProperty() { return Name; }
    public String getName() { return Name.get(); }
    public void setName(String value) { Name.set(value); }

    public DoubleProperty salePriceProperty() { return salePrice; }
    public double getSalePrice() { return salePrice.get(); }
    public void setSalePrice(double value) { salePrice.set(value); }

    public DoubleProperty costPriceProperty() { return costPrice; }
    public double getCostPrice() { return costPrice.get(); }
    public void setCostPrice(double value) { costPrice.set(value); }

    public IntegerProperty stockProperty() { return stock; }
    public int getStock() { return stock.get(); }
    public void setStock(int value) { stock.set(value); }

    public StringProperty remarkProperty() { return remark; }
    public String getRemark() { return remark.get(); }
    public void setRemark(String value) { remark.set(value); }

    @Override
    public String toString() {
        return String.format("%s,%s,%.2f,%.2f,%d,%s",
                getID(), getName(), getSalePrice(),
                getCostPrice(), getStock(), getRemark());
    }

}
