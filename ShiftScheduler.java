import java.util.*;

public class ShiftScheduler {
    private List<Doctor> doctors;
    private Calendar calendar;

    public ShiftScheduler(List<Doctor> doctors, Calendar calendar) {
        this.doctors = doctors;
        this.calendar = calendar;
    }

    public void distributeShifts() {
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Random random = new Random();

        for (Doctor doctor : doctors) {
            doctor.resetShifts();
        }

        for (Doctor doctor : doctors) {
            List<Date> assignedShifts = new ArrayList<>();

            while (doctor.getAssignedShifts() < doctor.getTotalShifts()) {
                int day = random.nextInt(daysInMonth) + 1;
                Calendar currentDay = (Calendar) calendar.clone();
                currentDay.set(Calendar.DAY_OF_MONTH, day);

                if (canAssignShift(doctor, currentDay, assignedShifts)) {
                    doctor.assignShift(stripTime(currentDay.getTime()));
                    assignedShifts.add(stripTime(currentDay.getTime()));
                }
            }
        }
    }

    private boolean canAssignShift(Doctor doctor, Calendar currentDay, List<Date> assignedShifts) {
        Date currentDate = stripTime(currentDay.getTime());
        System.out.println("Current: " + currentDate);
        System.out.println("Busy days: ");
        for (int i = 0; i < doctor.getBusyDays().size(); i++) {
            System.out.println(doctor.getBusyDays().get(i) + " -- " + currentDate.equals(doctor.getBusyDays().get(i)));
        }
        if (doctor.getBusyDays().contains(currentDate)) {
            return false;
        }

        Calendar prevDay = (Calendar) currentDay.clone();
        prevDay.add(Calendar.DAY_OF_MONTH, -1);
        Calendar nextDay = (Calendar) currentDay.clone();
        nextDay.add(Calendar.DAY_OF_MONTH, 1);

        if (assignedShifts.contains(stripTime(prevDay.getTime())) || assignedShifts.contains(stripTime(nextDay.getTime()))) {
            return false;
        }

        for (Date date : assignedShifts) {
            if (stripTime(date).equals(currentDate)) {
                return false;
            }
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
