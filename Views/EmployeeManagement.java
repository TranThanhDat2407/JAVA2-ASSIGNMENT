/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Assignment.Views;

import java.util.ArrayList;
import java.util.List;
import Assignment.Models.Employee;
import Assignment.Utils.ThreadClock;
import Assignment.Utils.XFile;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.JTableHeader;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Duy Nguyen
 */
public class EmployeeManagement extends javax.swing.JFrame {

    DefaultTableModel defaultTable = new DefaultTableModel();
    List<Employee> list = new ArrayList<>();
    private int index = -1;
    private int prevColumnIndex = -1;
    // status header checked
    private boolean[] temp = new boolean[5];

    private static final String EMAIL_REGEX = "\\w+@\\w+(\\.\\w+){1,2}";
    private static final String EMPLOYEE_LIST_ASM_PATH = "employee-list-asm.dat";

    public EmployeeManagement() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Quản lý nhân viên");
        this.initTable();
        this.initValueTable();
        this.fillToTable();
        this.recordLabel.setText(this.getCurrentRecord());
        this.startClock();

        // add event click header table
        JTableHeader header = employeeTbl.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = header.columnAtPoint(e.getPoint());

                handleColumnClick(columnIndex);

                // set white for previous column 
                if (prevColumnIndex != -1) {
                    header.getColumnModel().getColumn(prevColumnIndex).setCellRenderer(new StatusColumnCellRenderer(new Color(255, 255, 255)));
                    employeeTbl.repaint();
                }

                // set color for current column
                header.getColumnModel().getColumn(columnIndex).setCellRenderer(new StatusColumnCellRenderer(new Color(217, 235, 249)));
                employeeTbl.repaint();

