import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Doctor {
    private String name;
    private List<Date> busyDays;
    private int assignedShifts;
    private int totalShifts;
    private ImageIcon photo;
    private List<Date> shiftDates;

    public Doctor(String name, List<Date> busyDays, int totalShifts) {
        this.name = name;
        this.busyDays = busyDays;
        this.assignedShifts = 0;
        this.totalShifts = totalShifts;
        this.photo = null;
        this.shiftDates = new ArrayList<>();
    }

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

    public List<Date> getShiftDates() {
        return shiftDates;
    }

    public void assignShift(Date date) {
        this.assignedShifts++;
        this.shiftDates.add(date);
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

    @Override
    public String toString() {
        return name;
    }
}
