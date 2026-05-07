import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HomeScreen extends JFrame {

    private int    studentId;
    private String studentName;

    public HomeScreen(int studentId, String studentName) {
        this.studentId   = studentId;
        this.studentName = studentName;

        setTitle("StudyVault");
        setSize(780, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 248));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(25, 55, 95));
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel name = new JLabel("Hello, " + studentName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(Color.WHITE);

        JLabel app = new JLabel("StudyVault");
        app.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        app.setForeground(new Color(160, 185, 215));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(new Color(25, 55, 95));
        left.add(name);
        left.add(app);

        JButton logout = new JButton("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logout.setBackground(new Color(185, 50, 40));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.setBorderPainted(false);
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logout.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        bar.add(left,   BorderLayout.WEST);
        bar.add(logout, BorderLayout.EAST);
        return bar;
    }

    // ─── BODY ─────────────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(new Color(235, 240, 248));
        body.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(0, 0, 16, 0);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.gridwidth = GridBagConstraints.REMAINDER;
        g.weightx = 1.0;

        // Section label
        JLabel section = new JLabel("What would you like to work on?");
        section.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        section.setForeground(new Color(90, 105, 125));
        body.add(section, g);

        g.insets = new Insets(0, 0, 14, 0);

        // Tile 1 — Study Materials
        body.add(makeTile(
            "Study Materials",
            "Browse courses, theory, practical, assignments & projects",
            new Color(41, 128, 185),
            e -> new CoursesScreen(studentId, studentName).setVisible(true)
        ), g);

        // Tile 2 — Notes
        body.add(makeTile(
            "My Notes",
            "Write and save notes for your courses",
            new Color(39, 155, 105),
            e -> new NotesScreen(studentId, studentName).setVisible(true)
        ), g);

        // Tile 3 — To Do
        body.add(makeTile(
            "To Do List",
            "Track your tasks, assignments and deadlines",
            new Color(142, 68, 173),
            e -> new TodoScreen(studentId, studentName).setVisible(true)
        ), g);

        return body;
    }

    // ─── TILE ─────────────────────────────────────────────────────────────────

    private JPanel makeTile(String title, String subtitle, Color color,
                            java.awt.event.ActionListener action) {

        JPanel tile = new JPanel(new BorderLayout(0, 0));
        tile.setBackground(Color.WHITE);
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 232), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tile.setPreferredSize(new Dimension(0, 76));

        // Left color strip
        JPanel strip = new JPanel();
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(7, 0));

        // Text
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 4));
        text.setBackground(Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(new Color(25, 40, 65));

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(new Color(110, 125, 145));

        text.add(titleLbl);
        text.add(subLbl);

        // Arrow
        JLabel arrow = new JLabel("  >  ");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        arrow.setForeground(new Color(190, 200, 215));
        arrow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));

        tile.add(strip, BorderLayout.WEST);
        tile.add(text,  BorderLayout.CENTER);
        tile.add(arrow, BorderLayout.EAST);

        // Click
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tile.setBackground(new Color(245, 248, 255));
                text.setBackground(new Color(245, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBackground(Color.WHITE);
                text.setBackground(Color.WHITE);
            }
        });

        return tile;
    }
}