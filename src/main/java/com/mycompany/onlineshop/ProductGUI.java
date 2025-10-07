package com.mycompany.onlineshop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ProductGUI extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private DBHelper dbHelper;

    public ProductGUI() {
        setTitle("Online Shopping Portal");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        dbHelper = new DBHelper();

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(30, 144, 255));
        table.getTableHeader().setForeground(Color.white);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton viewCartBtn = new JButton("Cart");
        JButton searchBtn = new JButton("Search");

        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 248, 255));
        panel.add(refreshBtn);
        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(addToCartBtn);
        panel.add(viewCartBtn);
        panel.add(searchBtn);
        add(panel, BorderLayout.SOUTH);
         // Button actions
        refreshBtn.addActionListener(e -> refreshTable());
        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
        addToCartBtn.addActionListener(e -> addToCart());
        viewCartBtn.addActionListener(e -> viewCart());
        searchBtn.addActionListener(e -> searchProduct());
        // Load initial data
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Product> products = dbHelper.getAllProducts();
        for (Product p : products) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock()});
        }
    }

    private void addProduct() {
        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();
        Object[] fields = {"Name:", name, "Price:", price, "Stock:", stock};

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product(name.getText(),
                        Double.parseDouble(price.getText()),
                        Integer.parseInt(stock.getText()));
                if(dbHelper.addProduct(p)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Product added successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric values for Price and Stock.");
            }
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a product to update."); return; }

        int id = (int) table.getValueAt(row, 0);
        JTextField name = new JTextField((String) table.getValueAt(row, 1));
        JTextField price = new JTextField(table.getValueAt(row, 2).toString());
        JTextField stock = new JTextField(table.getValueAt(row, 3).toString());
        Object[] fields = {"Name:", name, "Price:", price, "Stock:", stock};

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            try {
                Product p = new Product(id,
                        name.getText(),
                        Double.parseDouble(price.getText()),
                        Integer.parseInt(stock.getText()));
                if(dbHelper.updateProduct(p)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Product updated successfully!");
                }
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numeric values for Price and Stock.");
            }
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a product to delete."); return; }

        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?");
        if(confirm == JOptionPane.YES_OPTION) {
            if(dbHelper.deleteProduct(id)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            }
        }
    }

    // Cart functionality
    private void addToCart() {
        int row = table.getSelectedRow();
        if(row == -1) { JOptionPane.showMessageDialog(this, "Select a product to add to cart."); return; }

        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        double price = (double) table.getValueAt(row, 2);
        int stock = (int) table.getValueAt(row, 3);

        Product p = new Product(id, name, price, stock);
        Cart.addToCart(p);
        JOptionPane.showMessageDialog(this, name + " has been added to cart!");
    }

    private void viewCart() {
        List<Product> cartItems = Cart.getCartItems();
        if(cartItems.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty."); return; }

        StringBuilder sb = new StringBuilder();
        double total = 0;
        for(Product p : cartItems) {
            sb.append(p.getName()).append(" - ₹").append(p.getPrice()).append("\n");
            total += p.getPrice();
        }
        sb.append("\nTotal: ₹").append(total);
        JOptionPane.showMessageDialog(this, sb.toString(), "Cart", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchProduct() {
        String keyword = JOptionPane.showInputDialog(this, "Enter product name to search:");
        if(keyword == null || keyword.trim().isEmpty()) return;

        List<Product> list = dbHelper.getAllProducts();
        model.setRowCount(0);
        for(Product p : list) {
            if(p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock()});
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductGUI().setVisible(true));
    }
}
