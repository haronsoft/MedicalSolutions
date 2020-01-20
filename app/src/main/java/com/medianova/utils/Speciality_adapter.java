package com.medianova.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medianova.doctorfinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Redixbit 2 on 12-08-2016.
 */
public class Speciality_adapter extends BaseAdapter {

private final ArrayList<SpecialityGetSet> data1;
    private final Activity activity;
    private LayoutInflater inflater = null;
    private final Context context;

    public Speciality_adapter(Activity a, ArrayList<SpecialityGetSet> str,Context context1) {
        activity = a;
        data1 = str;
        context = context1;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data1.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        Typeface TF_ralewayRegular = Typeface.createFromAsset(activity.getAssets(),"fonts/Raleway-Regular_4.ttf");

        if (convertView == null) {
            vi = inflater.inflate(R.layout.cell_doctor, parent,false);
        }

        TextView txt_name = vi.findViewById(R.id.txt_type);
        txt_name.setText((data1.get(position).getName()));
        txt_name.setTypeface(TF_ralewayRegular);

        ImageView imageview = vi.findViewById(R.id.img_cell);
        String image = data1.get(position).getIcon().replace(" ", "%20");

        String imagename = activity.getString(R.string.link1)+"uploads/"+image;
        imagename = imagename.replace(" ","%20");
        Picasso.with(context).load(imagename).into(imageview);
        Log.e("image111", imagename );

        return vi;
    }
}
