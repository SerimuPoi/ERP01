package ui;

import entity.Purchase;
import service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Purchasecontroller {
    @FXML private TableView<Purchase> purchaseTable;
    @FXML private TableColumn<Purchase, String> orderIdColumn;
    @FXML private TableColumn<Purchase, String> productIdColumn;
    @FXML private TableColumn<Purchase, Integer> quantityColumn;
    @FXML private TableColumn<Purchase, Double> costColumn;
    @FXML private TableColumn<Purchase, Date> purchaseTimeColumn; // 修改为Date类型
    @FXML private TableColumn<Purchase, String> purchaserColumn;
    @FXML private TableColumn<Purchase, String> supplierColumn;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;

    private final ProductService service = new ProductService();
    private final ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 初始化表格列
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        purchaserColumn.setCellValueFactory(new PropertyValueFactory<>("purchaser"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        // 自定义日期列格式
        purchaseTimeColumn.setCellFactory(column -> new TableCell<Purchase, Date>() {
            private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        // 加载数据
        refreshData();

        // 设置按钮事件
        addButton.setOnAction(e -> addPurchase());
        editButton.setOnAction(e -> editPurchase());
        deleteButton.setOnAction(e -> deletePurchase());
        refreshButton.setOnAction(e -> refreshData());

        // 设置搜索功能
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterPurchases(newVal));
    }

    private void refreshData() {
        purchaseList.setAll(service.listpurchases());
        purchaseTable.refresh();
    }

    private void filterPurchases(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            purchaseTable.setItems(purchaseList);
            return;
        }

        ObservableList<Purchase> filteredList = FXCollections.observableArrayList();
        String lowerKeyword = keyword.toLowerCase();

        for (Purchase purchase : purchaseList) {
            if (purchase.getOrderID().toLowerCase().contains(lowerKeyword) ||
                    purchase.getProductID().toLowerCase().contains(lowerKeyword) ||
                    purchase.getPurchaser().toLowerCase().contains(lowerKeyword) ||
                    purchase.getSupplier().toLowerCase().contains(lowerKeyword)) {
                filteredList.add(purchase);
            }
        }

        purchaseTable.setItems(filteredList);
    }

    private void addPurchase() {
        // 创建简单的输入对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加采购记录");
        dialog.setHeaderText("输入采购信息（格式：订单号,商品ID,数量,成本,采购人,供应商）");
        dialog.setContentText("例如: PO1001,P001,10,45.5,张三,联想供应商");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String input = result.get();
                String[] parts = input.split(",");
                if (parts.length < 6) {
                    showAlert("输入错误", "请输入完整信息（6个字段）");
                    return;
                }

                Purchase purchase = new Purchase();
                purchase.setOrderId(parts[0].trim());
                purchase.setProductId(parts[1].trim());
                purchase.setQuantity(Integer.parseInt(parts[2].trim()));
                purchase.setCost(Double.parseDouble(parts[3].trim()));
                purchase.setPurchaseTime(new Date());
                purchase.setPurchaser(parts[4].trim());
                purchase.setSupplier(parts[5].trim());

                // 使用正确的服务方法
                if (service.addpurchase(purchase)) {
                    purchaseList.add(purchase);
                    showAlert("成功", "采购记录已添加");
                } else {
                    showAlert("错误", "添加采购记录失败，订单号可能已存在");
                }
            } catch (NumberFormatException e) {
                showAlert("输入错误", "数量和成本必须是数字");
            } catch (Exception e) {
                showAlert("错误", "添加采购记录失败: " + e.getMessage());
            }
        }
    }

    private void editPurchase() {
        Purchase selected = purchaseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("警告", "请先选择一条采购记录");
            return;
        }

        // 创建简单的输入对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("编辑采购记录");
        dialog.setHeaderText("输入更新信息（格式：商品ID,数量,成本,采购人,供应商）");
        dialog.setContentText("例如: P001,15,42.0,李四,戴尔供应商");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String input = result.get();
                String[] parts = input.split(",");
                if (parts.length < 5) {
                    showAlert("输入错误", "请输入完整信息（5个字段）");
                    return;
                }

                Purchase updated = new Purchase();
                updated.setOrderId(selected.getOrderID()); // 订单号不变
                updated.setProductId(parts[0].trim());
                updated.setQuantity(Integer.parseInt(parts[1].trim()));
                updated.setCost(Double.parseDouble(parts[2].trim()));
                updated.setPurchaseTime(selected.getPurchaseTime()); // 使用原时间
                updated.setPurchaser(parts[3].trim());
                updated.setSupplier(parts[4].trim());

                // 使用正确的服务方法
                if (service.updatepurchase(updated)) {
                    // 更新UI
                    selected.setProductId(updated.getProductID());
                    selected.setQuantity(updated.getQuantity());
                    selected.setCost(updated.getCost());
                    selected.setPurchaser(updated.getPurchaser());
                    selected.setSupplier(updated.getSupplier());

                    purchaseTable.refresh();
                    showAlert("成功", "采购记录已更新");
                } else {
                    showAlert("错误", "更新采购记录失败");
                }
            } catch (NumberFormatException e) {
                showAlert("输入错误", "数量和成本必须是数字");
            } catch (Exception e) {
                showAlert("错误", "更新采购记录失败: " + e.getMessage());
            }
        }
    }

    private void deletePurchase() {
        Purchase selected = purchaseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("警告", "请先选择一条采购记录");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认删除");
        confirm.setHeaderText("删除采购订单: " + selected.getOrderID());
        confirm.setContentText("确定要删除此采购记录吗？");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (service.deletepurchase(selected.getOrderID())) {
                purchaseList.remove(selected);
                showAlert("成功", "采购记录已删除");
            } else {
                showAlert("错误", "删除采购记录失败");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}