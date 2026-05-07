import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class MaterialsScreen extends JFrame {

    private int    studentId;
    private int    courseId;
    private String courseName;
    private String courseCode;
    private Color  courseColor;

    private String        activeSection = "Theory";
    private JPanel        sectionBar;
    private DefaultTableModel tableModel;
    private JTable        table;
    private JLabel        statusLabel;
    private JTextField    searchField;

    private static final String[] SECTIONS = {"Theory", "Practical", "Assignments", "Projects"};
    private static final Color[]  SEC_COLORS = {
        new Color(41,  128, 185),
        new Color(39,  155, 105),
        new Color(192, 57,  43),
        new Color(142, 68,  173)
    };

    public MaterialsScreen(int studentId, int courseId,
                           String courseName, String courseCode, Color courseColor) {
        this.studentId   = studentId;
        this.courseId    = courseId;
        this.courseName  = courseName;
        this.courseCode  = courseCode;
        this.courseColor = courseColor;

        setTitle("StudyVault - " + courseName);
        setSize(820, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 248));

        add(buildTopBar(),     BorderLayout.NORTH);
        add(buildCenter(),     BorderLayout.CENTER);
        add(buildBottomBar(),  BorderLayout.SOUTH);

        loadMaterials();
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(courseColor);

        // Course info row
        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(courseColor);
        info.setBorder(BorderFactory.createEmptyBorder(14, 24, 10, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 3));
        left.setBackground(courseColor);

        JLabel nameLbl = new JLabel(courseName);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLbl.setForeground(Color.WHITE);

        JLabel codeLbl = new JLabel(courseCode + "  |  Study Materials");
        codeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        codeLbl.setForeground(new Color(220, 235, 250));

        left.add(nameLbl);
        left.add(codeLbl);

        JButton backBtn = new JButton("< Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(0, 0, 0, 30));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        backBtn.addActionListener(e -> dispose());

        info.add(left,    BorderLayout.WEST);
        info.add(backBtn, BorderLayout.EAST);

        // Section tab row
        sectionBar = new JPanel(new GridLayout(1, 4, 0, 0));
        sectionBar.setBackground(courseColor);

        for (int i = 0; i < SECTIONS.length; i++) {
            final String sec   = SECTIONS[i];
            final Color  color = SEC_COLORS[i];
            JButton tab = makeSectionTab(sec, color);
            sectionBar.add(tab);
        }

        wrap.add(info,       BorderLayout.CENTER);
        wrap.add(sectionBar, BorderLayout.SOUTH);
        return wrap;
    }

    private JButton makeSectionTab(String section, Color color) {
        JButton tab = new JButton(section);
        boolean active = section.equals(activeSection);

        tab.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        tab.setBackground(active ? Color.WHITE : new Color(0, 0, 0, 25));
        tab.setForeground(active ? color : Color.WHITE);
        tab.setFocusPainted(false);
        tab.setBorderPainted(false);
        tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tab.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        tab.setOpaque(true);

        tab.addActionListener(e -> {
            activeSection = section;
            refreshSectionTabs();
            loadMaterials();
        });

        return tab;
    }

    private void refreshSectionTabs() {
        sectionBar.removeAll();
        for (int i = 0; i < SECTIONS.length; i++) {
            final String sec   = SECTIONS[i];
            final Color  color = SEC_COLORS[i];
            sectionBar.add(makeSectionTab(sec, color));
        }
        sectionBar.revalidate();
        sectionBar.repaint();
    }

    // ─── CENTER ───────────────────────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(new Color(235, 240, 248));

        // Search bar
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(new Color(235, 240, 248));
        searchRow.setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(195, 205, 220), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setPreferredSize(new Dimension(0, 38));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(courseColor);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        searchBtn.addActionListener(e -> loadMaterials());
        searchField.addActionListener(e -> loadMaterials());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clearBtn.setBackground(new Color(200, 208, 220));
        clearBtn.setForeground(new Color(50, 65, 85));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        clearBtn.addActionListener(e -> { searchField.setText(""); loadMaterials(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setBackground(new Color(235, 240, 248));
        btns.add(searchBtn);
        btns.add(clearBtn);

        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(btns,        BorderLayout.EAST);

        // Table
        String[] cols = {"#", "Title", "Description", "File Path"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(34);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 243, 248));
        table.getTableHeader().setForeground(new Color(50, 65, 85));
        table.setSelectionBackground(new Color(210, 228, 248));
        table.setGridColor(new Color(225, 230, 238));
        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);

        // Double click to open file
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openFile();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(215, 222, 232)));

        center.add(searchRow, BorderLayout.NORTH);
        center.add(scroll,    BorderLayout.CENTER);
        return center;
    }

    // ─── BOTTOM BAR ───────────────────────────────────────────────────────────

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("Double-click a row to open the file.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(120, 135, 155));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(Color.WHITE);

        JButton addBtn  = btn("+ Add",   courseColor);
        JButton openBtn = btn("Open File", new Color(39, 155, 105));
        JButton delBtn  = btn("Delete",   new Color(192, 57, 43));

        addBtn.addActionListener(e  -> showAddDialog());
        openBtn.addActionListener(e -> openFile());
        delBtn.addActionListener(e  -> deleteSelected());

        btns.add(addBtn);
        btns.add(openBtn);
        btns.add(delBtn);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(btns,        BorderLayout.EAST);
        return bar;
    }

    // ─── LOAD ─────────────────────────────────────────────────────────────────

    private void loadMaterials() {
        tableModel.setRowCount(0);
        String keyword = searchField != null ? searchField.getText().trim() : "";

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT title, description, file_path FROM materials " +
                         "WHERE course_id = ? AND section = ?";
            if (!keyword.isEmpty()) sql += " AND title LIKE ?";
            sql += " ORDER BY title";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            ps.setString(2, activeSection);
            if (!keyword.isEmpty()) ps.setString(3, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            int row = 1;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    row++,
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("file_path")
                });
            }
            conn.close();

            int count = tableModel.getRowCount();
            status(count + " item" + (count != 1 ? "s" : "") + " in " + activeSection
                + (keyword.isEmpty() ? "" : "  (filtered)")
                + "   |   Double-click to open file", new Color(100, 115, 135));

        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    // ─── ADD DIALOG ───────────────────────────────────────────────────────────

    private void showAddDialog() {
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setPreferredSize(new Dimension(300, 34));

        JTextField descField = new JTextField();
        descField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descField.setPreferredSize(new Dimension(300, 34));

        JTextField fileField = new JTextField();
        fileField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton browseBtn = new JButton("Browse");
        browseBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                fileField.setText(fc.getSelectedFile().getAbsolutePath());
        });

        JPanel fileRow = new JPanel(new BorderLayout(6, 0));
        fileRow.add(fileField,  BorderLayout.CENTER);
        fileRow.add(browseBtn,  BorderLayout.EAST);

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        form.add(new JLabel("Title  *"));
        form.add(titleField);
        form.add(new JLabel("Description  (optional)"));
        form.add(descField);
        form.add(new JLabel("File Path  (optional - click Browse)"));
        form.add(fileRow);

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add to " + activeSection + "  -  " + courseName,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title is required.");
                return;
            }
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO materials (title, description, file_path, section, course_id, student_id)" +
                    " VALUES (?, ?, ?, ?, ?, ?)");
                ps.setString(1, title);
                ps.setString(2, descField.getText().trim());
                ps.setString(3, fileField.getText().trim());
                ps.setString(4, activeSection);
                ps.setInt(5, courseId);
                ps.setInt(6, studentId);
                ps.executeUpdate();
                conn.close();
                loadMaterials();
                status("Added: " + title, new Color(39, 130, 80));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ─── OPEN FILE ────────────────────────────────────────────────────────────

    private void openFile() {
        int row = table.getSelectedRow();
        if (row < 0) { status("Select a row first.", Color.RED); return; }

        String path = (String) tableModel.getValueAt(row, 3);
        if (path == null || path.isEmpty()) {
            status("No file path for this item.", Color.RED); return;
        }
        try {
            File f = new File(path);
            if (!f.exists()) { status("File not found: " + path, Color.RED); return; }
            Desktop.getDesktop().open(f);
            status("Opening: " + f.getName(), new Color(39, 130, 80));
        } catch (Exception ex) {
            status("Cannot open: " + ex.getMessage(), Color.RED);
        }
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { status("Select a row to delete.", Color.RED); return; }

        String title = (String) tableModel.getValueAt(row, 1);
        int confirm  = JOptionPane.showConfirmDialog(this,
            "Delete \"" + title + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM materials WHERE course_id = ? AND section = ? AND title = ? LIMIT 1");
                ps.setInt(1, courseId);
                ps.setString(2, activeSection);
                ps.setString(3, title);
                ps.executeUpdate();
                conn.close();
                loadMaterials();
                status("Deleted: " + title, new Color(39, 130, 80));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }
}