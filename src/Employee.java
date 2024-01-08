import java.sql.Date;

public class Employee {
    protected int Id;
    protected String CNP;
    protected String Name;
    protected String Address;
    protected Date Birthdate;
    protected Date EmploymentDate;
    protected String Position;

    public Employee(int id, String CNP, String name, String address, Date birthdate, Date employmentDate, String position) {
        Id = id;
        this.CNP = CNP;
        Name = name;
        Address = address;
        Birthdate = birthdate;
        EmploymentDate = employmentDate;
        Position = position;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Date getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(Date birthdate) {
        Birthdate = birthdate;
    }

    public Date getEmploymentDate() {
        return EmploymentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        EmploymentDate = employmentDate;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "Id=" + Id +
                ", CNP='" + CNP + '\'' +
                ", Name='" + Name + '\'' +
                ", Address='" + Address + '\'' +
                ", Birthdate=" + Birthdate +
                ", EmploymentDate=" + EmploymentDate +
                ", Position='" + Position + '\'' +
                '}';
    }
}