public class Car {
    protected int Id;
    protected String LicencePlate;
    protected String Brand;
    protected String Model;
    protected String ChassisSeries;
    protected int SeatsNumber;
    protected String FuelType;
    protected int ManufactureYear;
    protected String Color;
    protected float DailyRate;

    public Car(int id, String licencePlate, String brand, String model, String chassisSeries, int seatsNumber, String fuelType, int manufactureYear, String color, float dailyRate) {
        Id = id;
        LicencePlate = licencePlate;
        Brand = brand;
        Model = model;
        ChassisSeries = chassisSeries;
        SeatsNumber = seatsNumber;
        FuelType = fuelType;
        ManufactureYear = manufactureYear;
        Color = color;
        DailyRate = dailyRate;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLicencePlate() {
        return LicencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        LicencePlate = licencePlate;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getChassisSeries() {
        return ChassisSeries;
    }

    public void setChassisSeries(String chassisSeries) {
        ChassisSeries = chassisSeries;
    }

    public int getSeatsNumber() {
        return SeatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        SeatsNumber = seatsNumber;
    }

    public String getFuelType() {
        return FuelType;
    }

    public void setFuelType(String fuelType) {
        FuelType = fuelType;
    }

    public int getManufactureYear() {
        return ManufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        ManufactureYear = manufactureYear;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public float getDailyRate() {
        return DailyRate;
    }

    public void setDailyRate(float dailyRate) {
        DailyRate = dailyRate;
    }
    @Override
    public String toString() {
        return this.Brand+", "+this.Model+", "+this.LicencePlate;
    }
}