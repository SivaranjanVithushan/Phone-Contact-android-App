package com.example.contactlist_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import android.util.Patterns;
import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {

    private List<Contact> contactList;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Contacts");

        contactList = new ArrayList<>();
        initializeSampleContacts();

        // Sort the contactList in alphabetical order based on contact names
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                return contact1.getName().compareToIgnoreCase(contact2.getName());
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactAdapter(contactList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactDialog();
            }
        });

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase(Locale.getDefault());
                adapter.filterList(query);
            }
        });
    }


    @Override
    public void onContactClick(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_options, null);
        builder.setView(dialogView);

//        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        Button viewDetailsButton = dialogView.findViewById(R.id.viewDetailsButton);
        Button editContactButton = dialogView.findViewById(R.id.editContactButton);
        Button deleteContactButton = dialogView.findViewById(R.id.deleteContactButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        final AlertDialog dialog = builder.create();

        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactDetailsDialog(contact);
                dialog.dismiss();
            }
        });

        editContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditContactDialog(contact);
                dialog.dismiss();
            }
        });

        deleteContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(contact);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showContactDetailsDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Details");
        View view = getLayoutInflater().inflate(R.layout.dialog_contact_details, null);
        builder.setView(view);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView phoneTextView = view.findViewById(R.id.phoneTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        TextView addressTextView = view.findViewById(R.id.addressTextView);
        TextView birthdayTextView = view.findViewById(R.id.birthdayTextView);

        nameTextView.setText(contact.getName());
        phoneTextView.setText(contact.getPhoneNumber());
        emailTextView.setText(contact.getEmail());
        addressTextView.setText(contact.getAddress());
        birthdayTextView.setText(contact.getBirthday());

        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void showEditContactDialog(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Edit Contact");
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_contact, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText phoneEditText = view.findViewById(R.id.phoneEditText);
        final EditText emailEditText = view.findViewById(R.id.emailEditText);
        final EditText addressEditText = view.findViewById(R.id.addressEditText);
        final TextView birthdayEditText = view.findViewById(R.id.birthdayEditText);

        nameEditText.setText(contact.getName());
        phoneEditText.setText(contact.getPhoneNumber());
        emailEditText.setText(contact.getEmail());
        addressEditText.setText(contact.getAddress());
        birthdayEditText.setText(contact.getBirthday());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String editedName = nameEditText.getText().toString().trim();
                String editedPhone = phoneEditText.getText().toString().trim();
                String editedEmail = emailEditText.getText().toString().trim();
                String editedAddress = addressEditText.getText().toString().trim();
                String editedBirthday = birthdayEditText.getText().toString().trim();

                if (editedName.isEmpty() || editedPhone.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Name and Phone Number are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                contact.setName(editedName);
                contact.setPhoneNumber(editedPhone);
                contact.setEmail(editedEmail);
                contact.setAddress(editedAddress);
                contact.setBirthday(editedBirthday);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Contact updated!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteContact(final Contact contact) {
        // Show a confirmation dialog before deleting the contact
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = contactList.indexOf(contact);
                if (position != -1) {
                    adapter.removeContact(position);
                    Toast.makeText(MainActivity.this, "Successfully Contact deleted!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void initializeSampleContacts() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        contactList.add(new Contact("Alicia Johnson", "0772654321", "alicia.johnson@example.com", "15, Apple Street, Colombo", "1992-03-15"));
        contactList.add(new Contact("Ben Kingsley", "0712233445", "ben.kingsley@example.com", "22, Queen's Road, Kandy", "1988-11-05"));
        contactList.add(new Contact("Carla Mendes", "0778765432", "carla.mendes@example.com", "33, Ocean Drive, Galle", "1991-07-21"));
        contactList.add(new Contact("David Tan", "+65 9123 4567", "david.tan@example.com", "10, Orchard Road, Singapore", "1985-02-14"));
        contactList.add(new Contact("Emma Watson", "+1 987-123-4567", "emma.watson@example.com", "456 Elm St, Los Angeles", "1989-04-24"));
        contactList.add(new Contact("Felipe Oliveira", "+55 21 98765-4321", "felipe.oliveira@example.com", "789 Copacabana, Rio de Janeiro", "1978-05-19"));
        contactList.add(new Contact("Grace Kim", "+82 10-9876-5432", "grace.kim@example.com", "23, Gangnam-daero, Seoul", "1995-12-05"));
        contactList.add(new Contact("Henry Park", "+82 10-8765-4321", "henry.park@example.com", "34, Itaewon-ro, Seoul", "1982-06-22"));
        contactList.add(new Contact("Isabella Garcia", "+34 678 123 456", "isabella.garcia@example.com", "56 Gran Via, Madrid", "1990-08-13"));
        contactList.add(new Contact("Jack Miller", "+61 412 345 678", "jack.miller@example.com", "78 Pitt Street, Sydney", "1980-10-10"));
        contactList.add(new Contact("Katherine Lee", "+852 6123 4567", "katherine.lee@example.com", "89 Nathan Road, Hong Kong", "1983-03-07"));
        contactList.add(new Contact("Liam Wong", "+65 8123 4567", "liam.wong@example.com", "45, Bukit Timah Road, Singapore", "1997-01-16"));
        contactList.add(new Contact("Maria Hernandez", "+34 612 345 678", "maria.hernandez@example.com", "101 La Rambla, Barcelona", "1984-07-25"));
        contactList.add(new Contact("Nina Patel", "+91 98765 43210", "nina.patel@example.com", "12, MG Road, Mumbai", "1993-05-12"));
        contactList.add(new Contact("Oscar Chang", "+86 139 1234 5678", "oscar.chang@example.com", "67 Nanjing Road, Shanghai", "1979-09-30"));
        contactList.add(new Contact("Priya Singh", "+91 91234 56789", "priya.singh@example.com", "89 Connaught Place, Delhi", "1987-04-08"));
        contactList.add(new Contact("Quentin Dupont", "+33 1 23 45 67 89", "quentin.dupont@example.com", "23 Champs-Élysées, Paris", "1985-11-27"));
        contactList.add(new Contact("Rachel Green", "+44 7700 900123", "rachel.green@example.com", "22 Baker Street, London", "1992-08-18"));
        contactList.add(new Contact("Sam Brown", "+1 800-555-1234", "sam.brown@example.com", "123 Maple Street, Chicago", "1981-02-22"));
        contactList.add(new Contact("Tina Rossi", "+39 06 12345678", "tina.rossi@example.com", "45 Via Veneto, Rome", "1976-09-10"));
        contactList.add(new Contact("Umar Al-Mutairi", "+971 55 123 4567", "umar.al-mutairi@example.com", "56 Sheikh Zayed Road, Dubai", "1986-06-30"));
        contactList.add(new Contact("Victor Nguyen", "+84 912 345 678", "victor.nguyen@example.com", "78 Le Duan, Hanoi", "1989-01-15"));
        contactList.add(new Contact("Wendy Zhang", "+86 138 9876 5432", "wendy.zhang@example.com", "23 Zhongshan Road, Beijing", "1982-12-21"));
        contactList.add(new Contact("Xavier Lopez", "+34 687 543 210", "xavier.lopez@example.com", "12 Gran Via, Madrid", "1977-04-09"));
        contactList.add(new Contact("Yuki Nakamura", "+81 90-1234-5678", "yuki.nakamura@example.com", "5-1 Ginza, Tokyo", "1983-11-18"));
        contactList.add(new Contact("Zara Hussain", "+92 301 1234567", "zara.hussain@example.com", "67 Mall Road, Lahore", "1988-07-14"));

    }

    private void showAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Contact");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        builder.setView(view);

        final EditText nameEditText = view.findViewById(R.id.nameEditText);
        final EditText phoneEditText = view.findViewById(R.id.phoneEditText);
        final EditText emailEditText = view.findViewById(R.id.emailEditText);
        final EditText addressEditText = view.findViewById(R.id.addressEditText);
        final EditText birthdayEditText = view.findViewById(R.id.birthdayEditText);


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String birthday = birthdayEditText.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Name and Phone Number are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.PHONE.matcher(phone).matches()) {
                    phoneEditText.setError("Invalid phone number.");
                    return;
                }

                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email address.");
                    return;
                }

                Contact newContact = new Contact(name, phone, email, address, birthday);
                adapter.addContact(newContact);
                Toast.makeText(MainActivity.this, "Contact added!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        // Show an error message if the input data is not valid when the dialog is dismissed.
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                boolean isNameEmpty = name.isEmpty();
                boolean isPhoneInvalid = !Patterns.PHONE.matcher(phone).matches();
                boolean isEmailInvalid = !email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches();

                if (isNameEmpty || isPhoneInvalid || isEmailInvalid) {
                    Toast.makeText(MainActivity.this, "Invalid input data. Contact not added.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}



