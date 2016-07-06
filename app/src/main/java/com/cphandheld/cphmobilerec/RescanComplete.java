package com.cphandheld.cphmobilerec;

/**
 * Created by titan on 6/28/16.
 */
public class RescanComplete {
    // DealershipId, StepInstanceGuid, Flag, VinNumber, Latitude, Longitude, TimeStamp

    private String DealershipId;
    private String StepInstanceGuid;
    private String Flag; // entry method
    private String VinNumber;
    private String Latitude;
    private String Longitude;
    private String TimeStamp;

    public RescanComplete(String dealershipId, String siid, String flag, String vin, String lat, String lng, String ts)
    {
        DealershipId = dealershipId;
        StepInstanceGuid = siid;
        Flag = flag;
        VinNumber = vin;
        Latitude = lat;
        Longitude = lng;
        TimeStamp = ts;
    }

}
