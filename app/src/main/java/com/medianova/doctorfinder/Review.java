package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.Iterator;

import static com.medianova.doctorfinder.Home.TF_opensansRegular;
import static com.medianova.doctorfinder.Home.TF_ralewayRegular;

public class Review extends Activity {
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private ProgressDialog progressDialog;
    private ArrayList<Reviewgetset> rest;
    private ArrayList<User> rest1;
    private String profile_id;
    private View layout12;
    private EditText edt_review;
    private RatingBar rate;
    private String usercomment;
    private String userrate;
    private String uservalue = "20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent iv = getIntent();
        profile_id = iv.getStringExtra("profile_id");
        rest = new ArrayList<>();
        new getreviewdetail().execute();

        Button btn_add_review = findViewById(R.id.add_review);
        btn_add_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
                // check user is created or not
                // if user is already logged in
                if (prefs.getString("userid", null) != null) {
                    uservalue = prefs.getString("userid", null);
                    if (uservalue.equals("delete")) {
                        errorbookDialog(Review.this);
                    } else {

                        RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog1);
                        layout12 = getLayoutInflater().inflate(R.layout.givereview, rl_dialoguser, false);
                        rl_dialoguser.setVisibility(View.VISIBLE);
                        rl_dialoguser.addView(layout12);
                        edt_review = layout12.findViewById(R.id.txt_description);
                        rate = layout12.findViewById(R.id.rate1234);
                        Button btn_submit = layout12.findViewById(R.id.btn_submit);
                        btn_submit.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                // rl_adddialog.setVisibility(View.GONE);
                                layout12 = v;
                                // list_review.setEnabled(true);
                                // rl_home.setAlpha(1.0f);

                                try {
                                    usercomment = edt_review.getText().toString().replace(" ", "%20");
                                    userrate = String.valueOf(rate.getRating());
                                    if (usercomment.equals(null)) {
                                        usercomment = "";
                                    }

                                } catch (NullPointerException e) {
                                    // TODO: handle exception
                                }

                                if (usercomment.equals("")) {
                                    edt_review.setError(getString(R.string.request_review));
                                } else {
                                    new getratedetail().execute();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(Review.this);
                                    builder.setMessage(R.string.feedback_txt)
                                            .setTitle(R.string.thanks_txt);

                                    builder.setNeutralButton(android.R.string.ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    new getreviewdetail().execute();
                                                }
                                            });

                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    View myView = findViewById(R.id.rl_back);
                                    ViewGroup parent = (ViewGroup) myView.getParent();
                                    parent.removeView(myView);

                                }
                            }
                        });

                        Button btn_cancel = layout12.findViewById(R.id.btn_cancel);
                        btn_cancel.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                // list_review.setEnabled(true);
                                View myView = findViewById(R.id.rl_back);
                                ViewGroup parent = (ViewGroup) myView.getParent();
                                parent.removeView(myView);


                            }
                        });
                    }
                } else {
                    errorbookDialog(Review.this);
                }
            }
        });
    }

    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp = null;
        String error;
        try {
            rest.clear();
            hp = new URL(getString(R.string.link) + "getreview.php?profile_id=" + profile_id);
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
            JSONArray jarr = new JSONArray(total.toString());
            JSONObject jObject = jarr.getJSONObject(0);
            String currentKey = "";
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
            }


            JSONArray j = jObject.getJSONArray("List_review");
            for (int i = 0; i < j.length(); i++) {

                JSONObject Obj;
                Obj = j.getJSONObject(i);
                Reviewgetset temp = new Reviewgetset();
                temp.setId(Obj.getString("id"));
                temp.setUsername(Obj.getString("username"));
                temp.setReview_text(Obj.getString("review_text"));
                temp.setUserimage(Obj.getString("userimage"));
                temp.setRatting(Obj.getString("ratting"));
                rest.add(temp);

            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (NullPointerException e) {
            // TODO: handle exception
            error = e.getMessage();
        }
    }

    private void errorbookDialog(final Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alertdialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_dialog_title = dialog.findViewById(R.id.txt_dialog_title);
        TextView errormsg = dialog.findViewById(R.id.txt_error_description);
        errormsg.setText(getString(R.string.reviewerror));
        errormsg.setTypeface(TF_ralewayRegular);
        txt_dialog_title.setTypeface(TF_opensansRegular);

        Button btn_yes = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes.setTypeface(TF_ralewayRegular);
        btn_no.setTypeface(TF_ralewayRegular);

        btn_yes.setText(getString(R.string.button_yes));
        btn_no.setText(getString(R.string.button_no));

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

    public class getreviewdetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Review.this);
            progressDialog.setMessage("Loading");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getdetailforNearMe();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();


                final ListView list_review = findViewById(R.id.list_review);
                LazyAdapter lazy = new LazyAdapter(Review.this, rest);
                list_review.setAdapter(lazy);

