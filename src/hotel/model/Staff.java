package hotel.model;

public class Staff {
    private int id;
    private String name;
    private String position;
    private double salary;
    private int userId;

    public Staff() {
    }

    public Staff(int id, String name, String position, double salary, int userId) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                ", userId=" + userId +
                '}';
    }
}
