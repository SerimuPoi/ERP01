package console;

import entity.Product;
import entity.Purchase;
import entity.Sale;
import entity.Saleitem;
import service.ProductService;
import util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class Console {
    private final ProductService service = new ProductService();
    private final Scanner scanner = new Scanner(System.in);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public void start() {
        System.out.println("===== 进销存管理系统 =====");
        System.out.println("输入 'help' 查看可用命令");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if (command.isEmpty()) continue;

            String[] parts = command.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "product":
                    handleProductCommand(arg);
                    break;
                case "purchase":
                    handlePurchaseCommand(arg);
                    break;
                case "sale":
                    handleSaleCommand(arg);
                    break;
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.out.println("退出系统");
                    return;
                default:
                    System.out.println("未知命令: " + cmd);
            }
        }
    }

    private void handleProductCommand(String arg) {
        String[] parts = arg.split("\\s+", 2);
        String subCmd = parts[0].toLowerCase();
        String subArg = parts.length > 1 ? parts[1] : "";

        switch (subCmd) {
            case "list":
                listProducts();
                break;
            case "view":
                viewProduct(subArg);
                break;
            case "add":
                addProduct();
                break;
            case "edit":
                editProduct(subArg);
                break;
            case "delete":
                deleteProduct(subArg);
                break;
            default:
                System.out.println("未知商品命令: " + subCmd);
        }
    }

    private void listProducts() {
        List<Product> Products = service.listproducts();
        if (Products.isEmpty()) {
            System.out.println("没有商品记录");
            return;
        }

        System.out.println("商品列表:");
        System.out.println("ID\t名称\t售价\t成本\t库存\t备注");
        for (Product p : Products) {
            System.out.printf("%s\t%s\t%.2f\t%.2f\t%d\t%s%n",
                    p.getID(), p.getName(), p.getSalePrice(),
                    p.getCostPrice(), p.getStock(), p.getRemark());
        }
    }

    private void viewProduct(String id) {
        if (id.isEmpty()) {
            System.out.println("请提供商品ID");
            return;
        }

        Product product = service.getproductById(id);
        if (product == null) {
            System.out.println("找不到商品: " + id);
            return;
        }

        System.out.println("商品详情:");
        System.out.println("ID: " + product.getID());
        System.out.println("名称: " + product.getName());
        System.out.println("售价: " + product.getSalePrice());
        System.out.println("成本价: " + product.getCostPrice());
        System.out.println("库存: " + product.getStock());
        System.out.println("备注: " + product.getRemark());

        System.out.println("\n采购记录:");
        List<Purchase> Purchases = service.getpurchasesByproduct(id);
        if (Purchases.isEmpty()) {
            System.out.println("无采购记录");
        } else {
            System.out.println("订单号\t数量\t成本\t采购时间\t采购人\t供应商");
            for (Purchase p : Purchases) {
                System.out.printf("%s\t%d\t%.2f\t%s\t%s\t%s%n",
                        p.getOrderID(), p.getQuantity(), p.getCost(),
                        DateUtil.format(p.getPurchaseTime()), p.getPurchaser(), p.getSupplier());
            }
        }

        System.out.println("\n销售记录:");
        List<Sale> Sales = service.getsalesByproduct(id);
        if (Sales.isEmpty()) {
            System.out.println("无销售记录");
        } else {
            for (Sale sale : Sales) {
                System.out.println("销售订单: " + sale.getOrderId() +
                        ", 客户: " + sale.getCustomer() +
                        ", 时间: " + DateUtil.format(sale.getSaleTime()));
                for (Saleitem item : sale.getItems()) {
                    if (item.getProductId().equals(id)) {
                        System.out.printf("  - 数量: %d, 价格: %.2f, 小计: %.2f%n",
                                item.getQuantity(), item.getPrice(), item.getTotal());
                    }
                }
            }
        }
    }

    private void addProduct() {
        System.out.println("添加新商品");
        Product product = new Product();

        System.out.print("商品ID: ");
        product.setID(scanner.nextLine().trim());

        System.out.print("名称: ");
        product.setName(scanner.nextLine().trim());

        System.out.print("建议售价: ");
        product.setSalePrice(Double.parseDouble(scanner.nextLine().trim()));

        System.out.print("成本价: ");
        product.setCostPrice(Double.parseDouble(scanner.nextLine().trim()));

        System.out.print("初始库存: ");
        product.setStock(Integer.parseInt(scanner.nextLine().trim()));

        System.out.print("备注: ");
        product.setRemark(scanner.nextLine().trim());

        if (service.addproduct(product)) {
            System.out.println("商品添加成功");
        } else {
            System.out.println("商品添加失败，ID可能已存在");
        }
    }

    private void editProduct(String id) {
        if (id.isEmpty()) {
            System.out.println("请提供商品ID");
            return;
        }

        Product product = service.getproductById(id);
        if (product == null) {
            System.out.println("找不到商品: " + id);
            return;
        }

        System.out.println("编辑商品 (当前值)");

        System.out.print("名称 (" + product.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) product.setName(name);

        System.out.print("建议售价 (" + product.getSalePrice() + "): ");
        String salePrice = scanner.nextLine().trim();
        if (!salePrice.isEmpty()) product.setSalePrice(Double.parseDouble(salePrice));

        System.out.print("成本价 (" + product.getCostPrice() + "): ");
        String costPrice = scanner.nextLine().trim();
        if (!costPrice.isEmpty()) product.setCostPrice(Double.parseDouble(costPrice));

        System.out.print("备注 (" + product.getRemark() + "): ");
        String remark = scanner.nextLine().trim();
        if (!remark.isEmpty()) product.setRemark(remark);

        if (service.updateproduct(product)) {
            System.out.println("商品更新成功");
        } else {
            System.out.println("商品更新失败");
        }
    }

    private void deleteProduct(String id) {
        if (id.isEmpty()) {
            System.out.println("请提供商品ID");
            return;
        }

        if (service.deleteproduct(id)) {
            System.out.println("商品删除成功");
        } else {
            System.out.println("商品删除失败，可能不存在或库存不为0");
        }
    }

    private void handlePurchaseCommand(String arg) {
        // 类似商品命令处理，限于篇幅省略具体实现
        System.out.println("采购命令处理...");
    }

    private void handleSaleCommand(String arg) {
        // 类似商品命令处理，限于篇幅省略具体实现
        System.out.println("销售命令处理...");
    }

    private void printHelp() {
        System.out.println("可用命令:");
        System.out.println("  product list                  - 列出所有商品");
        System.out.println("  product view <ID>             - 查看商品详情");
        System.out.println("  product add                   - 添加新商品");
        System.out.println("  product edit <ID>             - 编辑商品");
        System.out.println("  product delete <ID>           - 删除商品");
        System.out.println("  purchase list                 - 列出所有采购记录");
        System.out.println("  purchase list <商品ID>        - 列出某商品的采购记录");
        System.out.println("  purchase view <订单号>        - 查看采购详情");
        System.out.println("  purchase add                  - 添加采购记录");
        System.out.println("  purchase edit <订单号>        - 编辑采购记录");
        System.out.println("  purchase delete <订单号>      - 删除采购记录");
        System.out.println("  sale list                     - 列出所有销售记录");
        System.out.println("  sale list <商品ID>            - 列出某商品的销售记录");
        System.out.println("  sale view <订单号>            - 查看销售详情");
        System.out.println("  sale add                      - 添加销售记录");
        System.out.println("  sale edit <订单号>            - 编辑销售记录");
        System.out.println("  sale delete <订单号>          - 删除销售记录");
        System.out.println("  help                          - 显示帮助");
        System.out.println("  exit                          - 退出系统");
    }

    public static void main(String[] args) {
        new Console().start();
    }
}