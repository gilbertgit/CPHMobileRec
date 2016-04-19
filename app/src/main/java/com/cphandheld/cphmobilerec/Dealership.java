package com.cphandheld.cphmobilerec;

import java.io.Serializable;

/**
 * Created by titan on 4/15/16.
 */
public class Dealership implements Serializable
{
    int Id;
    String Name;
    String DealerCode;
    String Lot1Name;
    String Lot2Name;
    String Lot3Name;
    String Lot4Name;
    String Lot5Name;
    String Lot6Name;
    String Lot7Name;
    String Lot8Name;
    String Lot9Name;

    Dealership() {
    }

    @Override
    public String toString() {
        return this.Name;
    }
}
