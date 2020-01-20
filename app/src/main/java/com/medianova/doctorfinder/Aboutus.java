package com.medianova.doctorfinder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import static com.medianova.doctorfinder.Home.TF_ralewayRegular;
import static com.medianova.doctorfinder.Home.TF_ralewaybold;

public class Aboutus extends Activity {

    public static void setTypefaces(ArrayList<TextView> t, ArrayList<TextView> m) {
        if(t!=null)
            for (TextView textView : t) {
                textView.setTypeface(TF_ralewayRegular);
            }
        if (m!=null)
            for (TextView textView : m) {
                textView.setTypeface(TF_ralewaybold);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        intitializtion();


    }

    private void intitializtion() {
        TextView header = findViewById(R.id.txtheading);
        TextView title = findViewById(R.id.h1);
        TextView title_detail = findViewById(R.id.h2);
        TextView version = findViewById(R.id.h3);
        TextView version_num = findViewById(R.id.txt_version);
        TextView h5 = findViewById(R.id.h5);
        TextView txt_company_name = findViewById(R.id.txt_company_name);
        TextView txt_email = findViewById(R.id.txt_email);
        TextView txt_website = findViewById(R.id.txt_website);
        TextView txt_aboutus = findViewById(R.id.txt_aboutus);
        TextView txt_about = findViewById(R.id.txt_about);

        ArrayList<TextView> total_textviews = new ArrayList<>();
        ArrayList<TextView> bold_textview = new ArrayList<>();

        total_textviews.add(header);
        bold_textview.add(title);
        total_textviews.add(title_detail);
        total_textviews.add(version_num);
        total_textviews.add(h5);
        bold_textview.add(txt_company_name);
        bold_textview.add(txt_website);
        total_textviews.add(txt_aboutus);
        total_textviews.add(txt_about);
        bold_textview.add(version);
        bold_textview.add(txt_email);


        setTypefaces(total_textviews, bold_textview);


//        header.setTypeface(TF_ralewayRegular);

    }

}
