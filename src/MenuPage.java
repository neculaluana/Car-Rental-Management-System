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
        setLocationRelativeTo(null); // Center the window

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
    //Car Panel
    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "License Plate", "Brand", "Model", "Chassis Series", "Seats", "Fuel Type", "Year", "Color", "Daily Rate"
        };
        carModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make none of the cells editable directly by double-clicking
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

        // Add/Edit Area
        JPanel addCarEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Car attributes
        JTextField licencePlateField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField chassisSeriesField = new JTextField();
        JTextField seatsNumberField = new JTextField();
        JTextField fuelTypeField = new JTextField();
        JTextField manufactureYearField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField dailyRateField = new JTextField();

        // Adding labels and text fields to the panel
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
                // Parse integer and float values from text fields
                int seatsNumber = Integer.parseInt(seatsNumberField.getText());
                int manufactureYear = Integer.parseInt(manufactureYearField.getText());
                float dailyRate = Float.parseFloat(dailyRateField.getText());

                // Create a new Car object with the values from the text fields
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

                // Call the addEditCar method from DbUtils
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
            // Clear all fields
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

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Car");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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
                int carId = carToDelete.getId(); // Get car ID from the Car object
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteCar(carId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Car deleted successfully.");
                        carModel.removeRow(row); // Remove the row from the table model
                        carList.remove(row); // Also remove the car from the list
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete car.");
                    }
            }}
        });

        // Add a selection listener to the table to enable the Delete button when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This check prevents double events
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });
//        panel.add(addNewEntryButton, BorderLayout.NORTH);
        // Double-click listener to load data into fields
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
                            dailyRateField.setText(carModel.getValueAt(row, 9).toString());
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
                // If no row is selected, hide the Add/Edit area
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
        List<Car> cars = DbUtils.getAllCars(); // Fetch the updated list
        model.setRowCount(0); // Clear existing data

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

    //Client Panel
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {
                "CNP", "Name", "Address", "Email", "Phone Number", "Birthdate", "Origin Country",
                "Driver License Number", "Issue Date", "Expiration Date", "Car Categories"
        };

        clientModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make none of the cells editable directly by double-clicking
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

        // Add/Edit Area
        JPanel addClientEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Client attributes
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

        // Adding labels and text fields to the panel
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
                // Parse Date values from text fields
                java.util.Date utilBirthDate = birthDatePicker.getDate();
                java.sql.Date birthDate = new java.sql.Date(utilBirthDate.getTime());
                java.util.Date utilIssueDate = issueDatePicker.getDate();
                java.sql.Date issueDate = new java.sql.Date(utilIssueDate.getTime());
                java.util.Date utilExpirationDate = expirationDatePicker.getDate();
                java.sql.Date expirationDate = new java.sql.Date(utilExpirationDate.getTime());

                // Create a new Client object with the values from the text fields
                Client client = new Client(
                        currentClientId, // Set the ID to -1 as it's a new client
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

                // Call the addEditClient method from DbUtils
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
            // Clear all fields
            clearClientAddEditFields(cnpField, nameField, addressField, emailField, phoneNumberField,
                    birthDatePicker, originCountryField, driverLicenseNumberField, issueDatePicker,
                    expirationDatePicker, carCategoriesField);
        });

        addClientEditArea.add(saveButton);
        addClientEditArea.add(clearButton);

        panel.add(addClientEditArea, BorderLayout.SOUTH);
        addClientEditArea.setVisible(false);

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Client");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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
                int clientId = clientToDelete.getId(); // Get client ID from the Client object

                // Display a confirmation dialog
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this client?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteClient(clientId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Client deleted successfully.");
                        clientModel.removeRow(row); // Remove the row from the table model
                        Client clientToRemove= clientList.get(row);
                        clientList.remove(clientToRemove); // Also remove the client from the list
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete client.");
                    }
                }
            }
        });

        // Add a selection listener to the table to enable the Delete button when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This check prevents double events
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });

        // Double-click listener to load data into fields
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
        // Clear the existing rows from the table model
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        // Get the latest client data from the database and add it to the table model
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

    //Employee Panel
    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {
                "CNP", "Name", "Address", "Birthdate", "Employment Date", "Position"
        };

        employeeModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make none of the cells editable directly by double-clicking
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

        // Add/Edit Area
        JPanel addEmployeeEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Employee attributes
        JTextField cnpField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JXDatePicker birthDatePicker = new JXDatePicker();
        birthDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker employmentDatePicker = new JXDatePicker();
        employmentDatePicker.setFormats("yyyy-MM-dd");
        JTextField positionField = new JTextField();

        // Adding labels and text fields to the panel
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
                // Parse Date value from text field
                java.util.Date utilBirthDate = birthDatePicker.getDate();
                java.sql.Date birthDate = new java.sql.Date(utilBirthDate.getTime());
                java.util.Date utilEmployementDate = employmentDatePicker.getDate();
                java.sql.Date employmentDate = new java.sql.Date(utilEmployementDate.getTime());

                // Create a new Employee object with the values from the text fields
                Employee employee = new Employee(
                        currentEmployeeId, // Set the ID to -1 as it's a new employee
                        cnpField.getText(),
                        nameField.getText(),
                        addressField.getText(),
                        birthDate,
                        employmentDate,
                        positionField.getText()
                );

                // Call the addEditEmployee method from DbUtils
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
            // Clear all fields
            clearEmployeeAddEditFields(cnpField, nameField, addressField, birthDatePicker,
                    employmentDatePicker, positionField);
        });

        addEmployeeEditArea.add(saveButton);
        addEmployeeEditArea.add(clearButton);

        panel.add(addEmployeeEditArea, BorderLayout.SOUTH);
        addEmployeeEditArea.setVisible(false);

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Employee");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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
                int employeeId = employeeToDelete.getId(); // Get employee ID from the Employee object

                // Display a confirmation dialog
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteEmployee(employeeId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                        employeeModel.removeRow(row); // Remove the row from the table model
                        employeeList.remove(row); // Also remove the employee from the list
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete employee.");
                    }
                }
            }
        });

        // Add a selection listener to the table to enable the Delete button when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This check prevents double events
                deleteButton.setEnabled(table.getSelectedRow() != -1);
            }
        });

        // Double-click listener to load data into fields
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
                // If no row is selected, hide the Add/Edit area
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
        // Clear the existing rows from the table model
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        // Get the latest employee data from the database and add it to the table model
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


    //Rental Panel
    private JPanel createRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {
                "Start Date", "End Date", "Car Plate", "Client Name", "Employee Name"
        };
        rentalModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // This will make none of the cells editable directly by double-clicking
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

        // Add/Edit Area
        JPanel addRentalEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Rental attributes
        JXDatePicker startDatePicker = new JXDatePicker();
        startDatePicker.setFormats("yyyy-MM-dd");
        JXDatePicker endDatePicker = new JXDatePicker();
        endDatePicker.setFormats("yyyy-MM-dd");
        JComboBox<Car> carComboBox = new JComboBox<>();
        JComboBox<Client> clientComboBox = new JComboBox<>();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();


        // Adding labels and text fields to the panel
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

                // Parse integer values from text fields
                Car selectedCar=(Car) carComboBox.getSelectedItem();
                int carId = selectedCar.getId();
                Client selectedClient=(Client) clientComboBox.getSelectedItem();
                int clientId = selectedClient.getId();
                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();

                // Parse date values from text fields
                java.util.Date utilStartDate = startDatePicker.getDate();
                java.sql.Date startDate = new java.sql.Date(utilStartDate.getTime());
                java.util.Date utilEndDate = endDatePicker.getDate();
                java.sql.Date endDate = new java.sql.Date(utilEndDate.getTime());


                // Create a new Rental object with the values from the text fields
                Rental rental = new Rental(
                        currentRentalId,
                        startDate,
                        endDate,
                        carId,
                        clientId,
                        employeeId
                );

                // Call the addEditRental method from DbUtils
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
            // Clear all fields
            startDatePicker.setDate(null);
            endDatePicker.setDate(null);
            if (carComboBox.getItemCount() > 0) {
                carComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
            }
            if (clientComboBox.getItemCount() > 0) {
                clientComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
            }
            if (employeeComboBox.getItemCount() > 0) {
                employeeComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
            } // This sets the selection to the first item in the combo box

        });

        addRentalEditArea.add(saveButton);
        addRentalEditArea.add(clearButton);

        panel.add(addRentalEditArea, BorderLayout.SOUTH);
        addRentalEditArea.setVisible(false);

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewEntryButton = new JButton("Add Rental");
        buttonPanel.add(addNewEntryButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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

                employeeComboBox.addItem(employee); // Add the entire employee object
            }
            addRentalEditArea.setVisible(true);
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && row < rentalList.size()) {
                Rental rentalToDelete = rentalList.get(row);
                int rentalId = rentalToDelete.getId(); // Get rental ID from the Rental object
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this rental?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    boolean success = DbUtils.deleteRental(rentalId);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Rental deleted successfully.");
                        rentalModel.removeRow(row); // Remove the row from the table model
                        rentalList.remove(row);// Also remove the rental from the list
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete rental.");
                    }
                }
            }
        });

        // Add a selection listener to the table to enable the Delete button when a row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This check prevents double events
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

                                employeeComboBox.addItem(employee); // Add the entire employee object
                            }
                            addRentalEditArea.setVisible(true);
                            int carId = selectedRental.getCarId(); // Assuming Rental object has getEmployeeId() method
                            for (int i = 0; i < carComboBox.getItemCount(); i++) {
                                Car car = carComboBox.getItemAt(i);
                                if (car.getId() == carId) { // Assuming Employee object has getId() method
                                    carComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            int clientId = selectedRental.getClientId(); // Assuming Rental object has getEmployeeId() method
                            for (int i = 0; i < clientComboBox.getItemCount(); i++) {
                                Client client = clientComboBox.getItemAt(i);
                                if (client.getId() == clientId) { // Assuming Employee object has getId() method
                                    clientComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            int employeeId = selectedRental.getEmployeeId(); // Assuming Rental object has getEmployeeId() method
                            for (int i = 0; i < employeeComboBox.getItemCount(); i++) {
                                Employee employee = employeeComboBox.getItemAt(i);
                                if (employee.getId() == employeeId) { // Assuming Employee object has getId() method
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
                // If no row is selected, hide the Add/Edit area
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
            carComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
        }
        if (clientComboBox.getItemCount() > 0) {
            clientComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
        }
        if (employeeComboBox.getItemCount() > 0) {
            employeeComboBox.setSelectedIndex(0); // Assumes the first item is a placeholder or the first employee
        }
    }
    private void refreshRentalTable(DefaultTableModel model) {
        List<Rental> rentals = DbUtils.getAllRentals(); // Fetch the updated list
        model.setRowCount(0); // Clear existing data

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
                // This will make none of the cells editable directly by double-clicking
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
                    break; // Stop searching once you've found the matching Rental
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

        // Add/Edit Area
        JPanel addPaymentEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Payment attributes
        JTextField receiptNumberField = new JTextField();
        JTextField paymentDateField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField serviceField = new JTextField();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();
        JComboBox<Rental> rentalComboBox = new JComboBox<>();

        // Adding labels and text fields to the panel
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
                // Parse integer values from text fields
                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();
                Rental selectedRental=(Rental) rentalComboBox.getSelectedItem();
                int rentalId = selectedRental.getId();
                // Parse date values from text field
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedPaymentDate = dateFormat.parse(paymentDateField.getText());
                Date paymentDate = new Date(parsedPaymentDate.getTime());

                // Parse float value from text field
                float amount = Float.parseFloat(amountField.getText());




                // Create a new Payment object with the values from the text fields
                Payment payment = new Payment(
                        currentPaymentId,
                        receiptNumberField.getText(),
                        paymentDate,
                        amount,
                        serviceField.getText(),
                        employeeId,
                        rentalId
                );

                // Call the addEditPayment method from DbUtils
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
            // Clear all fields
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

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewPaymentButton = new JButton("Add Payment");
        buttonPanel.add(addNewPaymentButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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

        // Add a selection listener to the table to enable the Delete button when a row is selected
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
                    break; // Stop searching once you've found the matching Rental
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
                // This will make none of the cells editable directly by double-clicking
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

        // Add/Edit Area
        JPanel addInvoiceEditArea = new JPanel(new GridLayout(0, 2));

        // Add fields for Invoice attributes
        JTextField supplierNameField = new JTextField();
        JTextField serviceField = new JTextField();
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats("yyyy-MM-dd");
        JTextField amountField = new JTextField();
        JComboBox<Employee> employeeComboBox = new JComboBox<>();

        // Adding labels and text fields to the panel
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
                // Parse integer value from text field


                // Parse date value from text field

                java.util.Date utilDate = datePicker.getDate();
                java.sql.Date selectedDate = new java.sql.Date(utilDate.getTime());


                // Parse float value from text field
                float amount = Float.parseFloat(amountField.getText());

                // Get the selected employee
                Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
                int employeeId = selectedEmployee.getId();

                // Create a new Invoice object with the values from the text fields
                Invoice invoice = new Invoice(
                        currentInvoiceId,
                        supplierNameField.getText(),
                        serviceField.getText(),
                        selectedDate,
                        amount,
                        employeeId
                );

                // Call the addEditInvoice method from DbUtils
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

        // Add New Entry Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addNewInvoiceButton = new JButton("Add Invoice");
        buttonPanel.add(addNewInvoiceButton);
        JButton deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);

        // Add buttons to the right-aligned panel
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

        // Add a selection listener to the table to enable the Delete button when a row is selected
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

                            // Set the date in the JXDatePicker
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