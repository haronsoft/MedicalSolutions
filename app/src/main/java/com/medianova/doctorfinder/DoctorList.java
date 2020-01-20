package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import static com.medianova.doctorfinder.Home.TF_opensansRegular;
import static com.medianova.doctorfinder.Home.TF_ralewayRegular;
import static com.medianova.utils.AdMobIntegration.loadAdmobBanner;
import static com.medianova.utils.AdMobIntegration.shouldDisplayAds;

public class DoctorList extends AppCompatActivity {

    private String specialities_id;
    private String doctor_id;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_doctorlist);

        Intent iv = getIntent();
        specialities_id = iv.getStringExtra("specialities_id");
        doctor_id = iv.getStringExtra("doctor_id");

        TextView headertext = findViewById(R.id.headertext);
        headertext.setTypeface(TF_ralewayRegular);
        switch (doctor_id) {
            case "1":
                headertext.setText(getString(R.string.doctor));
                break;
            case "2":
                headertext.setText(getString(R.string.pharmany));
                break;
            case "3":
                headertext.setText(getString(R.string.hospital));
                break;
        }
        final ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        Button btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent iv = new Intent(DoctorList.this, Setting.class);
                iv.putExtra("fromWhere", "DoctorList");
                iv.putExtra("specialities_id", specialities_id);
                iv.putExtra("doctor_id", doctor_id);
                startActivity(iv);
            }
        });
    }

    private void setupViewPager(final ViewPager viewPager) {
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        final String[] names = {getString(R.string.pager1), getString(R.string.city), getString(R.string.pager3)};

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @Override
        public Fragment getItem(int position) {
            Log.e("level", "" + position);
            switch (position) {
                case 0:
                    return new DoctorListNearby().init(specialities_id, doctor_id);
                case 1:
                    return new DoctorListCity().init(specialities_id, doctor_id);
                case 2:
                    return new DoctorListOrderby().init(specialities_id, doctor_id);
            }
            return null;
        }

        @Override
        public int getCount() {
            return names.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adView=findViewById(R.id.adView);
        if(shouldDisplayAds(DoctorList.this))
        loadAdmobBanner(adView,DoctorList.this);
    }

    public static void errorDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txt_dialog_title = dialog.findViewById(R.id.txt_dialog_title);
        TextView txt_error_description = dialog.findViewById(R.id.txt_error_description);
        txt_dialog_title.setTypeface(TF_opensansRegular);
        txt_error_description.setTypeface(TF_ralewayRegular);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setTypeface(TF_ralewayRegular);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