//                list_review.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                        // TODO Auto-generated method stub
//                                RelativeLayout rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
//                                layout12 = getLayoutInflater().inflate(R.layout.reviewclick_dialog, rl_dialog, false);
//                                rl_dialog.addView(layout12);
//
//                                TextView txt_name_comment = (TextView) layout12.findViewById(R.id.txt_nameuser);
//                                txt_name_comment.setText("" + Html.fromHtml(rest.get(position).getUsername()));
//                                try {
//                                    RatingBar rb = (RatingBar) layout12.findViewById(R.id.rate1234);
//                                    rb.setRating(Float.parseFloat(rest.get(position).getRatting()));
//                                } catch (NumberFormatException e) {
//                                    // TODO: handle exception
//                                }
//
//                                TextView txt_comment_desc = (TextView) layout12.findViewById(R.id.txt_desc);
//                                txt_comment_desc.setText("" + Html.fromHtml(rest.get(position).getReview_text()));
//
//                                String image = rest.get(position).getUserimage();
//
////                                ImageView img_user = (ImageView) layout12.findViewById(R.id.img_my);
////                                if (image != null) {
////                                Picasso.with(Review.this).load(image).into(img_user);
////                                } else {
////                                }
//
//                                Button btn_ok = (Button) layout12.findViewById(R.id.btn_ok);
//                                btn_ok.setOnClickListener(new View.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(View v) {
//                                        // TODO Auto-generated method stub
//                                        View myView = findViewById(R.id.rl_back);
//                                        ViewGroup parent = (ViewGroup) myView.getParent();
//                                        parent.removeView(myView);
//                                    }
//                                });
//
//                    }
//                });
            }

        }


    }

    public class LazyAdapter extends BaseAdapter {

        private final Activity activity;
        private final ArrayList<Reviewgetset> data;
        String s;
        private LayoutInflater inflater = null;

        public LazyAdapter(Activity a, ArrayList<Reviewgetset> str) {
            activity = a;
            data = str;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View vi = convertView;

            if (convertView == null) {

                // vi = inflater.inflate(R.layout.reviewcell, null);
                vi = inflater.inflate(R.layout.cell_review, parent,false);
            }

            Typeface TF_opensens = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Regular.ttf");
            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/Raleway-Regular_4.ttf");
            Typeface TF_opensens_bold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Bold.ttf");

            try {
                TextView txt_name = vi.findViewById(R.id.name);
                txt_name.setText(Html.fromHtml(data.get(position).getUsername()));
                txt_name.setTypeface(TF_opensens_bold);
                TextView rate = vi.findViewById(R.id.rate);
                rate.setText(Html.fromHtml(String.format("%.1f", Double.parseDouble(data.get(position).getRatting()))));
                rate.setTypeface(tf);

                TextView txt_comment = vi.findViewById(R.id.review);
                txt_comment.setText(Html.fromHtml(data.get(position).getReview_text()));
                txt_comment.setTypeface(TF_opensens);
                String image = "";

                image = data.get(position).getUserimage();
                ImageView img_user = vi.findViewById(R.id.img_user);
                if (image != null) {
                    Picasso.with(Review.this).load(image).fit().into(img_user);
                }

                RatingBar rb = vi.findViewById(R.id.rate_detail);
                Float i = Float.parseFloat(data.get(position).getRatting());
                rb.setRating(i);

                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.rl_infodialog).setVisibility(View.VISIBLE
                        );

                        showFullReview(position);
                    }
                });

            } catch (NumberFormatException e) {
                // TODO: handle exception
            } catch (NullPointerException e) {
                // TODO: handle exception
            }

            return vi;
        }
    }

    public class getratedetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL hp = null;
            try {

                hp = new URL(getString(R.string.link) + "postreview.php?user_id=" + uservalue
                        + "&profile_id=" + profile_id + "&ratting=" + userrate + "&review_text=" + usercomment);

                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String x = "";
                // x = r.readLine();
                StringBuilder total = new StringBuilder();
                while (x != null) {
                    total.append(x);
                    x = r.readLine();
                }
                JSONArray jarr = new JSONArray(total.toString());
                JSONObject j = jarr.getJSONObject(0);
                for (int i = 0; i < j.length(); i++) {
                    JSONObject Obj;
                    Obj = j.getJSONObject(String.valueOf(i));
                    User temp = new User();
                    temp.setStatus(Obj.getString("Status"));
                    rest1.add(temp);
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }

    private void showFullReview(int position) {
        layout12= findViewById(R.id.rl_infodialog);
        //layout12 = getLayoutInflater().inflate(R.layout.reviewclick_dialog, rl_dialog, false);
        //rl_dialog.addView(layout12);

        TextView txt_name_comment = findViewById(R.id.txt_nameuser);
        txt_name_comment.setText(rest.get(position).getUsername());
        try {
            RatingBar rb = findViewById(R.id.rate1234);
            rb.setRating(Float.parseFloat(rest.get(position).getRatting()));
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        TextView txt_comment_desc = findViewById(R.id.txt_desc);
        txt_comment_desc.setText(rest.get(position).getReview_text());

        String image = rest.get(position).getUserimage();

//                                ImageView img_user = (ImageView) layout12.findViewById(R.id.img_my);
//                                if (image != null) {
//                                Picasso.with(Review.this).load(image).into(img_user);
//                                } else {
//                                }

        Button btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                layout12.setVisibility(View.GONE);

            }
        });



        }
}
