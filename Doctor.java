import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Doctor {
    private String name;
    private List<Date> busyDays;
    private int assignedShifts;
    private int totalShifts;
    private ImageIcon photo;
    private Map<Date, String> shiftDates; // Map to store shift dates along with departments
    private List<String> departments;

    public Doctor(String name, List<Date> busyDays, int totalShifts) {
        this.name = name;
        this.busyDays = busyDays;
        this.assignedShifts = 0;
        this.totalShifts = totalShifts;
        this.photo = null;
        this.shiftDates = new HashMap<>();
        this.departments = new ArrayList<>();
    }

    // getters and setters

    public String getName() {
        return name;
    }

    public List<Date> getBusyDays() {
        return busyDays;
    }

    public int getAssignedShifts() {
        return assignedShifts;
    }

    public int getTotalShifts() {
        return totalShifts;
    }

    public Map<Date, String> getShiftDates() {
        return shiftDates;
    }

    public void assignShift(Date date, String department) {
        this.assignedShifts++;
        this.shiftDates.put(date, department);
    }

    public void resetShifts() {
        this.assignedShifts = 0;
        this.shiftDates.clear();
    }

    public ImageIcon getPhoto() {
        return photo;
    }

    public void setPhoto(ImageIcon photo) {
        this.photo = photo;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    @Override
    public String toString() {
        return name;
    }
}
