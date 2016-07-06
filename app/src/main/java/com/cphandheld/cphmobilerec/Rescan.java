package com.cphandheld.cphmobilerec;

import java.sql.Timestamp;

/**
 * Created by titan on 5/29/16.
 */
public class Rescan {

    // StepInstanceGuid, DealershipId, VinNumber, Assigned, Year, Make, Model, Color, Flag, TimeStamp, UserId

    private String StepInstanceGuid;
    private String DealerCode;
    private String VIN;
    private String OpenDate;
    private String Year;
    private String Make;
    private String Model;
    private String Color;
    private String Flag; // entry method
    private String TimeStamp;
    private String UserId;


    public Rescan(String siid, String dealership, String vin, String assigned, String year, String make, String model, String color, String entryMethod, String scannedDate, String userId) {
        StepInstanceGuid = siid;
        DealerCode = dealership;
        VIN = vin;
        OpenDate = assigned;
        Year = year;
        Make = make;
        Model = model;
        Color = color;
        Flag = entryMethod;
        TimeStamp = scannedDate;
        UserId = userId;
    }

    public String getSIID() {
        return StepInstanceGuid;
    }
    public String getVIN() {
        return VIN;
    }
    public String getDealership() {
        return DealerCode;
    }
    public String getAssigned() {
        return OpenDate;
    }
    public String getEntryType() {
        return Flag;
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
        return TimeStamp;
    }
    public String getUserId() {
        return UserId;
    }

}
