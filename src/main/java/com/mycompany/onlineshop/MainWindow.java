package com.mycompany.onlineshop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class MainWindow extends JFrame {

    private final DBHelper dbHelper;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JTextField searchField;
    private final JLabel totalProductsLabel;
    private final JLabel totalStockLabel;
    private final JLabel totalValueLabel;

    private final DecimalFormat currencyFmt = new DecimalFormat("₹#,##0.00");

    public MainWindow() {
        dbHelper = new DBHelper();

        setTitle("🛒 Online Shopping Portal");
        setSize(920, 600);
        setMinimumSize(new Dimension(800, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        // top: search + totals
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        add(top, BorderLayout.NORTH);

        // Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(28);
        searchPanel.add(searchField);
        top.add(searchPanel, BorderLayout.WEST);

        // Totals
        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 6));
        totalProductsLabel = new JLabel("Total Products: 0");
        totalStockLabel = new JLabel("Total Stock: 0");
        totalValueLabel = new JLabel("Total Value: ₹0.00");
        totalsPanel.add(totalProductsLabel);
        totalsPanel.add(totalStockLabel);
        totalsPanel.add(totalValueLabel);
        top.add(totalsPanel, BorderLayout.EAST);

        // center: table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // alternating colors
        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, col) -> {
            Component c = new JLabel(value == null ? "" : value.toString());
            ((JLabel) c).setOpaque(true);
            if (isSelected) {
                c.setBackground(new Color(102, 178, 255));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                c.setForeground(Color.BLACK);
            }
            return c;
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // bottom: buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        JButton addBtn = createButton("➕ Add");
        JButton updBtn = createButton("✏️ Update");
        JButton delBtn = createButton("🗑 Delete");
        JButton refreshBtn = createButton("🔄 Refresh");

        bottom.add(addBtn);
        bottom.add(updBtn);
        bottom.add(delBtn);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        // handlers
        addBtn.addActionListener(e -> onAdd());
        updBtn.addActionListener(e -> onUpdate());
        delBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> refreshData());

        // live search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String txt = searchField.getText();
                if (txt == null || txt.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt, 1)); // search only 'Name' column
            }
        });

        // initial load
        refreshData();
    }

    private JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(60, 120, 215));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return b;
    }

    private void refreshData() {
        tableModel.setRowCount(0);
        java.util.List<Product> products = dbHelper.getAllProducts();
        int totalStock = 0;
        double totalValue = 0;
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getStock()});
            totalStock += p.getStock();
            totalValue += p.getStock() * p.getPrice();
        }
        totalProductsLabel.setText("Total Products: " + products.size());
        totalStockLabel.setText("Total Stock: " + totalStock);
        totalValueLabel.setText("Total Value: " + currencyFmt.format(totalValue));
    }

    private void onAdd() {
        JTextField name = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();
        Object[] msg = {"Name:", name, "Price:", price, "Stock:", stock};
        int opt = JOptionPane.showConfirmDialog(this, msg, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String nm = name.getText().trim();
                double pr = Double.parseDouble(price.getText().trim());
                int st = Integer.parseInt(stock.getText().trim());
                if (nm.isEmpty()) throw new IllegalArgumentException("Name required");
                Product p = new Product(nm, pr, st);
                boolean ok = dbHelper.addProduct(p);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "✅ Product added successfully!");
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "⚠ Failed to add product.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and Stock must be numeric.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void onUpdate() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object idObj = tableModel.getValueAt(modelRow, 0);
        Object nameObj = tableModel.getValueAt(modelRow, 1);
        Object priceObj = tableModel.getValueAt(modelRow, 2);
        Object stockObj = tableModel.getValueAt(modelRow, 3);

        int id = ((Number) idObj).intValue();
        String currentName = nameObj.toString();
        double currentPrice = ((Number) priceObj).doubleValue();
        int currentStock = ((Number) stockObj).intValue();

        JTextField name = new JTextField(currentName);
        JTextField price = new JTextField(String.valueOf(currentPrice));
        JTextField stock = new JTextField(String.valueOf(currentStock));
        Object[] msg = {"Name:", name, "Price:", price, "Stock:", stock};
        int opt = JOptionPane.showConfirmDialog(this, msg, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String nm = name.getText().trim();
                double pr = Double.parseDouble(price.getText().trim());
                int st = Integer.parseInt(stock.getText().trim());
                Product p = new Product(id, nm, pr, st);
                boolean ok = dbHelper.updateProduct(p);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "✅ Product updated successfully!");
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, "⚠ Update failed.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and Stock must be numeric.");
            }
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        int id = ((Number) tableModel.getValueAt(modelRow, 0)).intValue();
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = dbHelper.deleteProduct(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Product deleted.");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "⚠ Delete failed.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
