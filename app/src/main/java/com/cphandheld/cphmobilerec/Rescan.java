package com.cphandheld.cphmobilerec;

/**
 * Created by titan on 5/29/16.
 */
public class Rescan {

    private String VIN;
    private String DealerCode;
    private String ScanMethod;
    private String Year;
    private String Make;
    private String Model;
    private String Color;
    private String ScannedDate;
    private String UserName;



    public Rescan( String vin, String dealership, String entryType, String year, String make, String model, String color, String scannedDate, String userName) {
        VIN = vin;
        DealerCode = dealership;
        ScanMethod = entryType;
        Year = year;
        Make = make;
        Model = model;
        Color = color;
        ScannedDate = scannedDate;
        UserName = userName;

    }

    public String getVIN() {
        return VIN;
    }
    public String getDealership() {
        return DealerCode;
    }
    public String getEntryType() {
        return ScanMethod;
    }
    public String getYear() {
        return Year;
    }
    public String getMake() {
        return Make;
    }
    public String getModel() {return Model;}
    public String getColor() {
        return Color;
    }
    public String getScanneDate() {
        return ScannedDate;
    }
    public String getUserName() {
        return UserName;
    }


}
