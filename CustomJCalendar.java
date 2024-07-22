import com.toedter.calendar.JCalendar;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class CustomJCalendar extends JCalendar {
    private List<Date> busyDays;

    public void setBusyDays(List<Date> busyDays) {
        this.busyDays = busyDays;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (busyDays != null) {
            for (Date busyDay : busyDays) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(busyDay);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                // Assuming that the day buttons are organized in a grid layout
                int rows = getDayChooser().getDayPanel().getComponentCount() / 7;
                int cols = 7;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        Component comp = getDayChooser().getDayPanel().getComponent(i * cols + j);
                        if (comp instanceof JButton button) {
                            if (button.getText().equals(String.valueOf(day))) {
                                button.setBackground(Color.RED);
                            } else {
                                button.setBackground(Color.WHITE);
                            }
                        }
                    }
                }
            }
        }
    }
}
