import java.util.*;

public class ShiftScheduler {
    private List<Doctor> doctors;
    private Calendar calendar;
    private List<Department> departments;

    public ShiftScheduler(List<Doctor> doctors, Calendar calendar, List<Department> departments) {
        this.doctors = doctors;
        this.calendar = calendar;
        this.departments = departments;
    }

    public void distributeShifts() {
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Random random = new Random();

        for (Doctor doctor : doctors) {
            doctor.resetShifts();
        }

        Map<String, Map<Date, List<Doctor>>> departmentShifts = new HashMap<>();
        for (Department department : departments) {
            departmentShifts.put(department.getName(), new HashMap<>());
        }

        for (int day = 1; day <= daysInMonth; day++) {
            for (Department department : departments) {
                Map<Date, List<Doctor>> shiftsForDepartment = departmentShifts.get(department.getName());
                Calendar currentDay = (Calendar) calendar.clone();
                currentDay.set(Calendar.DAY_OF_MONTH, day);
                Date shiftDate = stripTime(currentDay.getTime());

                if (!shiftsForDepartment.containsKey(shiftDate)) {
                    shiftsForDepartment.put(shiftDate, new ArrayList<>());
                }

                List<Doctor> assignedDoctors = shiftsForDepartment.get(shiftDate);
                int attempts = 0;
                while (assignedDoctors.size() < department.getDoctorsNeeded() && attempts < 100) {
                    Doctor randomDoctor = doctors.get(random.nextInt(doctors.size()));
                    if (canAssignShift(randomDoctor, shiftDate, shiftsForDepartment)) {
                        randomDoctor.assignShift(shiftDate, department.getName());
                        assignedDoctors.add(randomDoctor);
                    }
                    attempts++;
                }

                if (attempts >= 100) {
                    System.out.println("Failed to assign enough doctors for " + department.getName() + " on " + shiftDate);
                }
            }
        }
    }

    private boolean canAssignShift(Doctor doctor, Date shiftDate, Map<Date, List<Doctor>> shiftsForDepartment) {
        if (doctor.getBusyDays().contains(shiftDate) || doctor.getShiftDates().containsKey(shiftDate)) {
            return false;
        }

        Calendar shiftCalendar = Calendar.getInstance();
        shiftCalendar.setTime(shiftDate);
        shiftCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Date prevDay = stripTime(shiftCalendar.getTime());

        shiftCalendar.add(Calendar.DAY_OF_MONTH, 2);
        Date nextDay = stripTime(shiftCalendar.getTime());

        if (doctor.getShiftDates().containsKey(prevDay) || doctor.getShiftDates().containsKey(nextDay)) {
            return false;
        }

        return true;
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
}
