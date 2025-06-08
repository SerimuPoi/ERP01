package ui;

import entity.Sale;
import entity.Saleitem; // 确保正确导入
import entity.Product;
import service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Salecontroller {
    @FXML private TableView<Sale> saleTable;
    @FXML private TableColumn<Sale, String> orderIdColumn;
    @FXML private TableColumn<Sale, String> customerColumn;
    @FXML private TableColumn<Sale, String> sellerColumn;
    @FXML private TableColumn<Sale, Double> totalAmountColumn;
    @FXML private TableColumn<Sale, Date> saleTimeColumn; // 修改为Date类型

    @FXML private TextField searchField;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;

    private final ProductService service = new ProductService();
    private final ObservableList<Sale> saleList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 初始化表格列
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("seller"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        // 自定义日期列格式
        saleTimeColumn.setCellFactory(column -> new TableCell<Sale, Date>() {
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

        // 设置表格数据源
        saleTable.setItems(saleList);

        // 设置按钮事件
        addButton.setOnAction(e -> addSale());
        editButton.setOnAction(e -> editSale());
        deleteButton.setOnAction(e -> deleteSale());
        refreshButton.setOnAction(e -> refreshData());

        // 设置搜索功能
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterSales(newVal));
    }

    private void refreshData() {
        saleList.setAll(service.listsales());
        saleTable.refresh();
    }

    private void filterSales(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            saleTable.setItems(saleList);
            return;
        }

        ObservableList<Sale> filteredList = FXCollections.observableArrayList();
        String lowerKeyword = keyword.toLowerCase();

        for (Sale sale : saleList) {
            if (sale.getOrderId().toLowerCase().contains(lowerKeyword) ||
                    sale.getCustomer().toLowerCase().contains(lowerKeyword) ||
                    sale.getSeller().toLowerCase().contains(lowerKeyword)) {
                filteredList.add(sale);
            }
        }

        saleTable.setItems(filteredList);
    }

    private void addSale() {
        // 第一步：输入订单基本信息
        TextInputDialog baseDialog = new TextInputDialog();
        baseDialog.setTitle("添加销售记录");
        baseDialog.setHeaderText("输入基本信息（格式：订单号,客户,销售人）");
        baseDialog.setContentText("例如: SO1001,ABC公司,李四");

        Optional<String> baseResult = baseDialog.showAndWait();
        if (!baseResult.isPresent()) return;

        try {
            String input = baseResult.get();
            String[] baseParts = input.split(",");
            if (baseParts.length < 3) {
                showAlert("输入错误", "请输入完整信息（3个字段）");
                return;
            }

            // 第二步：输入商品明细
            TextInputDialog itemsDialog = new TextInputDialog();
            itemsDialog.setTitle("添加销售商品");
            itemsDialog.setHeaderText("输入商品明细（每行一个商品，格式：商品ID,数量）");
            itemsDialog.setContentText("例如:\nP001,2\nP002,1");

            Optional<String> itemsResult = itemsDialog.showAndWait();
            if (!itemsResult.isPresent()) return;

            List<Saleitem> items = parseSaleItems(itemsResult.get());
            if (items.isEmpty()) {
                showAlert("错误", "未添加任何商品");
                return;
            }

            // 创建销售订单
            Sale sale = new Sale();
            sale.setOrderId(baseParts[0].trim());
            sale.setCustomer(baseParts[1].trim());
            sale.setSeller(baseParts[2].trim());
            sale.setSaleTime(new Date());
            sale.setItems(items); // 使用正确的setItems方法

            // 计算总金额
            double total = calculateTotalAmount(items);
            sale.setTotalAmount(total);

            if (service.addsale(sale)) {
                saleList.add(sale);
                showAlert("成功", "销售记录已添加");
            } else {
                showAlert("错误", "添加销售记录失败，订单号可能已存在或库存不足");
            }
        } catch (NumberFormatException e) {
            showAlert("输入错误", "数量必须是数字");
        } catch (Exception e) {
            showAlert("错误", "添加销售记录失败: " + e.getMessage());
        }
    }

    private double calculateTotalAmount(List<Saleitem> items) {
        double total = 0;
        for (Saleitem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private List<Saleitem> parseSaleItems(String input) {
        List<Saleitem> items = new ArrayList<>();
        String[] lines = input.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) {
                showAlert("输入错误", "商品行格式错误: " + line);
                continue;
            }

            try {
                String productId = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());

                // 检查商品是否存在
                Product product = service.getproductById(productId);
                if (product == null) {
                    showAlert("错误", "商品不存在: " + productId);
                    continue;
                }

                // 检查库存是否足够
                if (product.getStock() < quantity) {
                    showAlert("库存不足", "商品 " + product.getName() + " 库存不足，当前库存: " + product.getStock());
                    continue;
                }

                // 使用正确的SaleItem构造函数
                items.add(new Saleitem(productId, product.getName(), product.getSalePrice(), quantity));
            } catch (NumberFormatException e) {
                showAlert("输入错误", "数量必须是数字: " + line);
            }
        }

        return items;
    }

    private void editSale() {
        Sale selected = saleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("警告", "请先选择一条销售记录");
            return;
        }

        // 第一步：编辑基本信息
        TextInputDialog baseDialog = new TextInputDialog(
                selected.getCustomer() + "," + selected.getSeller()
        );
        baseDialog.setTitle("编辑销售记录");
        baseDialog.setHeaderText("输入更新信息（格式：客户,销售人）");
        baseDialog.setContentText("例如: XYZ公司,王五");

        Optional<String> baseResult = baseDialog.showAndWait();
        if (!baseResult.isPresent()) return;

        try {
            String input = baseResult.get();
            String[] baseParts = input.split(",");
            if (baseParts.length < 2) {
                showAlert("输入错误", "请输入完整信息（2个字段）");
                return;
            }

            // 第二步：编辑商品明细
            TextInputDialog itemsDialog = new TextInputDialog(formatItems(selected.getItems()));
            itemsDialog.setTitle("编辑销售商品");
            itemsDialog.setHeaderText("输入商品明细（每行一个商品，格式：商品ID,数量）");
            itemsDialog.setContentText("例如:\nP001,2\nP002,1");

            Optional<String> itemsResult = itemsDialog.showAndWait();
            if (!itemsResult.isPresent()) return;

            List<Saleitem> items = parseSaleItems(itemsResult.get());
            if (items.isEmpty()) {
                showAlert("错误", "未添加任何商品");
                return;
            }

            // 更新销售订单
            Sale updated = new Sale();
            updated.setOrderId(selected.getOrderId()); // 订单号不变
            updated.setCustomer(baseParts[0].trim());
            updated.setSeller(baseParts[1].trim());
            updated.setSaleTime(selected.getSaleTime()); // 使用原时间
            updated.setItems(items); // 使用正确的setItems方法

            // 计算总金额
            double total = calculateTotalAmount(items);
            updated.setTotalAmount(total);

            if (service.updatesale(updated)) {
                // 更新UI
                selected.setCustomer(updated.getCustomer());
                selected.setSeller(updated.getSeller());
                selected.setTotalAmount(updated.getTotalAmount());
                selected.setItems(updated.getItems());

                saleTable.refresh();
                showAlert("成功", "销售记录已更新");
            } else {
                showAlert("错误", "更新销售记录失败，可能库存不足");
            }
        } catch (NumberFormatException e) {
            showAlert("输入错误", "数量必须是数字");
        } catch (Exception e) {
            showAlert("错误", "更新销售记录失败: " + e.getMessage());
        }
    }

    private String formatItems(List<Saleitem> items) {
        return items.stream()
                .map(item -> item.getProductId() + "," + item.getQuantity())
                .collect(Collectors.joining("\n"));
    }

    private void deleteSale() {
        Sale selected = saleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("警告", "请先选择一条销售记录");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认删除");
        confirm.setHeaderText("删除销售订单: " + selected.getOrderId());
        confirm.setContentText("确定要删除此销售记录吗？");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (service.deletesale(selected.getOrderId())) {
                saleList.remove(selected);
                showAlert("成功", "销售记录已删除");
            } else {
                showAlert("错误", "删除销售记录失败");
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