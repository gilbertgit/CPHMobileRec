package com.cphandheld.cphmobilerec;

/**
 * Created by titan on 4/8/16.
 */
public class Physical {

    private String _vin;
    private String _dealership;
    private String _entryType;
    private String _newUsed;
    private String _date;
    private String _time;
    private String _lot;


    public Physical( String vin, String dealership, String entryType, String newUsed, String date, String time, String lot) {
        _vin = vin;
        _dealership = dealership;
        _entryType = entryType;
        _newUsed = newUsed;
        _date = date;
        _time = time;
        _lot = lot;
    }

    public String getVIN() {
        return _vin;
    }

    public String getDealership() {
        return _dealership;
    }

    public String getEntryType() {
        return _entryType;
    }

    public String getNewUsed() {
        return _newUsed;
    }

    public String getDate() {
        return _date;
    }

    public String getTime() {
        return _time;
    }

    public String getLot() {
        return _lot;
    }

        @Override
        public String toString() {
            return this._vin;
        }
    }

