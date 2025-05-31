import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class j_apotek extends JFrame {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/apotik";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // No password
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(74, 144, 226);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color RECEIPT_COLOR = new Color(255, 193, 7);
    
    // UI Components
    private JComboBox<String> tableSelector;
    private JTable dataTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton receiptButton;
    
    // Database Connection
    private Connection connection;
    
    public j_apotek() {
        // Set modern look and feel
        try {
            // Use default look and feel
            setupModernUIDefaults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Apotek Database Manager");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        
        // Set modern background
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Initialize UI components
        initModernUI();
        
        // Connect to database
        connectToDatabase();
        
        // Display data from the first selected table
        loadTableData((String) tableSelector.getSelectedItem());
        
        setVisible(true);
    }
    
    private void setupModernUIDefaults() {
        // Customize UI Manager defaults for modern look
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("ComboBox.focus", new Color(0, 0, 0, 0));
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
        UIManager.put("Table.showGrid", false);
    }
    
    private void initModernUI() {
        setLayout(new BorderLayout(20, 20));
        
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Header panel with title and controls
        JPanel headerPanel = createHeaderPanel();
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(tablePanel, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        // Title
        JLabel titleLabel = new JLabel("Apotek Database Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage your pharmacy database with ease");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_COLOR);
        
        // Title container
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setBackground(BACKGROUND_COLOR);
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(subtitleLabel, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel selectLabel = new JLabel("Select Table:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectLabel.setForeground(TEXT_COLOR);
        
        // Modern table selector
        String[] tables = {"detail_transaksi_penjualan", "kategori_obat", "obat", 
                          "supplier", "transaksi_penjualan"};
        tableSelector = new JComboBox<>(tables);
        styleComboBox(tableSelector);
        tableSelector.addActionListener(e -> {
            loadTableData((String) tableSelector.getSelectedItem());
            updateReceiptButtonVisibility();
        });
        
        controlPanel.add(selectLabel);
        controlPanel.add(tableSelector);
        
        headerPanel.add(titleContainer, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Table setup
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        styleTable(dataTable);
        
        scrollPane = new JScrollPane(dataTable);
        styleScrollPane(scrollPane);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        // Create modern buttons
        refreshButton = createModernButton("🔄 Refresh", PRIMARY_COLOR);
        addButton = createModernButton("➕ Add Record", ACCENT_COLOR);
        editButton = createModernButton("✏️ Edit Record", SECONDARY_COLOR);
        deleteButton = createModernButton("🗑️ Delete Record", DANGER_COLOR);
        receiptButton = createModernButton("🧾 Generate Receipt", RECEIPT_COLOR);
        
        // Add action listeners
        refreshButton.addActionListener(e -> loadTableData((String) tableSelector.getSelectedItem()));
        addButton.addActionListener(e -> addRecord((String) tableSelector.getSelectedItem()));
        editButton.addActionListener(e -> editRecord((String) tableSelector.getSelectedItem()));
        deleteButton.addActionListener(e -> deleteRecord((String) tableSelector.getSelectedItem()));
        receiptButton.addActionListener(e -> generateReceipt());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(receiptButton);
        
        // Initially hide receipt button
        updateReceiptButtonVisibility();
        
        return buttonPanel;
    }
    
    private void updateReceiptButtonVisibility() {
        String selectedTable = (String) tableSelector.getSelectedItem();
        receiptButton.setVisible("detail_transaksi_penjualan".equals(selectedTable));
    }
    
    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(backgroundColor == RECEIPT_COLOR ? TEXT_COLOR : Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 40));
        
        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setPreferredSize(new Dimension(250, 35));
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 30));
        table.setSelectionForeground(TEXT_COLOR);
        table.setBackground(CARD_COLOR);
        table.setForeground(TEXT_COLOR);
        
        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 242, 245));
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(CARD_COLOR);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }
                
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return c;
            }
        });
    }
    
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        
        // Style scrollbars
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
    }
    
    // Custom ScrollBar UI for modern look
    private class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = BORDER_COLOR;
            this.trackColor = BACKGROUND_COLOR;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
    
    private void connectToDatabase() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection without password
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to database successfully");
        } catch (ClassNotFoundException e) {
            showModernDialog("Database driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            showModernDialog("Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    private void loadTableData(String tableName) {
        try {
            // Clear existing data
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
            
            // Get metadata for the selected table
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            // Add columns to table model
            while (columns.next()) {
                tableModel.addColumn(columns.getString("COLUMN_NAME"));
            }
            
            // Execute query and fetch data
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            
            // Get number of columns
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            
            // Add data rows
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i-1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }
            
            // Close resources
            resultSet.close();
            statement.close();
            
        } catch (SQLException e) {
            showModernDialog("Error loading table data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReceipt() {
    int selectedRow = dataTable.getSelectedRow();
    if (selectedRow == -1) {
        showModernDialog("Please select a transaction detail record to generate receipt", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        // Debug: Print all column names to see what's available
        System.out.println("Available columns:");
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            System.out.println("Column " + i + ": " + tableModel.getColumnName(i));
        }
        
        // Try to find transaction ID column with different possible names
        int transactionColumnIndex = -1;
        String[] possibleColumnNames = {"id_transaksi", "ID_TRANSAKSI", "transaksi_id", "transaction_id"};
        
        for (String columnName : possibleColumnNames) {
            transactionColumnIndex = getColumnIndex(columnName);
            if (transactionColumnIndex != -1) {
                System.out.println("Found transaction column: " + columnName + " at index " + transactionColumnIndex);
                break;
            }
        }
        
        if (transactionColumnIndex == -1) {
            // If we can't find the transaction ID column, show available columns to user
            StringBuilder availableColumns = new StringBuilder("Available columns:\n");
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                availableColumns.append("- ").append(tableModel.getColumnName(i)).append("\n");
            }
            showModernDialog("Transaction ID column not found.\n\n" + availableColumns.toString(), 
                           "Column Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get transaction ID from selected row
        Object transactionId = tableModel.getValueAt(selectedRow, transactionColumnIndex);
        
        if (transactionId == null) {
            showModernDialog("Transaction ID is null in the selected row", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        System.out.println("Transaction ID: " + transactionId.toString());
        
        // Create and show receipt dialog
        showReceiptDialog(transactionId.toString());
        
    } catch (Exception e) {
        e.printStackTrace(); // This will help you see the full error in console
        showModernDialog("Error generating receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getColumnIndex(String columnName) {
    for (int i = 0; i < tableModel.getColumnCount(); i++) {
        String modelColumnName = tableModel.getColumnName(i);
        // Case-insensitive comparison
        if (modelColumnName != null && modelColumnName.equalsIgnoreCase(columnName)) {
            return i;
        }
    }
    return -1;
}
    
private void showReceiptDialog(String transactionId) {
    try {
        // First, let's check what tables and columns actually exist
        DatabaseMetaData metaData = connection.getMetaData();
        
        // Check columns in each table
        System.out.println("=== Checking table structures ===");
        String[] tableNames = {"transaksi_penjualan", "detail_transaksi_penjualan", "obat", "kategori_obat", "supplier"};
        
        for (String tableName : tableNames) {
            System.out.println("\nTable: " + tableName);
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                System.out.println("  - " + columns.getString("COLUMN_NAME"));
            }
            columns.close();
        }
        
        // Try a simpler query first - just get data from detail_transaksi_penjualan
        // FIXED: Use 'transaksi_id' instead of 'id_transaksi'
        String simpleSQL = "SELECT * FROM detail_transaksi_penjualan WHERE transaksi_id = ?";
        PreparedStatement simplePstmt = connection.prepareStatement(simpleSQL);
        simplePstmt.setString(1, transactionId);
        ResultSet simpleRs = simplePstmt.executeQuery();
        
        if (!simpleRs.next()) {
            showModernDialog("No transaction details found for ID: " + transactionId, "Error", JOptionPane.ERROR_MESSAGE);
            simpleRs.close();
            simplePstmt.close();
            return;
        }
        simpleRs.close();
        simplePstmt.close();
        
        // Now try to build the receipt with a step-by-step approach
        StringBuilder receiptContent = new StringBuilder();
        receiptContent.append("═══════════════════════════════════════\n");
        receiptContent.append("              APOTEK RECEIPT\n");
        receiptContent.append("═══════════════════════════════════════\n\n");
        receiptContent.append("Transaction ID: ").append(transactionId).append("\n");
        receiptContent.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        receiptContent.append("───────────────────────────────────────\n");
        receiptContent.append("ITEMS PURCHASED:\n");
        receiptContent.append("───────────────────────────────────────\n");
        
        // Get transaction details
        // FIXED: Use 'transaksi_id' instead of 'id_transaksi'
        String detailSQL = "SELECT * FROM detail_transaksi_penjualan WHERE transaksi_id = ?";
        PreparedStatement detailPstmt = connection.prepareStatement(detailSQL);
        detailPstmt.setString(1, transactionId);
        ResultSet detailRs = detailPstmt.executeQuery();
        
        double totalAmount = 0;
        int itemCount = 0;
        
        while (detailRs.next()) {
            itemCount++;
            // FIXED: Use 'obat_id' instead of 'id_obat'
            String obatId = detailRs.getString("obat_id");
            int jumlah = detailRs.getInt("jumlah");
            // FIXED: Use 'harga' instead of 'subtotal' (based on your schema)
            double subtotal = detailRs.getDouble("harga") * jumlah; // Calculate subtotal
            totalAmount += subtotal;
            
            // Try to get medicine name
            String medicineName = "Unknown Medicine";
            double unitPrice = 0;
            try {
                // FIXED: Use 'obat_id' instead of 'id_obat' and 'harga' instead of 'harga_jual'
                String obatSQL = "SELECT nama_obat, harga FROM obat WHERE obat_id = ?";
                PreparedStatement obatPstmt = connection.prepareStatement(obatSQL);
                obatPstmt.setString(1, obatId);
                ResultSet obatRs = obatPstmt.executeQuery();
                
                if (obatRs.next()) {
                    medicineName = obatRs.getString("nama_obat");
                    unitPrice = obatRs.getDouble("harga");
                }
                obatRs.close();
                obatPstmt.close();
            } catch (SQLException e) {
                System.out.println("Error getting medicine details: " + e.getMessage());
            }
            
            receiptContent.append(String.format("• %s (ID: %s)\n", medicineName, obatId));
            receiptContent.append(String.format("  Price: Rp %,.2f x %d\n", unitPrice, jumlah));
            receiptContent.append(String.format("  Subtotal: Rp %,.2f\n\n", subtotal));
        }
        
        detailRs.close();
        detailPstmt.close();
        
        if (itemCount == 0) {
            showModernDialog("No items found for this transaction", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Try to get total from main transaction table
        try {
            // FIXED: Use 'transaksi_id' instead of 'id_transaksi' and 'tanggal' instead of 'tanggal_transaksi'
            String totalSQL = "SELECT total_harga, tanggal FROM transaksi_penjualan WHERE transaksi_id = ?";
            PreparedStatement totalPstmt = connection.prepareStatement(totalSQL);
            totalPstmt.setString(1, transactionId);
            ResultSet totalRs = totalPstmt.executeQuery();
            
            if (totalRs.next()) {
                double dbTotal = totalRs.getDouble("total_harga");
                String transactionDate = totalRs.getString("tanggal");
                if (dbTotal > 0) {
                    totalAmount = dbTotal;
                }
                // Update the date in receipt if available
                receiptContent.insert(receiptContent.indexOf("Date: ") + 6, transactionDate + " - ");
            }
            totalRs.close();
            totalPstmt.close();
        } catch (SQLException e) {
            System.out.println("Could not get transaction total: " + e.getMessage());
        }
        
        receiptContent.append("───────────────────────────────────────\n");
        receiptContent.append(String.format("TOTAL AMOUNT: Rp %,.2f\n", totalAmount));
        receiptContent.append("───────────────────────────────────────\n\n");
        receiptContent.append("Thank you for your purchase!\n");
        receiptContent.append("Visit us again soon.\n\n");
        receiptContent.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        receiptContent.append("\n═══════════════════════════════════════");
        
        // Create receipt dialog
        JDialog receiptDialog = new JDialog(this, "Receipt - Transaction " + transactionId, true);
        receiptDialog.setSize(500, 600);
        receiptDialog.setLocationRelativeTo(this);
        
        // Create text area for receipt
        JTextArea receiptArea = new JTextArea(receiptContent.toString());
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        receiptArea.setEditable(false);
        receiptArea.setBackground(CARD_COLOR);
        receiptArea.setForeground(TEXT_COLOR);
        receiptArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
        receiptScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create button panel
        JPanel receiptButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        receiptButtonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton printButton = createModernButton("🖨 Print", PRIMARY_COLOR);
        JButton closeButton = createModernButton("✖ Close", SECONDARY_COLOR);
        
        printButton.addActionListener(e -> printReceipt(receiptContent.toString()));
        closeButton.addActionListener(e -> receiptDialog.dispose());
        
        receiptButtonPanel.add(printButton);
        receiptButtonPanel.add(closeButton);
        
        // Layout receipt dialog
        receiptDialog.setLayout(new BorderLayout());
        receiptDialog.add(receiptScrollPane, BorderLayout.CENTER);
        receiptDialog.add(receiptButtonPanel, BorderLayout.SOUTH);
        receiptDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        receiptDialog.setVisible(true);
        
    } catch (SQLException e) {
        e.printStackTrace();
        showModernDialog("Error creating receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}  
    private void printReceipt(String receiptContent) {
        try {
            // Create a simple printable component
            JTextArea printArea = new JTextArea(receiptContent);
            printArea.setFont(new Font("Consolas", Font.PLAIN, 10));
            printArea.setEditable(false);
            
            // Print the receipt
            boolean printed = printArea.print();
            
            if (printed) {
                showModernDialog("Receipt sent to printer successfully!", "Print Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showModernDialog("Print operation was cancelled", "Print Cancelled", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            showModernDialog("Error printing receipt: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createModernInputDialog(java.util.List<String> columnNames, java.util.List<String> currentValues) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        for (int i = 0; i < columnNames.size(); i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            
            JLabel label = new JLabel(columnNames.get(i) + ":");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(TEXT_COLOR);
            panel.add(label, gbc);
            
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            
            JTextField field = new JTextField(20);
            if (currentValues != null && i < currentValues.size()) {
                field.setText(currentValues.get(i));
            }
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 12, 8, 12)
            ));
            panel.add(field, gbc);
            
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
        }
        
        return panel;
    }
    
    private void addRecord(String tableName) {
        try {
            // Dynamically get column names to create input fields
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            java.util.List<String> columnNames = new java.util.ArrayList<>();
            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
            }
            
            // Create modern input dialog
            JPanel panel = createModernInputDialog(columnNames, null);
            
            // Get input fields from panel
            java.util.List<JTextField> inputFields = new java.util.ArrayList<>();
            Component[] components = panel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JTextField) {
                    inputFields.add((JTextField) comp);
                }
            }
            
            // Show input dialog
            int result = JOptionPane.showConfirmDialog(this, panel, 
                    "Add New Record", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                // Build SQL insert statement
                StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
                StringBuilder values = new StringBuilder(" VALUES (");
                
                for (int i = 0; i < columnNames.size(); i++) {
                    sql.append(columnNames.get(i));
                    values.append("?");
                    
                    if (i < columnNames.size() - 1) {
                        sql.append(", ");
                        values.append(", ");
                    }
                }
                
                sql.append(")");
                values.append(")");
                sql.append(values);
                
                // Execute prepared statement
                PreparedStatement pstmt = connection.prepareStatement(sql.toString());
                for (int i = 0; i < inputFields.size(); i++) {
                    String value = inputFields.get(i).getText();
                    pstmt.setString(i + 1, value.isEmpty() ? null : value);
                }
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showModernDialog("Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData(tableName);
                }
                
                pstmt.close();
            }
        } catch (SQLException e) {
            showModernDialog("Error adding record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editRecord(String tableName) {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernDialog("Please select a record to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get primary key column
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            
            if (!primaryKeys.next()) {
                showModernDialog("Could not identify primary key for table", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String pkColumn = primaryKeys.getString("COLUMN_NAME");
            int pkColumnIndex = -1;
            
            // Find index of primary key column in the table model
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                if (tableModel.getColumnName(i).equals(pkColumn)) {
                    pkColumnIndex = i;
                    break;
                }
            }
            
            if (pkColumnIndex == -1) {
                showModernDialog("Primary key column not found in table model", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get primary key value for selected row
            Object pkValue = tableModel.getValueAt(selectedRow, pkColumnIndex);
            
            // Get all columns and current values
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            java.util.List<String> columnNames = new java.util.ArrayList<>();
            java.util.List<String> currentValues = new java.util.ArrayList<>();
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                columnNames.add(columnName);
                
                // Find column index in table model
                int colIndex = -1;
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    if (tableModel.getColumnName(i).equals(columnName)) {
                        colIndex = i;
                        break;
                    }
                }
                
                // Get current value
                String currentValue = "";
                if (colIndex != -1) {
                    Object value = tableModel.getValueAt(selectedRow, colIndex);
                    currentValue = (value != null) ? value.toString() : "";
                }
                currentValues.add(currentValue);
            }
            
            // Create modern input dialog
            JPanel panel = createModernInputDialog(columnNames, currentValues);
            
            // Get input fields from panel
            java.util.List<JTextField> inputFields = new java.util.ArrayList<>();
            Component[] components = panel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JTextField) {
                    inputFields.add((JTextField) comp);
                }
            }
            
            // Show edit dialog
            int result = JOptionPane.showConfirmDialog(this, panel, 
                    "Edit Record", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                // Build SQL update statement
                StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
                
                for (int i = 0; i < columnNames.size(); i++) {
                    sql.append(columnNames.get(i) + " = ?");
                    
                    if (i < columnNames.size() - 1) {
                        sql.append(", ");
                    }
                }
                
                sql.append(" WHERE " + pkColumn + " = ?");
                
                // Execute prepared statement
                PreparedStatement pstmt = connection.prepareStatement(sql.toString());
                for (int i = 0; i < inputFields.size(); i++) {
                    String value = inputFields.get(i).getText();
                    pstmt.setString(i + 1, value.isEmpty() ? null : value);
                }
                
                // Set primary key value in WHERE clause
                pstmt.setObject(inputFields.size() + 1, pkValue);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showModernDialog("Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData(tableName);
                }
                
                pstmt.close();
            }
            
        } catch (SQLException e) {
            showModernDialog("Error editing record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteRecord(String tableName) {
        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            showModernDialog("Please select a record to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get primary key column
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            
            if (!primaryKeys.next()) {
                showModernDialog("Could not identify primary key for table", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String pkColumn = primaryKeys.getString("COLUMN_NAME");
            int pkColumnIndex = -1;
            
            // Find index of primary key column in the table model
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                if (tableModel.getColumnName(i).equals(pkColumn)) {
                    pkColumnIndex = i;
                    break;
                }
            }
            
            if (pkColumnIndex == -1) {
                showModernDialog("Primary key column not found in table model", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get primary key value for selected row
            Object pkValue = tableModel.getValueAt(selectedRow, pkColumnIndex);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                                                      "Are you sure you want to delete this record?\nThis action cannot be undone.",
                                                      "Confirm Delete",
                                                      JOptionPane.YES_NO_OPTION,
                                                      JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Execute delete statement
                String sql = "DELETE FROM " + tableName + " WHERE " + pkColumn + " = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setObject(1, pkValue);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showModernDialog("Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData(tableName);
                }
                
                pstmt.close();
            }
            
        } catch (SQLException e) {
            showModernDialog("Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new j_apotek());
    }
}