import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ShiftSchedulerApp extends JFrame {
    private JTextField doctorNameField;
    private JTextField doctorShiftsField;
    private CustomJCalendar calendar;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private JTextArea scheduleArea;
    private DefaultListModel<Doctor> doctorListModel;
    private JList<Doctor> doctorList;
    private JLabel photoLabel;
    private JTextArea busyDaysArea;
    private Doctor selectedDoctor;

    public ShiftSchedulerApp() {
        setTitle("Doctor Shift Scheduler");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left panel for displaying doctors
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
        doctorListScrollPane.setPreferredSize(new Dimension(200, 0));
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Doctors"), BorderLayout.NORTH);
        leftPanel.add(doctorListScrollPane, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // Center panel for calendar and doctor details
        JPanel centerPanel = new JPanel(new BorderLayout());
        calendar = new CustomJCalendar();
        centerPanel.add(calendar, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBusyDayButton = new JButton("Add Busy Day");
        JButton addButton = new JButton("Add Doctor");
        JButton deleteButton = new JButton("Delete Doctor");
        JButton scheduleButton = new JButton("Schedule Shifts");
        buttonPanel.add(addBusyDayButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(scheduleButton);
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


        JButton uploadPhotoButton = new JButton("Upload Photo");
        uploadPhotoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadPhoto();
            }
        });
        rightPanel.add(uploadPhotoButton, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        // North panel for month, year, and doctor details input
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

        add(northPanel, BorderLayout.NORTH);

        // South panel for displaying schedule
        scheduleArea = new JTextArea(10, 40);
        scheduleArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(new JLabel("Schedule"), BorderLayout.NORTH);
        southPanel.add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Button Actions
        addBusyDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBusyDay();
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
    }

    private void addBusyDay() {
        Date selectedDate = stripTime(calendar.getDate());
        if (selectedDoctor != null && !selectedDoctor.getBusyDays().contains(selectedDate)) {
            selectedDoctor.getBusyDays().add(selectedDate);
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
        updateCalendarRenderer();
    }

    private void deleteDoctor() {
        int selectedIndex = doctorList.getSelectedIndex();
        if (selectedIndex != -1) {
            doctorListModel.remove(selectedIndex);
            selectedDoctor = null;
            doctorNameField.setText("");
            doctorShiftsField.setText("");
            photoLabel.setIcon(null);
            busyDaysArea.setText("");
            calendar.setBusyDays(new ArrayList<>());
        }
    }

    private void selectDoctor(Doctor doctor) {
        if (doctor != null) {
            selectedDoctor = doctor;
            doctorNameField.setText(doctor.getName());
            doctorShiftsField.setText(String.valueOf(doctor.getTotalShifts()));
            // Load the doctor's photo if available
            if (doctor.getPhoto() != null) {
                Image img = doctor.getPhoto().getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
            } else {
                photoLabel.setIcon(null);
            }
            updateCalendarRenderer();
            updateBusyDaysArea();
        }
    }

    private void uploadPhoto() {
        if (selectedDoctor != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                ImageIcon photo = new ImageIcon(fileChooser.getSelectedFile().getPath());
                selectedDoctor.setPhoto(photo);
                Image img = photo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
            }
        }
    }

    private void scheduleShifts() {
        Calendar selectedMonth = Calendar.getInstance();
        selectedMonth.set(Calendar.MONTH, monthChooser.getMonth());
        selectedMonth.set(Calendar.YEAR, yearChooser.getYear());

        List<Doctor> doctorList = Collections.list(doctorListModel.elements());
        ShiftScheduler scheduler = new ShiftScheduler(doctorList, selectedMonth); // Assuming shifts are based on individual doctor needs
        scheduler.distributeShifts();

        StringBuilder schedule = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Doctor doctor : doctorList) {
            schedule.append(doctor.getName()).append(": ").append(doctor.getAssignedShifts()).append(" shifts\n");
            for (Date date : doctor.getShiftDates()) {
                schedule.append("    ").append(dateFormat.format(date)).append("\n");
            }
        }
        scheduleArea.setText(schedule.toString());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShiftSchedulerApp().setVisible(true);
            }
        });
    }
}