                prevColumnIndex = columnIndex;
            }
        });
        
        // event press keyboard
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
//                  event
            }

            @Override
            public void keyPressed(KeyEvent e) {
//                  event
            }

            @Override
            public void keyReleased(KeyEvent e) {
                search();
            }
        });

        // event press right keyboard
        nextBtn.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    nextBtnActionPerformed(null);
                }
            }
        });
    }

    // class set color for cells
    public class StatusColumnCellRenderer extends DefaultTableCellRenderer {

        private final Color textColor;

        public StatusColumnCellRenderer(Color textColor) {
            this.textColor = textColor;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel column = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            column.setBackground(textColor);
            return column;
        }
    }

    public void initTable() {
        defaultTable = (DefaultTableModel) employeeTbl.getModel();
        String[] cols = new String[]{"MÃ", "HỌ VÀ TÊN", "TUỔI", "EMAIL", "LƯƠNG"};

        defaultTable.setColumnIdentifiers(cols);
    }

    public void initValueTable() {
        list.add(new Employee("CODE-001", "Nguyễn Tấn Duy", 22, "duynguyen@edu.com", 12000));
        list.add(new Employee("CODE-002", "Đỗ Đức Minh", 22, "ducminh@gmail.com", 1800));
        list.add(new Employee("CODE-003", "Trần Thành Đạt", 25, "thanhdat@fpt.edu.vn", 127700));
        list.add(new Employee("CODE-004", "Nguyễn Tấn Bảo", 14, "baonguyen@gmail.com", 60000));
        list.add(new Employee("CODE-005", "Nguyễn Thị Hoa", 27, "hoanguyen18231@edu.com.vn", 18000));
        list.add(new Employee("CODE-006", "Phạm Thị Cúc", 22, "phamthi223@gmail.com", 15000));
        list.add(new Employee("CODE-007", "Trần Nguyễn Thị Lệ Nam", 29, "lennam238@fpt.edu.vn", 213800));
        list.add(new Employee("CODE-008", "Dương Nguyễn Duy", 19, "nguyenduy232@fpt.edu.vn", 60000));
    }

    public void fillToTable() {
        defaultTable.setRowCount(0);

        for (Employee item : list) {
            defaultTable.addRow(new Object[]{item.getCode(), item.getName(), item.getAge(), item.getEmail(), item.getSalary()});
        }
    }

    public void onClearForm() {
        this.codeField.setText("");
        this.nameField.setText("");
        this.ageField.setText("");
        this.emailField.setText("");
        this.salaryField.setText("");

        this.index = -1;
    }

    public void onSaveEmployee() {
        if (this.validateForm()) {
            Employee nv = new Employee("CODE-" + this.codeField.getText(), this.toUpperCase(this.nameField.getText()),
                    Integer.parseInt(this.ageField.getText()), this.emailField.getText(), Double.parseDouble(this.salaryField.getText()));
            if (this.index == -1) {
                defaultTable.addRow(new Object[]{"CODE-" + this.codeField.getText(), this.toUpperCase(this.nameField.getText()),
                    this.ageField.getText(), this.emailField.getText(), this.salaryField.getText()});

                list.add(nv);
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Successfuly", JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateEmployeeByCode(nv);
            }
        }
    }

    public void updateEmployeeByCode(Employee employeeUpdate) {
        if (this.validateForm()) {
            Employee employeeFinded = this.findByCode(employeeUpdate.getCode());

            if (employeeFinded != null) {
                defaultTable.setValueAt(this.nameField.getText(), index, 1);
                defaultTable.setValueAt(this.ageField.getText(), index, 2);
                defaultTable.setValueAt(this.emailField.getText(), index, 3);
                defaultTable.setValueAt(this.salaryField.getText(), index, 4);

                employeeFinded.setName(this.nameField.getText());
                employeeFinded.setAge(Integer.parseInt(this.ageField.getText()));
                employeeFinded.setEmail(this.emailField.getText());
                employeeFinded.setSalary(Double.parseDouble(this.salaryField.getText()));

                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Successfully", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void fillInfoToForm(int index) {
        Employee currentEmployee = this.list.get(index);

        this.codeField.setText(currentEmployee.getCode());
        this.nameField.setText(currentEmployee.getName());
        this.ageField.setText(String.valueOf(currentEmployee.getAge()));
        this.emailField.setText(currentEmployee.getEmail());
        this.salaryField.setText(String.valueOf(currentEmployee.getSalary()));
    }

    public void fillInfoToForm(Employee nv) {
        this.codeField.setText(nv.getCode());
        this.nameField.setText(nv.getName());
        this.ageField.setText(String.valueOf(nv.getAge()));
        this.emailField.setText(nv.getEmail());
        this.salaryField.setText(String.valueOf(nv.getSalary()));
    }

    public void onDeleteEmployee() {
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] selectedRows = employeeTbl.getSelectedRows();
        int option = JOptionPane.showConfirmDialog(this, "Bạn muốn xóa người dùng này?", "Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                this.defaultTable.removeRow(row);
                this.list.remove(row);
            }
        }

        JOptionPane.showMessageDialog(this, "Xóa thành công!", "Successful", JOptionPane.INFORMATION_MESSAGE);
        this.onClearForm();
    }

    public Employee findByCode(String code) {
        for (Employee item : list) {
            if (code.equalsIgnoreCase(item.getCode())) {
                return item;
            }
        }
        return null;
    }

    public String getCurrentRecord() {
        return "Record: " + (this.index + 1) + " of " + list.size();
    }

    public void firstButton() {
        index = 0;
        employeeTbl.setRowSelectionInterval(index, index);
        fillInfoToForm(index);
        recordLabel.setText(this.getCurrentRecord());
    }

    public void prevButton() {
        index--;
        if (index < 0) {
            index = list.size() - 1;
        }

        employeeTbl.setRowSelectionInterval(index, index);
        fillInfoToForm(index);
        recordLabel.setText(this.getCurrentRecord());
    }

    public void nextButton() {
        index++;

        if (index > list.size() - 1) {
            index = 0;
        }

        employeeTbl.setRowSelectionInterval(index, index);
        fillInfoToForm(index);
        recordLabel.setText(this.getCurrentRecord());

    }

    public void lastButton() {
        index = list.size() - 1;

        employeeTbl.setRowSelectionInterval(index, index);
        fillInfoToForm(index);
        recordLabel.setText(this.getCurrentRecord());
    }

    public boolean validateForm() {
        if (codeField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Chưa nhập mã nhân viên", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        for (Employee item : list) {
            if (("CODE-" + this.codeField.getText()).equalsIgnoreCase(item.getCode())) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không được trùng", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (nameField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Chưa nhập tên nhân viên", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!this.nameField.getText().matches("^[^0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*$")) {
            JOptionPane.showMessageDialog(this, "Tên không được chứa số hoặc ký tự đặc biệt!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ageField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Chưa nhập tuổi nhân viên", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(ageField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Tuỗi không đúng định dạng", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (emailField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Chưa nhập email", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Matcher emailMatcher = Pattern.compile(EMAIL_REGEX).matcher(emailField.getText());
        if (!emailMatcher.matches()) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (salaryField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Chưa nhập lương nhân viên", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(salaryField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lương phải là số", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void saveFile() {
        try {
            XFile.writeObject(EMPLOYEE_LIST_ASM_PATH, list);
            JOptionPane.showMessageDialog(this, "Ghi file thành công", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Xảy ra lỗi khi ghi File", "Warning", JOptionPane.WARNING_MESSAGE);
            System.out.println("Write file error");
        }
    }

    public void readFile() {
        try {
            list = (List<Employee>) XFile.readObject(EMPLOYEE_LIST_ASM_PATH);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Xảy ra lỗi khi đọc File", "Warning", JOptionPane.WARNING_MESSAGE);
            System.out.println("Read file error " + e.getMessage());
        }
    }

    private void startClock() {
        ThreadClock clock = new ThreadClock(this.clockLabel);
        Thread t1 = new Thread(clock);
        t1.start();
    }

    private String toUpperCase(String fullname) {
        String[] arrName = fullname.trim().split("");
        arrName[0] = arrName[0].toUpperCase();

        String name = "";
        for (int i = 0; i < arrName.length; i++) {
            if (arrName[i].equals(" ")) {
                String firstCharUpperCase = arrName[i + 1].toUpperCase();
                arrName[i + 1] = firstCharUpperCase;
            }

            name += arrName[i];
        }
        return name;
    }

    public void handleColumnClick(int columnIndex) {
        for (int i = 0; i < 5; i++) {
            if (temp[i]) {
                temp[i] = false;
                switch (columnIndex) {
                    case 1:
                        sortNameDecrement();
                        break;
                    case 2:
                        this.sortAgeDecrement();
                        break;
                    case 4:
                        this.sortSalaryDecrement();
                        break;
                }

            } else {
                temp[columnIndex] = !temp[columnIndex];
                switch (columnIndex) {
                    case 1:
                        sortNameIncrement();
                        break;
                    case 2:
                        this.sortAgeIncrement();
                        break;
                    case 4:
                        this.sortSalaryIncrement();
                        break;
                }
            }
        }
    }

    public void sortAgeIncrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> Integer.compare(nv1.getAge(), nv2.getAge()));
        this.fillToTable();
    }

    public void sortAgeDecrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> Integer.compare(nv2.getAge(), nv1.getAge()));
        this.fillToTable();
    }

    public void sortSalaryIncrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> Double.compare(nv1.getSalary(), nv2.getSalary()));
        this.fillToTable();
    }

    public void sortSalaryDecrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> Double.compare(nv2.getSalary(), nv1.getSalary()));
        this.fillToTable();
    }

    public String getLastname(String fullname) {
        String lastname = fullname.substring(fullname.lastIndexOf(" "));

        return lastname;
    }

    public String getMiddlename(String fullname) {
        String middleName = fullname.substring(fullname.indexOf(" "), fullname.lastIndexOf(" "));

        return middleName;
    }

    public void sortNameIncrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> {
            int lastnameCompare = getLastname(nv1.getName()).compareToIgnoreCase(getLastname(nv2.getName()));
            int middlenameCompare = getMiddlename(nv1.getName()).compareToIgnoreCase(getMiddlename(nv2.getName()));

            return lastnameCompare == 0 ? middlenameCompare : lastnameCompare;
        });
        this.fillToTable();
    }

    public void sortNameDecrement() {
        Collections.sort(list, (Employee nv1, Employee nv2) -> {
            int lastnameCompare = getLastname(nv2.getName()).compareToIgnoreCase(getLastname(nv1.getName()));
            int middlenameCompare = getMiddlename(nv2.getName()).compareToIgnoreCase(getMiddlename(nv1.getName()));

            return lastnameCompare == 0 ? middlenameCompare : lastnameCompare;
        });
        this.fillToTable();
    }

    
    // search all fields
    private void search() {
        String searchText = searchField.getText().toLowerCase();
        defaultTable.setRowCount(0);
        for (Employee item : list) {
            if (item.containsKeyword(searchText)) {
                Object[] row = new Object[]{item.getCode(), item.getName(), item.getAge(), item.getEmail(), item.getSalary()};
                defaultTable.addRow(row);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        employeeTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        codeField = new javax.swing.JTextField();
        nameField = new javax.swing.JTextField();
        ageField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        salaryField = new javax.swing.JTextField();
        firstBtn = new javax.swing.JButton();
        preBtn = new javax.swing.JButton();
        nextBtn = new javax.swing.JButton();
        lastBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        employeeTbl = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        newBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        findBtn = new javax.swing.JButton();
        openBtn = new javax.swing.JButton();
        exitBtn = new javax.swing.JButton();
        recordLabel = new javax.swing.JLabel();
        clockLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();

        employeeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jScrollPane2.setViewportView(employeeTable);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("QUẢN LÝ NHÂN VIÊN");

        jLabel2.setText("MÃ NHÂN VIÊN");

        jLabel3.setText("HỌ TÊN");

        jLabel4.setText("TUỔI");

        jLabel5.setText("EMAIL");

        jLabel6.setText("LƯƠNG");

        ageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ageFieldActionPerformed(evt);
            }
        });

        firstBtn.setText("|<");
        firstBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstBtnActionPerformed(evt);
            }
        });

        preBtn.setText("<<");
        preBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preBtnActionPerformed(evt);
            }
        });

        nextBtn.setText(">>");
        nextBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBtnActionPerformed(evt);
            }
        });

        lastBtn.setText("|>");
        lastBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastBtnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));

        employeeTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        employeeTbl.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                employeeTblAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        employeeTbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employeeTblMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                employeeTblMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(employeeTbl);
        if (employeeTbl.getColumnModel().getColumnCount() > 0) {
            employeeTbl.getColumnModel().getColumn(0).setResizable(false);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        newBtn.setText("New");
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        deleteBtn.setText("Delete");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        findBtn.setText("Find");
        findBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findBtnActionPerformed(evt);
            }
        });

        openBtn.setText("Open");
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });

        exitBtn.setText("Exit");
        exitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exitBtn)
                    .addComponent(openBtn)
                    .addComponent(findBtn)
                    .addComponent(deleteBtn)
                    .addComponent(saveBtn)
                    .addComponent(newBtn))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(newBtn)
                .addGap(12, 12, 12)
                .addComponent(saveBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(findBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(openBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exitBtn)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        recordLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        recordLabel.setForeground(new java.awt.Color(255, 0, 0));
        recordLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recordLabel.setText("Record:  1 of 10");

        clockLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        clockLabel.setForeground(new java.awt.Color(255, 0, 0));
        clockLabel.setText("00:00 AM");

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("TÌM KIẾM");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 707, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(firstBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(preBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lastBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(recordLabel))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addComponent(emailField)
                                .addComponent(codeField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ageField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(salaryField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(clockLabel)
                        .addGap(68, 68, 68))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(clockLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(codeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(ageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(salaryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(firstBtn)
                            .addComponent(preBtn)
                            .addComponent(nextBtn)
                            .addComponent(lastBtn)
                            .addComponent(jLabel7)
                            .addComponent(recordLabel)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(46, 46, 46)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
        this.onClearForm();
    }//GEN-LAST:event_newBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        this.onSaveEmployee();
    }//GEN-LAST:event_saveBtnActionPerformed

    private void employeeTblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeTblMouseClicked
        index = this.employeeTbl.getSelectedRow();
        this.fillInfoToForm(index);
        this.recordLabel.setText(this.getCurrentRecord());
    }//GEN-LAST:event_employeeTblMouseClicked

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        this.onDeleteEmployee();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void exitBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBtnActionPerformed
        this.saveFile();
        System.exit(0);
    }//GEN-LAST:event_exitBtnActionPerformed

    private void findBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findBtnActionPerformed
        if (this.findByCode(codeField.getText()) == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhận viên!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.fillInfoToForm(this.findByCode(codeField.getText()));
    }//GEN-LAST:event_findBtnActionPerformed

    private void firstBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstBtnActionPerformed
        this.firstButton();
    }//GEN-LAST:event_firstBtnActionPerformed

    private void lastBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastBtnActionPerformed
        this.lastButton();
    }//GEN-LAST:event_lastBtnActionPerformed

    private void preBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preBtnActionPerformed
        this.prevButton();
    }//GEN-LAST:event_preBtnActionPerformed

    private void nextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBtnActionPerformed
        this.nextButton();
    }//GEN-LAST:event_nextBtnActionPerformed

    private void openBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
        this.readFile();
    }//GEN-LAST:event_openBtnActionPerformed

    private void employeeTblAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_employeeTblAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_employeeTblAncestorAdded

    private void employeeTblMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeeTblMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeeTblMousePressed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void ageFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ageFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ageFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EmployeeManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EmployeeManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EmployeeManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmployeeManagement.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EmployeeManagement().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ageField;
    private javax.swing.JLabel clockLabel;
    private javax.swing.JTextField codeField;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JTextField emailField;
    private javax.swing.JTable employeeTable;
    private javax.swing.JTable employeeTbl;
    private javax.swing.JButton exitBtn;
    private javax.swing.JButton findBtn;
    private javax.swing.JButton firstBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton lastBtn;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton newBtn;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton openBtn;
    private javax.swing.JButton preBtn;
    private javax.swing.JLabel recordLabel;
    private javax.swing.JTextField salaryField;
    private javax.swing.JButton saveBtn;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
