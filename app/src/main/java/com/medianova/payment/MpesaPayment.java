package com.medianova.payment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.freddygenicho.mpesa.stkpush.Mode;
import com.freddygenicho.mpesa.stkpush.api.response.STKPushResponse;
import com.freddygenicho.mpesa.stkpush.interfaces.STKListener;
import com.freddygenicho.mpesa.stkpush.interfaces.TokenListener;
import com.freddygenicho.mpesa.stkpush.model.Mpesa;
import com.freddygenicho.mpesa.stkpush.model.STKPush;
import com.freddygenicho.mpesa.stkpush.model.Token;
import com.medianova.doctorfinder.R;

import java.io.UnsupportedEncodingException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MpesaPayment extends AppCompatActivity implements TokenListener {
    private Mpesa mpesa;
    String TAG;
    private EditText phoneET, amountET;
     private SweetAlertDialog sweetAlertDialog;
    private String phone_number;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_payment);
        //        MPESA INITIALIZATION
        mpesa = new Mpesa(Config.CONSUMER_KEY, Config.CONSUMER_SECRET, Mode.SANDBOX);
        phoneET = findViewById(R.id.phoneET);
        amountET = findViewById(R.id.amountET);
    }

    public void startMpesa(View view) {

        phone_number = phoneET.getText().toString();
        amount = amountET.getText().toString();

        if (phone_number.isEmpty()) {
            Toast.makeText(MpesaPayment.this, "Phone Number is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount.isEmpty()) {
            Toast.makeText(MpesaPayment.this, "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone_number.isEmpty() && !amount.isEmpty()) {
            try {
               // sweetAlertDialog.show();
                mpesa.getToken(this);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UnsupportedEncodingException: " + e.getLocalizedMessage());
            }
        } else {
            Toast.makeText(MpesaPayment.this,
                    "Please make sure that phone number and amount is not empty ",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onToken(Token token) {
        STKPush stkPush = new STKPush();
        stkPush.setBusinessShortCode(Config.BUSINESS_SHORT_CODE);
        stkPush.setPassword(STKPush.getPassword(Config.BUSINESS_SHORT_CODE, Config.PASSKEY, STKPush.getTimestamp()));
        stkPush.setTimestamp(STKPush.getTimestamp());
        stkPush.setTransactionType(Transaction.CUSTOMER_PAY_BILL_ONLINE);
        stkPush.setAmount(amount);
        stkPush.setPartyA(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setPartyB(Config.PARTYB);
        stkPush.setPhoneNumber(STKPush.sanitizePhoneNumber(phone_number));
        stkPush.setCallBackURL(Config.CALLBACKURL);
        stkPush.setAccountReference("HaronPayAid");
        stkPush.setTransactionDesc("some description");

        mpesa.startStkPush(token, stkPush, new STKListener() {
            @Override
            public void onResponse(STKPushResponse stkPushResponse) {
                Log.e(TAG, "onResponse: " + stkPushResponse.toJson(stkPushResponse));
                String message = "Please enter your pin to complete transaction";
                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                sweetAlertDialog.setTitleText("Transaction started");
                sweetAlertDialog.setContentText(message);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "stk onError: " + throwable.getMessage());
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText("Error");
                sweetAlertDialog.setContentText(throwable.getMessage());
            }
        });
    }

    @Override
    public void OnError(Throwable throwable) {
        Log.e(TAG, "mpesa Error: " + throwable.getMessage());
        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("Error");
        sweetAlertDialog.setContentText(throwable.getMessage());
    }
}
