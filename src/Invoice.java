import java.sql.Date;

public class Invoice {
    protected int Id;
    protected String SupplierName;
    protected String Service;
    protected Date Date;
    protected float Amount;
    protected int EmployeeId;

    public Invoice(int id, String supplierName, String service, java.sql.Date date, float amount, int employeeId) {
        Id = id;
        SupplierName = supplierName;
        Service = service;
        Date = date;
        Amount = amount;
        EmployeeId = employeeId;
    }

    public int getId() {
        return Id;
    }
    public void setId(int id) {
        Id = id;
    }

    public String getSupplierName() {
        return SupplierName;
    }
    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public String getService() {
        return Service;
    }
    public void setService(String service) {
        Service = service;
    }

    public java.sql.Date getDate() {
        return Date;
    }
    public void setDate(java.sql.Date date) {
        Date = date;
    }

    public float getAmount() {
        return Amount;
    }
    public void setAmount(float amount) {
        Amount = amount;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }
    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "Id=" + Id +
                ", SupplierName='" + SupplierName + '\'' +
                ", Service='" + Service + '\'' +
                ", Date=" + Date +
                ", Amount=" + Amount +
                ", EmployeeId=" + EmployeeId +
                '}';
    }
}
