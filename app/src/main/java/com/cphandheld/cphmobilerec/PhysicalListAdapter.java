package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by titan on 4/8/16.
 */
public class PhysicalListAdapter  extends ArrayAdapter<Physical> {

    ArrayList<Physical> mPhysicals = new ArrayList<Physical>();
    private LayoutInflater mInflater;
    Context mContext;

    View.OnTouchListener mTouchListener;


    public PhysicalListAdapter(Context context, int resource, ArrayList<Physical> phys, View.OnTouchListener listener) {
        super(context, resource, phys);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mPhysicals = phys;
        mTouchListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Inflate view for each element in list.
        convertView = mInflater.inflate(R.layout.physical_list_item, null);
        convertView.setOnTouchListener(mTouchListener);
        convertView.setClickable(true);

        Physical phy = mPhysicals.get(position);

        ((TextView) convertView.findViewById(R.id.textVin)).setText((phy.getVIN()));
        ((TextView) convertView.findViewById(R.id.textLot)).setText((phy.getLot()));
        ((TextView) convertView.findViewById(R.id.textDate)).setText((phy.getDate()));
        ((TextView) convertView.findViewById(R.id.textTime)).setText((phy.getTime()));
        ((TextView) convertView.findViewById(R.id.textEntryType)).setText((phy.getEntryType()));
        String newUsed = phy.getNewUsed();

        switch (newUsed) {
            case "0":
                newUsed = "New";
                break;
            case "1":
                newUsed = "Used";
                break;
            case "2":
                newUsed = "Loaner";
                break;
        }
        ((TextView) convertView.findViewById(R.id.textNewUsed)).setText((newUsed));

        if(!Utilities.isValidVin(phy.getVIN()))
            ((ImageView)convertView.findViewById(R.id.imageLengthWarning)).setVisibility(View.VISIBLE);
        else
            ((ImageView)convertView.findViewById(R.id.imageLengthWarning)).setVisibility(View.GONE);

        //return convertView;
        return convertView;
    }
}
