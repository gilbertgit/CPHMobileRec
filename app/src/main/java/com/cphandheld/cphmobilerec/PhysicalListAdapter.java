package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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


    public PhysicalListAdapter(Context context, int resource, ArrayList<Physical> bins, View.OnTouchListener listener) {
        super(context, resource, bins);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mPhysicals = bins;
        mTouchListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //View view = super.getView(position, convertView, parent);

        //Inflate view for each element in list.
        convertView = mInflater.inflate(R.layout.physical_list_item, null);
       // if (view != convertView) {
            // Add touch listener to every new view to track swipe motion
        convertView.setOnTouchListener(mTouchListener);
        //convertView.setOnClickListener(mClickListener);
        convertView.setClickable(true);
        //}
        //convertView.setOnTouchListener(mTouchListener);
        //Get details for bin
        Physical phy = mPhysicals.get(position);

        ((TextView) convertView.findViewById(R.id.textVin)).setText((phy.getVIN()));
        ((TextView) convertView.findViewById(R.id.textLot)).setText((phy.getLot()));
        ((TextView) convertView.findViewById(R.id.textDate)).setText((phy.getDate()));
        ((TextView) convertView.findViewById(R.id.textTime)).setText((phy.getTime()));
        ((TextView) convertView.findViewById(R.id.textEntryType)).setText((phy.getEntryType()));
        ((TextView) convertView.findViewById(R.id.textNewUsed)).setText((phy.getNewUsed()));
        //return convertView;
        return convertView;
    }
}
