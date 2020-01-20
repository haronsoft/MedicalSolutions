package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.medianova.utils.CitylistGetSet;
import com.medianova.utils.Citylist_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Setting extends Activity {

    public static final String CityId1 = "CityID";
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private SeekBar Sk_radius;
    private TextView txt_radius;
    private TextView txt_cityname;
    private TextView txt_orderby;
    private Switch Sw_OrderBy,Sw_radius_onoff;
    private ListView listCity;
    private ProgressDialog progressDialog;
    private ArrayList<CitylistGetSet> Arr_cityname;
    private SharedPreferences sharedpreferences;
    private RelativeLayout rl_distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setLayout();
        CheckSharedPrefrances();

    }


    private void CheckSharedPrefrances() {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        // CITYNAME

        String cityName = sharedpreferences.getString("CityName", null);

        if (cityName != null && !cityName.isEmpty() && !cityName.equals("null")) {

            txt_cityname.setText(cityName);
        } else {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("CityName", "");
            editor.apply();
        }


        // RADIUS


        String radius = sharedpreferences.getString("Radius", null);

        if (radius != null && !radius.isEmpty() && !radius.equals("null") && !radius.equals("100000")) {
            Sk_radius.setProgress(Integer.parseInt(radius));
            Sw_radius_onoff.setChecked(true);
        } else {
           disableRadiusLayout();
        }




        // ORDER BY


        String orderBy = sharedpreferences.getString("OrderBy", null);

        if (orderBy != null && !orderBy.isEmpty() && !orderBy.equals("null")) {

            if (orderBy.equals("a-z")) {

                txt_orderby.setText(getString(R.string.orderby_accending));
                Sw_OrderBy.setChecked(true);

            } else if (orderBy.equals("z-a")) {

                txt_orderby.setText(getString(R.string.orderby_decending));
                Sw_OrderBy.setChecked(false);
            }
        } else {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("OrderBy", "a-z");
            editor.apply();

        }

        // Get City name List

        new GetDataAsyncTask().execute();
    }



    private void setLayout() {

        Typeface TF_opensansBold = Typeface.createFromAsset(Setting.this.getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface TF_ralewayRegular = Typeface.createFromAsset(Setting.this.getAssets(), "fonts/Raleway-Regular_4.ttf");

        // Textview

        TextView txt_settingtitle = findViewById(R.id.txt_settingtitle);
        txt_radius = findViewById(R.id.txt_radius);
        txt_cityname = findViewById(R.id.txt_city);
        txt_orderby = findViewById(R.id.txt_orderby);
        TextView txt_radius_onoff = findViewById(R.id.txt_distanceontitle);

        TextView txt_distancetitle = findViewById(R.id.txt_distancetitle);
        TextView txt_radiustitle = findViewById(R.id.txt_radiustitle);
        TextView txt_orderbytitle = findViewById(R.id.txt_orderbytitle);
        TextView txt_currentlocationtitle = findViewById(R.id.txt_currentlocationtitle);

        txt_distancetitle.setTypeface(TF_opensansBold);
        txt_orderbytitle.setTypeface(TF_opensansBold);
        txt_currentlocationtitle.setTypeface(TF_opensansBold);
        txt_radius_onoff.setTypeface(TF_opensansBold);

        txt_radiustitle.setTypeface(TF_ralewayRegular);
        txt_orderby.setTypeface(TF_ralewayRegular);
        txt_settingtitle.setTypeface(TF_ralewayRegular);

        txt_radius.setTypeface(TF_opensansBold);
        txt_cityname.setTypeface(TF_opensansBold);

        // Seek Bar

        Sk_radius = findViewById(R.id.seekBar);
        Sk_radius.setMax(50);
        Sk_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progress1 = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                txt_radius.setText(progress + " " + getString(R.string.Km));
                progress1 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("Radius", String.valueOf(progress1));
                editor.apply();
            }
        });

        rl_distance = findViewById(R.id.rl_distance);
        // Switch Value
        Sw_radius_onoff = findViewById(R.id.Sw_radius_onoff);

        Sw_radius_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    Sk_radius.setEnabled(true);
                    AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F);
                    alpha.setDuration(0); // Make animation instant
                    alpha.setFillAfter(true); // Tell it to persist after the animation ends
                    rl_distance.startAnimation(alpha);

                } else {
                    disableRadiusLayout();
                   }

            }
        });

        Sw_OrderBy = findViewById(R.id.Sw_OrderBy);

        Sw_OrderBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {

                    txt_orderby.setText(getString(R.string.orderby_accending));
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("OrderBy", "a-z");
                    editor.apply();

                } else {

                    txt_orderby.setText(getString(R.string.orderby_decending));
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("OrderBy", "z-a");
                    editor.apply();
                }
            }
        });


        // City Values

        Arr_cityname = new ArrayList<>();

        listCity = findViewById(R.id.listCity);
    }
    private void disableRadiusLayout()
    {
        AlphaAnimation alpha = new AlphaAnimation(0.3F, 0.3F);
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends
        rl_distance.startAnimation(alpha);

        String radius1;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        radius1="100000";
        Sk_radius.setProgress(0);
        Sk_radius.setEnabled(false);
        editor.putString("Radius", radius1);
        txt_radius.setText(50 +" "+ getString(R.string.Km));
        editor.apply();

    }

    public class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Setting.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {
                Arr_cityname.clear();

                hp = new URL(getString(R.string.link) + "city.php");
                Log.d("URL", "" + hp);

                // URL Connection

                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                Log.d("input", "" + input);
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.d("URL", "" + total);

                // Json Parsing

                JSONArray jObject = new JSONArray(total.toString());
                Log.d("URL12", "" + jObject);
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
                Log.e("Obj", Obj.toString());

                JSONArray jarr = Obj.getJSONArray("Cities");

                for (int i = 0; i < jarr.length(); i++) {

                    CitylistGetSet temp1 = new CitylistGetSet();

                    JSONObject Obj1;
                    Obj1 = jarr.getJSONObject(i);
                    Log.e("Obj1", Obj1.toString());
                    temp1.setId(Obj1.getString("id"));
                    temp1.setName(Obj1.getString("name"));
                    Arr_cityname.add(temp1);
                    Log.e("nearbylist", Arr_cityname.get(0).getId());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } catch (NullPointerException e) {
                // TODO: handle exception
                e.printStackTrace();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            final Citylist_adapter adapter = new Citylist_adapter(Setting.this, Arr_cityname, Setting.this);
            listCity.setAdapter(adapter);
            listCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("CityName", Arr_cityname.get(position).getName());
                    editor.putString("CityID", Arr_cityname.get(position).getId());
                    editor.apply();
                    txt_cityname.setText(Arr_cityname.get(position).getName());
                    adapter.notifyDataSetChanged();

                }
            });
        }
    }

    @Override
    public void onBackPressed() {

        Intent i = getIntent();
        String from_where=i.getStringExtra("fromWhere");
        String Doctor_id =  i.getStringExtra("doctor_id");
        String SpecialistId = i.getStringExtra("specialities_id");
        if (from_where != null) {
            if (from_where.compareTo("DoctorList") == 0) {
                Intent q = new Intent(Setting.this, DoctorList.class);
                q.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                q.putExtra("specialities_id", SpecialistId);
                q.putExtra("doctor_id", Doctor_id);
                startActivity(q);
                finish();

            }
        } else super.onBackPressed();



    }
}
