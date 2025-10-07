package com.mycompany.onlineshop;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBHelper {

    private String url;
    private String user;
    private String password;

    public DBHelper() {
        try {
            // Load properties from classpath (src/main/resources)
            Properties props = new Properties();
            InputStream in = Thread.currentThread()
                                   .getContextClassLoader()
                                   .getResourceAsStream("db.properties");
            if (in == null) {
                throw new RuntimeException("db.properties not found in classpath.");
            }
            props.load(in);

            url = props.getProperty("url");
            user = props.getProperty("user");
            password = props.getProperty("password");

            System.out.println("DBHelper: connecting to " + url + " with user " + user);

            // Ensure table exists
            createProductsTableIfNotExists();

            System.out.println("✅ DBHelper ready.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DBHelper: " + e.getMessage(), e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void createProductsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS products ("
                + "product_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(255) NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "stock INT NOT NULL"
                + ")";
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create products table: " + e.getMessage(), e);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, name, price, stock FROM products ORDER BY product_id";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (name, price, stock) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, p.getName());
            pst.setDouble(2, p.getPrice());
            pst.setInt(3, p.getStock());
            int affected = pst.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ? WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, p.getName());
            pst.setDouble(2, p.getPrice());
            pst.setInt(3, p.getStock());
            pst.setInt(4, p.getId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
