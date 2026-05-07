import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class LoginScreen extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         messageLabel;

    public LoginScreen() {
        setTitle("StudyVault - Login");
        setSize(380, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill      = GridBagConstraints.HORIZONTAL;
        g.insets    = new Insets(6, 0, 6, 0);
        g.gridwidth = GridBagConstraints.REMAINDER;

        // Title
        JLabel title = new JLabel("StudyVault", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(30, 60, 100));
        panel.add(title, g);

        JLabel sub = new JLabel("Login to your account", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        panel.add(sub, g);

        panel.add(Box.createVerticalStrut(10), g);

        // Username
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setPreferredSize(new Dimension(0, 55));
        panel.add(usernameField, g);

        // Password
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setPreferredSize(new Dimension(0, 55));
        panel.add(passwordField, g);

        // Login button
        JButton loginBtn = makeButton("Login", new Color(30, 100, 200));
        panel.add(loginBtn, g);

        // Register link
        JButton regBtn = makeButton("Create New Account", new Color(60, 170, 90));
        panel.add(regBtn, g);

        // Message
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(messageLabel, g);

        add(panel);

        // Actions
        loginBtn.addActionListener(e -> doLogin());
        regBtn.addActionListener(e -> {
            new RegisterScreen().setVisible(true);
            dispose();
        });
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            message("Please fill in all fields.", Color.RED);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM students WHERE username = ? AND password = SHA2(?,256)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int    id   = rs.getInt("id");
                String name = rs.getString("name");
                conn.close();
                new HomeScreen(id, name).setVisible(true);
                dispose();
            } else {
                conn.close();
                message("Wrong username or password.", Color.RED);
                passwordField.setText("");
            }
        } catch (Exception ex) {
            message("Connection error: " + ex.getMessage(), Color.RED);
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