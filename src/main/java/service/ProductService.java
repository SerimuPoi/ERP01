package service;

import entity.Product;
import entity.Purchase;
import entity.Sale;
import entity.Saleitem;

import java.util.*;
import java.util.stream.Collectors;

public class ProductService {
    private final FileStorage storage = new FileStorage();
    private List<Product> Products;
    private List<Purchase> Purchases;
    private List<Sale> Sales;

    public ProductService() {
        loadData();
    }

    private void loadData() {
        Products = storage.loadProducts();
        Purchases = storage.loadPurchases();
        Sales = storage.loadSales();
    }

    private void saveData() {
        storage.saveProducts(Products);
        storage.savePurchases(Purchases);
        storage.saveSales(Sales);
    }

    // 商品操作
    public List<Product> listproducts() {
        return new ArrayList<>(Products);
    }

    public Product getproductById(String id) {
        return Products.stream()
                .filter(p -> p.getID().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean addproduct(Product product) {
        if (getproductById(product.getID()) != null) {
            return false; // ID重复
        }
        Products.add(product);
        saveData();
        return true;
    }

    public boolean updateproduct(Product updatedproduct) {
        for (int i = 0; i < Products.size(); i++) {
            Product p = Products.get(i);
            if (p.getID().equals(updatedproduct.getID())) {
                // 只能修改名称、售价、成本价和备注
                p.setName(updatedproduct.getName());
                p.setSalePrice(updatedproduct.getSalePrice());
                p.setCostPrice(updatedproduct.getCostPrice());
                p.setRemark(updatedproduct.getRemark());
                saveData();
                return true;
            }
        }
        return false;
    }

    public boolean deleteproduct(String id) {
        Product product = getproductById(id);
        if (product == null || product.getStock() != 0) {
            return false; // 库存不为0不能删除
        }
        Products.removeIf(p -> p.getID().equals(id));
        saveData();
        return true;
    }

    // 采购操作
    public List<Purchase> listpurchases() {
        return new ArrayList<>(Purchases);
    }

    public List<Purchase> getpurchasesByproduct(String productId) {
        return Purchases.stream()
                .filter(p -> p.getProductID().equals(productId))
                .collect(Collectors.toList());
    }

    public Purchase getpurchaseById(String orderId) {
        return Purchases.stream()
                .filter(p -> p.getOrderID().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public boolean addpurchase(Purchase purchase) {
        if (getpurchaseById(purchase.getOrderID()) != null) {
            return false; // 订单号重复
        }

        Product product = getproductById(purchase.getProductID());
        if (product == null) {
            return false; // 商品不存在
        }

        // 更新库存
        product.setStock(product.getStock() + purchase.getQuantity());
        Purchases.add(purchase);
        saveData();
        return true;
    }

    public boolean updatepurchase(Purchase updatedpurchase) {
        for (int i = 0; i < Purchases.size(); i++) {
            Purchase p = Purchases.get(i);
            if (p.getOrderID().equals(updatedpurchase.getOrderID())) {
                // 还原原采购数量的库存
                Product originalproduct = getproductById(p.getProductID());
                if (originalproduct != null) {
                    originalproduct.setStock(originalproduct.getStock() - p.getQuantity());
                }

                // 应用新采购数量的库存
                Product newproduct = getproductById(updatedpurchase.getProductID());
                if (newproduct == null) {
                    return false; // 新商品不存在
                }
                newproduct.setStock(newproduct.getStock() + updatedpurchase.getQuantity());

                // 更新采购记录
                p.setProductId(updatedpurchase.getProductID());
                p.setQuantity(updatedpurchase.getQuantity());
                p.setCost(updatedpurchase.getCost());
                p.setPurchaseTime(updatedpurchase.getPurchaseTime());
                p.setPurchaser(updatedpurchase.getPurchaser());
                p.setSupplier(updatedpurchase.getSupplier());

                saveData();
                return true;
            }
        }
        return false;
    }

    public boolean deletepurchase(String orderId) {
        Purchase purchase = getpurchaseById(orderId);
        if (purchase == null) {
            return false;
        }

        // 还原库存
        Product product = getproductById(purchase.getProductID());
        if (product != null) {
            product.setStock(product.getStock() - purchase.getQuantity());
        }

        Purchases.removeIf(p -> p.getOrderID().equals(orderId));
        saveData();
        return true;
    }

    // 销售操作
    public List<Sale> listsales() {
        return new ArrayList<>(Sales);
    }

    public List<Sale> getsalesByproduct(String productId) {
        return Sales.stream()
                .filter(Sale -> Sale.getItems().stream()
                        .anyMatch(item -> item.getProductId().equals(productId)))
                .collect(Collectors.toList());
    }

    public Sale getsaleById(String orderId) {
        return Sales.stream()
                .filter(s -> s.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public boolean addsale(Sale sale) {
        if (getsaleById(sale.getOrderId()) != null) {
            return false; // 订单号重复
        }

        // 验证库存和计算总金额
        double total = 0;
        for (Saleitem item : sale.getItems()) {
            Product product = getproductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                return false; // 商品不存在或库存不足
            }
            total += item.getPrice() * item.getQuantity();
        }
        sale.setTotalAmount(total);

        // 更新库存
        for (Saleitem item : sale.getItems()) {
            Product product = getproductById(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
        }

        Sales.add(sale);
        saveData();
        return true;
    }

    public boolean updatesale(Sale updatedsale) {
        Sale originalsale = getsaleById(updatedsale.getOrderId());
        if (originalsale == null) {
            return false;
        }

        // 还原原销售记录的库存
        for (Saleitem item : originalsale.getItems()) {
            Product product = getproductById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        // 验证新销售记录的库存
        double total = 0;
        for (Saleitem item : updatedsale.getItems()) {
            Product product = getproductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                return false; // 商品不存在或库存不足
            }
            total += item.getPrice() * item.getQuantity();
        }
        updatedsale.setTotalAmount(total);

        // 更新库存
        for (Saleitem item : updatedsale.getItems()) {
            Product product = getproductById(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
        }

        // 更新销售记录
        originalsale.setCustomer(updatedsale.getCustomer());
        originalsale.setSeller(updatedsale.getSeller());
        originalsale.setSaleTime(updatedsale.getSaleTime());
        originalsale.setItems(new ArrayList<>(updatedsale.getItems()));
        originalsale.setTotalAmount(updatedsale.getTotalAmount());

        saveData();
        return true;
    }

    public boolean deletesale(String orderId) {
        Sale sale = getsaleById(orderId);
        if (sale == null) {
            return false;
        }

        // 还原库存
        for (Saleitem item : sale.getItems()) {
            Product product = getproductById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        Sales.removeIf(s -> s.getOrderId().equals(orderId));
        saveData();
        return true;
    }
}