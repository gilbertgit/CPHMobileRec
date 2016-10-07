package com.cphandheld.cphmobilerec;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by titan on 10/6/16.
 */
public class DealershipListViewAdapter extends ArrayAdapter<Dealership> {

    ArrayList<Dealership> mDealerships = new ArrayList<Dealership>();
    private LayoutInflater mInflater;
    Context mContext;
    View.OnClickListener mClickListener;


    public DealershipListViewAdapter(Context context, int resource, ArrayList<Dealership> dealers, View.OnClickListener listener) {
        super(context, resource, dealers);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mDealerships = dealers;
        mClickListener = listener;
    }

//    public DealershipListViewAdapter(Context context, List<ModuleItem> list, Module mod) {
//        super();
//        this.list = list;
//        this.context = context;
//        this.mod = mod;
//        // TODO Auto-generated constructor stub
//    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDealerships.size();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Inflate view for each element in list.
        convertView = mInflater.inflate(R.layout.dealership_list_item, null);
        convertView.setOnClickListener(mClickListener);
        convertView.setClickable(true);

        Dealership dealer = mDealerships.get(position);

        ((TextView) convertView.findViewById(R.id.textDealerName)).setText((dealer.getDealerName()));
        ((TextView) convertView.findViewById(R.id.textDealerCode)).setText((dealer.getDealerCode()));

        return convertView;

    }
}
