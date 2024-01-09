import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUtils {

    private static String connectionUrl;
    private static Connection conn;
    public static int isWorking()
    {
        if(getConnection()==null)
        {
            return 0;
        }

        return 1;
    }


    public static Connection getConnection()
    {




        //connectionUrl = ApplicationProperties.getProperties().getProperty(Constants.CONNECTION_STRING);
        connectionUrl = "jdbc:sqlserver://localhost:1433;database=dbCarRental;user=sa;password=pass;encrypt=true;trustServerCertificate=true";

        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(connectionUrl);

            if(conn==null)
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
        return conn;
    }
    public static List<Car> getAllCars(){

        List<Car> carList = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spCarSelectAllActive");


            while (resultSet.next()){
                Car car = new Car(resultSet.getInt("CarID")
                        ,resultSet.getString("LicensePlate")
                        ,resultSet.getString("Brand")
                        ,resultSet.getString("Model")
                        ,resultSet.getString("ChassisSeries")
                        ,resultSet.getInt("SeatsNumber")
                        ,resultSet.getString("FuelType")
                        ,resultSet.getInt("ManufactureYear")
                        ,resultSet.getString("Color")
                        ,resultSet.getBoolean("Availability")
                        ,resultSet.getFloat("DailyRate"));
                carList.add(car);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return carList;

    }
    public static List<Client> getAllClients() {
        List<Client> clientList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spClientSelectAllActive");

            while (resultSet.next()) {
                Client client = new Client(
                        resultSet.getInt("ClientId"),
                        resultSet.getString("CNP"),
                        resultSet.getString("Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Email"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getDate("Birthdate"),
                        resultSet.getString("OriginCountry"),
                        resultSet.getString("DriverLicenseNumber"),
                        resultSet.getDate("IssueDate"),
                        resultSet.getDate("ExpirationDate"),
                        resultSet.getString("CarCategories")
                );
                clientList.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientList;
    }
    public static List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spEmployeeSelectAllActive");

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("EmployeeId"),
                        resultSet.getString("CNP"),
                        resultSet.getString("Name"),
                        resultSet.getString("Address"),
                        resultSet.getDate("Birthdate"),
                        resultSet.getDate("EmploymentDate"),
                        resultSet.getString("Position")
                );
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }
    public static List<Payment> getAllPayments() {
        List<Payment> paymentList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spPaymentSelectAllActive");

            while (resultSet.next()) {
                Payment payment = new Payment(
                        resultSet.getInt("PaymentId"),
                        resultSet.getString("ReceiptNumber"),
                        resultSet.getDate("PaymentDate"),
                        resultSet.getFloat("Amount"),
                        resultSet.getString("Service"),
                        resultSet.getInt("EmployeeId"),
                        resultSet.getInt("RentalId")
                );
                paymentList.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentList;
    }
    public static List<Rental> getAllRentals() {
        List<Rental> rentalList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spRentalSelectAllActive");

            while (resultSet.next()) {
                Rental rental = new Rental(
                        resultSet.getInt("RentalId"),
                        resultSet.getDate("StartDate"),
                        resultSet.getDate("EndDate"),
                        resultSet.getInt("CarId"),
                        resultSet.getInt("ClientId"),
                        resultSet.getInt("EmployeeId")
                );
                rentalList.add(rental);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentalList;
    }
    public static List<Invoice> getAllInvoices() {
        List<Invoice> invoiceList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spInvoiceSelectAll");

            while (resultSet.next()) {
                Invoice invoice = new Invoice(
                        resultSet.getInt("InvoiceId"),
                        resultSet.getString("SupplierName"),
                        resultSet.getString("Service"),
                        resultSet.getDate("Date"),
                        resultSet.getFloat("Amount"),
                        resultSet.getInt("EmployeeId")
                );
                invoiceList.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoiceList;
    }
    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("spUserSelectAllActive");

            while (resultSet.next()) {
                User user = new User(resultSet.getString("Username")
                        ,resultSet.getString("Password")
                        ,resultSet.getInt("UserId")
                        ,resultSet.getInt("EmployeeId")
                        ,resultSet.getInt("RoleId")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    static public User getAuthenticatedUser(String username, String password) {
        List<User> userList = getAllUsers();
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static String getClientNameById(int clientId) {
        String clientName = "";
        try (PreparedStatement statement = conn.prepareStatement("{CALL spClientSelectById(?)}")) {
            statement.setInt(1, clientId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                clientName = resultSet.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientName;
    }

    public static String getCarPlateById(int carId) {
        String carPlate = "";
        try (PreparedStatement statement = conn.prepareStatement("{CALL spCarSelectById(?)}")) {
            statement.setInt(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                carPlate = resultSet.getString("LicensePlate");  // Assuming 'Model' represents the car name
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carPlate;
    }

    public static String getEmployeeNameById(int employeeId) {
        String employeeName = "";
        try (PreparedStatement statement = conn.prepareStatement("{CALL spEmployeeSelectById(?)}")) {
            statement.setInt(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                employeeName = resultSet.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeName;
    }
    public static float getCarDailyRateById(int carId) {
        float carDailyRate = 0.0f;
        try (PreparedStatement statement = conn.prepareStatement("{CALL spCarSelectById(?)}")) {
            statement.setInt(1, carId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                carDailyRate = resultSet.getFloat("DailyRate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carDailyRate;
    }

    public static boolean deleteCar(int carId) {
        try (PreparedStatement statement = conn.prepareStatement("{CALL spDeleteCar(?)}")) {
            statement.setInt(1, carId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addEditCar(Car car) {
        try (CallableStatement statement = conn.prepareCall("{CALL spAddEditCar(?,?,?,?,?,?,?,?,?,?,?)}")) {
            // Using column names instead of indices
            statement.setObject("CarId", car.getId()); // Replace "CarID" with the actual parameter name in your stored procedure
            statement.setObject("LicensePlate", car.getLicencePlate());
            statement.setObject("Brand", car.getBrand());
            statement.setObject("Model", car.getModel());
            statement.setObject("ChassisSeries", car.getChassisSeries());
            statement.setObject("SeatsNumber", car.getSeatsNumber());
            statement.setObject("FuelType", car.getFuelType());
            statement.setObject("ManufactureYear", car.getManufactureYear());
            statement.setObject("Color", car.getColor());
            statement.setObject("Availability", car.getAvailability());
            statement.setObject("DailyRate", car.getDailyRate());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteClient(int clientId) {
        try (PreparedStatement statement = conn.prepareStatement("{CALL spDeleteClient(?)}")) {
            statement.setInt(1, clientId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addEditClient(Client client) {
        try (CallableStatement statement = conn.prepareCall("{CALL spAddEditClient(?,?,?,?,?,?,?,?,?,?,?,?)}")) {
            statement.setObject("ClientId", client.getId()); // Replace "ClientId" with the actual parameter name in your stored procedure
            statement.setObject("CNP", client.getCNP());
            statement.setObject("Name", client.getName());
            statement.setObject("Address", client.getAddress());
            statement.setObject("Email", client.getEmail());
            statement.setObject("PhoneNumber", client.getPhoneNumber());
            statement.setObject("Birthdate", client.getBirthdate());
            statement.setObject("OriginCountry", client.getOriginCountry());
            statement.setObject("DriverLicenseNumber", client.getDriverLicenseNumber());
            statement.setObject("IssueDate", client.getIssueDate());
            statement.setObject("ExpirationDate", client.getExpirationDate());
            statement.setObject("CarCategories", client.getCarCategories());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteEmployee(int employeeId) {
        try (PreparedStatement statement = conn.prepareStatement("{CALL spDeleteEmployee(?)}")) {
            statement.setInt(1, employeeId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addEditEmployee(Employee employee) {
        try (CallableStatement statement = conn.prepareCall("{CALL spAddEditEmployee(?,?,?,?,?,?,?)}")) {
            statement.setObject("EmployeeId", employee.getId()); // Replace "EmployeeId" with the actual parameter name in your stored procedure
            statement.setObject("CNP", employee.getCNP());
            statement.setObject("Name", employee.getName());
            statement.setObject("Address", employee.getAddress());
            statement.setObject("Birthdate", employee.getBirthdate());
            statement.setObject("EmploymentDate", employee.getEmploymentDate());
            statement.setObject("Position", employee.getPosition());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteRental(int rentalId) {
        try (PreparedStatement statement = conn.prepareStatement("{CALL spDeleteRental(?)}")) {
            statement.setInt(1, rentalId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addEditRental(Rental rental) {
        try (CallableStatement statement = conn.prepareCall("{CALL spAddEditRental(?,?,?,?,?,?)}")) {
            statement.setObject("RentalId", rental.getId()); // Replace "RentalId" with the actual parameter name in your stored procedure
            statement.setObject("StartDate", rental.getStartDate());
            statement.setObject("EndDate", rental.getEndDate());
            statement.setObject("CarId", rental.getCarId());
            statement.setObject("ClientId", rental.getClientId());
            statement.setObject("EmployeeId", rental.getEmployeeId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deletePayment(int paymentId) {
        try (PreparedStatement statement = conn.prepareStatement("{CALL spDeletePayment(?)}")) {
            statement.setInt(1, paymentId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addEditPayment(Payment payment) {
        try (CallableStatement statement = conn.prepareCall("{CALL spAddEditPayment(?,?,?,?,?,?,?)}")) {
            statement.setObject("PaymentId", payment.getId()); // Replace "PaymentId" with the actual parameter name in your stored procedure
            statement.setObject("ReceiptNumber", payment.getReceiptNumber());
            statement.setObject("PaymentDate", payment.getPaymentDate());
            statement.setObject("Amount", payment.getAmount());
            statement.setObject("Service", payment.getService());
            statement.setObject("EmployeeId", payment.getEmployeeId());
            statement.setObject("RentalId", payment.getRentalId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}