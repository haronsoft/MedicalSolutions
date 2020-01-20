package com.medianova.doctorfinder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.medianova.doctorfinder.Registernew.verifyStoragePermissions;

public class SendRecipe extends AppCompatActivity {
    private static final String MyPREFERENCES = "DoctorPrefrance";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_cam_IMAGE = 2;
    private EditText et_call;
    private TextView et_description;
    private String uname;
    private String emailfrom;
    private String emailto;
    private Uri shareimage;
    private String mobile_number;
    private ImageView order_img;
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_recipe);
        getSupportActionBar().hide();
        TextView username = findViewById(R.id.uname);
        TextView emailid = findViewById(R.id.email);
        et_description = findViewById(R.id.et_description);
        ImageView img = findViewById(R.id.circle);
        Button btn_submit = findViewById(R.id.close);
        et_call = findViewById(R.id.et_call);
        order_img = findViewById(R.id.order_img);
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        Intent i = getIntent();
        emailto = i.getStringExtra("doctoremail");
        String image = prefs.getString("picture", null);
        uname = prefs.getString("username", null);
        emailfrom = prefs.getString("email", null);
        username.setText(uname);
        emailid.setText(emailfrom);
        Picasso.with(this).load(image).into(img);

        LinearLayout ll_camera = findViewById(R.id.camera);
        LinearLayout ll_gallery = findViewById(R.id.gallery);
        ll_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(SendRecipe.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    verifyStoragePermissions(SendRecipe.this);
                } else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, RESULT_cam_IMAGE);
                }
            }
        });

        ll_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permission = ActivityCompat.checkSelfPermission(SendRecipe.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    verifyStoragePermissions(SendRecipe.this);
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobile_number = et_call.getText().toString();
                description = et_description.getText().toString();
                if (shareimage == null) {
                    Toast.makeText(SendRecipe.this, R.string.selectprescription_txt, Toast.LENGTH_SHORT).show();
                } else if (mobile_number.equals("")) {
                    et_call.setError(getString(R.string.mobile_num_text));
                } else if (description.equals("")) {
                    et_description.setError(getString(R.string.error_desc_text));
                } else {
                    try {
                        Intent gmail = new Intent(Intent.ACTION_SEND);
                        gmail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailto});
                        gmail.setData(Uri.parse(emailto));
                        gmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sendrecipe_txt));
                        gmail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        gmail.putExtra(Intent.EXTRA_STREAM, shareimage);
                        gmail.setType("text/plain");
                        gmail.setType("image/*");
                        gmail.putExtra(Intent.EXTRA_TEXT, getString(R.string.orderingmedicine) + "\n" + getString(R.string.email1) + uname + "\n" + getString(R.string.email2) + emailfrom + "\n" + getString(R.string.email3) + mobile_number + "\n" + "Description: " + description);
                        startActivity(gmail);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendEmail();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = data.getData();
            String picturepath;


            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

                String[] filePathColumn = {MediaStore.MediaColumns.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturepath = cursor.getString(columnIndex);
                cursor.close();
                File file = new File(picturepath);
                shareimage = FileProvider.getUriForFile(SendRecipe.this, BuildConfig.APPLICATION_ID + ".provider", file);
                Picasso.with(SendRecipe.this).load(shareimage).into(order_img);
                Toast.makeText(SendRecipe.this, R.string.selectionsuccess_text, Toast.LENGTH_SHORT).show();

            } else if (requestCode == RESULT_cam_IMAGE && resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                picturepath = String.valueOf(finalFile);
                File file = new File(picturepath);
//                shareimage = Uri.fromFile(file);
                shareimage = FileProvider.getUriForFile(SendRecipe.this, BuildConfig.APPLICATION_ID + ".provider", file);
                Picasso.with(SendRecipe.this).load(shareimage).into(order_img);
                Toast.makeText(SendRecipe.this, R.string.select_sucess_txt, Toast.LENGTH_SHORT).show();
            }

        } catch (NullPointerException e) {
            Toast.makeText(SendRecipe.this, R.string.later_txt, Toast.LENGTH_SHORT).show();

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

    private void sendEmail() {
        // TODO Auto-generated method stub
        String recipient = emailto;
        String subject = getString(R.string.inqu_txt);
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

            Toast.makeText(SendRecipe.this, R.string.noemailclient, Toast.LENGTH_LONG).show();

        }
    }

}
