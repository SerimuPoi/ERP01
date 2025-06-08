package entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DateUtil;

import java.util.Date;
import java.util.List;

public class Sale {
    private final StringProperty orderID = new SimpleStringProperty();
    private final StringProperty customer = new SimpleStringProperty();
    private final StringProperty seller = new SimpleStringProperty();
    private final DoubleProperty totalAmount = new SimpleDoubleProperty();
    private final ObjectProperty<Date> saleTime = new SimpleObjectProperty<>();
    private final ListProperty<Saleitem> Saleitems = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Sale() {}

    public Sale(String orderID, String customer, String seller,
                double totalAmount, Date saleTime, List<Saleitem> items) {
        setOrderId(orderID);
        setCustomer(customer);
        setSeller(seller);
        setTotalAmount(totalAmount);
        setSaleTime(saleTime);
        getItems().addAll(items);
    }

    // Getters and Setters for properties
    public StringProperty orderIdProperty() { return orderID; }
    public String getOrderId() { return orderID.get(); }
    public void setOrderId(String value) { orderID.set(value); }

    public StringProperty customerProperty() { return customer; }
    public String getCustomer() { return customer.get(); }
    public void setCustomer(String value) { customer.set(value); }

    public StringProperty sellerProperty() { return seller; }
    public String getSeller() { return seller.get(); }
    public void setSeller(String value) { seller.set(value); }

    public DoubleProperty totalAmountProperty() { return totalAmount; }
    public double getTotalAmount() { return totalAmount.get(); }
    public void setTotalAmount(double value) { totalAmount.set(value); }

    public ObjectProperty<Date> saleTimeProperty() { return saleTime; }
    public Date getSaleTime() { return saleTime.get(); }
    public void setSaleTime(Date value) { saleTime.set(value); }

    public ListProperty<Saleitem> itemsProperty() { return Saleitems; }
    public ObservableList<Saleitem> getItems() { return Saleitems.get(); }
    public void setItems(List<Saleitem> value) {
        Saleitems.set(FXCollections.observableArrayList(value));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s,%s,%s,%.2f,%s\n",
                getOrderId(), getCustomer(), getSeller(), getTotalAmount(),
                DateUtil.format(getSaleTime())));
        for (Saleitem item : getItems()) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString().trim();
    }
}