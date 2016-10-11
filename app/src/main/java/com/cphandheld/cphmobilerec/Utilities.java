package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

/**
 * Created by titan on 4/7/16.
 */
public class Utilities {

    private static final String TAG = "Utilities";
    public static final String PREFS_FILE = "SharedPrefs";
    public static String AppURL = "";
    public static final String OrganizationsURL = "Organizations";
    public static final String InventoryUploadURL = "inventory/upload";
    public static final String RescanUploadURL = "rescan/upload";
    public static final String RescanDownloadURL = "rescan/getrescan/";
    public static final String LoginURL = "dealerships/getbypin/";


    public static List<Dealership> dealerships;
    public static User currentUser = new User();
    public static HashMap<String, String> keyCodeMap;
    public static String androidId = "";
    public static String currentDealership = "";

    public static String StreamToString(InputStreamReader isr) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( isr);
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        bufferedReader.close();
        return result;

    }

    public static String CheckVinSpecialCases(String vin) {
        String formattedVIN = vin.trim();

        if (vin.length() > 17) {
            if (vin.substring(0, 1).toUpperCase().equals("I") || vin.substring(0, 1).toUpperCase().equals("A") || vin.substring(0, 1).equals(" ")) // Ford, Mazda, Honda Issues
                formattedVIN = vin.substring(1, 18);
            else if (vin.length() == 18)
                formattedVIN = vin.substring(0, 17); // Lexus Issue

            formattedVIN = formattedVIN.substring(0,20);
        }

        return formattedVIN;
    }

    // This is temporary function for NADA
    public static void SetAppUrl(String url)
    {
        AppURL = url;
    }

    public static void PlayClick(Context context)
    {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float vol = 0.5f; //This will be half of the default system sound
        am.playSoundEffect(AudioManager.FX_KEY_CLICK, vol);
    }

    public static void PlayBadClick(Context context)
    {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float vol = 0.5f; //This will be half of the default system sound
        am.playSoundEffect(AudioManager.FX_KEYPRESS_INVALID, vol);
    }


    public static void initKeyMap()
    {
        keyCodeMap = new HashMap<String, String>();
        keyCodeMap.put("1", "1");
        keyCodeMap.put("2", "2");
        keyCodeMap.put("3", "3");
        keyCodeMap.put("4", "4");
        keyCodeMap.put("5", "5");
        keyCodeMap.put("6", "6");
        keyCodeMap.put("7", "7");
        keyCodeMap.put("8", "8");
        keyCodeMap.put("9", "9");
        keyCodeMap.put("0", "0");
        keyCodeMap.put("11", "Q");
        keyCodeMap.put("12", "W");
        keyCodeMap.put("13", "E");
        keyCodeMap.put("14", "R");
        keyCodeMap.put("15", "T");
        keyCodeMap.put("16", "Y");
        keyCodeMap.put("17", "U");
        keyCodeMap.put("18", "I");
        keyCodeMap.put("19", "O");
        keyCodeMap.put("10", "P");
        keyCodeMap.put("21", "A");
        keyCodeMap.put("22", "S");
        keyCodeMap.put("23", "D");
        keyCodeMap.put("24", "F");
        keyCodeMap.put("25", "G");
        keyCodeMap.put("26", "H");
        keyCodeMap.put("27", "J");
        keyCodeMap.put("28", "K");
        keyCodeMap.put("29", "L");
        keyCodeMap.put("31", "Z");
        keyCodeMap.put("32", "X");
        keyCodeMap.put("33", "C");
        keyCodeMap.put("34", "V");
        keyCodeMap.put("35", "B");
        keyCodeMap.put("36", "N");
        keyCodeMap.put("37", "M");
    }

    private static Date shiftTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);
        System.out.println("........"+targetCalendar.getTimeZone());
        return targetCalendar.getTime();
    }


    public static String GetDateTimeString()
    {
        SimpleDateFormat sf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");


        Date date= new Date();
        //SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sf.format(date);
//        TimeZone tz =  sf.getTimeZone();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        TimeZone tz1 = TimeZone.getTimeZone("CST");
        Date c= shiftTimeZone( date,tz,tz1);
        return sf.format(c);
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean hasInternetAccess(Context context) {

        if(isNetworkAvailable(context)) {
            try {
                InetAddress ipAddr = InetAddress.getByName("m.google.com");

                if (ipAddr.equals(""))
                    return false;
                else
                    return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    public static boolean isValidVinLength(String vin)
    {
        boolean result = false;
        if(vin.length() == 6 || vin.length() == 8 || vin.length() == 17)
            result = true;

        return result;
    }

    public static boolean hasValidVinCharacters(String vin) {
        boolean result = false;
        if(!vin.toUpperCase().contains("I,O,Q"))
            result = true;

        return result;
    }

    public static boolean isValidVin(String vin) {
        return isValidVinLength(vin) && hasValidVinCharacters(vin);
    }
}
