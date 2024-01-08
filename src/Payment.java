import java.sql.Date;

public class Payment {
    protected int Id;
    protected String ReceiptNumber;
    protected Date PaymentDate;
    protected float Amount;
    protected String Service;
    protected int ClientId;
    protected int EmployeeId;
    protected int RentalId;

    public Payment(int id, String receiptNumber, Date paymentDate, float amount, String service, int clientId, int employeeId, int rentalId) {
        Id = id;
        ReceiptNumber = receiptNumber;
        PaymentDate = paymentDate;
        Amount = amount;
        Service = service;
        ClientId = clientId;
        EmployeeId = employeeId;
        RentalId = rentalId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getReceiptNumber() {
        return ReceiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        ReceiptNumber = receiptNumber;
    }

    public Date getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        PaymentDate = paymentDate;
    }

    public float getAmount() {
        return Amount;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public int getClientId() {
        return ClientId;
    }

    public void setClientId(int clientId) {
        ClientId = clientId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public int getRentalId() {
        return RentalId;
    }

    public void setRentalId(int rentalId) {
        RentalId = rentalId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "Id=" + Id +
                ", ReceiptNumber='" + ReceiptNumber + '\'' +
                ", PaymentDate=" + PaymentDate +
                ", Amount=" + Amount +
                ", Service='" + Service + '\'' +
                ", ClientId=" + ClientId +
                ", EmployeeId=" + EmployeeId +
                ", RentalId=" + RentalId +
                '}';
    }
}
