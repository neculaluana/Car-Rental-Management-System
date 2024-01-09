import java.sql.Date;

public class Rental {
    protected int Id;
    protected Date StartDate;
    protected Date EndDate;
    protected int CarId;
    protected int ClientId;
    protected int EmployeeId;

    public Rental(int id, Date startDate, Date endDate, int carId, int clientId, int employeeId) {
        Id = id;
        StartDate = startDate;
        EndDate = endDate;
        CarId = carId;
        ClientId = clientId;
        EmployeeId = employeeId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public int getCarId() {
        return CarId;
    }

    public void setCarId(int carId) {
        CarId = carId;
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

    @Override
    public String toString() {
        return StartDate+", "+DbUtils.getClientNameById(ClientId)+", "+DbUtils.getCarPlateById(CarId);
    }
}
