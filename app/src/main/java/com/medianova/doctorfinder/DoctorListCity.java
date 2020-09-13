package com.medianova.doctorfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
import java.util.List;
import java.util.Locale;

public class DoctorListCity extends Fragment {

    private ListView rel_near_listview;
    private static ArrayList<DoctorlistGetSet> nearbylist;
    private ProgressDialog progressDialog;
    private double latitudecur;
    private double longitudecur;
    private String specialities_id;
    private String CityId;
    private String CurrentCity;
    private String doctor_id;
    private boolean success = true;
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private static final String CityId1 = "CityID";
    private boolean isViewShown;



    public DoctorListCity init(String Special_id, String doctor) {
        specialities_id = Special_id;
        doctor_id = doctor;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list,
                container, false);

        rel_near_listview = rootView.findViewById(R.id.rel_near_listview);
        nearbylist = new ArrayList<>();

        checkinternet();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        CityId = sharedpreferences.getString(CityId1, null);
        if (CityId != null) {
            CityId = sharedpreferences.getString(CityId1, null);
            CurrentCity = sharedpreferences.getString("CityName", null);
        }
            new GetDataAsyncTask().execute();
               return rootView;
    }


    private void GetCurrentCity_UsingGps() {
        new GetCityName().execute();
    }

    public class GetCityName extends AsyncTask<Void, Void, Void> {
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
            Geocoder geoCoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            try {
                List<Address> address = geoCoder.getFromLocation(latitudecur, longitudecur, 1);
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i = 0; i < maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }
                String fnialAddress = builder.toString();
            } catch (IOException e) {
                // Handle IOException
            } catch (NullPointerException e) {
                // Handle NullPointerException
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
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
                        str_url = getString(R.string.link) + "getprofile.php?specialities_id=" + specialities_id + "&lat=" + latitudecur + "&lon=" + longitudecur + "&city=" + CityId + "&current_city=" + CurrentCity;
                        break;
                    case "2":
                        str_url = getString(R.string.link) + "hospitalandpharmacie.php?category_id=" + doctor_id + "&lat=" + latitudecur + "&lon=" + longitudecur + "&city=" + CityId + "&current_city=" + CurrentCity;
                        break;
                    case "3":
                        str_url = getString(R.string.link) + "hospitalandpharmacie.php?category_id=" + doctor_id + "&lat=" + latitudecur + "&lon=" + longitudecur + "&city=" + CityId + "&current_city=" + CurrentCity;
                        break;
                        //https://doc.softmekdev.xyz/Apicontrollers/hospitalandpharmacie.php?category_id=2&lat=-1.4771028&lon=36.9522598&city=1&current_city=kisii
                }
                str_url = str_url.replace(" ","%20");
                Log.d("City_WS",str_url);
                hp=new URL(str_url);
                // URL Connection
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
                    icon = icon.replace(" ","%20");
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
                // TODO Auto-generated catch block
                e.printStackTrace();
                error = e.getMessage();
                Log.e("Error",e.getMessage());
                success = false;
            } catch (NullPointerException e) {
                // TODO: handle exception
                error = e.getMessage();
                success = false;
                Log.e("Error",e.getMessage());


            } catch (MalformedURLException e) {
                e.printStackTrace();
                success = false;
                Log.e("Error",e.getMessage());


            } catch (IOException e) {
                e.printStackTrace();
                success = false;
                Log.e("Error",e.getMessage());


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
            Log.e("Size ",nearbylist.size()+"");
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
        }}
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !success) {
            DoctorList.errorDialog(getActivity());
        }
    }
}
