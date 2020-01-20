package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.medianova.utils.ConnectionDetector;
import com.medianova.utils.CustomMarker;
import com.medianova.utils.DBAdapter;
import com.medianova.utils.GPSTracker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.medianova.doctorfinder.Home.TF_opensansRegular;
import static com.medianova.doctorfinder.Home.TF_ralewayRegular;
import static com.medianova.doctorfinder.Home.TF_ralewaybold;

public class DoctorDetail extends FragmentActivity implements OnMapReadyCallback {

    private static final String MyPREFERENCES = "DoctorPrefrance";
    private HashMap<CustomMarker, Marker> markersHashMap;
    private String name;
    private String service;
    private String distance;
    private String doctorid;
    private String uniqueid;
    private GoogleMap googleMap;
    private TextView txt_name;
    private TextView txt_desc;
    private TextView txt_distance;
    private TextView txt_ratenumber;
    private TextView txt_servicedesc;
    private TextView txt_timingdesc;
    private TextView txt_healthcare;
    private TextView txt_aboutus;
    private ArrayList<Getsetfav> FileList;
    private Button btn_favorite;
    private Button btn_favorite1;
    private ImageView img_profile;
    private SQLiteDatabase db;
    private Cursor cur = null;

    private ProgressDialog progressDialog;
    private double latitudecur;
    private double longitudecur;
    private String specialities_id;
    private String email = "";
    private String uservalue = "";
    private String doctor_id;
    private String urlToSharetwitter;
    private String originalMessageEscaped;
    private String urlToSharefb;
    private JSONObject Obj_detail;
    private String inquiry;
    private Button btn_recipe;
    private Button btn_bookappointment ;


    private static double roundMyData(double Rval, int numberOfDigitsAfterDecimal) {
        double p = (float) Math.pow(10, numberOfDigitsAfterDecimal);
        Rval = Rval * p;
        double tmp = Math.floor(Rval);
        System.out.println("~~~~~~tmp~~~~~" + tmp);
        return tmp / p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctordetail);
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        // check user is created or not
        // if user is already logged in
        if (prefs.getString("userid", null) != null) {
            uservalue = prefs.getString("userid", null);
        } else {
            uservalue = "delete";
        }
        FileList = new ArrayList<>();
        Intent iv = getIntent();
        specialities_id = iv.getStringExtra("profile_id");
        doctor_id = iv.getStringExtra("doctor_id");
        btn_recipe = findViewById(R.id.btn_recipe);
        ImageView img_corner = findViewById(R.id.corner);
        TextView txt_letter = findViewById(R.id.letter);
        btn_bookappointment = findViewById(R.id.btn_bookappointment);
        switch (doctor_id) {
            case "1":
                img_corner.setBackgroundResource(R.drawable.d_bg);
                txt_letter.setText(getString(R.string.doctor_symbol));
                inquiry = getString(R.string.doctor_mail_subject);
                btn_recipe.setVisibility(View.GONE);
                btn_bookappointment.setVisibility(View.VISIBLE);

                break;
            case "2":
                img_corner.setBackgroundResource(R.drawable.p_bg);
                txt_letter.setText(getString(R.string.pharmacy_symbol));
                inquiry = getString(R.string.pharmacy_mail_subject);
                btn_recipe.setVisibility(View.VISIBLE);
                btn_bookappointment.setVisibility(View.GONE);

                break;
            case "3":
                img_corner.setBackgroundResource(R.drawable.h_bg);
                txt_letter.setText(getString(R.string.hospital_symbol));
                inquiry = getString(R.string.hospital_mail_subject);
                btn_recipe.setVisibility(View.GONE);
                btn_bookappointment.setVisibility(View.GONE);

                break;
        }
        checkinternet();

