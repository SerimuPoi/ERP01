package service;

import entity.Product;
import entity.Purchase;
import entity.Sale;
import entity.Saleitem;
import util.DateUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class FileStorage {
    private static final String DATA_DIR = "data/";
    private static final String PRODUCT_FILE = DATA_DIR + "products.csv";
    private static final String PURCHASE_FILE = DATA_DIR + "purchases.csv";
    private static final String SALE_FILE = DATA_DIR + "sales.csv";
    private static final String SALE_ITEM_FILE = DATA_DIR + "sale_items.csv";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("无法创建数据目录: " + e.getMessage());
        }
    }

    // 商品数据操作
    public List<Product> loadProducts() {
        return loadFromFile(PRODUCT_FILE, line -> {
            String[] parts = line.split(",");
            if (parts.length < 6) return null;
            return new Product(
                    parts[0], parts[1], Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]), Integer.parseInt(parts[4]), parts[5]
            );
        });
    }

    public void saveProducts(List<Product> Products) {
        saveToFile(PRODUCT_FILE, Products, Product::toString);
    }

    // 采购数据操作
    public List<Purchase> loadPurchases() {
        return loadFromFile(PURCHASE_FILE, line -> {
            String[] parts = line.split(",");
            if (parts.length < 7) return null;
            try {
                return new Purchase(
                        parts[0], parts[1], Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3]), DateUtil.parse(parts[4]),
                        parts[5], parts[6]
                );
            } catch (ParseException e) {
                System.err.println("日期解析错误: " + e.getMessage());
                return null;
            }
        });
    }

    public void savePurchases(List<Purchase> Purchases) {
        saveToFile(PURCHASE_FILE, Purchases, Purchase::toString);
    }

    // 销售数据操作
    public List<Sale> loadSales() {
        List<Sale> Sales = new ArrayList<>();
        Map<String, List<Saleitem>> saleItemsMap = loadSaleItems();

        List<String> saleLines = loadLines(SALE_FILE);
        for (String line : saleLines) {
            String[] parts = line.split(",");
            if (parts.length < 5) continue;

            try {
                String orderId = parts[0];
                List<Saleitem> items = saleItemsMap.getOrDefault(orderId, new ArrayList<>());

                Sale sale = new Sale(
                        orderId, parts[1], parts[2],
                        Double.parseDouble(parts[3]),
                        DateUtil.parse(parts[4]), items
                );
                Sales.add(sale);
            } catch (ParseException e) {
                System.err.println("日期解析错误: " + e.getMessage());
            }
        }
        return Sales;
    }

    public void saveSales(List<Sale> Sales) {
        List<String> saleLines = new ArrayList<>();
        List<String> saleItemLines = new ArrayList<>();

        for (Sale sale : Sales) {
            saleLines.add(String.format("%s,%s,%s,%.2f,%s",
                    sale.getOrderId(), sale.getCustomer(), sale.getSeller(),
                    sale.getTotalAmount(), DateUtil.format(sale.getSaleTime())));

            for (Saleitem item : sale.getItems()) {
                saleItemLines.add(String.format("%s,%s", sale.getOrderId(), item.toString()));
            }
        }

        saveLines(SALE_FILE, saleLines);
        saveLines(SALE_ITEM_FILE, saleItemLines);
    }

    private Map<String, List<Saleitem>> loadSaleItems() {
        Map<String, List<Saleitem>> map = new HashMap<>();

        loadLines(SALE_ITEM_FILE).forEach(line -> {
            String[] parts = line.split(",", 2);
            if (parts.length < 2) return;

            String[] itemParts = parts[1].split(",");
            if (itemParts.length < 4) return;

            Saleitem item = new Saleitem(
                    itemParts[0], itemParts[1],
                    Double.parseDouble(itemParts[2]), Integer.parseInt(itemParts[3])
            );

            map.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(item);
        });

        return map;
    }

    // 通用文件操作方法
    private <T> List<T> loadFromFile(String filename, LineParser<T> parser) {
        return loadLines(filename).stream()
                .map(parser::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> loadLines(String filename) {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) return new ArrayList<>();

        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("读取文件错误: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private <T> void saveToFile(String filename, List<T> items, ToStringFunction<T> toStringFunction) {
        List<String> lines = items.stream()
                .map(toStringFunction::toString)
                .collect(Collectors.toList());
        saveLines(filename, lines);
    }

    private void saveLines(String filename, List<String> lines) {
        try {
            Files.write(Paths.get(filename), lines);
        } catch (IOException e) {
            System.err.println("保存文件错误: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface LineParser<T> {
        T parse(String line);
    }

    @FunctionalInterface
    private interface ToStringFunction<T> {
        String toString(T item);
    }
}