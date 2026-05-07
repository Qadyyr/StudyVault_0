import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class TodoScreen extends JFrame {

    private int    studentId;
    private String studentName;

    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            statusLabel;
    private JLabel            summaryLabel;

    public TodoScreen(int studentId, String studentName) {
        this.studentId   = studentId;
        this.studentName = studentName;

        setTitle("StudyVault - To Do List");
        setSize(760, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(235, 240, 248));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        loadTasks();
    }

    // ─── TOP BAR ──────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(142, 68, 173));
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setBackground(new Color(142, 68, 173));

        JLabel title = new JLabel("To Do List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Track your tasks, assignments and deadlines");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(220, 195, 240));

        left.add(title);
        left.add(sub);

        JButton backBtn = new JButton("< Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backBtn.setBackground(new Color(110, 50, 140));
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

    // ─── CENTER ───────────────────────────────────────────────────────────────

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(new Color(235, 240, 248));

        // Summary bar
        summaryLabel = new JLabel(" ");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryLabel.setForeground(new Color(100, 115, 135));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(12, 22, 8, 22));

        // Table
        String[] cols = {"#", "Task", "Priority", "Due Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(34);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 243, 248));
        table.getTableHeader().setForeground(new Color(50, 65, 85));
        table.setSelectionBackground(new Color(225, 210, 245));
        table.setGridColor(new Color(225, 230, 238));
        table.setShowVerticalLines(false);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(90);
        table.getColumnModel().getColumn(3).setMaxWidth(110);
        table.getColumnModel().getColumn(4).setMaxWidth(100);

        // Color priority column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    switch (String.valueOf(val)) {
                        case "High":   lbl.setForeground(new Color(192, 57, 43));
                                       lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); break;
                        case "Medium": lbl.setForeground(new Color(243, 156, 18));
                                       lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); break;
                        case "Low":    lbl.setForeground(new Color(39, 155, 105));
                                       lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); break;
                        default:       lbl.setForeground(new Color(80, 95, 115));
                    }
                }
                return lbl;
            }
        });

        // Color status column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    switch (String.valueOf(val)) {
                        case "Done":    lbl.setForeground(new Color(39, 155, 105));
                                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); break;
                        case "Pending": lbl.setForeground(new Color(243, 156, 18));
                                        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); break;
                        default:        lbl.setForeground(new Color(80, 95, 115));
                    }
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(215, 222, 232)));

        center.add(summaryLabel, BorderLayout.NORTH);
        center.add(scroll,       BorderLayout.CENTER);
        return center;
    }

    // ─── BOTTOM BAR ───────────────────────────────────────────────────────────

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("Select a task to mark it done or delete it.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(120, 135, 155));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(Color.WHITE);

        JButton addBtn    = btn("+ Add Task",   new Color(142, 68, 173));
        JButton doneBtn   = btn("Mark Done",    new Color(39, 155, 105));
        JButton undoBtn   = btn("Mark Pending", new Color(243, 156, 18));
        JButton deleteBtn = btn("Delete",       new Color(192, 57, 43));

        addBtn.addActionListener(e    -> showAddDialog());
        doneBtn.addActionListener(e   -> markTask(true));
        undoBtn.addActionListener(e   -> markTask(false));
        deleteBtn.addActionListener(e -> deleteTask());

        btns.add(addBtn);
        btns.add(doneBtn);
        btns.add(undoBtn);
        btns.add(deleteBtn);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(btns,        BorderLayout.EAST);
        return bar;
    }

    // ─── DATA ─────────────────────────────────────────────────────────────────

    private void loadTasks() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT id, task, priority, due_date, is_done FROM todos " +
                "WHERE student_id = ? ORDER BY is_done ASC, " +
                "FIELD(priority,'High','Medium','Low'), due_date ASC");
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            int row = 1;
            int pending = 0, done = 0;

            while (rs.next()) {
                boolean isDone = rs.getBoolean("is_done");
                String  status = isDone ? "Done" : "Pending";
                String  due    = rs.getString("due_date");
                tableModel.addRow(new Object[]{
                    row++,
                    rs.getString("task"),
                    rs.getString("priority"),
                    due != null ? due : "--",
                    status
                });
                if (isDone) done++; else pending++;
            }
            conn.close();
            summaryLabel.setText(
                pending + " pending   |   " + done + " done   |   " +
                (pending + done) + " total");
        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void showAddDialog() {
        JTextField taskField = new JTextField();
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskField.setPreferredSize(new Dimension(300, 34));

        JComboBox<String> priBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        priBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priBox.setSelectedItem("Medium");

        JTextField dueField = new JTextField("YYYY-MM-DD  (optional)");
        dueField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dueField.setForeground(Color.GRAY);
        dueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (dueField.getText().contains("optional")) {
                    dueField.setText("");
                    dueField.setForeground(new Color(25, 40, 65));
                }
            }
        });

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
        form.add(new JLabel("Task description  *"));
        form.add(taskField);
        form.add(new JLabel("Priority"));
        form.add(priBox);
        form.add(new JLabel("Due Date"));
        form.add(dueField);

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add New Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String task = taskField.getText().trim();
            if (task.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Task description is required.");
                return;
            }
            String due = dueField.getText().trim();
            if (due.contains("optional") || due.isEmpty()) due = null;

            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO todos (task, priority, due_date, is_done, student_id)" +
                    " VALUES (?, ?, ?, false, ?)");
                ps.setString(1, task);
                ps.setString(2, (String) priBox.getSelectedItem());
                if (due != null) ps.setString(3, due); else ps.setNull(3, java.sql.Types.DATE);
                ps.setInt(4, studentId);
                ps.executeUpdate();
                conn.close();
                loadTasks();
                status("Task added.", new Color(39, 130, 80));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void markTask(boolean done) {
        int row = table.getSelectedRow();
        if (row < 0) { status("Select a task first.", Color.RED); return; }

        // Get task id by matching task name
        String taskName = (String) tableModel.getValueAt(row, 1);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE todos SET is_done = ? WHERE student_id = ? AND task = ? LIMIT 1");
            ps.setBoolean(1, done);
            ps.setInt(2, studentId);
            ps.setString(3, taskName);
            ps.executeUpdate();
            conn.close();
            loadTasks();
            status(done ? "Marked as done." : "Marked as pending.",
                new Color(39, 130, 80));
        } catch (Exception ex) {
            status("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void deleteTask() {
        int row = table.getSelectedRow();
        if (row < 0) { status("Select a task to delete.", Color.RED); return; }

        String taskName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + taskName + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM todos WHERE student_id = ? AND task = ? LIMIT 1");
                ps.setInt(1, studentId);
                ps.setString(2, taskName);
                ps.executeUpdate();
                conn.close();
                loadTasks();
                status("Task deleted.", new Color(39, 130, 80));
            } catch (Exception ex) {
                status("Error: " + ex.getMessage(), Color.RED);
            }
        }
    }

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
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }
}