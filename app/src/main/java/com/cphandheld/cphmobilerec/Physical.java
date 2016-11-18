package com.cphandheld.cphmobilerec;

/**
 * Created by titan on 4/8/16.
 */
public class Physical {

    private String VIN;
    private String DealerCode;
    private String ScanMethod;
    private String ScanType;
    private String _date;
    private String _time;
    private String Lot;
    private String Notes;
    private String TimeStamp;
    private String UserId;
    private String Latitude;
    private String Longitude;

    public Physical() {}

    public Physical( String vin, String dealership, String entryType, String newUsed, String date, String time, String lot, String notes, String userId, String latitude, String longitude) {
        VIN = vin;
        DealerCode = dealership;
        ScanMethod = entryType;
        ScanType = newUsed;
        _date = date;
        _time = time;
        Lot = lot;
        Notes = notes;
        TimeStamp = date + " " + time;
        UserId = userId;
        Latitude = latitude;
        Longitude = longitude;
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

    public String getNewUsed() {
        return ScanType;
    }

    public String getDate() {
        return _date;
    }

    public String getTime() {
        return _time;
    }

    public String getLot() {
        return Lot;
    }

    public String getNotes() {
        return Notes;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public String getLatitude() {
        return Latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

        @Override
        public String toString() {
            return this.VIN;
        }
    }

