import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class RegisterScreen extends JFrame {

    private JTextField     nameField;
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         messageLabel;

    public RegisterScreen() {
        setTitle("StudyVault - Create Account");
        setSize(380, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill      = GridBagConstraints.HORIZONTAL;
        g.insets    = new Insets(6, 0, 6, 0);
        g.gridwidth = GridBagConstraints.REMAINDER;

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30, 60, 100));
        panel.add(title, g);

        panel.add(Box.createVerticalStrut(6), g);

        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        nameField.setBorder(BorderFactory.createTitledBorder("Full Name"));
        nameField.setPreferredSize(new Dimension(0, 55));
        panel.add(nameField, g);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setPreferredSize(new Dimension(0, 55));
        panel.add(usernameField, g);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setPreferredSize(new Dimension(0, 55));
        panel.add(passwordField, g);

        JButton registerBtn = makeButton("Register", new Color(60, 170, 90));
        panel.add(registerBtn, g);

        JButton backBtn = makeButton("Back to Login", new Color(120, 120, 120));
        panel.add(backBtn, g);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(messageLabel, g);

        add(panel);

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });
    }

    private void doRegister() {
        String name     = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            message("Please fill in all fields.", Color.RED);
            return;
        }
        if (password.length() < 4) {
            message("Password must be at least 4 characters.", Color.RED);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // Check username exists
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM students WHERE username = ?");
            check.setString(1, username);
            if (check.executeQuery().next()) {
                message("Username already taken.", Color.RED);
                conn.close();
                return;
            }

            // Insert with SHA2 hashed password
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO students (name, username, password) VALUES (?, ?, SHA2(?,256))");
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
            conn.close();

            message("Account created! Redirecting...", new Color(39, 130, 80));
            Timer t = new Timer(1200, ev -> {
                new LoginScreen().setVisible(true);
                dispose();
            });
            t.setRepeats(false);
            t.start();

        } catch (Exception ex) {
            message("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void message(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }
}