package Project;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Code_Alpha_Task2 extends JFrame {
    
    JTextField stockSymbolField, sharesField, priceField, totalField;
    JButton calculateButton, viewButton;
    Color color, color1;

    public Code_Alpha_Task2() {
       
        setTitle("Stock Trading Platform");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        getRootPane().setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

       
        color = new Color(128, 128, 128);
        getContentPane().setBackground(color);

      
        JLabel titleLabel = new JLabel("Stock Trading Platform");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(150, 10, 250, 30);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for title
        add(titleLabel);

        
        JLabel stockSymbolLabel = new JLabel("Stock Symbol:");
        stockSymbolLabel.setForeground(Color.BLACK);
        stockSymbolLabel.setBounds(50, 60, 100, 30);
        add(stockSymbolLabel);

        stockSymbolField = new JTextField();
        stockSymbolField.setBounds(150, 60, 250, 30);
        stockSymbolField.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for text field
        add(stockSymbolField);

        
        JLabel sharesLabel = new JLabel("Shares:");
        sharesLabel.setForeground(Color.BLACK);
        sharesLabel.setBounds(50, 100, 100, 30);
        add(sharesLabel);

        sharesField = new JTextField();
        sharesField.setBounds(150, 100, 250, 30);
        sharesField.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for text field
        add(sharesField);

        
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(Color.BLACK);
        priceLabel.setBounds(50, 140, 100, 30);
        add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(150, 140, 250, 30);
        priceField.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for text field
        add(priceField);

      
        JLabel totalLabel = new JLabel("Total Value:");
        totalLabel.setForeground(Color.BLACK);
        totalLabel.setBounds(50, 180, 100, 30);
        add(totalLabel);

        totalField = new JTextField();
        totalField.setBounds(150, 180, 250, 30);
        totalField.setEditable(false);
        totalField.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for text field
        add(totalField);

      
        calculateButton = new JButton("Calculate");
        calculateButton.setForeground(Color.BLACK);
        calculateButton.setBackground(color);
        calculateButton.setBounds(150, 220, 150, 30);
        calculateButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for button
        add(calculateButton);

        
        viewButton = new JButton("View Trades");
        viewButton.setBackground(color);
        viewButton.setForeground(Color.BLACK);
        viewButton.setBounds(150, 260, 150, 30);
        viewButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED)); // Raised border for button
        add(viewButton);

       
        calculateButton.addActionListener(new ActionListener() {
          
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });


        viewButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                new ViewForm().setVisible(true);
            }
        });
    }

    
    private void calculateTotal() {
        try {
            String stockSymbol = stockSymbolField.getText();
            int shares = Integer.parseInt(sharesField.getText());
            double price = Double.parseDouble(priceField.getText());

            double total = shares * price;

            totalField.setText(String.format("%.2f", total));

          
            saveToDatabase(stockSymbol, shares, price, total);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numbers.");
        }
    }

  
    private void saveToDatabase(String stockSymbol, int shares, double price, double total) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");

           
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Stock_Trading_Platform", "root", "root");

         
            String sql = "INSERT INTO stock (stock_symbol, share, price, total) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, stockSymbol);
            pstmt.setInt(2, shares);
            pstmt.setDouble(3, price);
            pstmt.setDouble(4, total);
            pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private class ViewForm extends JFrame {
        JTable table;
        DefaultTableModel tableModel;
        Color color1;

        public ViewForm() {
          
            setTitle("View Trades");
            setSize(500, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            color1 = new Color(173, 216, 230);
            getContentPane().setBackground(color1);

       
            tableModel = new DefaultTableModel();
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

 
            tableModel.addColumn("Stock Symbol");
            tableModel.addColumn("Share");
            tableModel.addColumn("Price");
            tableModel.addColumn("Total");

            
            loadData();
        }

        private void loadData() {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");

               
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Stock_Trading_Platform", "root", "root");

               
                stmt = conn.createStatement();
                String sql = "SELECT * FROM stock";
                rs = stmt.executeQuery(sql);

               
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getString("stock_symbol");
                    row[1] = rs.getInt("share");
                    row[2] = rs.getDouble("price");
                    row[3] = rs.getDouble("total");
                    tableModel.addRow(row);
                }

            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
             
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
           
            public void run() {
                new Code_Alpha_Task2().setVisible(true);
            }
        });
    }
}
