package com.example.alaap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class SendOtpActivity extends AppCompatActivity {

    private EditText phonenumber;
    private Button getotpbutton;
    private CountryCodePicker countryCodePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        phonenumber = findViewById(R.id.phoneno);
        countryCodePicker = findViewById(R.id.countrypicker);
        getotpbutton = findViewById(R.id.getotpbutton);

        String email,name,password;

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");


        getotpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!countryCodePicker.isValidFullNumber()){
                    phonenumber.setError("Invalid phone number");
                }

                else {

                    Intent intent = new Intent(SendOtpActivity.this, VerifyOTPActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
                    intent.putExtra("name", name);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            }
        });
    }
}