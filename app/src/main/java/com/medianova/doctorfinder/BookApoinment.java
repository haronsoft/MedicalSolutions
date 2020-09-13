package com.medianova.doctorfinder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.medianova.payment.MpesaPayment;
import com.medianova.utils.AlarmReceiver;
import com.medianova.utils.bookapointmentgetset;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import static com.medianova.doctorfinder.Aboutus.setTypefaces;

public class BookApoinment extends Activity {
    private static final String MyPREFERENCES = "DoctorPrefrance";
    String TAG;
    private Button btn_date;
    private Button btn_time;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int hour;
    private int minute;
    private long time;
    private long date;
    private String call;
    private EditText et_description;
    private EditText et_call;
    private String coment;
    private String timepick;
    private String datepick;
    private String timestamppick;
    private String doctoremail;
    private Calendar c = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_apoinment);
        Intent iv = getIntent();


        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String image = prefs.getString("picture", null);
        final String uname = prefs.getString("username", null);
        final String email = prefs.getString("email", null);
        TextView header = findViewById(R.id.txt_appoint);
//        ((TextView)findViewById(R.id.note1)).setTypeface(TF_ralewayRegular);
//        ((TextView)findViewById(R.id.note2)).setTypeface(TF_ralewayRegular);
//        ((TextView)findViewById(R.id.note3)).setTypeface(TF_ralewayRegular);
//        ((TextView)findViewById(R.id.note4)).setTypeface(TF_ralewayRegular);
//        header.setTypeface(TF_ralewayRegular);
        TextView username = findViewById(R.id.uname);
        username.setText(uname);
        TextView emailid = findViewById(R.id.email);
        emailid.setText(email);
        ImageView img = findViewById(R.id.circle);
//        ImageLoader imgLoader = new ImageLoader(BookApoinment.this);
//
//        imgLoader.DisplayImage(image, img);
        Picasso.with(this).load(image).into(img);
        String fulldetail_id = iv.getStringExtra("doctor_id");
        String user_id = iv.getStringExtra("userid");
        doctoremail = iv.getStringExtra("doctor_email");
