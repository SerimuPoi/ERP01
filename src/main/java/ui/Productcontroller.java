package ui;

import entity.Product;
import service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Productcontroller {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, Double> salePriceColumn;
    @FXML private TableColumn<Product, Double> costPriceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, String> remarkColumn;

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;

    private final ProductService service = new ProductService();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 初始化表格列
        idColumn.setCellValueFactory(cellData -> cellData.getValue().IDProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        salePriceColumn.setCellValueFactory(cellData -> cellData.getValue().salePriceProperty().asObject());
        costPriceColumn.setCellValueFactory(cellData -> cellData.getValue().costPriceProperty().asObject());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        remarkColumn.setCellValueFactory(cellData -> cellData.getValue().remarkProperty());

        // 加载数据
        refreshData();

        // 设置表格数据源
        productTable.setItems(productList);

        // 设置按钮事件
        addButton.setOnAction(e -> addProduct());
        editButton.setOnAction(e -> editProduct());
        deleteButton.setOnAction(e -> deleteProduct());
        refreshButton.setOnAction(e -> refreshData());

        // 设置搜索功能
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterProducts(newVal));
    }

    private void refreshData() {
        productList.setAll(service.listproducts());
    }

    private void filterProducts(String keyword) {
        // 实现搜索过滤
    }

    private void addProduct() {
        // 添加商品逻辑
    }

    private void editProduct() {
        // 编辑商品逻辑
    }

    private void deleteProduct() {
        // 删除商品逻辑
    }
}