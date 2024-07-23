import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class ShiftSchedulerApp extends JFrame {
    private JTextField doctorNameField;
    private JTextField doctorShiftsField;
    private CustomJCalendar calendar;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private DefaultListModel<Doctor> doctorListModel;
    private JList<Doctor> doctorList;
    private JTextArea busyDaysArea;
    private JTextField departmentNameField;
    private JTextField departmentDoctorsNeededField;
    private DefaultListModel<Department> departmentListModel;
    private JList<Department> departmentList;
    private Doctor selectedDoctor;
    private List<Department> allDepartments;

    public ShiftSchedulerApp() {
        setTitle("Doctor Shift Scheduler");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        allDepartments = new ArrayList<>();

        // Menu bar with Import and Export options
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem importItem = new JMenuItem("Import TXT");
        JMenuItem exportItem = new JMenuItem("Export TXT");
        importItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importData();
            }
        });
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportData();
            }
        });
        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Left panel for displaying doctors and departments
        doctorListModel = new DefaultListModel<>();
        doctorList = new JList<>(doctorListModel);
        doctorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selectDoctor(doctorList.getSelectedValue());
            }
        });
        JScrollPane doctorListScrollPane = new JScrollPane(doctorList);

        // Panel for displaying departments
        departmentListModel = new DefaultListModel<>();
        departmentList = new JList<>(departmentListModel);
        departmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane departmentListScrollPane = new JScrollPane(departmentList);

        JPanel departmentPanel = new JPanel(new BorderLayout());
        departmentPanel.add(new JLabel("All Departments"), BorderLayout.NORTH);
        departmentPanel.add(departmentListScrollPane, BorderLayout.CENTER);

        JButton deleteDepartmentButton = new JButton("Delete Department");
        deleteDepartmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDepartment();
            }
        });
        departmentPanel.add(deleteDepartmentButton, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Doctors"), BorderLayout.NORTH);
        leftPanel.add(doctorListScrollPane, BorderLayout.CENTER);
        leftPanel.add(departmentPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        // Center panel for calendar and doctor details
        JPanel centerPanel = new JPanel(new BorderLayout());
        calendar = new CustomJCalendar();
        centerPanel.add(calendar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBusyDayButton = new JButton("Add Busy Day");
        JButton deleteBusyDayButton = new JButton("Delete Busy Day");
        JButton addButton = new JButton("Add Doctor");
        JButton deleteButton = new JButton("Delete Doctor");
        JButton scheduleButton = new JButton("Schedule Shifts");
        JButton assignDepartmentButton = new JButton("Assign Departments");
        buttonPanel.add(addBusyDayButton);
        buttonPanel.add(deleteBusyDayButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(assignDepartmentButton);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Right panel for busy days and doctor details
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Nested panel for busy days label and scroll pane
        JPanel busyDaysPanel = new JPanel(new BorderLayout());
        busyDaysPanel.add(new JLabel("Busy Days"), BorderLayout.NORTH);
        busyDaysArea = new JTextArea(10, 20);
        busyDaysArea.setEditable(false);
        JScrollPane busyDaysScrollPane = new JScrollPane(busyDaysArea);
        busyDaysPanel.add(busyDaysScrollPane, BorderLayout.CENTER);

        rightPanel.add(busyDaysPanel, BorderLayout.NORTH);

        add(rightPanel, BorderLayout.EAST);

        // North panel for month, year, doctor details input, and departments
        JPanel northPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        doctorNameField = new JTextField(20);
        doctorShiftsField = new JTextField(5);
        monthChooser = new JMonthChooser();
        yearChooser = new JYearChooser();

        gbc.gridx = 0;
        gbc.gridy = 0;
        northPanel.add(new JLabel("Doctor Name:"), gbc);
        gbc.gridx = 1;
        northPanel.add(doctorNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        northPanel.add(new JLabel("Number of Shifts:"), gbc);
        gbc.gridx = 1;
        northPanel.add(doctorShiftsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        northPanel.add(new JLabel("Select Month:"), gbc);
        gbc.gridx = 1;
        northPanel.add(monthChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        northPanel.add(new JLabel("Select Year:"), gbc);
        gbc.gridx = 1;
        northPanel.add(yearChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        northPanel.add(new JLabel("Department Name:"), gbc);
        departmentNameField = new JTextField(15);
        gbc.gridx = 1;
        northPanel.add(departmentNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        northPanel.add(new JLabel("Doctors Needed:"), gbc);
        departmentDoctorsNeededField = new JTextField(5);
        gbc.gridx = 1;
        northPanel.add(departmentDoctorsNeededField, gbc);

        JButton addDepartmentButton = new JButton("Add Department");
        addDepartmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDepartment();
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 5;
        northPanel.add(addDepartmentButton, gbc);

        add(northPanel, BorderLayout.NORTH);

        // Button Actions
        addBusyDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBusyDay();
            }
        });

        deleteBusyDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBusyDay();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDoctor();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDoctor();
            }
        });

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleShifts();
            }
        });

        assignDepartmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assignDepartments();
            }
        });
    }

    private void addBusyDay() {
        Date selectedDate = stripTime(calendar.getDate());
        if (selectedDoctor != null && !selectedDoctor.getBusyDays().contains(selectedDate)) {
            selectedDoctor.getBusyDays().add(selectedDate);
        }
        updateCalendarRenderer();
        updateBusyDaysArea();
    }

    private void deleteBusyDay() {
        Date selectedDate = stripTime(calendar.getDate());
        if (selectedDoctor != null && selectedDoctor.getBusyDays().contains(selectedDate)) {
            selectedDoctor.getBusyDays().remove(selectedDate);
        }
        updateCalendarRenderer();
        updateBusyDaysArea();
    }

    private void addDoctor() {
        String name = doctorNameField.getText();
        int totalShifts = Integer.parseInt(doctorShiftsField.getText());
        Doctor doctor = new Doctor(name, new ArrayList<>(), totalShifts);
        doctorListModel.addElement(doctor);
        doctorNameField.setText("");
        doctorShiftsField.setText("");
        departmentList.clearSelection();
        updateCalendarRenderer();
    }

    private void deleteDoctor() {
        int selectedIndex = doctorList.getSelectedIndex();
        if (selectedIndex != -1) {
            doctorListModel.remove(selectedIndex);
            selectedDoctor = null;
            doctorNameField.setText("");
            doctorShiftsField.setText("");
            busyDaysArea.setText("");
            calendar.setBusyDays(new ArrayList<>());
        }
    }

    private void deleteDepartment() {
        int selectedIndex = departmentList.getSelectedIndex();
        if (selectedIndex != -1) {
            Department selectedDepartment = departmentListModel.getElementAt(selectedIndex);
            allDepartments.remove(selectedDepartment);
            departmentListModel.remove(selectedIndex);
        }
    }

    private void selectDoctor(Doctor doctor) {
        if (doctor != null) {
            selectedDoctor = doctor;
            doctorNameField.setText(doctor.getName());
            doctorShiftsField.setText(String.valueOf(doctor.getTotalShifts()));
            departmentList.setSelectedIndices(getSelectedIndices(doctor.getDepartments()));
            updateCalendarRenderer();
            updateBusyDaysArea();
        }
    }

    private int[] getSelectedIndices(List<String> doctorDepartments) {
        List<Integer> indices = new ArrayList<>();
        for (String dept : doctorDepartments) {
            int index = getDepartmentNames().indexOf(dept);
            if (index != -1) {
                indices.add(index);
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }

    private List<String> getDepartmentNames() {
        List<String> departmentNames = new ArrayList<>();
        for (Department department : allDepartments) {
            departmentNames.add(department.getName());
        }
        return departmentNames;
    }

    private void scheduleShifts() {
        Calendar selectedMonth = Calendar.getInstance();
        selectedMonth.set(Calendar.MONTH, monthChooser.getMonth());
        selectedMonth.set(Calendar.YEAR, yearChooser.getYear());

        List<Doctor> doctorList = Collections.list(doctorListModel.elements());
        ShiftScheduler scheduler = new ShiftScheduler(doctorList, selectedMonth, allDepartments); // Assuming shifts are based on individual doctor needs
        scheduler.distributeShifts();

        displayScheduleTable(doctorList);
    }

    private void displayScheduleTable(List<Doctor> doctorList) {
        // Count total number of columns required
        int totalColumns = 1; // Initial column for days
        Map<String, Integer> departmentColumns = new HashMap<>();
        for (Department department : allDepartments) {
            totalColumns += department.getDoctorsNeeded();
            departmentColumns.put(department.getName(), department.getDoctorsNeeded());
        }

        // Prepare column names
        String[] columnNames = new String[totalColumns];
        columnNames[0] = "Day";
        int colIndex = 1;
        for (Department department : allDepartments) {
            for (int i = 0; i < department.getDoctorsNeeded(); i++) {
                columnNames[colIndex] = department.getName() + " (" + (i + 1) + ")";
                colIndex++;
            }
        }

        Calendar selectedMonth = Calendar.getInstance();
        selectedMonth.set(Calendar.MONTH, monthChooser.getMonth());
        selectedMonth.set(Calendar.YEAR, yearChooser.getYear());
        int daysInMonth = selectedMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        String[][] data = new String[daysInMonth][totalColumns];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int day = 1; day <= daysInMonth; day++) {
            data[day - 1][0] = String.valueOf(day);
            for (int j = 1; j < totalColumns; j++) {
                data[day - 1][j] = "";
            }
        }

        Map<String, Integer> departmentShiftCounters = new HashMap<>();
        for (Department department : allDepartments) {
            departmentShiftCounters.put(department.getName(), 0);
        }

        for (Doctor doctor : doctorList) {
            for (Entry<Date, String> entry : doctor.getShiftDates().entrySet()) {
                Date shiftDate = entry.getKey();
                Calendar shiftCalendar = Calendar.getInstance();
                shiftCalendar.setTime(shiftDate);
                int day = shiftCalendar.get(Calendar.DAY_OF_MONTH);

                String department = entry.getValue();
                int departmentIndex = getDepartmentIndex(departmentColumns, department) + departmentShiftCounters.get(department);
                data[day - 1][departmentIndex] = doctor.getName();
                departmentShiftCounters.put(department, (departmentShiftCounters.get(department) + 1) % departmentColumns.get(department));
            }
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        JDialog dialog = new JDialog(this, "Scheduled Shifts", true);
        dialog.setSize(800, 600);
        dialog.add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export to XLSX");
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportTableToExcel(table, selectedMonth.get(Calendar.MONTH), selectedMonth.get(Calendar.YEAR));
            }
        });
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);
        dialog.setJMenuBar(menuBar);

        dialog.setVisible(true);
    }

    private void exportTableToExcel(JTable table, int month, int year) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Append .xlsx extension if not present
            if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xlsx");
            }
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Duty List");

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue((month + 1) + ", " + year + " Duty List");

                // Create column headers
                Row columnHeaderRow = sheet.createRow(1);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    columnHeaderRow.createCell(i).setCellValue(table.getColumnName(i));
                }

                // Create data rows
                for (int i = 0; i < table.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 2);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        row.createCell(j).setCellValue(table.getValueAt(i, j).toString());
                    }
                }

                workbook.write(fos);
                workbook.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private int getDepartmentIndex(Map<String, Integer> departmentColumns, String department) {
        int index = 1;
        for (Map.Entry<String, Integer> entry : departmentColumns.entrySet()) {
            if (entry.getKey().equals(department)) {
                break;
            }
            index += entry.getValue();
        }
        return index;
    }


    private void updateCalendarRenderer() {
        if (selectedDoctor != null) {
            calendar.setBusyDays(selectedDoctor.getBusyDays());
        }
    }

    private void updateBusyDaysArea() {
        if (selectedDoctor != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            StringBuilder busyDaysText = new StringBuilder();
            for (Date busyDay : selectedDoctor.getBusyDays()) {
                busyDaysText.append(dateFormat.format(busyDay)).append("\n");
            }
            busyDaysArea.setText(busyDaysText.toString());
        }
    }

    private Date stripTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void addDepartment() {
        String departmentName = departmentNameField.getText();
        int doctorsNeeded = Integer.parseInt(departmentDoctorsNeededField.getText());
        if (!departmentName.isEmpty() && doctorsNeeded > 0 && getDepartmentNames().indexOf(departmentName) == -1) {
            Department department = new Department(departmentName, doctorsNeeded);
            allDepartments.add(department);
            departmentListModel.addElement(department);
            departmentNameField.setText("");
            departmentDoctorsNeededField.setText("");
        }
    }

    private void assignDepartments() {
        if (selectedDoctor != null) {
            JDialog dialog = new JDialog(this, "Assign Departments", true);
            dialog.setSize(300, 400);
            dialog.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            List<JCheckBox> checkBoxes = new ArrayList<>();
            for (Department department : allDepartments) {
                JCheckBox checkBox = new JCheckBox(department.getName());
                if (selectedDoctor.getDepartments().contains(department.getName())) {
                    checkBox.setSelected(true);
                }
                checkBoxes.add(checkBox);
                panel.add(checkBox);
            }

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    List<String> selectedDepartments = new ArrayList<>();
                    for (JCheckBox checkBox : checkBoxes) {
                        if (checkBox.isSelected()) {
                            selectedDepartments.add(checkBox.getText());
                        }
                    }
                    selectedDoctor.setDepartments(selectedDepartments);
                    dialog.dispose();
                }
            });

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(saveButton, BorderLayout.SOUTH);

            dialog.setVisible(true);
        }
    }

    private void importData() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isDepartmentsSection = false;
                boolean isDoctorsSection = false;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.equals("Departments:")) {
                        isDepartmentsSection = true;
                        isDoctorsSection = false;
                    } else if (line.equals("Doctors:")) {
                        isDepartmentsSection = false;
                        isDoctorsSection = true;
                    } else if (isDepartmentsSection && !line.isEmpty()) {
                        String[] parts = line.split(",");
                        String departmentName = parts[0].trim();
                        int doctorsNeeded = Integer.parseInt(parts[1].trim());
                        Department department = new Department(departmentName, doctorsNeeded);
                        allDepartments.add(department);
                        departmentListModel.addElement(department);
                    } else if (isDoctorsSection && !line.isEmpty()) {
                        String[] parts = line.split(",");
                        String doctorName = parts[0].trim();
                        int numberOfShifts = Integer.parseInt(parts[1].trim());
                        List<String> assignedDepartments = Arrays.asList(parts[2].trim().split("\\|"));
                        Doctor doctor = new Doctor(doctorName, new ArrayList<>(), numberOfShifts);
                        doctor.setDepartments(assignedDepartments);
                        doctorListModel.addElement(doctor);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Append .txt extension if not present
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write("Departments:\n");
                for (Department department : allDepartments) {
                    writer.write(department.getName() + "," + department.getDoctorsNeeded() + "\n");
                }
                writer.write("\nDoctors:\n");
                for (int i = 0; i < doctorListModel.getSize(); i++) {
                    Doctor doctor = doctorListModel.getElementAt(i);
                    writer.write(doctor.getName() + "," + doctor.getTotalShifts() + "," +
                            String.join("|", doctor.getDepartments()) + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShiftSchedulerApp().setVisible(true);
            }
        });
    }
}
