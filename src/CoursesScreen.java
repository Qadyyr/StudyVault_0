import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class CoursesScreen extends JFrame {

    private int    studentId;
    private String studentName;
    private JPanel tilesPanel;
    private JTextField searchField;

    private static final Color[] COLORS = {
        new Color(41,  128, 185),
        new Color(39,  155, 105),
        new Color(192, 57,  43),
        new Color(142, 68,  173),
        new Color(243, 156, 18),
        new Color(26,  188, 156)
    };

    public CoursesScreen(int studentId, String studentName) {
        this.studentId   = studentId;
        this.studentName = studentName;

        setTitle("StudyVault - Study Materials");
        setSize(820, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 248));

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        loadCourses("");
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(41, 128, 185));
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(new Color(41, 128, 185));

        JLabel title = new JLabel("Study Materials");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Select a course to view materials");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(195, 220, 240));

        left.add(title);
        left.add(sub);

        JButton backBtn = new JButton("< Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(30, 100, 160));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        backBtn.addActionListener(e -> dispose());

        bar.add(left,    BorderLayout.WEST);
        bar.add(backBtn, BorderLayout.EAST);
        return bar;
    }

    // ─── BODY ─────────────────────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(new Color(235, 240, 248));

        // Search bar
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setBackground(new Color(235, 240, 248));
        searchBar.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(195, 205, 220), 1),
            BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
        searchField.setPreferredSize(new Dimension(0, 42));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setBackground(new Color(41, 128, 185));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        searchBtn.addActionListener(e -> loadCourses(searchField.getText().trim()));

        JButton addBtn = new JButton("+ Add Course");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(new Color(39, 155, 105));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));
        addBtn.addActionListener(e -> showAddCourseDialog());

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setBackground(new Color(235, 240, 248));
        btnGroup.add(searchBtn);
        btnGroup.add(addBtn);

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(btnGroup,    BorderLayout.EAST);

        // Also search on Enter key
        searchField.addActionListener(e -> loadCourses(searchField.getText().trim()));

        // Tiles area
        tilesPanel = new JPanel();
        tilesPanel.setLayout(new BoxLayout(tilesPanel, BoxLayout.Y_AXIS));
        tilesPanel.setBackground(new Color(235, 240, 248));
        tilesPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 24, 24));

        JScrollPane scroll = new JScrollPane(tilesPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(235, 240, 248));

        body.add(searchBar, BorderLayout.NORTH);
        body.add(scroll,    BorderLayout.CENTER);
        return body;
    }

    // ─── LOAD COURSES ─────────────────────────────────────────────────────────

    private void loadCourses(String keyword) {
        tilesPanel.removeAll();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT id, name, code FROM courses WHERE student_id = ?";
            if (!keyword.isEmpty()) sql += " AND (name LIKE ? OR code LIKE ?)";
            sql += " ORDER BY name";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            if (!keyword.isEmpty()) {
                ps.setString(2, "%" + keyword + "%");
                ps.setString(3, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();
            int i = 0;
            boolean any = false;

            while (rs.next()) {
                int    cid  = rs.getInt("id");
                String name = rs.getString("name");
                String code = rs.getString("code");
                tilesPanel.add(makeCourseTile(cid, name, code, COLORS[i % COLORS.length]));
                tilesPanel.add(Box.createVerticalStrut(12));
                i++;
                any = true;
            }
            conn.close();

            if (!any) {
                JLabel empty = new JLabel(keyword.isEmpty()
                    ? "No courses yet. Click '+ Add Course' to get started."
                    : "No courses found for \"" + keyword + "\".");
                empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                empty.setForeground(new Color(130, 145, 165));
                empty.setBorder(BorderFactory.createEmptyBorder(20, 4, 0, 0));
                tilesPanel.add(empty);
            }

        } catch (Exception ex) {
            JLabel err = new JLabel("Error: " + ex.getMessage());
            err.setForeground(Color.RED);
            tilesPanel.add(err);
        }

        tilesPanel.revalidate();
        tilesPanel.repaint();
    }

    // ─── COURSE TILE ──────────────────────────────────────────────────────────

    private JPanel makeCourseTile(int courseId, String name, String code, Color color) {
        JPanel tile = new JPanel(new BorderLayout(0, 0));
        tile.setBackground(Color.WHITE);
        tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        tile.setPreferredSize(new Dimension(0, 78));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 232), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Left color strip
        JPanel strip = new JPanel();
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(7, 0));

        // Text
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 5));
        text.setBackground(Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setForeground(new Color(25, 40, 65));

        JLabel codeLbl = new JLabel(code);
        codeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        codeLbl.setForeground(color);

        text.add(nameLbl);
        text.add(codeLbl);

        // Right: delete + arrow
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));

        JButton delBtn = new JButton("Remove");
        delBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        delBtn.setBackground(new Color(245, 246, 248));
        delBtn.setForeground(new Color(185, 55, 45));
        delBtn.setFocusPainted(false);
        delBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        delBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 232), 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        delBtn.addActionListener(e -> deleteCourse(courseId, name));

        JLabel arrow = new JLabel(">");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrow.setForeground(new Color(190, 200, 215));

        right.add(delBtn);
        right.add(arrow);

        tile.add(strip, BorderLayout.WEST);
        tile.add(text,  BorderLayout.CENTER);
        tile.add(right, BorderLayout.EAST);

        // Click tile to open materials
        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!(e.getSource() instanceof JButton))
                    new MaterialsScreen(studentId, courseId, name, code, color).setVisible(true);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                tile.setBackground(new Color(245, 248, 255));
                text.setBackground(new Color(245, 248, 255));
                right.setBackground(new Color(245, 248, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBackground(Color.WHITE);
                text.setBackground(Color.WHITE);
                right.setBackground(Color.WHITE);
            }
        });

        return tile;
    }

    // ─── ADD COURSE DIALOG ────────────────────────────────────────────────────

    private void showAddCourseDialog() {
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(280, 36));

        JTextField codeField = new JTextField();
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        codeField.setPreferredSize(new Dimension(280, 36));

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        form.add(new JLabel("Course Name  (e.g.  Data Structures)"));
        form.add(nameField);
        form.add(new JLabel("Course Code  (e.g.  CS-301)"));
        form.add(codeField);

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
            if (name.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both fields are required.");
                return;
            }
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO courses (name, code, student_id) VALUES (?, ?, ?)");
                ps.setString(1, name);
                ps.setString(2, code);
                ps.setInt(3, studentId);
                ps.executeUpdate();
                conn.close();
                loadCourses(searchField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ─── DELETE COURSE ────────────────────────────────────────────────────────

    private void deleteCourse(int courseId, String name) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove \"" + name + "\"?\nAll materials inside will also be deleted.",
            "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                conn.prepareStatement(
                    "DELETE FROM materials WHERE course_id = " + courseId).executeUpdate();
                conn.prepareStatement(
                    "DELETE FROM courses WHERE id = " + courseId).executeUpdate();
                conn.close();
                loadCourses(searchField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
}