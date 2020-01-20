package com.medianova.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medianova.doctorfinder.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Redixbit 2 on 13-08-2016.
 */
public class Citylist_adapter extends BaseAdapter {
    private final ArrayList<CitylistGetSet> data1;
    private final Activity activity;
    private LayoutInflater inflater = null;
    private final SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "DoctorPrefrance" ;

    public Citylist_adapter(Activity a, ArrayList<CitylistGetSet> nearbylist,Context context1) {
        activity = a;
        data1 = nearbylist;
        sharedpreferences = context1.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return data1.size();
    }

    @Override
    public Object getItem(int position) {
        return data1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(data1.get(position).getId());
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        Typeface TF_ralewayRegular = Typeface.createFromAsset(activity.getAssets(),"fonts/Raleway-Regular_4.ttf");

        ImageView selected_img;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.cell_citylist, parent,false);
            TextView txt_name = vi.findViewById(R.id.txt_name);
            selected_img = vi.findViewById(R.id.img_selected);
        txt_name.setText((data1.get(position).getName()));
        txt_name.setTypeface(TF_ralewayRegular);
        }
        else {
            TextView txt_name = vi.findViewById(R.id.txt_name);
            selected_img = vi.findViewById(R.id.img_selected);
            txt_name.setText((data1.get(position).getName()));
            txt_name.setTypeface(TF_ralewayRegular);
        }
        String cityName = sharedpreferences.getString("CityName", null);
        if (cityName != null && !cityName.isEmpty() && !cityName.equals("null") && cityName.equals(data1.get(position).getName())) {
           selected_img.setVisibility(View.VISIBLE);
        }else{

            selected_img.setVisibility(View.GONE);
        }
        return vi;
    }
}
