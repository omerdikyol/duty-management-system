import java.util.*;

public class ShiftScheduler {
    private List<Doctor> doctors;
    private Calendar month;
    private List<Department> departments;
    private Date startDate;
    private Date endDate;
    private Map<Date, Map<String, List<Doctor>>> availableDoctorsPerDay;
    private Map<String, Integer> departmentShiftsNeeded;

    public ShiftScheduler(List<Doctor> doctors, Calendar month, List<Department> departments, Date startDate, Date endDate) {
        this.doctors = doctors;
        this.month = month;
        this.departments = departments;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableDoctorsPerDay = preprocessAvailableDoctors();
        this.departmentShiftsNeeded = new HashMap<>();
        for (Department department : departments) {
            departmentShiftsNeeded.put(department.getName(), department.getDoctorsNeeded());
        }
    }

    public boolean distributeShifts() {
        return backtrack(startDate);
    }

    private boolean backtrack(Date currentDate) {
        if (currentDate.after(endDate)) {
            return validateShifts(); // Validate shifts after attempting to assign all
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        for (Department department : departments) {
            List<Doctor> assignedDoctors = new ArrayList<>();
            if (!assignShiftsForDay(department, currentDate, assignedDoctors)) {
                return false;
            }
        }

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return backtrack(stripTime(calendar.getTime()));
    }

    private boolean assignShiftsForDay(Department department, Date date, List<Doctor> assignedDoctors) {
        List<Doctor> availableDoctors = availableDoctorsPerDay.get(date).get(department.getName());

        if (availableDoctors == null || availableDoctors.size() < department.getDoctorsNeeded()) {
            return false; // Not enough doctors available for the department
        }

        Collections.shuffle(availableDoctors); // Shuffle to randomize assignments

        for (Doctor doctor : availableDoctors) {
            if (assignedDoctors.size() >= department.getDoctorsNeeded()) {
                break;
            }

            if (canAssignShift(doctor, date, department.getName())) {
                doctor.assignShift(date, department.getName());
                assignedDoctors.add(doctor);
                departmentShiftsNeeded.put(department.getName(), departmentShiftsNeeded.get(department.getName()) - 1);
            }
        }

        if (assignedDoctors.size() < department.getDoctorsNeeded()) {
            // Try to reassign from other departments to balance the shifts
            for (Doctor doctor : doctors) {
                if (assignedDoctors.size() >= department.getDoctorsNeeded()) {
                    break;
                }
                if (!assignedDoctors.contains(doctor) && canAssignShift(doctor, date, department.getName())) {
                    doctor.assignShift(date, department.getName());
                    assignedDoctors.add(doctor);
                    departmentShiftsNeeded.put(department.getName(), departmentShiftsNeeded.get(department.getName()) - 1);
                }
            }
        }

        return assignedDoctors.size() == department.getDoctorsNeeded();
    }

    private Map<Date, Map<String, List<Doctor>>> preprocessAvailableDoctors() {
        Map<Date, Map<String, List<Doctor>>> availableDoctorsPerDay = new HashMap<>();
        for (Doctor doctor : doctors) {
            Calendar calendar = (Calendar) month.clone();
            calendar.setTime(startDate);
            while (!calendar.getTime().after(endDate)) {
                Date date = stripTime(calendar.getTime());
                if (!doctor.getBusyDays().contains(date)) {
                    for (String department : doctor.getDepartments()) {
                        availableDoctorsPerDay
                                .computeIfAbsent(date, k -> new HashMap<>())
                                .computeIfAbsent(department, k -> new ArrayList<>())
                                .add(doctor);
                    }
                }
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        return availableDoctorsPerDay;
    }

    private boolean canAssignShift(Doctor doctor, Date date, String department) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date previousDay = stripTime(calendar.getTime());

        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date nextDay = stripTime(calendar.getTime());

        return !doctor.getShiftDates().containsKey(previousDay) &&
                !doctor.getShiftDates().containsKey(date) &&
                !doctor.getShiftDates().containsKey(nextDay) &&
                doctor.getDepartments().contains(department);
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

    private boolean validateShifts() {
        for (Doctor doctor : doctors) {
            if (doctor.getShiftDates().size() != doctor.getTotalShifts()) {
                return false; // Doctor does not have the required number of shifts
            }
        }
        return true; // All doctors have the required number of shifts
    }
}
