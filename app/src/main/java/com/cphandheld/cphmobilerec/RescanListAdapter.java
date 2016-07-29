package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by titan on 5/29/16.
 */
public class RescanListAdapter extends ArrayAdapter<Rescan> {

    ArrayList<Rescan> mRescans = new ArrayList<Rescan>();
    private LayoutInflater mInflater;
    Context mContext;

    View.OnTouchListener mTouchListener;


    public RescanListAdapter(Context context, int resource, ArrayList<Rescan> rescans, View.OnTouchListener listener) {
        super(context, resource, rescans);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mRescans = rescans;
        mTouchListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.rescan_list_item, null);
        convertView.setOnTouchListener(mTouchListener);
        convertView.setClickable(true);

        Rescan rescan = mRescans.get(position);
        String year = rescan.getYear() == null ? "" : rescan.getYear();
        String make = rescan.getMake() == null ? "" : rescan.getMake();
        String model = rescan.getModel() == null ? "" : rescan.getModel();
        String color = rescan.getColor() == null ? "" : rescan.getColor();

        ((TextView) convertView.findViewById(R.id.textVin)).setText((rescan.getVIN()));
        String ymmc = year + " " + make + " " + model + " " + color;
        ((TextView) convertView.findViewById(R.id.textYearMakeModel)).setText((ymmc));
        ((TextView) convertView.findViewById(R.id.textEntryType)).setText((rescan.getEntryType()));
        ((TextView) convertView.findViewById(R.id.textDateTime)).setText((rescan.getScanneDate()));
        ((TextView) convertView.findViewById(R.id.textDealerCode)).setText((rescan.getDealership()));
        //((TextView) convertView.findViewById(R.id.textScannedBy)).setText((rescan.getScannedBy()));

        return convertView;
    }
}
