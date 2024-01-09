import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdesktop.swingx.JXDatePicker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuPage extends JFrame {
    private JTabbedPane tabbedPane;
    private int currentCarId = -1;
    private int currentClientId = -1;
    private int currentEmployeeId = -1;
    private int currentRentalId = -1;
    private int currentPaymentId = -1;
    private int currentInvoiceId = -1;

    List<Car> carList=new ArrayList<>();
    List<Client> clientList=new ArrayList<>();
    List<Employee> employeeList=new ArrayList<>();
    List<Rental> rentalList=new ArrayList<>();
    List<Payment> paymentList=new ArrayList<>();
    List<Invoice> invoiceList=new ArrayList<>();

    DefaultTableModel carModel;
    DefaultTableModel clientModel;
    DefaultTableModel employeeModel;
    DefaultTableModel rentalModel;
    DefaultTableModel paymentModel;
    DefaultTableModel invoiceModel;

    public MenuPage() {
        setTitle("Car Rental");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImageIcon icon=new ImageIcon("C:\\MyRepos\\CarRental\\images.jpg");
        setIconImage(icon.getImage());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Cars", createCarsPanel());
        tabbedPane.addTab("Clients", createClientsPanel());
        tabbedPane.addTab("Employee", createEmployeesPanel());
        tabbedPane.addTab("Rentals", createRentalsPanel());
        tabbedPane.addTab("Payments", createPaymentsPanel());
        tabbedPane.addTab("Invoices", createInvoicesPanel());


        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "License Plate", "Brand", "Model", "Chassis Series", "Seats", "Fuel Type", "Year", "Color", "Daily Rate"
        };
        carModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        carList = DbUtils.getAllCars();
        for (Car car : carList) {
            carModel.addRow(new Object[]{
                    car.getLicencePlate(),
                    car.getBrand(),
                    car.getModel(),
                    car.getChassisSeries(),
                    car.getSeatsNumber(),
                    car.getFuelType(),
                    car.getManufactureYear(),
                    car.getColor(),
                    car.getDailyRate()
            });
        }


        JTable table = new JTable(carModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addCarEditArea = new JPanel(new GridLayout(0, 2));


        JTextField licencePlateField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField chassisSeriesField = new JTextField();
        JTextField seatsNumberField = new JTextField();
        JTextField fuelTypeField = new JTextField();
        JTextField manufactureYearField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField dailyRateField = new JTextField();


        addCarEditArea.add(new JLabel("Licence Plate:"));
        addCarEditArea.add(licencePlateField);
        addCarEditArea.add(new JLabel("Brand:"));
        addCarEditArea.add(brandField);
        addCarEditArea.add(new JLabel("Model:"));
        addCarEditArea.add(modelField);
        addCarEditArea.add(new JLabel("Chassis Series:"));
        addCarEditArea.add(chassisSeriesField);
        addCarEditArea.add(new JLabel("Seats Number:"));
        addCarEditArea.add(seatsNumberField);
        addCarEditArea.add(new JLabel("Fuel Type:"));
        addCarEditArea.add(fuelTypeField);
        addCarEditArea.add(new JLabel("Manufacture Year:"));
        addCarEditArea.add(manufactureYearField);
        addCarEditArea.add(new JLabel("Color:"));
        addCarEditArea.add(colorField);
        addCarEditArea.add(new JLabel("Daily Rate:"));
        addCarEditArea.add(dailyRateField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {

                int seatsNumber = Integer.parseInt(seatsNumberField.getText());
                int manufactureYear = Integer.parseInt(manufactureYearField.getText());
                float dailyRate = Float.parseFloat(dailyRateField.getText());


                Car car = new Car(
                        currentCarId,
                        licencePlateField.getText(),
                        brandField.getText(),
                        modelField.getText(),
                        chassisSeriesField.getText(),
                        seatsNumber,
                        fuelTypeField.getText(),
                        manufactureYear,
                        colorField.getText(),
                        dailyRate
                );


                boolean success = DbUtils.addEditCar(car);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Car saved successfully.");
                    List<Car> newCars=DbUtils.getAllCars();
                    carList.clear();
                    carList.addAll(newCars);
                    refreshCarTable(carModel);
                    refreshRentalTable(rentalModel);
                    refreshPaymentTable(paymentModel);

                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save car.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number format in one of the fields.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {

            licencePlateField.setText("");
            brandField.setText("");
            modelField.setText("");
            chassisSeriesField.setText("");
            seatsNumberField.setText("");
            fuelTypeField.setText("");
            manufactureYearField.setText("");
            colorField.setText("");
            dailyRateField.setText("");
        });

        addCarEditArea.add(saveButton);
        addCarEditArea.add(clearButton);

        panel.add(addCarEditArea, BorderLayout.SOUTH);
        addCarEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Car");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewEntryButton);
        buttonPanel.add(deleteButton);
        addNewEntryButton.addActionListener(e -> {
            clearCarAddEditFields(licencePlateField, brandField, modelField, chassisSeriesField, seatsNumberField, fuelTypeField, manufactureYearField, colorField, dailyRateField);
            addCarEditArea.setVisible(true);
            currentCarId=-1;
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < carList.size()) {
                Car carToDelete = carList.get(row);
                int carId = carToDelete.getId();
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteCar(carId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Car deleted successfully.");
                        carModel.removeRow(row);
                        carList.remove(row);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete car.");
                    }
            }}
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row=rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if(e.getClickCount() ==2){
                            Car selectedCar = carList.get(rowIndex);
                            currentCarId = selectedCar.getId();
                            licencePlateField.setText(carModel.getValueAt(row, 0).toString());
                            brandField.setText(carModel.getValueAt(row, 1).toString());
                            modelField.setText(carModel.getValueAt(row, 2).toString());
                            chassisSeriesField.setText(carModel.getValueAt(row, 3).toString());
                            seatsNumberField.setText(carModel.getValueAt(row, 4).toString());
                            fuelTypeField.setText(carModel.getValueAt(row, 5).toString());
                            manufactureYearField.setText(carModel.getValueAt(row, 6).toString());
                            colorField.setText(carModel.getValueAt(row, 7).toString());
                            dailyRateField.setText(carModel.getValueAt(row, 8).toString());
                            addCarEditArea.setVisible(true);
                        }
                    }
                    else{
                        table.clearSelection();
                        addCarEditArea.setVisible(false);
                        deleteButton.setEnabled(false);
                    }
                }else {
                    table.clearSelection();
                    addCarEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = (table.getSelectedRow() != -1);
                deleteButton.setEnabled(rowSelected);

                if (!rowSelected) {
                    addCarEditArea.setVisible(false);
                }
            }
        });
        clearButton.addActionListener(e -> {
            clearCarAddEditFields(licencePlateField, brandField, modelField, chassisSeriesField, seatsNumberField, fuelTypeField, manufactureYearField, colorField, dailyRateField);
            addCarEditArea.setVisible(false);
            table.clearSelection();
        });
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addCarEditArea, BorderLayout.SOUTH);
        return panel;
    }
    private void clearCarAddEditFields(JTextField licencePlateField, JTextField brandField, JTextField modelField, JTextField chassisSeriesField, JTextField seatsNumberField, JTextField fuelTypeField, JTextField manufactureYearField, JTextField colorField, JTextField dailyRateField) {
        licencePlateField.setText("");
        brandField.setText("");
        modelField.setText("");
        chassisSeriesField.setText("");
        seatsNumberField.setText("");
        fuelTypeField.setText("");
        manufactureYearField.setText("");
        colorField.setText("");
        dailyRateField.setText("");
    }
    private void refreshCarTable(DefaultTableModel model) {
        List<Car> cars = DbUtils.getAllCars();
        model.setRowCount(0);

        for (Car car : cars) {
            model.addRow(new Object[]{
                    car.getLicencePlate(),
                    car.getBrand(),
                    car.getModel(),
                    car.getChassisSeries(),
                    car.getSeatsNumber(),
                    car.getFuelType(),
                    car.getManufactureYear(),
                    car.getColor(),
                    car.getDailyRate()
            });
        }
    }


    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {
                "CNP", "Name", "Address", "Email", "Phone Number", "Birthdate", "Origin Country",
                "Driver License Number", "Issue Date", "Expiration Date", "Car Categories"
        };

        clientModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        clientList = DbUtils.getAllClients();

        for (Client client : clientList) {
            clientModel.addRow(new Object[]{
                    client.getCNP(),
                    client.getName(),
                    client.getAddress(),
                    client.getEmail(),
                    client.getPhoneNumber(),
                    client.getBirthdate(),
                    client.getOriginCountry(),
                    client.getDriverLicenseNumber(),
                    client.getIssueDate(),
                    client.getExpirationDate(),
                    client.getCarCategories()
            });
        }

        JTable table = new JTable(clientModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addClientEditArea = new JPanel(new GridLayout(0, 2));


        JTextField cnpField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneNumberField = new JTextField();
        JXDatePicker birthDatePicker = new JXDatePicker();
        birthDatePicker.setFormats("yyyy-MM-dd");
        JTextField originCountryField = new JTextField();
        JTextField driverLicenseNumberField = new JTextField();
        JXDatePicker issueDatePicker = new JXDatePicker();
        issueDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker expirationDatePicker = new JXDatePicker();
        expirationDatePicker.setFormats("yyyy-MM-dd");
        JTextField carCategoriesField = new JTextField();


        addClientEditArea.add(new JLabel("CNP:"));
        addClientEditArea.add(cnpField);
        addClientEditArea.add(new JLabel("Name:"));
        addClientEditArea.add(nameField);
        addClientEditArea.add(new JLabel("Address:"));
        addClientEditArea.add(addressField);
        addClientEditArea.add(new JLabel("Email:"));
        addClientEditArea.add(emailField);
        addClientEditArea.add(new JLabel("Phone Number:"));
        addClientEditArea.add(phoneNumberField);
        addClientEditArea.add(new JLabel("Birthdate:"));
        addClientEditArea.add(birthDatePicker);
        addClientEditArea.add(new JLabel("Origin Country:"));
        addClientEditArea.add(originCountryField);
        addClientEditArea.add(new JLabel("Driver License Number:"));
        addClientEditArea.add(driverLicenseNumberField);
        addClientEditArea.add(new JLabel("Issue Date:"));
        addClientEditArea.add(issueDatePicker);
        addClientEditArea.add(new JLabel("Expiration Date:"));
        addClientEditArea.add(expirationDatePicker);
        addClientEditArea.add(new JLabel("Car Categories:"));
        addClientEditArea.add(carCategoriesField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {

                java.util.Date utilBirthDate = birthDatePicker.getDate();
                java.sql.Date birthDate = new java.sql.Date(utilBirthDate.getTime());
                java.util.Date utilIssueDate = issueDatePicker.getDate();
                java.sql.Date issueDate = new java.sql.Date(utilIssueDate.getTime());
                java.util.Date utilExpirationDate = expirationDatePicker.getDate();
                java.sql.Date expirationDate = new java.sql.Date(utilExpirationDate.getTime());


                Client client = new Client(
                        currentClientId,
                        cnpField.getText(),
                        nameField.getText(),
                        addressField.getText(),
                        emailField.getText(),
                        phoneNumberField.getText(),
                        birthDate,
                        originCountryField.getText(),
                        driverLicenseNumberField.getText(),
                        issueDate,
                        expirationDate,
                        carCategoriesField.getText()
                );


                boolean success = DbUtils.addEditClient(client);

                if (success) {
                    JOptionPane.showMessageDialog(panel, "Client saved successfully.");
                    List<Client> newClients = DbUtils.getAllClients();
                    clientList.clear();
                    clientList.addAll(newClients);
                    refreshClientTable(clientModel);
                    refreshRentalTable(rentalModel);
                    refreshPaymentTable(paymentModel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save client.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {

            clearClientAddEditFields(cnpField, nameField, addressField, emailField, phoneNumberField,
                    birthDatePicker, originCountryField, driverLicenseNumberField, issueDatePicker,
                    expirationDatePicker, carCategoriesField);
        });

        addClientEditArea.add(saveButton);
        addClientEditArea.add(clearButton);

        panel.add(addClientEditArea, BorderLayout.SOUTH);
        addClientEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Client");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewEntryButton);
        buttonPanel.add(deleteButton);
        addNewEntryButton.addActionListener(e -> {
            clearClientAddEditFields(cnpField, nameField, addressField, emailField, phoneNumberField,
                    birthDatePicker, originCountryField, driverLicenseNumberField, issueDatePicker,
                    expirationDatePicker, carCategoriesField);
            addClientEditArea.setVisible(true);
            currentClientId=-1;
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < clientList.size()) {
                Client clientToDelete = clientList.get(row);
                int clientId = clientToDelete.getId();


                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this client?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteClient(clientId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Client deleted successfully.");
                        clientModel.removeRow(row);
                        Client clientToRemove= clientList.get(row);
                        clientList.remove(clientToRemove);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete client.");
                    }
                }
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row=rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if(e.getClickCount() ==2){
                        Client selectedClient = clientList.get(rowIndex);
                        cnpField.setText(clientModel.getValueAt(rowIndex, 0).toString());
                        nameField.setText(clientModel.getValueAt(rowIndex, 1).toString());
                        addressField.setText(clientModel.getValueAt(rowIndex, 2).toString());
                        emailField.setText(clientModel.getValueAt(rowIndex, 3).toString());
                        phoneNumberField.setText(clientModel.getValueAt(rowIndex, 4).toString());
                        String birthDateStr = clientModel.getValueAt(row, 5).toString();
                        java.sql.Date birthDate = java.sql.Date.valueOf(birthDateStr);
                        birthDatePicker.setDate(birthDate);
                        originCountryField.setText(clientModel.getValueAt(rowIndex, 6).toString());
                        driverLicenseNumberField.setText(clientModel.getValueAt(rowIndex, 7).toString());
                        String issueDateStr = clientModel.getValueAt(row, 8).toString();
                        java.sql.Date issueDate = java.sql.Date.valueOf(issueDateStr);
                        issueDatePicker.setDate(issueDate);
                        String expirationDateStr = clientModel.getValueAt(row, 9).toString();
                        java.sql.Date expirationDate = java.sql.Date.valueOf(expirationDateStr);
                        expirationDatePicker.setDate(expirationDate);
                        carCategoriesField.setText(clientModel.getValueAt(rowIndex, 10).toString());
                        addClientEditArea.setVisible(true);
                        currentClientId=selectedClient.getId();
                    }
                }
                else{
                    table.clearSelection();
                    addClientEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }else {
                table.clearSelection();
                addClientEditArea.setVisible(false);
                deleteButton.setEnabled(false);
            }
        }
    });

        clearButton.addActionListener(e -> {
            clearClientAddEditFields(cnpField, nameField, addressField, emailField, phoneNumberField,
                    birthDatePicker, originCountryField, driverLicenseNumberField, issueDatePicker,
                    expirationDatePicker, carCategoriesField);
            addClientEditArea.setVisible(false);
            table.clearSelection();
        });

        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addClientEditArea, BorderLayout.SOUTH);

        return panel;
    }
    private void clearClientAddEditFields(JTextField cnpField, JTextField nameField, JTextField addressField,JTextField emailField, JTextField phoneNumberField, JXDatePicker birthdatePicker,JTextField originCountryField, JTextField driverLicenseNumberField,JXDatePicker issueDatePicker, JXDatePicker expirationDatePicker,JTextField carCategoriesField) {
        cnpField.setText("");
        nameField.setText("");
        addressField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");
        birthdatePicker.setDate(null);
        originCountryField.setText("");
        driverLicenseNumberField.setText("");
        issueDatePicker.setDate(null);
        expirationDatePicker.setDate(null);
        carCategoriesField.setText("");
    }
    private void refreshClientTable(DefaultTableModel model) {

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }


        List<Client> clients = DbUtils.getAllClients();
        for (Client client : clients) {
            model.addRow(new Object[]{
                    client.getCNP(),
                    client.getName(),
                    client.getAddress(),
                    client.getEmail(),
                    client.getPhoneNumber(),
                    client.getBirthdate(),
                    client.getOriginCountry(),
                    client.getDriverLicenseNumber(),
                    client.getIssueDate(),
                    client.getExpirationDate(),
                    client.getCarCategories()
            });
        }
    }


    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {
                "CNP", "Name", "Address", "Birthdate", "Employment Date", "Position"
        };

        employeeModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        employeeList = DbUtils.getAllEmployees();

        for (Employee employee : employeeList) {
            employeeModel.addRow(new Object[]{
                    employee.getCNP(),
                    employee.getName(),
                    employee.getAddress(),
                    employee.getBirthdate(),
                    employee.getEmploymentDate(),
                    employee.getPosition()
            });
        }

        JTable table = new JTable(employeeModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addEmployeeEditArea = new JPanel(new GridLayout(0, 2));


        JTextField cnpField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JXDatePicker birthDatePicker = new JXDatePicker();
        birthDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker employmentDatePicker = new JXDatePicker();
        employmentDatePicker.setFormats("yyyy-MM-dd");
        JTextField positionField = new JTextField();


        addEmployeeEditArea.add(new JLabel("CNP:"));
        addEmployeeEditArea.add(cnpField);
        addEmployeeEditArea.add(new JLabel("Name:"));
        addEmployeeEditArea.add(nameField);
        addEmployeeEditArea.add(new JLabel("Address:"));
        addEmployeeEditArea.add(addressField);
        addEmployeeEditArea.add(new JLabel("Birthdate:"));
        addEmployeeEditArea.add(birthDatePicker);
        addEmployeeEditArea.add(new JLabel("Employment Date:"));
        addEmployeeEditArea.add(employmentDatePicker);
        addEmployeeEditArea.add(new JLabel("Position:"));
        addEmployeeEditArea.add(positionField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {

                java.util.Date utilBirthDate = birthDatePicker.getDate();
                java.sql.Date birthDate = new java.sql.Date(utilBirthDate.getTime());
                java.util.Date utilEmployementDate = employmentDatePicker.getDate();
                java.sql.Date employmentDate = new java.sql.Date(utilEmployementDate.getTime());


                Employee employee = new Employee(
                        currentEmployeeId,
                        cnpField.getText(),
                        nameField.getText(),
                        addressField.getText(),
                        birthDate,
                        employmentDate,
                        positionField.getText()
                );


                boolean success = DbUtils.addEditEmployee(employee);

                if (success) {
                    JOptionPane.showMessageDialog(panel, "Employee saved successfully.");
                    List<Employee> newEmployees = DbUtils.getAllEmployees();
                    employeeList.clear();
                    employeeList.addAll(newEmployees);
                    refreshEmployeeTable(employeeModel);
                    refreshRentalTable(rentalModel);
                    refreshPaymentTable(paymentModel);
                    refreshInvoiceTable(invoiceModel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save employee.");
                }
            }  catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {

            clearEmployeeAddEditFields(cnpField, nameField, addressField, birthDatePicker,
                    employmentDatePicker, positionField);
        });

        addEmployeeEditArea.add(saveButton);
        addEmployeeEditArea.add(clearButton);

        panel.add(addEmployeeEditArea, BorderLayout.SOUTH);
        addEmployeeEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Employee");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewEntryButton);
        buttonPanel.add(deleteButton);
        addNewEntryButton.addActionListener(e -> {
            clearEmployeeAddEditFields(cnpField, nameField, addressField, birthDatePicker,
                    employmentDatePicker, positionField);
            addEmployeeEditArea.setVisible(true);
            currentEmployeeId = -1;
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < employeeList.size()) {
                Employee employeeToDelete = employeeList.get(row);
                int employeeId = employeeToDelete.getId();


                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteEmployee(employeeId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                        employeeModel.removeRow(row);
                        employeeList.remove(row);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete employee.");
                    }
                }
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row = rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if (e.getClickCount() == 2) {
                            Employee selectedEmployee = employeeList.get(rowIndex);
                            cnpField.setText(employeeModel.getValueAt(rowIndex, 0).toString());
                            nameField.setText(employeeModel.getValueAt(rowIndex, 1).toString());
                            addressField.setText(employeeModel.getValueAt(rowIndex, 2).toString());
                            String birthDateStr = employeeModel.getValueAt(row, 3).toString();
                            java.sql.Date birthDate = java.sql.Date.valueOf(birthDateStr);
                            birthDatePicker.setDate(birthDate);
                            String employmentDateStr = employeeModel.getValueAt(row, 4).toString();
                            java.sql.Date employmentDate = java.sql.Date.valueOf(employmentDateStr);
                            employmentDatePicker.setDate(employmentDate);
                            positionField.setText(employeeModel.getValueAt(rowIndex, 5).toString());
                            addEmployeeEditArea.setVisible(true);
                            currentEmployeeId = selectedEmployee.getId();
                        }
                    } else {
                        table.clearSelection();
                        addEmployeeEditArea.setVisible(false);
                        deleteButton.setEnabled(false);
                    }
                } else {
                    table.clearSelection();
                    addEmployeeEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = (table.getSelectedRow() != -1);
                deleteButton.setEnabled(rowSelected);

                if (!rowSelected) {
                    addEmployeeEditArea.setVisible(false);
                }
            }
        });
        clearButton.addActionListener(e -> {
            clearEmployeeAddEditFields(cnpField, nameField, addressField, birthDatePicker,
                    employmentDatePicker, positionField);
            addEmployeeEditArea.setVisible(false);
            table.clearSelection();
        });

        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addEmployeeEditArea, BorderLayout.SOUTH);

        return panel;
    }
    private void clearEmployeeAddEditFields(JTextField cnpField, JTextField nameField, JTextField addressField,JXDatePicker birthdatePicker, JXDatePicker employmentDatePicker,JTextField positionField) {
        cnpField.setText("");
        nameField.setText("");
        addressField.setText("");
        birthdatePicker.setDate(null);
        employmentDatePicker.setDate(null);
        positionField.setText("");
    }
    private void refreshEmployeeTable(DefaultTableModel model) {

        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }


        List<Employee> employees = DbUtils.getAllEmployees();
        for (Employee employee : employees) {
            model.addRow(new Object[]{
                    employee.getCNP(),
                    employee.getName(),
                    employee.getAddress(),
                    employee.getBirthdate(),
                    employee.getEmploymentDate(),
                    employee.getPosition()
            });
        }
    }



    private JPanel createRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "Start Date", "End Date", "Car Plate", "Client Name", "Employee Name"
        };
        rentalModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        rentalList = DbUtils.getAllRentals();
        for (Rental rental : rentalList) {
            String carPlate = DbUtils.getCarPlateById(rental.getCarId());
            String clientName = DbUtils.getClientNameById(rental.getClientId());
            String employeeName = DbUtils.getEmployeeNameById(rental.getEmployeeId());

            rentalModel.addRow(new Object[]{
                    rental.getStartDate(),
                    rental.getEndDate(),
                    carPlate,
                    clientName,
                    employeeName
            });
        }


        JTable table = new JTable(rentalModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addRentalEditArea = new JPanel(new GridLayout(0, 2));


        JXDatePicker startDatePicker = new JXDatePicker();
        startDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker endDatePicker = new JXDatePicker();
        endDatePicker.setFormats("yyyy-MM-dd");
        JComboBox<Car> carComboBox = new JComboBox<>();
        JComboBox<Client> clientComboBox = new JComboBox<>();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();



        addRentalEditArea.add(new JLabel("Start Date:"));
        addRentalEditArea.add(startDatePicker);
        addRentalEditArea.add(new JLabel("End Date:"));
        addRentalEditArea.add(endDatePicker);
        addRentalEditArea.add(new JLabel("Car:"));
        addRentalEditArea.add(carComboBox);
        addRentalEditArea.add(new JLabel("Client:"));
        addRentalEditArea.add(clientComboBox);
        addRentalEditArea.add(new JLabel("Employee:"));
        addRentalEditArea.add(employeeComboBox);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {


                Car selectedCar=(Car) carComboBox.getSelectedItem();
                int carId = selectedCar.getId();
                Client selectedClient=(Client) clientComboBox.getSelectedItem();
                int clientId = selectedClient.getId();
                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();


                java.util.Date utilStartDate = startDatePicker.getDate();
                java.sql.Date startDate = new java.sql.Date(utilStartDate.getTime());
                java.util.Date utilEndDate = endDatePicker.getDate();
                java.sql.Date endDate = new java.sql.Date(utilEndDate.getTime());



                Rental rental = new Rental(
                        currentRentalId,
                        startDate,
                        endDate,
                        carId,
                        clientId,
                        employeeId
                );


                boolean success = DbUtils.addEditRental(rental);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Rental saved successfully.");
                    List<Rental> newRentals = DbUtils.getAllRentals();
                    rentalList.clear();
                    rentalList.addAll(newRentals);
                    refreshRentalTable(rentalModel);
                    refreshPaymentTable(paymentModel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save rental.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number format in one of the fields.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {

            startDatePicker.setDate(null);
            endDatePicker.setDate(null);
            if (carComboBox.getItemCount() > 0) {
                carComboBox.setSelectedIndex(0);
            }
            if (clientComboBox.getItemCount() > 0) {
                clientComboBox.setSelectedIndex(0);
            }
            if (employeeComboBox.getItemCount() > 0) {
                employeeComboBox.setSelectedIndex(0);
            }

        });

        addRentalEditArea.add(saveButton);
        addRentalEditArea.add(clearButton);

        panel.add(addRentalEditArea, BorderLayout.SOUTH);
        addRentalEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Rental");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewEntryButton);
        buttonPanel.add(deleteButton);
        addNewEntryButton.addActionListener(e -> {
            clearRentalAddEditFields(startDatePicker, endDatePicker, carComboBox, clientComboBox, employeeComboBox);
            carComboBox.removeAllItems();
            clientComboBox.removeAllItems();
            employeeComboBox.removeAllItems();
            currentRentalId=-1;
            List<Car> sortedCarList=new ArrayList<>(carList);
            Collections.sort(sortedCarList, new Comparator<Car>() {
                @Override
                public int compare(Car c1, Car c2) {
                    return c1.toString().compareToIgnoreCase(c2.toString());
                }
            });
            for (Car car : sortedCarList) {

                carComboBox.addItem(car);
            }
            List<Client> sortedClientList=new ArrayList<>(clientList);
            Collections.sort(sortedClientList, new Comparator<Client>() {
                @Override
                public int compare(Client c1, Client c2) {
                    return c1.toString().compareToIgnoreCase(c2.toString());
                }
            });
            for (Client client : sortedClientList) {

                clientComboBox.addItem(client);
            }
            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
            Collections.sort(sortedEmployeeList, new Comparator<Employee>() {
                @Override
                public int compare(Employee e1, Employee e2) {
                    return e1.toString().compareToIgnoreCase(e2.toString());
                }
            });
            for (Employee employee : sortedEmployeeList) {

                employeeComboBox.addItem(employee);
            }
            addRentalEditArea.setVisible(true);
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < rentalList.size()) {
                Rental rentalToDelete = rentalList.get(row);
                int rentalId = rentalToDelete.getId();
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this rental?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteRental(rentalId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Rental deleted successfully.");
                        rentalModel.removeRow(row);
                        rentalList.remove(row);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete rental.");
                    }
                }
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row = rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if (e.getClickCount() == 2) {
                            Rental selectedRental = rentalList.get(rowIndex);

                            String startDateStr = rentalModel.getValueAt(row, 0).toString();
                            java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
                            startDatePicker.setDate(startDate);
                            String endDateStr = rentalModel.getValueAt(row, 1).toString();
                            java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
                            endDatePicker.setDate(endDate);
                            carComboBox.removeAllItems();
                            clientComboBox.removeAllItems();
                            employeeComboBox.removeAllItems();
                            currentRentalId=-1;
                            List<Car> sortedCarList=new ArrayList<>(carList);
                            Collections.sort(sortedCarList, new Comparator<Car>() {
                                @Override
                                public int compare(Car c1, Car c2) {
                                    return c1.toString().compareToIgnoreCase(c2.toString());
                                }
                            });
                            for (Car car : sortedCarList) {

                                carComboBox.addItem(car);
                            }
                            List<Client> sortedClientList=new ArrayList<>(clientList);
                            Collections.sort(sortedClientList, new Comparator<Client>() {
                                @Override
                                public int compare(Client c1, Client c2) {
                                    return c1.toString().compareToIgnoreCase(c2.toString());
                                }
                            });
                            for (Client client : sortedClientList) {

                                clientComboBox.addItem(client);
                            }
                            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
                            Collections.sort(sortedEmployeeList, new Comparator<Employee>() {
                                @Override
                                public int compare(Employee e1, Employee e2) {
                                    return e1.toString().compareToIgnoreCase(e2.toString());
                                }
                            });
                            for (Employee employee : sortedEmployeeList) {

                                employeeComboBox.addItem(employee);
                            }
                            addRentalEditArea.setVisible(true);
                            int carId = selectedRental.getCarId();
                            for (int i = 0; i < carComboBox.getItemCount(); i++) {
                                Car car = carComboBox.getItemAt(i);
                                if (car.getId() == carId) {
                                    carComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            int clientId = selectedRental.getClientId();
                            for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                                Client client = clientComboBox.getItemAt(i);
                                if (client.getId() == clientId) {
                                    clientComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            int employeeId = selectedRental.getEmployeeId();
                            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                                Employee employee = employeeComboBox.getItemAt(i);
                                if (employee.getId() == employeeId) {
                                    employeeComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            addRentalEditArea.setVisible(true);
                            currentRentalId=selectedRental.getId();
                        }
                    } else {
                        table.clearSelection();
                        addRentalEditArea.setVisible(false);
                        deleteButton.setEnabled(false);
                    }
                } else {
                    table.clearSelection();
                    addRentalEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = (table.getSelectedRow() != -1);
                deleteButton.setEnabled(rowSelected);

                if (!rowSelected) {
                    addRentalEditArea.setVisible(false);
                }
            }
        });

        clearButton.addActionListener(e -> {
            clearRentalAddEditFields(startDatePicker, endDatePicker, carComboBox, clientComboBox, employeeComboBox);
            addRentalEditArea.setVisible(false);
            table.clearSelection();
        });

        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addRentalEditArea, BorderLayout.SOUTH);
        return panel;
    }
    private void clearRentalAddEditFields(JXDatePicker startdatePicker, JXDatePicker enddatePicker, JComboBox<Car> carComboBox,JComboBox<Client> clientComboBox, JComboBox<Employee> employeeComboBox) {

        startdatePicker.setDate(null);
        enddatePicker.setDate(null);
        if (carComboBox.getItemCount() > 0) {
            carComboBox.setSelectedIndex(0);
        }
        if (clientComboBox.getItemCount() > 0) {
            clientComboBox.setSelectedIndex(0);
        }
        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }
    }
    private void refreshRentalTable(DefaultTableModel model) {
        List<Rental> rentals = DbUtils.getAllRentals();
        model.setRowCount(0);

        for (Rental rental : rentalList) {
            String carPlate = DbUtils.getCarPlateById(rental.getCarId());
            String clientName = DbUtils.getClientNameById(rental.getClientId());
            String employeeName = DbUtils.getEmployeeNameById(rental.getEmployeeId());

            model.addRow(new Object[]{
                    rental.getStartDate(),
                    rental.getEndDate(),
                    carPlate,
                    clientName,
                    employeeName
            });
        }
    }


    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "Receipt Number", "Payment Date", "Amount", "Service", "Employee Name", "Rental"
        };
        paymentModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        paymentList = DbUtils.getAllPayments();
        rentalList=DbUtils.getAllRentals();
        for (Payment payment : paymentList) {
            String employeeName = DbUtils.getEmployeeNameById(payment.getEmployeeId());
            String rentalInfo = "";
            for (Rental rental : rentalList) {
                if (rental.getId() == payment.getRentalId()) {
                    rentalInfo = rental.toString();
                    break;
                }}
            paymentModel.addRow(new Object[]{
                    payment.getReceiptNumber(),
                    payment.getPaymentDate(),
                    payment.getAmount(),
                    payment.getService(),
                    employeeName,
                    rentalInfo,
            });
        }

        JTable table = new JTable(paymentModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addPaymentEditArea = new JPanel(new GridLayout(0, 2));


        JTextField receiptNumberField = new JTextField();
        JTextField paymentDateField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField serviceField = new JTextField();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();
        JComboBox<Rental> rentalComboBox = new JComboBox<>();


        addPaymentEditArea.add(new JLabel("Receipt Number:"));
        addPaymentEditArea.add(receiptNumberField);
        addPaymentEditArea.add(new JLabel("Payment Date:"));
        addPaymentEditArea.add(paymentDateField);
        addPaymentEditArea.add(new JLabel("Amount:"));
        addPaymentEditArea.add(amountField);
        addPaymentEditArea.add(new JLabel("Service:"));
        addPaymentEditArea.add(serviceField);
        addPaymentEditArea.add(new JLabel("Employee:"));
        addPaymentEditArea.add(employeeComboBox);
        addPaymentEditArea.add(new JLabel("Rental :"));
        addPaymentEditArea.add(rentalComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {

                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();
                Rental selectedRental=(Rental) rentalComboBox.getSelectedItem();
                int rentalId = selectedRental.getId();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedPaymentDate = dateFormat.parse(paymentDateField.getText());
                Date paymentDate = new Date(parsedPaymentDate.getTime());


                float amount = Float.parseFloat(amountField.getText());





                Payment payment = new Payment(
                        currentPaymentId,
                        receiptNumberField.getText(),
                        paymentDate,
                        amount,
                        serviceField.getText(),
                        employeeId,
                        rentalId
                );


                boolean success = DbUtils.addEditPayment(payment);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Payment saved successfully.");
                    List<Payment> newPayments = DbUtils.getAllPayments();
                    paymentList.clear();
                    paymentList.addAll(newPayments);
                    refreshPaymentTable(paymentModel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save payment.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number format in one of the fields.");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid date format in Payment Date.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {

            receiptNumberField.setText("");
            paymentDateField.setText("");
            amountField.setText("");
            serviceField.setText("");
            if (employeeComboBox.getItemCount() > 0) {
                employeeComboBox.setSelectedIndex(0);
            }
            if (rentalComboBox.getItemCount() > 0) {
                rentalComboBox.setSelectedIndex(0);
            }
        });

        addPaymentEditArea.add(saveButton);
        addPaymentEditArea.add(clearButton);

        panel.add(addPaymentEditArea, BorderLayout.SOUTH);
        addPaymentEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewPaymentButton = new JButton("Add Payment");
        buttonPanel.add(addNewPaymentButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewPaymentButton);
        buttonPanel.add(deleteButton);
        addNewPaymentButton.addActionListener(e -> {
            clearPaymentAddEditFields(receiptNumberField, paymentDateField, amountField, serviceField, employeeComboBox, rentalComboBox);

            employeeComboBox.removeAllItems();
            rentalComboBox.removeAllItems();
            currentPaymentId = -1;

            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
            Collections.sort(sortedEmployeeList, Comparator.comparing(Employee::toString, String.CASE_INSENSITIVE_ORDER));
            for (Employee employee : sortedEmployeeList) {
                employeeComboBox.addItem(employee);
            }
            List<Rental> sortedRentalList = new ArrayList<>(rentalList);
            Collections.sort(sortedRentalList, Comparator.comparing(Rental::toString, String.CASE_INSENSITIVE_ORDER));
            for (Rental rental : sortedRentalList) {
                rentalComboBox.addItem(rental);
            }
            addPaymentEditArea.setVisible(true);
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < paymentList.size()) {
                Payment paymentToDelete = paymentList.get(row);
                int paymentId = paymentToDelete.getId();
                int choice = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this payment?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deletePayment(paymentId);
                    if (success) {
                        JOptionPane.showMessageDialog(panel, "Payment deleted successfully.");
                        paymentModel.removeRow(row);
                        paymentList.remove(row);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to delete payment.");
                    }
                }
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row = rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if (e.getClickCount() == 2) {
                            Payment selectedPayment = paymentList.get(rowIndex);

                            receiptNumberField.setText(paymentModel.getValueAt(row, 0).toString());
                            paymentDateField.setText(paymentModel.getValueAt(row, 1).toString());
                            amountField.setText(paymentModel.getValueAt(row, 2).toString());
                            serviceField.setText(paymentModel.getValueAt(row, 3).toString());


                            employeeComboBox.removeAllItems();
                            rentalComboBox.removeAllItems();
                            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
                            Collections.sort(sortedEmployeeList, Comparator.comparing(Employee::toString, String.CASE_INSENSITIVE_ORDER));
                            for (Employee employee : sortedEmployeeList) {
                                employeeComboBox.addItem(employee);
                            }
                            List<Rental> sortedRentalList = new ArrayList<>(rentalList);
                            Collections.sort(sortedRentalList, Comparator.comparing(Rental::toString, String.CASE_INSENSITIVE_ORDER));
                            for (Rental rental : sortedRentalList) {
                                rentalComboBox.addItem(rental);
                            }
                            int rentalId = selectedPayment.getRentalId();
                            for (int i = 0; i < rentalComboBox.getItemCount(); i++) {
                                Rental rental = rentalComboBox.getItemAt(i);
                                if (rental.getId() == rentalId) {
                                    rentalComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            int employeeId = selectedPayment.getEmployeeId();
                            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                                Employee employee = employeeComboBox.getItemAt(i);
                                if (employee.getId() == employeeId) {
                                    employeeComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            currentPaymentId = selectedPayment.getId();
                            addPaymentEditArea.setVisible(true);
                        }
                    } else {
                        table.clearSelection();
                        addPaymentEditArea.setVisible(false);
                        deleteButton.setEnabled(false);
                    }
                } else {
                    table.clearSelection();
                    addPaymentEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = (table.getSelectedRow() != -1);
                deleteButton.setEnabled(rowSelected);
                if (!rowSelected) {
                    addPaymentEditArea.setVisible(false);
                }
            }
        });

        clearButton.addActionListener(e -> {
            clearPaymentAddEditFields(receiptNumberField, paymentDateField, amountField, serviceField, employeeComboBox, rentalComboBox);
            addPaymentEditArea.setVisible(false);
            table.clearSelection();
        });

        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addPaymentEditArea, BorderLayout.SOUTH);
        return panel;
    }
    private void clearPaymentAddEditFields(JTextField receiptNumberField, JTextField paymentDateField, JTextField amountField, JTextField serviceField, JComboBox<Employee> employeeComboBox, JComboBox<Rental> rentalComboBox) {
        receiptNumberField.setText("");
        paymentDateField.setText("");
        amountField.setText("");
        serviceField.setText("");

        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }
        if (rentalComboBox.getItemCount() > 0) {
            rentalComboBox.setSelectedIndex(0);
        }
    }
    private void refreshPaymentTable(DefaultTableModel model) {
        List<Payment> payments = DbUtils.getAllPayments();
        model.setRowCount(0);

        for (Payment payment : paymentList) {
            String employeeName = DbUtils.getEmployeeNameById(payment.getEmployeeId());
            String rentalInfo = "";
            for (Rental rental : rentalList) {
                if (rental.getId() == payment.getRentalId()) {
                    rentalInfo = rental.toString();
                    break;
                }}
            model.addRow(new Object[]{
                    payment.getReceiptNumber(),
                    payment.getPaymentDate(),
                    payment.getAmount(),
                    payment.getService(),
                    employeeName,
                    rentalInfo,
            });
        }
    }


    private JPanel createInvoicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "Supplier Name", "Service", "Date", "Amount", "Employee Name"
        };
        invoiceModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        invoiceList = DbUtils.getAllInvoices();
        employeeList = DbUtils.getAllEmployees();

        for (Invoice invoice : invoiceList) {
            String employeeName = DbUtils.getEmployeeNameById(invoice.getEmployeeId());
            invoiceModel.addRow(new Object[]{
                    invoice.getSupplierName(),
                    invoice.getService(),
                    invoice.getDate(),
                    invoice.getAmount(),
                    employeeName,
            });
        }

        JTable table = new JTable(invoiceModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);


        JPanel addInvoiceEditArea = new JPanel(new GridLayout(0, 2));


        JTextField supplierNameField = new JTextField();
        JTextField serviceField = new JTextField();
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats("yyyy-MM-dd");
        JTextField amountField = new JTextField();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();


        addInvoiceEditArea.add(new JLabel("Supplier Name:"));
        addInvoiceEditArea.add(supplierNameField);
        addInvoiceEditArea.add(new JLabel("Service:"));
        addInvoiceEditArea.add(serviceField);
        addInvoiceEditArea.add(new JLabel("Date:"));
        addInvoiceEditArea.add(datePicker);
        addInvoiceEditArea.add(new JLabel("Amount:"));
        addInvoiceEditArea.add(amountField);
        addInvoiceEditArea.add(new JLabel("Employee:"));
        addInvoiceEditArea.add(employeeComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {





                java.util.Date utilDate = datePicker.getDate();
                java.sql.Date selectedDate = new java.sql.Date(utilDate.getTime());



                float amount = Float.parseFloat(amountField.getText());


                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();


                Invoice invoice = new Invoice(
                        currentInvoiceId,
                        supplierNameField.getText(),
                        serviceField.getText(),
                        selectedDate,
                        amount,
                        employeeId
                );


                boolean success = DbUtils.addEditInvoice(invoice);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "Invoice saved successfully.");
                    List<Invoice> newInvoices = DbUtils.getAllInvoices();
                    invoiceList.clear();
                    invoiceList.addAll(newInvoices);
                    refreshInvoiceTable(invoiceModel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Failed to save invoice.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number format in one of the fields.");
            }  catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        JButton clearButton = new JButton("Clear/Cancel");
        clearButton.addActionListener(e -> {
            supplierNameField.setText("");
            serviceField.setText("");
            datePicker.setDate(null);
            amountField.setText("");
            if (employeeComboBox.getItemCount() > 0) {
                employeeComboBox.setSelectedIndex(0);
            }
        });

        addInvoiceEditArea.add(saveButton);
        addInvoiceEditArea.add(clearButton);

        panel.add(addInvoiceEditArea, BorderLayout.SOUTH);
        addInvoiceEditArea.setVisible(false);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewInvoiceButton = new JButton("Add Invoice");
        buttonPanel.add(addNewInvoiceButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);


        buttonPanel.add(addNewInvoiceButton);
        buttonPanel.add(deleteButton);
        addNewInvoiceButton.addActionListener(e -> {
            clearInvoiceAddEditFields(supplierNameField, serviceField, datePicker, amountField, employeeComboBox);
            employeeComboBox.removeAllItems();
            currentInvoiceId = -1;

            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
            Collections.sort(sortedEmployeeList, Comparator.comparing(Employee::toString, String.CASE_INSENSITIVE_ORDER));
            for (Employee employee : sortedEmployeeList) {
                employeeComboBox.addItem(employee);
            }
            addInvoiceEditArea.setVisible(true);
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < invoiceList.size()) {
                Invoice invoiceToDelete = invoiceList.get(row);
                int invoiceId = invoiceToDelete.getId();
                int choice = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete this invoice?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteInvoice(invoiceId);
                    if (success) {
                        JOptionPane.showMessageDialog(panel, "Invoice deleted successfully.");
                        invoiceModel.removeRow(row);
                        invoiceList.remove(row);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Failed to delete invoice.");
                    }
                }
            }
        });


        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    int colIndex = table.columnAtPoint(e.getPoint());
                    boolean isRowValid = rowIndex >= 0 && rowIndex < table.getRowCount();
                    boolean isColumnValid = colIndex >= 0 && colIndex < table.getColumnCount();
                    boolean isCellValid = isRowValid && isColumnValid;
                    int row = rowIndex;

                    if (isCellValid) {
                        table.setRowSelectionInterval(row, row);
                        if (e.getClickCount() == 2) {
                            Invoice selectedInvoice = invoiceList.get(rowIndex);


                            supplierNameField.setText(invoiceModel.getValueAt(row, 0).toString());
                            serviceField.setText(invoiceModel.getValueAt(row, 1).toString());
                            String dateStr = invoiceModel.getValueAt(row, 2).toString();
                            java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);


                            datePicker.setDate(sqlDate);

                            amountField.setText(invoiceModel.getValueAt(row, 3).toString());

                            employeeComboBox.removeAllItems();
                            List<Employee> sortedEmployeeList = new ArrayList<>(employeeList);
                            Collections.sort(sortedEmployeeList, Comparator.comparing(Employee::toString, String.CASE_INSENSITIVE_ORDER));
                            for (Employee employee : sortedEmployeeList) {
                                employeeComboBox.addItem(employee);
                            }
                            int employeeId = selectedInvoice.getEmployeeId();
                            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                                Employee employee = employeeComboBox.getItemAt(i);
                                if (employee.getId() == employeeId) {
                                    employeeComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            currentInvoiceId = selectedInvoice.getId();
                            addInvoiceEditArea.setVisible(true);
                        }
                    } else {
                        table.clearSelection();
                        addInvoiceEditArea.setVisible(false);
                        deleteButton.setEnabled(false);
                    }
                } else {
                    table.clearSelection();
                    addInvoiceEditArea.setVisible(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = (table.getSelectedRow() != -1);
                deleteButton.setEnabled(rowSelected);
                if (!rowSelected) {
                    addInvoiceEditArea.setVisible(false);
                }
            }
        });

        clearButton.addActionListener(e -> {
            clearInvoiceAddEditFields( supplierNameField, serviceField, datePicker, amountField, employeeComboBox);
            addInvoiceEditArea.setVisible(false);
            table.clearSelection();
        });

        panel.add(buttonPanel, BorderLayout.PAGE_START);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addInvoiceEditArea, BorderLayout.SOUTH);
        return panel;
    }
    private void clearInvoiceAddEditFields(JTextField supplierNameField, JTextField serviceField, JXDatePicker datePicker, JTextField amountField, JComboBox<Employee> employeeComboBox) {

        supplierNameField.setText("");
        serviceField.setText("");
        datePicker.setDate(null);
        amountField.setText("");

        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0);
        }
    }
    private void refreshInvoiceTable(DefaultTableModel model) {
        List<Invoice> invoices = DbUtils.getAllInvoices();
        model.setRowCount(0);

        for (Invoice invoice : invoices) {
            String employeeName = DbUtils.getEmployeeNameById(invoice.getEmployeeId());
            model.addRow(new Object[]{
                    invoice.getSupplierName(),
                    invoice.getService(),
                    invoice.getDate(),
                    invoice.getAmount(),
                    employeeName,
            });
        }
    }

}