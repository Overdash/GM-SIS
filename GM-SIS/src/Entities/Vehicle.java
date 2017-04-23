package Entities;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Ussas on 18/02/2017.
 */
public class Vehicle {
    private String RegNumber;
    private String Make;
    private String Model;
    private String Colour;
    private String fuelType;
    private String engineSize;
    private String MOTdate;
    private String lastService;
    private int CustID;
    private boolean Warranty;
    private int mileage;
    private String vehicleType;

    public Vehicle(ResultSet rsVehicle) {
        try {
            RegNumber = rsVehicle.getString("RegNumber");
            Make=rsVehicle.getString("Make");
            Model=rsVehicle.getString("Model");
            Colour=rsVehicle.getString("Colour");
            fuelType=rsVehicle.getString("FuelType");
            engineSize=rsVehicle.getString("EngineSize");
            MOTdate=rsVehicle.getString("MoTDate");
            lastService=rsVehicle.getString("LastService");
            Warranty=rsVehicle.getBoolean("Warranty");
            mileage=rsVehicle.getInt("Mileage");
            vehicleType=rsVehicle.getString("Type");
            CustID=rsVehicle.getInt("CustomerID");


        } catch (SQLException e) {e.printStackTrace();}
    }

    public String getRegNumber() {
        return RegNumber;
    }

    public void setRegNumber(String regNumber) {
        RegNumber = regNumber;
    }

    public String getMake() {
        return Make;
    }

    public void setMake(String make) {
        Make = make;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getColour() {
        return Colour;
    }

    public void setColour(String colour) {
        Colour = colour;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public String getMOTdate() {
        return MOTdate;
    }

    public void setMOTdate(String MOTdate) {
        this.MOTdate = MOTdate;
    }

    public String getLastService() {
        return lastService;
    }

    public void setLastService(String lastService) {
        this.lastService = lastService;
    }

    public boolean isWarranty() {
        return Warranty;
    }

    public void setWarranty(boolean warranty) {
        Warranty = warranty;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getCustID() {return CustID;}

    public void setCustID(int custID) {CustID = custID;}
}