//        Log.d("FUllDETAIL_id123",fulldetail_id);
        ArrayList<bookapointmentgetset> apoinmentlist = new ArrayList<>();
        btn_date = findViewById(R.id.btn_date);
        btn_time = findViewById(R.id.btn_time);
        Button btn_bookapointment = findViewById(R.id.btn_bookapointment);
        et_description = findViewById(R.id.et_description);
        et_call = findViewById(R.id.et_call);

        ArrayList<TextView> text_tf = new ArrayList<>();
        text_tf.add((TextView) findViewById(R.id.note1));
        text_tf.add((TextView) findViewById(R.id.note2));
        text_tf.add((TextView) findViewById(R.id.note3));
        text_tf.add((TextView) findViewById(R.id.note4));
        text_tf.add(username);
        text_tf.add(emailid);
        text_tf.add(header);
        setTypefaces(text_tf, null);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                date = c.getTimeInMillis() / 1000L;
                Log.e("resultdate", "" + date);
                System.out.println("the selected " + mDay);
                DatePickerDialog dialog = new DatePickerDialog(BookApoinment.this,
                        new mDateSetListener(), mYear, mMonth,
                        mDay);
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calset = (Calendar) mcurrentTime.clone();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(BookApoinment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        btn_time.setText(selectedHour + ":" + selectedMinute);
                        Log.e("currenttime", "" + btn_time.getText().toString());
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR, hour);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.MILLISECOND, 0);
                        time = c.getTimeInMillis() / 1000L;
                        Log.e("timepickertimestemp", "" + time);
                    }
                }, hour, minute, true);// Yes 24 hour time
                mTimePicker.setTitle("Select  Time");
                mTimePicker.show();
            }
        });
        timestamp(mYear, mMonth, mDay, hour, minute);

        btn_bookapointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(BookApoinment.this, "Make Payment Here", Toast.LENGTH_SHORT).show();
                Intent paymentIntent = new Intent(BookApoinment.this, MpesaPayment.class);
                startActivity(paymentIntent);
                finish();

                c = Calendar.getInstance();
                c.set(Calendar.MONTH, mMonth);
                c.set(Calendar.YEAR, mYear);
                c.set(Calendar.DAY_OF_MONTH, mDay);

                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                final Long result1 = c.getTimeInMillis() / 1000L;
                Log.e("result123", "" + result1);
                coment = et_description.getText().toString();
                call = et_call.getText().toString();
                Log.d("callclick", "" + call);
                timepick = btn_time.getText().toString();
                datepick = btn_date.getText().toString();
                timestamppick = result1.toString();
                Log.e("hellotime", timepick);
                Log.e("allparameter", "" + coment + call + timepick + datepick + timestamppick);


                if (datepick != null && !datepick.isEmpty()) {

                    if (timepick != null && !timepick.isEmpty()) {
                        if (call != null && !call.isEmpty()) {

                            if (coment != null && !coment.isEmpty()) {

                                try {
                                    String txt_detail = getString(R.string.aph1) + "\n" + getString(R.string.aph2) + uname + "\n" + getString(R.string.aph3) + email + "\n" + getString(R.string.aph4) + call + "\n" + getString(R.string.aph5) + datepick + "\n" + getString(R.string.aph6) + timepick + "\n" + getString(R.string.ap7) + coment + "\n" + getString(R.string.ap8) + "\n" + getString(R.string.ap9);
                                    Intent gmail = new Intent(Intent.ACTION_SEND);
                                    gmail.putExtra(Intent.EXTRA_EMAIL, new String[]{doctoremail});
                                    gmail.setData(Uri.parse("mailto:"));
                                    gmail.putExtra(Intent.EXTRA_SUBJECT, "Appointment Details");
                                    gmail.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    gmail.setType("text/plain");
                                    gmail.putExtra(Intent.EXTRA_TEXT, txt_detail);
                                    if (gmail.resolveActivity(getPackageManager()) != null) {
                                        startActivity(gmail);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendEmail();
                                }
                                long ONE_MINUTE_IN_MILLIS = 60000;

                                long t = c.getTimeInMillis();
                                long minus15min = t - (15 * ONE_MINUTE_IN_MILLIS);
                                if (hour > 12) {
                                    c.set(Calendar.AM_PM, Calendar.PM);
                                } else {
                                    c.set(Calendar.AM_PM, Calendar.AM);
                                }
                                Intent myIntent1 = new Intent(BookApoinment.this, AlarmReceiver.class);

                                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(BookApoinment.this, 0, myIntent1,
                                        0);

                                AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
                                alarmManager1.set(AlarmManager.RTC, minus15min, pendingIntent1);

                            } else {
                                Toast.makeText(BookApoinment.this, "Enter Description", Toast.LENGTH_LONG).show();

                            }
                        } else {
                            Toast.makeText(BookApoinment.this, "Enter Contact Number", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(BookApoinment.this, "Enter Time", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BookApoinment.this, "Enter Date", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private int timestamp(int mYear, int mMonth, int mDay, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, mYear);
        c.set(Calendar.MONTH, mMonth);
        c.set(Calendar.DAY_OF_MONTH, mDay);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Log.d("time123456", "" + (c.getTimeInMillis() / 1000L));
        return (int) (c.getTimeInMillis() / 1000L);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        // TODO Auto-generated method stub
        String recipient = doctoremail;
        String subject = "Book Appointment";
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

            Toast.makeText(BookApoinment.this, "There is no email client exists", Toast.LENGTH_LONG).show();

        }
    }

    // date picker class
    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub

            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, mYear);
            c.set(Calendar.MONTH, mMonth);
            c.set(Calendar.DAY_OF_MONTH, mDay);
            Long date = c.getTimeInMillis() / 1000L;
            Log.e("resultdatepicker", "" + date.toString());
            /*
             * btn_date.setText(new StringBuilder() // Month is 0 based so add 1
             * .append(mMonth + 1).append("/").append(mDay).append("/")
             * .append(mYear).append(" "));
             */

            btn_date.setText(new StringBuilder().append(mDay).append("/").append(mMonth + 1).append("/").append(mYear));

            Log.e("btncurrentdate", "" + btn_date.getText().toString());


        }
    }


}
