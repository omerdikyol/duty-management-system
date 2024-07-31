import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doctor {
    private String name;
    private List<Date> busyDays;
    private int totalShifts;
    private List<String> departments;
    private Map<Date, String> shiftDates; // Map to store shift dates and corresponding departments

    public Doctor(String name, List<Date> busyDays, int totalShifts) {
        this.name = name;
        this.busyDays = busyDays;
        this.totalShifts = totalShifts;
        this.departments = new ArrayList<>();
        this.shiftDates = new HashMap<>();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public List<Date> getBusyDays() {
        return busyDays;
    }

    public int getTotalShifts() {
        return totalShifts;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public Map<Date, String> getShiftDates() {
        return shiftDates;
    }

    public void assignShift(Date shiftDate, String department) {
        this.shiftDates.put(shiftDate, department);
    }

    public void resetShifts() {
        this.shiftDates.clear();
    }

    @Override
    public String toString() {
        return name;
    }
}
