package com.medianova.doctorfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.medianova.utils.ConnectionDetector;
import com.medianova.utils.DoctorlistGetSet;
import com.medianova.utils.Doctorlist_adapter;
import com.medianova.utils.GPSTracker;

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

public class DoctorListNearby extends Fragment {

    private ListView rel_near_listview;
    private static ArrayList<DoctorlistGetSet> nearbylist;
    private double latitudecur;
    private double longitudecur;
    private String specialities_id;
    private String doctor_id;
    private String radius;
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private boolean success = true;
    private boolean isViewShown;

    public DoctorListNearby init(String Special_id, String doctor) {
        specialities_id = Special_id;
        doctor_id = doctor;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkinternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list,
                container, false);

        rel_near_listview = rootView.findViewById(R.id.rel_near_listview);

        nearbylist = new ArrayList<>();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        radius = sharedpreferences.getString("Radius", null);
        if (radius == null) {
            radius = "100000";
        }
            new GetDataAsyncTask().execute();

        return rootView;
    }

    private void checkinternet() {
        // TODO Auto-generated method stub

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            GPSTracker gps = new GPSTracker(getActivity());
            // check if GPS enabled
            if (gps.canGetLocation()) {
                try {

                    latitudecur = gps.getLatitude();
                    longitudecur = gps.getLongitude();

                } catch (NullPointerException e) {
                    // TODO: handle exception
                } catch (NumberFormatException e) {
                    // TODO: handle exception
                }

            } else {

                gps.showSettingsAlert();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.requestinternet, Toast.LENGTH_SHORT).show();
        }
    }


    public class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp = null;
            String str_url="";
            String error;
            try {
                nearbylist.clear();
                switch (doctor_id) {
                    case "1":
                        str_url = getString(R.string.link) + "getprofile.php?specialities_id=" + specialities_id + "&" + "lat=" + latitudecur + "&" + "lon=" + longitudecur + "&" + "radius=" + radius;
                        break;
                    case "2":
                        str_url = getString(R.string.link) + "hospitalandpharmacie.php?category_id=" + doctor_id + "&lat=" + latitudecur + "&lon=" + longitudecur + "&radius=" + radius;
                        break;
                    case "3":
                        str_url = getString(R.string.link) + "hospitalandpharmacie.php?category_id=" + doctor_id + "&lat=" + latitudecur + "&lon=" + longitudecur + "&radius=" + radius;
                        break;
                }
                str_url = str_url.replace(" ","%20");
                Log.d("Nearby_WS",str_url.toString());

                hp=new URL(str_url);

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
                // Json Parsing
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
                DoctorlistGetSet temp1 = new DoctorlistGetSet();
                temp1.setStatus(Obj.getString("status"));


                if(Obj.getString("status").equals("Success")){
                    JSONArray jarr = Obj.getJSONArray("List_profile");
                    success = true;
                    for (int i = 0; i < jarr.length(); i++) {
                    JSONObject Obj1;
                    Obj1 = jarr.getJSONObject(i);
                    DoctorlistGetSet temp = new DoctorlistGetSet();
                        temp.setId(Obj1.getString("id"));
                        temp.setDistance(Obj1.getString("distance"));
                        String icon = Obj1.getString("icon");
                        icon = icon.replace(" ", "%20");
                        temp.setIcon(icon);
                        temp.setName(Obj1.getString("name"));
                        temp.setServices(Obj1.getString("services"));
                        temp.setRatting(Obj1.getString("ratting"));
                        nearbylist.add(temp);
                }}
                  else if(Obj.getString("status").equals("Failed")){
                    String ErrorMessage = Obj.getString("Error");
                    success = false;
                    Log.e("Error",ErrorMessage);


                }
            } catch (JSONException e) {
                success = false;

                // TODO Auto-generated catch block
                e.printStackTrace();
                error = e.getMessage();
            } catch (NullPointerException e) {
                success = false;

                // TODO: handle exception
                error = e.getMessage();
            } catch (MalformedURLException e) {
                success = false;

                e.printStackTrace();
            } catch (IOException e) {
                success = false;

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (!success) {
                rel_near_listview.setVisibility(View.GONE);
                if(getUserVisibleHint())
                { DoctorList.errorDialog(getActivity());}
           }
            else {

                Doctorlist_adapter adapter = new Doctorlist_adapter(getActivity(), nearbylist, getActivity().getApplicationContext(), doctor_id);
                rel_near_listview.setAdapter(adapter);
                rel_near_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent iv = new Intent(getActivity(), DoctorDetail.class);
                        iv.putExtra("profile_id", "" + nearbylist.get(position).getId());
                        iv.putExtra("doctor_id", "" + doctor_id);
                        startActivity(iv);
                    }
                });
            }
        }
    }
}
