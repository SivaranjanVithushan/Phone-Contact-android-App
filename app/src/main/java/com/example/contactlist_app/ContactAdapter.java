package com.example.contactlist_app;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private List<Contact> filteredContactList;
    private OnContactClickListener clickListener;

    public ContactAdapter(List<Contact> contactList, OnContactClickListener clickListener) {
        this.contactList = contactList;
        this.filteredContactList = new ArrayList<>(contactList);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = filteredContactList.get(position);
        holder.nameTextView.setText(contact.getName());
        holder.phoneTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onContactClick(contact);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredContactList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterList(String query) {
        if (query.trim().isEmpty()) {
            filteredContactList = new ArrayList<>(contactList);
        } else {
            filteredContactList = contactList.stream()
                    .filter(contact -> contact.getName().toLowerCase(Locale.getDefault()).contains(query.toLowerCase(Locale.getDefault())))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void addContact(Contact contact) {
        contactList.add(contact);
        filteredContactList.add(contact);
        notifyDataSetChanged();
    }

    public void editContact(int position, Contact editedContact) {
        contactList.set(position, editedContact);
        filteredContactList.set(position, editedContact);
        notifyDataSetChanged();
    }

    public void removeContact(int position) {
        if (position >= 0 && position < contactList.size()){
            Contact removedContact = contactList.get(position);
            contactList.remove(removedContact);
            filteredContactList.remove(removedContact);

            notifyDataSetChanged();
        }
        // Find the corresponding position in the filtered list and remove it
//        contactList.remove(position);
//        filteredContactList.remove(position);

    }

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;


        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
        }
    }
}
