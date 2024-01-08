import java.sql.Date;

public class Client {
    protected int Id;
    protected String CNP;
    protected String Name;
    protected String Address;
    protected String Email;
    protected String PhoneNumber;
    protected Date Birthdate;
    protected String OriginCountry;
    protected String DriverLicenseNumber;
    protected Date IssueDate;
    protected Date ExpirationDate;
    protected String CarCategories;

    public Client(int id, String CNP, String name, String address, String email, String phoneNumber, Date birthdate, String originCountry, String driverLicenseNumber, Date issueDate, Date expirationDate, String carCategories) {
        this.Id = id;
        this.CNP = CNP;
        Name = name;
        Address = address;
        Email = email;
        PhoneNumber = phoneNumber;
        Birthdate = birthdate;
        OriginCountry = originCountry;
        DriverLicenseNumber = driverLicenseNumber;
        IssueDate = issueDate;
        ExpirationDate = expirationDate;
        CarCategories = carCategories;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Date getBirthdate() {
        return Birthdate;
    }

    public void setBirthdate(Date birthdate) {
        Birthdate = birthdate;
    }

    public String getOriginCountry() {
        return OriginCountry;
    }

    public void setOriginCountry(String originCountry) {
        OriginCountry = originCountry;
    }

    public String getDriverLicenseNumber() {
        return DriverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        DriverLicenseNumber = driverLicenseNumber;
    }

    public Date getIssueDate() {
        return IssueDate;
    }

    public void setIssueDate(Date issueDate) {
        IssueDate = issueDate;
    }

    public Date getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        ExpirationDate = expirationDate;
    }

    public String getCarCategories() {
        return CarCategories;
    }

    public void setCarCategories(String carCategories) {
        CarCategories = carCategories;
    }

    @Override
    public String toString() {
        return this.Name+", "+this.Birthdate;
    }
}
