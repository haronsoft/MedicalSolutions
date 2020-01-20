package com.medianova.doctorfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Introduction extends AppCompatActivity {

    private ViewPager mViewPager;
    private static final String MyPREFERENCES = "DoctorPrefrance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intoduction);

        getSupportActionBar().hide();
        mViewPager = findViewById(R.id.viewpager);
        lazyadapter la = new lazyadapter();
        mViewPager.setAdapter(la);
        boolean user = true;
        SharedPreferences.Editor sp = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        sp.putBoolean("userfirsttime", user);
        sp.apply();
    }

    class lazyadapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {

            TextView title1;
            TextView title2;
            TextView title3;

            ImageView sliderimage;
            RelativeLayout mainlayout;
            ImageView p1, p2, p3;
            LayoutInflater inflater = LayoutInflater.from(Introduction.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.sliderview, container, false);

            title1 = layout.findViewById(R.id.txt_tittle1);
            title2 = layout.findViewById(R.id.txt_tittle2);
            Button btn_skip = layout.findViewById(R.id.btn_skip);
            title3 = layout.findViewById(R.id.txt_tittle3);
            Button btn_continue = layout.findViewById(R.id.btn_continue);
            sliderimage = layout.findViewById(R.id.img);

            Typeface TF_ralewaybold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold_0.ttf");
            Typeface TF_ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular_4.ttf");
            Typeface TF_ralewaymidium = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium_2.ttf");

            title1.setTypeface(TF_ralewaybold);
            title2.setTypeface(TF_ralewayRegular);
            title3.setTypeface(TF_ralewayRegular);
            btn_continue.setTypeface(TF_ralewaymidium);
            btn_skip.setTypeface(TF_ralewayRegular);

            p1 = layout.findViewById(R.id.p1);
            p2 = layout.findViewById(R.id.p2);
            p3 = layout.findViewById(R.id.p3);
            mainlayout = layout.findViewById(R.id.main_rel);


            switch (position) {
                case 0:

                    title1.setText(R.string.slidetitle1);
                    title2.setText(R.string.slidetitle2);
                    title3.setText(R.string.slidetitle3);

                    sliderimage.setImageResource(R.drawable.sliderimg1);
                    mainlayout.setBackgroundColor(getResources().getColor(R.color.docotorcolor));
                    p1.setImageResource(R.drawable.pager1);
                    p2.setImageResource(R.drawable.pager12);
                    p3.setImageResource(R.drawable.pager12);
                    btn_skip.setVisibility(View.VISIBLE);
                    break;

                case 1:

                    title1.setText(R.string.pharmacytittle1);
                    title2.setText(R.string.pharmacytittle2);
                    title3.setText(R.string.pharmacytittle3);

                    sliderimage.setImageResource(R.drawable.pharmacyicon);
                    mainlayout.setBackgroundColor(getResources().getColor(R.color.pharmacycolor));
                    p1.setImageResource(R.drawable.pager12);
                    p2.setImageResource(R.drawable.pager1);
                    p3.setImageResource(R.drawable.pager12);
                    btn_skip.setVisibility(View.VISIBLE);
                    break;
                case 2:

                    title1.setText(R.string.hospitaltittle1);
                    title2.setText(R.string.hospitaltittle2);
                    title3.setText(R.string.hospitaltittle3);

                    sliderimage.setImageResource(R.drawable.hospitalicon);
                    mainlayout.setBackgroundColor(getResources().getColor(R.color.hospitalcolor));
                    p1.setImageResource(R.drawable.pager12);
                    p2.setImageResource(R.drawable.pager12);
                    p3.setImageResource(R.drawable.pager1);
                    btn_continue.setText(R.string.gotit_txt);
                    btn_skip.setVisibility(View.GONE);
                    break;
            }

            btn_skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Introduction.this, Home.class);
                    startActivity(i);
                }

            });
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 2) {
                        Intent i = new Intent(Introduction.this, Home.class);
                        startActivity(i);
                    } else
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            });


            container.addView(layout);
            return layout;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public float getPageWidth(int position) {
            return super.getPageWidth(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
