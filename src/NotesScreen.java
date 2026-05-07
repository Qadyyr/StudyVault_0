import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;

public class NotesScreen extends JFrame {

    private int    studentId;
    private String studentName;

    private JList<String>        noteList;
    private DefaultListModel<String> listModel;
    private JTextField           titleField;
    private JTextArea            contentArea;
    private JComboBox<String>    courseCombo;
    private JLabel               statusLabel;
    private JTextField           searchField;

    private java.util.List<int[]> noteIds = new ArrayList<>(); // stores [noteId, courseId]

    public NotesScreen(int studentId, String studentName) {
        this.studentId   = studentId;
        this.studentName = studentName;

        setTitle("StudyVault - My Notes");
        setSize(860, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 248));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);

        loadCourses();
        loadNotes("");
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(39, 155, 105));
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(new Color(39, 155, 105));

        JLabel title = new JLabel("My Notes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Write and save notes for your courses");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(195, 235, 215));

        left.add(title);
        left.add(sub);

        JButton backBtn = new JButton("< Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(30, 120, 80));
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

    private JSplitPane buildBody() {
        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            buildSidebar(),
            buildEditor()
        );
        split.setDividerLocation(260);
        split.setDividerSize(3);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }

    // ─── SIDEBAR ──────────────────────────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
            new Color(210, 218, 228)));

        // Search bar at top
        JPanel searchRow = new JPanel(new BorderLayout(6, 0));
        searchRow.setBackground(new Color(245, 248, 252));
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(195, 205, 220), 1),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));

        JButton searchBtn = new JButton("Go");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setBackground(new Color(39, 155, 105));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        searchBtn.addActionListener(e -> loadNotes(searchField.getText().trim()));
        searchField.addActionListener(e -> loadNotes(searchField.getText().trim()));

        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchBtn,   BorderLayout.EAST);

        // Notes list
        listModel = new DefaultListModel<>();
        noteList  = new JList<>(listModel);
        noteList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noteList.setFixedCellHeight(46);
        noteList.setBackground(new Color(245, 248, 252));
        noteList.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        noteList.setCellRenderer(new NoteListRenderer());
        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedNote();
        });

        JScrollPane listScroll = new JScrollPane(noteList);
        listScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
            new Color(210, 218, 228)));

        // New note button at bottom
        JButton newBtn = new JButton("+ New Note");
        newBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newBtn.setBackground(new Color(39, 155, 105));
        newBtn.setForeground(Color.WHITE);
        newBtn.setFocusPainted(false);
        newBtn.setBorderPainted(false);
        newBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newBtn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        newBtn.addActionListener(e -> clearEditor());

        panel.add(searchRow,  BorderLayout.NORTH);
        panel.add(listScroll, BorderLayout.CENTER);
        panel.add(newBtn,     BorderLayout.SOUTH);
        return panel;
    }

    // ─── EDITOR ───────────────────────────────────────────────────────────────

    private JPanel buildEditor() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Color.WHITE);

        // Title + course row
        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setBackground(Color.WHITE);
        topRow.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 230, 238)),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        titleField = new JTextField("Note title...");
        titleField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleField.setForeground(new Color(180, 190, 200));
        titleField.setBorder(BorderFactory.createEmptyBorder());
        titleField.setBackground(Color.WHITE);
        titleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (titleField.getText().equals("Note title...")) {
                    titleField.setText("");
                    titleField.setForeground(new Color(25, 40, 65));
                }
            }
        });

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        courseCombo.setPreferredSize(new Dimension(180, 32));

        topRow.add(titleField,  BorderLayout.CENTER);
        topRow.add(courseCombo, BorderLayout.EAST);

        // Content area
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        contentArea.setForeground(new Color(30, 45, 65));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());

        // Bottom toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(245, 248, 252));
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(215, 222, 232)),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        statusLabel = new JLabel("Select a note or click + New Note");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(130, 145, 165));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(new Color(245, 248, 252));

        JButton saveBtn   = toolbarBtn("Save Note",   new Color(39, 155, 105));
        JButton deleteBtn = toolbarBtn("Delete Note", new Color(192, 57, 43));

        saveBtn.addActionListener(e   -> saveNote());
        deleteBtn.addActionListener(e -> deleteNote());

        btnRow.add(saveBtn);
        btnRow.add(deleteBtn);

        toolbar.add(statusLabel, BorderLayout.WEST);
        toolbar.add(btnRow,      BorderLayout.EAST);

        panel.add(topRow,        BorderLayout.NORTH);
        panel.add(contentScroll, BorderLayout.CENTER);
        panel.add(toolbar,       BorderLayout.SOUTH);
        return panel;
    }

    // ─── DATA ─────────────────────────────────────────────────────────────────

    private void loadCourses() {
        courseCombo.removeAllItems();
        courseCombo.addItem("-- Select Course --");
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT name FROM courses WHERE student_id = ? ORDER BY name");
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) courseCombo.addItem(rs.getString("name"));
            conn.close();
        } catch (Exception ex) {
            status("Error loading courses: " + ex.getMessage(), Color.RED);
        }
    }

    private void loadNotes(String keyword) {
        listModel.clear();
        noteIds.clear();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT n.id, n.title, c.name FROM notes n " +
                         "LEFT JOIN courses c ON n.course_id = c.id " +
                         "WHERE n.student_id = ?";
            if (!keyword.isEmpty()) sql += " AND n.title LIKE ?";
            sql += " ORDER BY n.id DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            if (!keyword.isEmpty()) ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int    id         = rs.getInt("id");
                String noteTitle  = rs.getString("title");
                String courseName = rs.getString("name");
                listModel.addElement(noteTitle + (courseName != null ? "  |  " + courseName : ""));
                noteIds.add(new int[]{id});
            }
            conn.close();
        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void loadSelectedNote() {
        int idx = noteList.getSelectedIndex();
        if (idx < 0 || idx >= noteIds.size()) return;
        int noteId = noteIds.get(idx)[0];

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT n.title, n.content, c.name FROM notes n " +
                "LEFT JOIN courses c ON n.course_id = c.id WHERE n.id = ?");
            ps.setInt(1, noteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                titleField.setForeground(new Color(25, 40, 65));
                contentArea.setText(rs.getString("content"));
                contentArea.setCaretPosition(0);
                String cname = rs.getString("name");
                if (cname != null) {
                    for (int i = 0; i < courseCombo.getItemCount(); i++) {
                        if (courseCombo.getItemAt(i).equals(cname)) {
                            courseCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
                status("Note loaded.", new Color(39, 130, 80));
            }
            conn.close();
        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void saveNote() {
        String title   = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String course  = (String) courseCombo.getSelectedItem();

        if (title.isEmpty() || title.equals("Note title...")) {
            status("Please enter a title.", Color.RED);
            titleField.requestFocus();
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();

            // Get course id
            int courseId = -1;
            if (course != null && !course.startsWith("--")) {
                PreparedStatement cp = conn.prepareStatement(
                    "SELECT id FROM courses WHERE name = ? AND student_id = ?");
                cp.setString(1, course);
                cp.setInt(2, studentId);
                ResultSet cr = cp.executeQuery();
                if (cr.next()) courseId = cr.getInt("id");
            }

            int idx = noteList.getSelectedIndex();
            if (idx >= 0 && idx < noteIds.size()) {
                // Update existing
                int noteId = noteIds.get(idx)[0];
                PreparedStatement ps = conn.prepareStatement(
                    "UPDATE notes SET title = ?, content = ?, course_id = ? WHERE id = ?");
                ps.setString(1, title);
                ps.setString(2, content);
                if (courseId > 0) ps.setInt(3, courseId); else ps.setNull(3, java.sql.Types.INTEGER);
                ps.setInt(4, noteId);
                ps.executeUpdate();
                status("Note updated.", new Color(39, 130, 80));
            } else {
                // Insert new
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO notes (title, content, course_id, student_id) VALUES (?, ?, ?, ?)");
                ps.setString(1, title);
                ps.setString(2, content);
                if (courseId > 0) ps.setInt(3, courseId); else ps.setNull(3, java.sql.Types.INTEGER);
                ps.setInt(4, studentId);
                ps.executeUpdate();
                status("Note saved.", new Color(39, 130, 80));
            }
            conn.close();
            loadNotes(searchField.getText().trim());
        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void deleteNote() {
        int idx = noteList.getSelectedIndex();
        if (idx < 0) { status("Select a note to delete.", Color.RED); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this note?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int noteId = noteIds.get(idx)[0];
                Connection conn = DBConnection.getConnection();
                conn.prepareStatement("DELETE FROM notes WHERE id = " + noteId).executeUpdate();
                conn.close();
                clearEditor();
                loadNotes(searchField.getText().trim());
                status("Note deleted.", new Color(39, 130, 80));
            } catch (Exception ex) {
                status("Error: " + ex.getMessage(), Color.RED);
            }
        }
    }

    private void clearEditor() {
        noteList.clearSelection();
        titleField.setText("Note title...");
        titleField.setForeground(new Color(180, 190, 200));
        contentArea.setText("");
        courseCombo.setSelectedIndex(0);
        status("New note — type a title and start writing.", new Color(100, 115, 135));
        titleField.requestFocus();
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private JButton toolbarBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // ─── LIST RENDERER ────────────────────────────────────────────────────────

    private class NoteListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 226, 234)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            if (isSelected) {
                lbl.setBackground(new Color(210, 235, 220));
                lbl.setForeground(new Color(25, 80, 50));
            } else {
                lbl.setBackground(new Color(245, 248, 252));
                lbl.setForeground(new Color(40, 55, 75));
            }
            return lbl;
        }
    }
}