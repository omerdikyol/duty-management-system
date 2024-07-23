public class Department {
    private String name;
    private int doctorsNeeded;

    public Department(String name, int doctorsNeeded) {
        this.name = name;
        this.doctorsNeeded = doctorsNeeded;
    }

    public String getName() {
        return name;
    }

    public int getDoctorsNeeded() {
        return doctorsNeeded;
    }

    @Override
    public String toString() {
        return name + " (" + doctorsNeeded + " doctors needed)";
    }
}
