package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.medianova.utils.DBAdapter;

import java.io.IOException;
import java.util.ArrayList;

import static com.medianova.doctorfinder.Home.TF_ralewayRegular;
import static com.medianova.utils.AdMobIntegration.loadAdmobBanner;
import static com.medianova.utils.AdMobIntegration.shouldDisplayAds;

public class Favorite extends Activity {
    private ArrayList<Getsetfav> FileList;
    private ArrayList<Getsetfav> FileListph;
    private ArrayList<Getsetfav> FileListho;
    private ProgressDialog progressDialog;
    private SQLiteDatabase db;
    private String uniqueid;
    private String doctorid;
    private String name;
    private String service;
    private String distance;
    private RelativeLayout topd;
    private RelativeLayout topp;
    private RelativeLayout toph;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        TextView header = findViewById(R.id.header);
        topd = findViewById(R.id.topd);
        topp = findViewById(R.id.topp);
        toph = findViewById(R.id.toph);
        header.setTypeface(TF_ralewayRegular);
        FileList = new ArrayList<>();
        FileListph = new ArrayList<>();
        FileListho = new ArrayList<>();
        new getList().execute();
        new getListph().execute();
        new getListho().execute();
    }

    //if any store will be unfavorite then back to again this page will be refresh
    private void errorbookDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        adView=findViewById(R.id.adView);
        if(shouldDisplayAds(Favorite.this))
            loadAdmobBanner(adView,Favorite.this);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // When BACK BUTTON is pressed, the activity on the stack is restarted
        // Do what you want on the refresh procedure here
        new getList().execute();
        new getListph().execute();
        new getListho().execute();
    }

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(Favorite.this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileList.clear();

            DBAdapter myDbHelper = new DBAdapter(Favorite.this);
            myDbHelper = new DBAdapter(Favorite.this);
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
                Cursor cur = db.rawQuery("select * from favorite where doctorid=1;", null);
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            uniqueid = cur.getString(cur.getColumnIndex("uniqueid"));
                            doctorid = cur.getString(cur.getColumnIndex("doctorid"));
                            name = cur.getString(cur.getColumnIndex("name"));
                            service = cur.getString(cur.getColumnIndex("service"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            obj.setName(name);
                            obj.setService(service);
                            obj.setUniqueid(uniqueid);
                            obj.setDoctorid(doctorid);
                            obj.setDistance(distance);
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

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                ListView list_fav = findViewById(R.id.list_fvrt);
                if (FileList.size() == 0) {
                    topd.setVisibility(View.GONE);

                } else {
                    topd.setVisibility(View.VISIBLE);
                    list_fav = findViewById(R.id.list_fvrt);
                    list_fav.setVisibility(View.VISIBLE);
                    LazyAdapter lazy = new LazyAdapter(Favorite.this, FileList);
                    lazy.notifyDataSetChanged();
                    list_fav.setAdapter(lazy);
                    list_fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub

                            Intent iv = new Intent(Favorite.this, DoctorDetail.class);
                            iv.putExtra("profile_id", "" + FileList.get(position).getUniqueid());
                            iv.putExtra("doctor_id", "" + FileList.get(position).getDoctorid());
                            iv.putExtra("fromclass","fav");
                            startActivity(iv);
                        }
                    });
                }

            }
        }
    }

    // bind data in listview

    private class getListph extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileListph.clear();
            DBAdapter myDbHelper;
            myDbHelper = new DBAdapter(Favorite.this);
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

                Cursor cur1 = db.rawQuery("select * from favorite where doctorid=2;", null);

                if (cur1.getCount() != 0) {
                    if (cur1.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            uniqueid = cur1.getString(cur1.getColumnIndex("uniqueid"));
                            doctorid = cur1.getString(cur1.getColumnIndex("doctorid"));
                            name = cur1.getString(cur1.getColumnIndex("name"));
                            service = cur1.getString(cur1.getColumnIndex("service"));
                            distance = cur1.getString(cur1.getColumnIndex("distance"));
                            obj.setName(name);
                            obj.setService(service);
                            obj.setUniqueid(uniqueid);
                            obj.setDoctorid(doctorid);
                            obj.setDistance(distance);
                            FileListph.add(obj);

                        } while (cur1.moveToNext());

                    }

                }
                cur1.close();
                db.close();
                myDbHelper.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {


            ListView list_fav1 = findViewById(R.id.list_fvrt1);

            if (FileListph.size() == 0) {

                topp.setVisibility(View.GONE);
            } else {
                topp.setVisibility(View.VISIBLE);
                list_fav1 = findViewById(R.id.list_fvrt1);
                list_fav1.setVisibility(View.VISIBLE);
                LazyAdapter1 lazy1 = new LazyAdapter1(Favorite.this, FileListph);
                lazy1.notifyDataSetChanged();
                list_fav1.setAdapter(lazy1);

                list_fav1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub

                        Intent iv = new Intent(Favorite.this, DoctorDetail.class);
                        iv.putExtra("profile_id", "" + FileListph.get(position).getUniqueid());
                        iv.putExtra("doctor_id", "" + FileListph.get(position).getDoctorid());
                        startActivity(iv);
                    }
                });
            }


        }
    }

    private class getListho extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileListho.clear();
            DBAdapter myDbHelper = new DBAdapter(Favorite.this);
            myDbHelper = new DBAdapter(Favorite.this);
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

                Cursor cur2 = db.rawQuery("select * from favorite where doctorid=3;", null);
                if (cur2.getCount() != 0) {
                    if (cur2.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            uniqueid = cur2.getString(cur2.getColumnIndex("uniqueid"));
                            doctorid = cur2.getString(cur2.getColumnIndex("doctorid"));
                            name = cur2.getString(cur2.getColumnIndex("name"));
                            service = cur2.getString(cur2.getColumnIndex("service"));
                            distance = cur2.getString(cur2.getColumnIndex("distance"));
                            obj.setName(name);
                            obj.setService(service);
                            obj.setUniqueid(uniqueid);
                            obj.setDoctorid(doctorid);
                            obj.setDistance(distance);
                            FileListho.add(obj);
                        } while (cur2.moveToNext());

                    }

                }
                cur2.close();
                db.close();
                myDbHelper.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {


            ListView list_fav2 = findViewById(R.id.list_fvrt2);

            if (FileList.size() == 0 && FileListho.size() == 0 && FileListph.size() == 0) {
                errorbookDialog(Favorite.this);
            }

            if (FileListho.size() == 0) {
                toph.setVisibility(View.GONE);

            } else {
                toph.setVisibility(View.VISIBLE);
                list_fav2 = findViewById(R.id.list_fvrt2);
                list_fav2.setVisibility(View.VISIBLE);
                LazyAdapter2 lazy2 = new LazyAdapter2(Favorite.this, FileListho);
                lazy2.notifyDataSetChanged();
                list_fav2.setAdapter(lazy2);

                list_fav2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub

                        Intent iv = new Intent(Favorite.this, DoctorDetail.class);
                        iv.putExtra("profile_id", "" + FileListho.get(position).getUniqueid());
                        iv.putExtra("doctor_id", "" + FileListho.get(position).getDoctorid());
                        startActivity(iv);
                    }
                });
            }

        }
    }

    public class LazyAdapter extends BaseAdapter {

        private final Activity activity;
        private final ArrayList<Getsetfav> data;
        private LayoutInflater inflater = null;


        public LazyAdapter(Activity a, ArrayList<Getsetfav> d) {
            activity = a;
            data = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
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

            if (convertView == null) {

                vi = inflater.inflate(R.layout.cell_fvrt, parent,false);

            }

            Typeface TF_opensens = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/Raleway-Regular_4.ttf");
            Typeface TF_opensens_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Bold.ttf");
            try {


                ImageView img_bg = vi.findViewById(R.id.img_bg);
                String doctorid = data.get(position).getDoctorid();
                switch (doctorid) {
                    case "1":
                        img_bg.setBackgroundResource(R.drawable.dr_cell);

                        break;
                    case "2":
                        img_bg.setBackgroundResource(R.drawable.pharmacies_cell);
                        break;
                    case "3":
                        img_bg.setBackgroundResource(R.drawable.hospital_cell);
                        break;
                }

                Spanned namefirst = Html.fromHtml(data.get(position).getName());

                String s = String.valueOf(namefirst).substring(0, 1).toUpperCase();

                TextView txt_first = vi.findViewById(R.id.first_letter);
                txt_first.setText("" + Html.fromHtml(s));

                TextView txt_name = vi.findViewById(R.id.name);
                txt_name.setText(Html.fromHtml(data.get(position).getName()));
                txt_name.setTypeface(TF_opensens_bold);

                TextView txt_service = vi.findViewById(R.id.service);
                txt_service.setText(Html.fromHtml(data.get(position).getService()));
                txt_service.setTypeface(TF_opensens);

                TextView txt_km = vi.findViewById(R.id.txt_distance);
                txt_km.setText(data.get(position).getDistance() + " km");
                txt_km.setTypeface(tf);
            } catch (StringIndexOutOfBoundsException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }

    public class LazyAdapter1 extends BaseAdapter {

        private final Activity activity;
        private final ArrayList<Getsetfav> data;
        private LayoutInflater inflater = null;


        public LazyAdapter1(Activity a, ArrayList<Getsetfav> d) {
            activity = a;
            data = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
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

            if (convertView == null) {

                vi = inflater.inflate(R.layout.cell_fvrt, parent,false);

            }
            try {

                ImageView img_bg = vi.findViewById(R.id.img_bg);
                String doctorid = data.get(position).getDoctorid();
                switch (doctorid) {
                    case "1":
                        img_bg.setBackgroundResource(R.drawable.dr_cell);
                        break;
                    case "2":
                        img_bg.setBackgroundResource(R.drawable.pharmacies_cell);
                        break;
                    case "3":
                        img_bg.setBackgroundResource(R.drawable.hospital_cell);
                        break;
                }

                Spanned namefirst = Html.fromHtml(data.get(position).getName());

                String s = String.valueOf(namefirst).substring(0, 1).toUpperCase();

                TextView txt_first = vi.findViewById(R.id.first_letter);
                txt_first.setText("" + Html.fromHtml(s));

                TextView txt_name = vi.findViewById(R.id.name);
                txt_name.setText(Html.fromHtml(data.get(position).getName()));


                TextView txt_service = vi.findViewById(R.id.service);
                txt_service.setText(Html.fromHtml(data.get(position).getService()));


                TextView txt_km = vi.findViewById(R.id.txt_distance);
                txt_km.setText(data.get(position).getDistance() + " km");

            } catch (StringIndexOutOfBoundsException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }

    public class LazyAdapter2 extends BaseAdapter {

        private final Activity activity;
        private final ArrayList<Getsetfav> data;
        private LayoutInflater inflater = null;


        public LazyAdapter2(Activity a, ArrayList<Getsetfav> d) {
            activity = a;
            data = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
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

            if (convertView == null) {

                vi = inflater.inflate(R.layout.cell_fvrt, parent,false);

            }
            try {
                ImageView img_bg = vi.findViewById(R.id.img_bg);
                String doctorid = data.get(position).getDoctorid();
                switch (doctorid) {
                    case "1":
                        img_bg.setBackgroundResource(R.drawable.dr_cell);
                        break;
                    case "2":
                        img_bg.setBackgroundResource(R.drawable.pharmacies_cell);
                        break;
                    case "3":
                        img_bg.setBackgroundResource(R.drawable.hospital_cell);
                        break;
                }

                Spanned namefirst = Html.fromHtml(data.get(position).getName());
                String s = String.valueOf(namefirst).substring(0, 1).toUpperCase();
                TextView txt_first = vi.findViewById(R.id.first_letter);
                txt_first.setText("" + Html.fromHtml(s));
                TextView txt_name = vi.findViewById(R.id.name);
                txt_name.setText(Html.fromHtml(data.get(position).getName()));
                TextView txt_service = vi.findViewById(R.id.service);
                txt_service.setText(Html.fromHtml(data.get(position).getService()));
                TextView txt_km = vi.findViewById(R.id.txt_distance);
                txt_km.setText(data.get(position).getDistance() + " km");

            } catch (StringIndexOutOfBoundsException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }
}
