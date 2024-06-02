package com.example.contactlist_app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // Retrieve data passed from MainActivity
        String contactName = getIntent().getStringExtra("contact_name");
        String contactPhone = getIntent().getStringExtra("contact_phone");
        String contactEmail = getIntent().getStringExtra("contact_email");
        String contactAddress = getIntent().getStringExtra("contact_address");
        String contactBirthday = getIntent().getStringExtra("contact_birthday");

        // Display contact details
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);
        TextView birthdayTextView = findViewById(R.id.birthdayTextView);

        nameTextView.setText(contactName);
        phoneTextView.setText(contactPhone);
        emailTextView.setText(contactEmail);
        addressTextView.setText(contactAddress);
        birthdayTextView.setText(contactBirthday);
    }
}