        new GetDataAsyncTask().execute();
        setLayout();
    }

    private void setLayout() {

        RelativeLayout rl_profile = findViewById(R.id.rl_profile);
        img_profile = findViewById(R.id.img_profile);
        TextView txt_settingtitle = findViewById(R.id.txt_settingtitle);
        TextView txt_distancetitle = findViewById(R.id.txt_distancetitle);
        TextView txt_ratingtitle = findViewById(R.id.txt_ratingtitle);
        txt_name = findViewById(R.id.txt_name);
        txt_desc = findViewById(R.id.txt_desc);
        txt_distance = findViewById(R.id.txt_distance);
        txt_ratenumber = findViewById(R.id.txt_ratenumber);
        TextView txt_servicetitle = findViewById(R.id.txt_servicetitle);
        txt_servicedesc = findViewById(R.id.txt_servicedesc);
        TextView txt_timingtitle = findViewById(R.id.txt_timingtitle);
        txt_timingdesc = findViewById(R.id.txt_timingdesc);
        TextView txt_sharetitle = findViewById(R.id.txt_sharetitle);
        TextView txt_healthcaretitle = findViewById(R.id.txt_healthcaretitle);
        txt_healthcare = findViewById(R.id.txt_healthcare);
        txt_aboutus = findViewById(R.id.txt_aboutus);

        // Set Font
         Typeface TF_ralewayMidium = Typeface.createFromAsset(DoctorDetail.this.getAssets(), "fonts/Raleway-Medium_2.ttf");

        txt_settingtitle.setTypeface(TF_ralewayRegular);
        txt_name.setTypeface(TF_ralewayRegular);
        txt_desc.setTypeface(TF_ralewayRegular);
        txt_distancetitle.setTypeface(TF_ralewayMidium);
        txt_ratingtitle.setTypeface(TF_ralewayMidium);
        txt_distance.setTypeface(TF_ralewayMidium);
        txt_ratenumber.setTypeface(TF_ralewayMidium);
        txt_servicedesc.setTypeface(TF_opensansRegular);
        txt_timingdesc.setTypeface(TF_opensansRegular);
        txt_healthcare.setTypeface(TF_opensansRegular);
        txt_aboutus.setTypeface(TF_opensansRegular);
        txt_servicetitle.setTypeface(TF_ralewaybold);
        txt_timingtitle.setTypeface(TF_ralewaybold);
        txt_sharetitle.setTypeface(TF_ralewaybold);
        txt_healthcaretitle.setTypeface(TF_ralewaybold);


        btn_bookappointment.setTypeface(TF_opensansRegular);
        btn_bookappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uservalue.equals("delete")) {
                    errorbookDialog(DoctorDetail.this,1);
                } else {
                    try {
                        Intent iv = new Intent(DoctorDetail.this, BookApoinment.class);
                        iv.putExtra("userid", "" + uservalue);
                        iv.putExtra("doctor_id", "" + Obj_detail.getString("id"));
                        iv.putExtra("doctor_email",""+Obj_detail.getString("email"));
                        startActivity(iv);
                    } catch (JSONException e) {
                        Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Share Buttons

        Button btn_fb = findViewById(R.id.btn_fb);
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlToShare = null;
                try {
                    urlToShare = Obj_detail.getString("email1") + getString(R.string.fbtt1)+Obj_detail.getString("services")+getString(R.string.fbt2)+Obj_detail.getString("phone")+getString(R.string.fbt3)+Obj_detail.getString("address");
                    urlToSharefb = "Name: " + Obj_detail.getString("name") + "\n " + "\n\n" + Obj_detail.getString("facebook");
                   ShareDialog shareDialog = new ShareDialog(DoctorDetail.this);
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(Obj_detail.getString("facebook")))
                                .setQuote(urlToShare)
                                .build();
                        shareDialog.show(linkContent);
                    }

                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        Button btn_twitter = findViewById(R.id.btn_twitter);
        btn_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    urlToSharetwitter = getString(R.string.email1) + Obj_detail.getString("name") + "\n "+ Obj_detail.getString("twiter");
                    Uri bmpUri = getLocalBitmapUri(img_profile);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, urlToSharetwitter);
                    intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    intent.setType("text/plain");
                    intent.setType("image/*");
                    intent.setPackage("com.twitter.android");
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    try {
                        urlToSharetwitter = getString(R.string.email1) + Obj_detail.getString("name") + "\n " + "\n\n" + Obj_detail.getString("twiter");
                        originalMessageEscaped = String.format(
                                "https://twitter.com/intent/tweet?source=webclient&text=%s",
                                URLEncoder.encode(String.valueOf(Html.fromHtml(urlToSharetwitter)), "UTF-8"));
                    } catch (UnsupportedEncodingException e1) {
                        e.printStackTrace();
                    } catch (JSONException e1) {
                        Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                    }
                    if (originalMessageEscaped != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(originalMessageEscaped));
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }
            }
        });

        Button btn_whatsapp = findViewById(R.id.btn_whatsapp);
        btn_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //startActivity(whatsappIntent);
                try {
                    String urlToShare = getString(R.string.email1) + Obj_detail.getString("name") + "\n"+ getString(R.string.email2)+Obj_detail.getString("phone")+"\n"+getString(R.string.txt_address)+Obj_detail.getString("address");
                    Uri bmpUri = getLocalBitmapUri(img_profile);
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setType("image/*");
                    whatsappIntent.setPackage("com.whatsapp");
                    startActivity(whatsappIntent);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(DoctorDetail.this, R.string.whatsapperror, Toast.LENGTH_LONG)
                            .show();
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Bottom Bar Buttons

        Button btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String uri = "tel:" + Obj_detail.getString("phone");
                    Intent i = new Intent(Intent.ACTION_DIAL);
                    i.setData(Uri.parse(uri));
                    startActivity(i);
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this,  getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }

            }
        });
        Button btn_mail = findViewById(R.id.btn_mail);
        btn_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] recipients={email};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, inquiry);
                    intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.txt_quer)+Obj_detail.getString("name") + getString(R.string.txt_here)+"\n");
                    intent.setType("text/html");
                    startActivity(Intent.createChooser(intent, "Send mail"));

                } catch (Exception e) {
                    sendEmail();
                }

            }
        });
        Button btn_bottom_map = findViewById(R.id.btn_bottom_map);
        btn_bottom_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + Obj_detail.getString("lat") + "," + Obj_detail.getString("lon")
                                    + "&daddr=" + latitudecur + "," + longitudecur));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this,  getString(R.string.later_txt), Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button btn_review = findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent iv = new Intent(DoctorDetail.this, Review.class);
                    iv.putExtra("profile_id", "" + Obj_detail.getString("id"));
                    startActivity(iv);
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this,  getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }
            }
        });


        btn_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uservalue.equals("delete")) {
                    errorbookDialog(DoctorDetail.this,2);
                }else {
                  Intent i = new Intent(DoctorDetail.this,SendRecipe.class);
                  i.putExtra("doctoremail",email);
                          startActivity(i);}

            }
        });

        btn_favorite1 = findViewById(R.id.btn_favorite1);
        btn_favorite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_favorite.setVisibility(View.VISIBLE);
                btn_favorite1.setVisibility(View.INVISIBLE);

                // data store in database of favorite store

                DBAdapter myDbHelpel = new DBAdapter(DoctorDetail.this);
                try {
                    myDbHelpel.createDataBase();
                } catch (IOException io) {
                    throw new Error("Unable TO Create DataBase");
                }
                try {
                    myDbHelpel.openDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                db = myDbHelpel.getWritableDatabase();
                ContentValues values = new ContentValues();

                try {
                    values.put("name", Obj_detail.getString("name"));
                    values.put("service", Obj_detail.getString("services"));
                    values.put("distance", Obj_detail.getString("distancekm"));
                    values.put("doctorid", doctor_id);
                    values.put("uniqueid", Obj_detail.getString("id"));
                    db.insert("favorite", null, values);

                    myDbHelpel.close();
                } catch (JSONException e) {
                    Toast.makeText(DoctorDetail.this,  getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }


            }
        });

        btn_favorite = findViewById(R.id.btn_favorite);
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Image names are

                // detail_favorite
                // detail_unfavorite
                btn_favorite1.setVisibility(View.VISIBLE);
                btn_favorite.setVisibility(View.INVISIBLE);

                // remove record of store from database to unfavourite

                DBAdapter myDbHelper;
                myDbHelper = new DBAdapter(DoctorDetail.this);
                try {
                    myDbHelper.createDataBase();
                } catch (IOException e) {

                    e.printStackTrace();
                }

                try {

                    myDbHelper.openDataBase();

                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }

                int i = 1;
                try {
                    db = myDbHelper.getWritableDatabase();

                    cur = db.rawQuery("Delete from favorite where uniqueid =" + Obj_detail.getString("id") + ";", null);
                    if (cur.getCount() != 0) {
                        if (cur.moveToFirst()) {
                            do {
                                Getsetfav obj = new Getsetfav();

                                uniqueid = cur.getString(cur.getColumnIndex("uniqueid"));
                                name = cur.getString(cur.getColumnIndex("name"));
                                service = cur.getString(cur.getColumnIndex("service"));
                                doctorid = cur.getString(cur.getColumnIndex("doctorid"));
                                distance = cur.getString(cur.getColumnIndex("distance"));
                                obj.setName(name);
                                obj.setService(service);
                                obj.setDoctorid(doctorid);
                                obj.setDistance(distance);
                                obj.setUniqueid(uniqueid);
                                FileList.add(obj);

                            } while (cur.moveToNext());
                        }
                    }
                    cur.close();
                    db.close();
                    myDbHelper.close();

                } catch (Exception e) {
                    Toast.makeText(DoctorDetail.this,  getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

                }

            }
        });

        new getList().execute();
    }

    private void checkinternet() {
        // TODO Auto-generated method stub
        ConnectionDetector cd = new ConnectionDetector(DoctorDetail.this);
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            GPSTracker gps = new GPSTracker(DoctorDetail.this);
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

    private void setValuesToLayout() {


        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
        mapFragment.getMapAsync(this);


    }

    private void afterMapReady(double latitude, double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        CustomMarker customMarkerOne = new CustomMarker("markerOne", latitude, longitude);
        try {
            MarkerOptions markerOption = new MarkerOptions().position(

                    new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title(Obj_detail.getString("name"));

            Marker newMark = googleMap.addMarker(markerOption);

            addMarkerToHashMap(customMarkerOne, newMark);

            // googleMap.animateCamera(CameraUpdateFactory.zoomTo(5), 2000, null);
            // zoomToMarkers(layout12);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));
        } catch (JSONException e1) {
            Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();

        }
        try {
            email = Obj_detail.getString("email");
            txt_name.setText(Obj_detail.getString("name"));
            txt_desc.setText(Obj_detail.getString("address"));

            double Distance = roundMyData(Double.parseDouble(Obj_detail.getString("distance")), 1);
            txt_distance.setText("" + Distance + " "+getString(R.string.distance_miles));

            double ratting = roundMyData(Double.parseDouble(Obj_detail.getString("ratting")), 1);
            txt_ratenumber.setText("" + ratting);

            txt_servicedesc.setText(Obj_detail.getString("services"));
            txt_timingdesc.setText(Obj_detail.getString("hours"));
            txt_healthcare.setText(Obj_detail.getString("helthcare"));
            txt_aboutus.setText(Obj_detail.getString("about"));

            RatingBar rate_detail = findViewById(R.id.rate_detail);
            rate_detail.setRating(Float.parseFloat("" + ratting));

            Picasso.with(this).load(getString(R.string.link1) + "uploads/" + Obj_detail.getString("icon")).into(img_profile);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        double latitude = 0, longitude = 0;
        try {
            String lat = Obj_detail.getString("lat");
            String lon = Obj_detail.getString("lon");
            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(lon);
        } catch (NumberFormatException e) {
            // TODO: handle exception
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
            Toast.makeText(DoctorDetail.this, getString(R.string.later_txt), Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        afterMapReady(latitude, longitude);
    }

    private Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    private Uri getLocalBitmapUUri(RelativeLayout imageView) {
        // Extract Bitmap from ImageView drawable
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bmp=Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void sendEmail() {
        // TODO Auto-generated method stub
        String recipient = email;
        String subject = "For Inqueries";
        @SuppressWarnings("unused")
        String body = "";
        String[] recipients = {recipient};
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {

            startActivity(Intent.createChooser(email, "E-mail"));

        } catch (ActivityNotFoundException ex) {

            Toast.makeText(DoctorDetail.this, "There is no email client exists", Toast.LENGTH_LONG).show();

        }
    }

    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        markersHashMap.put(customMarker, marker);
    }

    private void setUpMarkersHashMap() {
        if (markersHashMap == null) {
            markersHashMap = new HashMap<>();
        }
    }

    public void zoomToMarkers(View v) {
        zoomAnimateLevelToFitMarkers(120);
    }

    private void zoomAnimateLevelToFitMarkers(int padding) {
        Iterator<Map.Entry<CustomMarker, Marker>> iter = markersHashMap.entrySet().iterator();
        LatLngBounds.Builder b = new LatLngBounds.Builder();

        LatLng ll;
        while (iter.hasNext()) {
            Map.Entry mEntry = iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());

            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        // Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 400, 17);
        googleMap.animateCamera(cu);

    }

    private void errorbookDialog(final Activity activity,int i) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alertdialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView errormsg = dialog.findViewById(R.id.txt_error_description);
        TextView txt_dialog_title = dialog.findViewById(R.id.txt_dialog_title);

        if(i==2)
        {
            errormsg.setText(R.string.orderalert);
        }

        errormsg.setTypeface(TF_ralewayRegular);
        txt_dialog_title.setTypeface(TF_opensansRegular);

        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes.setText(getString(R.string.button_yes));
        btn_no.setText(getString(R.string.button_no));

        btn_yes.setTypeface(TF_ralewayRegular);
        btn_no.setTypeface(TF_ralewayRegular);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, Login.class);
                startActivity(i);
            }
        });
        dialog.show();
    }

    public class GetDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DoctorDetail.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp;
            try {

                hp = new URL(getString(R.string.link) + "getprofilefulldetail.php?profile_id=" + specialities_id + "&lat=" + latitudecur + "&lon=" + longitudecur);

                // URL Connection
                Log.e("check1",hp.toString());
                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x;
                x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                Log.e("check1",total.toString());

                // Json Parsing
                JSONArray jObject = new JSONArray(total.toString());
                JSONObject Obj;
                Obj = jObject.getJSONObject(0);
                JSONArray profile_detail;
                profile_detail = Obj.getJSONArray("profile_detail");
                Obj_detail = profile_detail.getJSONObject(0);

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
            setValuesToLayout();
        }
    }

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileList.clear();
            DBAdapter myDbHelper = new DBAdapter(DoctorDetail.this);
            myDbHelper = new DBAdapter(DoctorDetail.this);
            try {
                myDbHelper.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                myDbHelper.openDataBase();

            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            int i = 1;
            db = myDbHelper.getReadableDatabase();

            try {
                cur = db.rawQuery("select * from favorite where uniqueid=" + Obj_detail.getString("id") + ";", null);
                Log.d("SIZWA", "" + "select * from favorite where uniqueid=" + Obj_detail.getString("id") + ";");
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            uniqueid = cur.getString(cur.getColumnIndex("uniqueid"));

                            name = cur.getString(cur.getColumnIndex("name"));
                            service = cur.getString(cur.getColumnIndex("service"));
                            doctorid = cur.getString(cur.getColumnIndex("doctorid"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            obj.setName(name);
                            obj.setService(service);
                            obj.setDoctorid(doctorid);
                            obj.setDistance(distance);
                            obj.setUniqueid(uniqueid);
                            FileList.add(obj);

                        } while (cur.moveToNext());

                    }

                }

                cur.close();
                db.close();
                myDbHelper.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (FileList.size() == 0) {

                Log.d("favorite", "no");
                btn_favorite1.setVisibility(View.VISIBLE);
                btn_favorite.setVisibility(View.INVISIBLE);
            } else {
                Log.d("favorite", "yes");
                btn_favorite1.setVisibility(View.INVISIBLE);
                btn_favorite.setVisibility(View.VISIBLE);

            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent i;
        i = getIntent();
        if(i!=null){
        if(i.getStringExtra("fromclass")==("fav"))
        {Intent intent = new Intent(DoctorDetail.this,Favorite.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);}
        else
        super.onBackPressed();}

    }
}
