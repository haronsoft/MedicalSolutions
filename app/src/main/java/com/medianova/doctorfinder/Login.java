package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.medianova.utils.CommonUtilities;
import com.medianova.utils.WakeLocker;
import com.medianova.utils.logingetset;

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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.medianova.doctorfinder.Home.TF_opensansRegular;
import static com.medianova.doctorfinder.Home.TF_ralewayRegular;

public class Login extends Activity {
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            // Releasing wake lock
            WakeLocker.release();
        }
    };

    private EditText mail;
    private EditText password;
    private String email;
    private String pwd;
    private ArrayList<logingetset> login;
    private String status;
    private String regId;
    private String name;
    private String imagefb;
    private String ppic;
    private String click;
    private CallbackManager callbackManager;
    private String personname;
    private String personemail;
    private String personPhotoUrl;

    public static void logout() {
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView header = findViewById(R.id.header);
        header.setTypeface(TF_ralewayRegular);
        Button btn_facebook = findViewById(R.id.facebook);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
//        regId = GCMRegistrar.getRegistrationId(Login.this);
//        GCMRegistrar.register(Login.this, CommonUtilities.SENDER_ID);
        TextView create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iv = new Intent(Login.this, Registernew.class);
                startActivity(iv);
            }
        });
        login = new ArrayList<>();
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        Button signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = "signin";
                email = mail.getText().toString();
                pwd = password.getText().toString();

                if (!email.isEmpty()) {
                    if (!pwd.isEmpty()) {
                        new getlogin().execute();
                    } else {
                        password.setError(getString(R.string.pass_enter_text));
                    }
                } else {
                    mail.setError(getString(R.string.username_enter));
                }
            }
        });

//        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        final List<String> permissionNeeds = Arrays.asList("email");
        final boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetail(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("check1", error.getMessage());
            }
        });
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = "fb";
                LoginManager.getInstance().logInWithReadPermissions(Login.this, permissionNeeds);
            }


//                loginToFacebook();

            // postToWall();
            // logoutFromFacebook();

//                getProfileInformation();


        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//       unregisterReceiver(mHandleMessageReceiver);
    }

    private void getdetailforNearMe() {
        URL hp = null;
        String error;
        try {
            login.clear();
            switch (click) {
                case "signin":
                    hp = new URL(getString(R.string.link) + "userlogin.php?username=" + email + "&password=" + pwd);
                    break;
                case "fb":
                    hp = new URL(getString(R.string.link) + "userlogin.php?logintype=Facebook&email=" + email + "&name=" + name + "&image=" + imagefb + "&platform=Android&reg_id=" + regId);
                    break;
                case "gplus":
                    hp = new URL(getString(R.string.link) + "userlogin.php?logintype=Google&email=" + personemail + "&name=" + personname + "&image=" + personPhotoUrl + "&platform=Android&reg_id=" + regId);

                    break;
            }
            Log.d("login", "" + hp);
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
            JSONArray jarr = new JSONArray(total.toString());
            JSONObject jObject = jarr.getJSONObject(0);
            String currentKey;
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
                Log.d("currentkey", "" + currentKey);
            }

            status = jObject.getString("status");
            if (status.equals("Failed")) {

            } else if (status.equals("Success")) {
                JSONObject j = jObject.getJSONObject("User_info");
                for (int i = 0; i < j.length(); i++) {

                    logingetset temp = new logingetset();
                    temp.setId(j.getString("id"));
                    temp.setUsername(j.getString("username"));
                    temp.setEmail(j.getString("email"));
                    temp.setImage(j.getString("image"));
                    login.add(temp);
                }
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

    private void getUserDetail(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Log.e("User Data", json_object.toString());
                        String json = json_object.toString();
                        try {
                            JSONObject profile = new JSONObject(json);

                            // getting name of the user
                            name = profile.getString("name");
                            name = name.replace(" ", "%20");
                            // getting email of the user
                            regId = profile.getString("id");
                            email = profile.getString("email");
                            JSONObject picture = profile.getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            ppic = data.getString("url");
                            if (name != null) {
                                if (ppic != null) {
                                    imagefb = "https://graph.facebook.com/" + regId + "/picture?type=large";
                                    Log.d("fbimage", "" + imagefb);
                                    email = email.replace(" ", "%20");
                                    new getlogin().execute();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                        }

                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    @Override
    protected void onDestroy() {
//
//        try {
//            unregisterReceiver(mHandleMessageReceiver);
////            GCMRegistrar.onDestroy(this);
//        } catch (Exception e) {
//            Log.e("UnRegister", "> " + e.getMessage());
//        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    private void errorDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView img = dialog.findViewById(R.id.img_cancel);
        img.setImageResource(R.drawable.dialogsucess);

        TextView txt_dialog_title = dialog.findViewById(R.id.txt_dialog_title);
        TextView txt_error_description = dialog.findViewById(R.id.txt_error_description);
        txt_dialog_title.setTypeface(TF_opensansRegular);
        txt_error_description.setTypeface(TF_ralewayRegular);

        txt_dialog_title.setText(getString(R.string.success_title));
        txt_error_description.setText(getString(R.string.login_message));

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setTypeface(TF_ralewayRegular);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent iv = new Intent(Login.this, Home.class);
                startActivity(iv);
            }
        });
        dialog.show();
    }

    public class getlogin extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);
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
            }

            try {
                if (status.equals("Success")) {
                    SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                    editor.putString("userid", "" + login.get(0).getId());
                    editor.putString("username", "" + login.get(0).getUsername());
                    editor.putString("email", "" + login.get(0).getEmail());
                    editor.putString("picture", "" + login.get(0).getImage());
                    editor.apply();

                    errorDialog(Login.this);

                } else if (status.equals("Failed")) {
                    Toast.makeText(Login.this, R.string.toast_warn, Toast.LENGTH_LONG).show();
                }
            } catch (NullPointerException e) {
                Toast.makeText(Login.this, R.string.toast_warn, Toast.LENGTH_LONG).show();
            }
        }
    }
}
