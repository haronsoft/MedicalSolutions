package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.medianova.utils.ConnectionDetector;
import com.medianova.utils.GPSTracker;
import com.medianova.utils.SpecialityGetSet;
import com.medianova.utils.Speciality_adapter;

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

import static com.medianova.doctorfinder.Home.TF_ralewayRegular;

public class DoctorSpeciality extends Activity {

    private static ArrayList<SpecialityGetSet> categorylist;
    private GridView grid_detail;
    private String doctor_id;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorspeciality);

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setTypeface(TF_ralewayRegular);
        Intent iv = getIntent();
        doctor_id = iv.getStringExtra("doctor_id");
        grid_detail = findViewById(R.id.gridview);
        categorylist = new ArrayList<>();
        checkinternet();
        new GetDataAsyncTask().execute();
    }

    private void checkinternet() {
        // TODO Auto-generated method stub
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            GPSTracker gps = new GPSTracker(DoctorSpeciality.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                try {
                    double latitudecur = gps.getLatitude();
                    double longitudecur = gps.getLongitude();
                } catch (NullPointerException e) {
                    // TODO: handle exception
                } catch (NumberFormatException e) {
                    // TODO: handle exception
                }

            } else {
                gps.showSettingsAlert();
            }
        }
    }

    public class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DoctorSpeciality.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            String error;
            try {
                categorylist.clear();
                hp = new URL(getString(R.string.link) + "specialities.php?category_id=" + doctor_id);
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x = "";
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
                SpecialityGetSet temp1 = new SpecialityGetSet();
                temp1.setStatus(Obj.getString("status"));
                JSONArray jarr = Obj.getJSONArray("Specialities");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject Obj1;
                    Obj1 = jarr.getJSONObject(i);
                    SpecialityGetSet temp = new SpecialityGetSet();
                    temp.setId(Obj1.getString("id"));
                    temp.setSp_id(Obj1.getString("sp_id"));
                    temp.setName(Obj1.getString("name"));
                    temp.setIcon(Obj1.getString("icon"));
                    temp.setCreated_at(Obj1.getString("created_at"));
                    categorylist.add(temp);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                error = e.getMessage();
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
            Speciality_adapter adapter = new Speciality_adapter(DoctorSpeciality.this, categorylist, DoctorSpeciality.this);
            grid_detail.setAdapter(adapter);
            grid_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iv = new Intent(DoctorSpeciality.this, DoctorList.class);
                    iv.putExtra("specialities_id", "" + categorylist.get(position).getId());
                    iv.putExtra("doctor_id", "" + doctor_id);
                    startActivity(iv);
                }
            });
        }
    }
}
