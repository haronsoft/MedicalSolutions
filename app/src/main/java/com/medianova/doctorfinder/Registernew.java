package com.medianova.doctorfinder;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.medianova.utils.CommonUtilities;
import com.medianova.utils.WakeLocker;
import com.medianova.utils.logingetset;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static com.medianova.doctorfinder.Home.TF_ralewayRegular;


public class Registernew extends Activity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_cam_IMAGE = 2;
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA

    };
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
    private ImageView img_user;
    private String picturepath;
    private ArrayList<logingetset> login;
    private String status;
    private EditText username;
    private EditText mail;
    private EditText password;
    private String uname;
    private String email;
    private String pwd;
    private String responseStr;
    private ProgressDialog progressDialog;

    public static void verifyStoragePermissions(final Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(activity)
                    .setTitle("Storage")
                    .setMessage("Need permission to access storage!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(activity,
                                    PERMISSIONS_STORAGE,
                                    REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .create()
                    .show();


        } else {
            ActivityCompat.requestPermissions(activity,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew);
        TextView header = findViewById(R.id.header);
        header.setTypeface(TF_ralewayRegular);
        login = new ArrayList<>();
        String UPLOAD_URL = getString(R.string.link) + "userregister.php";
//        GCMRegistrar.checkDevice(Registernew.this);
//        GCMRegistrar.checkManifest(Registernew.this);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

//        // Get GCM registration id
//        regId = GCMRegistrar.getRegistrationId(Registernew.this);
//        Log.d("regIdrest", "" + regId);
//        GCMRegistrar.register(Registernew.this, CommonUtilities.SENDER_ID);
        username = findViewById(R.id.username);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        Button camera = findViewById(R.id.camera);
        Button gallery = findViewById(R.id.gallery);
        img_user = findViewById(R.id.img_user);
        Button btn_close = findViewById(R.id.close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_user.setImageResource(R.drawable.profile_reg);
                picturepath = null;
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(Registernew.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    verifyStoragePermissions(Registernew.this);
                }else {

                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);}
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(Registernew.this, Manifest.permission.CAMERA);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            Registernew.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                } else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, RESULT_cam_IMAGE);
                }
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uname = username.getText().toString();
                uname=uname.replace(" ","%20");
                pwd = password.getText().toString();
                pwd=uname.replace(" ","%20");
                email = mail.getText().toString();

                if (!uname.isEmpty()) {
                    if (!pwd.isEmpty()) {
                        if (!email.isEmpty()) {
                            if (picturepath != null) {
                                new PostDataAsyncTask().execute();
                            } else {
                                Toast.makeText(Registernew.this, R.string.selectimagetxt, Toast.LENGTH_LONG).show();
                            }
                        }else mail.setError(getString(R.string.enteremailid));
                    } else {
                        password.setError(getString(R.string.pass_enter_text));
                    }
                } else {
                    username.setError(getString(R.string.username_enter));

                }
            }
        });
    }

    private void postdata() {
        // TODO Auto-generated method stub
        HttpClient httpClient = new DefaultHttpClient();
        String boundary = "-------------" + System.currentTimeMillis();


        String platform = "Android";
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        HttpEntity entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary).addTextBody("email", email).addTextBody("username", uname)
                .addTextBody("password", pwd).addBinaryBody("file", new File(picturepath),
                        ContentType.create("application/octet-stream"), "filename")
                .addTextBody("platform", platform).addTextBody("reg_id", "" + regId).build();


        HttpPost httpPost = new HttpPost(getString(R.string.link) + "userregister.php");
        httpPost.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
        httpPost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpEntity result = response.getEntity();
        if (result != null) {

            try {
                responseStr = EntityUtils.toString(result).trim();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {


        Uri selectedImage = data.getData();
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            String[] filePathColumn = {MediaStore.MediaColumns.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturepath = cursor.getString(columnIndex);

            Log.d("picturepath", "" + picturepath);
            cursor.close();
            String fileNameSegments[] = picturepath.split("/");
            String fileName = fileNameSegments[fileNameSegments.length - 1];
            Bitmap myImg = BitmapFactory.decodeFile(picturepath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] arr = stream.toByteArray();
            // Encode Image to String
            String encodedString = Base64.encodeToString(arr, 0);
            img_user.setImageBitmap(decodeFile(picturepath));
        } else if (requestCode == RESULT_cam_IMAGE && resultCode == RESULT_OK && null != data) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            img_user.setImageBitmap(photo);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            picturepath = String.valueOf(finalFile);
            Bitmap bm = decodeFile(picturepath);

        }}
        catch (NullPointerException e)
        {
            e.getMessage();
        }

    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String temp = cursor.getString(idx);
        cursor.close();
        return temp;
    }

    private Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bmap = BitmapFactory.decodeFile(path, o2);
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
//        try {
//            unregisterReceiver(mHandleMessageReceiver);
//            GCMRegistrar.onDestroy(this);
//        } catch (Exception e) {
//            //Log.e("UnRegister Receiver Error", "> " + e.getMessage());
//        }
        super.onDestroy();
    }

    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp = null;
        String error;
        try {

            JSONArray jarr = new JSONArray(responseStr);
            Log.d("mainarray", "" + jarr);
            JSONObject jObject = jarr.getJSONObject(0);
            Log.d("mainobj", "" + jObject);
            String currentKey;
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
                Log.d("currentkey", "" + currentKey);
            }

            status = jObject.getString("status");
            if (status.equals("Failed")) {

            } else if (status.equals("Success")) {
                JSONObject j = jObject.getJSONObject("UserDetail");
                // JSONArray j = new JSONArray(total);
                Log.d("jsonarray", "" + j);
                Log.d("URL1", "" + j);
                for (int i = 0; i < j.length(); i++) {

                    //JSONObject Obj;
                    // Obj = j.getJSONObject(i);
                    // JSONArray jarr = Obj.getJSONArray("images");
                    logingetset temp = new logingetset();

                    temp.setId(j.getString("id"));
                    temp.setUsername(j.getString("username"));
                    temp.setEmail(j.getString("email"));
                    temp.setImage(j.getString("image"));


                    login.add(temp);

                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (NullPointerException e) {
            // TODO: handle exception
            error = e.getMessage();
        }
    }

    public class PostDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
            progressDialog = new ProgressDialog(Registernew.this);
            progressDialog.setMessage(getString(R.string.load_text));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            postdata();
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                new getlogin().execute();
            }

        }
    }

    public class getlogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getdetailforNearMe();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //  if (progressDialog.isShowing()) {
            //  progressDialog.dismiss();

            if (status.equals("Success")) {
                SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                editor.putString("userid", "" + login.get(0).getId());
                editor.putString("username", "" + login.get(0).getUsername());
                editor.putString("email", "" + login.get(0).getEmail());
                editor.putString("picture", "" + login.get(0).getImage());
                editor.apply();
                Intent iv = new Intent(Registernew.this, Home.class);
                startActivity(iv);
            } else if (status.equals("Failed")) {
                Toast.makeText(Registernew.this, "Username or password is incorrect", Toast.LENGTH_LONG).show();
            }
        }

        // }


    }

}
