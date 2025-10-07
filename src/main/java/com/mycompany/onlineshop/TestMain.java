package com.mycompany.onlineshop;

import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        try {
            DBHelper db = new DBHelper();

            // Since DBHelper does not have listTables(), we skip that part
            System.out.println("Fetching all products from the 'products' table:");

            List<Product> products = db.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("No products found.");
            } else {
                for (Product p : products) {
                    System.out.println("ID: " + p.getId() + ", Name: " + p.getName() +
                            ", Price: " + p.getPrice() + ", Stock: " + p.getStock());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
