package com.cirkel.nativo.screens.contacts;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cirkel.nativo.models.Contact;
import com.cirkel.nativo.network.responses.ContactResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;
import static com.cirkel.nativo.network.FirebaseNetwork.getDatabaseReference;

public class ContactsInteractor implements ContactsContract.Interactor {

    private ContactsContract.onGetDataListener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public ContactsInteractor(ContactsContract.onGetDataListener mListener) {
        this.mListener = mListener;
        this.mDatabase = getDatabaseReference();
        this.mAuth = getAuthInstance();
    }

    @Override
    public void performGetContacts() {
        String userId = mAuth.getCurrentUser().getUid();
        Query contactsQuery = mDatabase.child("users").child(userId).child("contacts");
        contactsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Contact> contacts = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ContactResponse contactResponse = postSnapshot.getValue(ContactResponse.class);
                    contacts.add(new Contact(contactResponse.getEmail(),
                            contactResponse.getName(), postSnapshot.getKey()));
                }
                mListener.onSuccess(contacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.v("Error", databaseError.getMessage());
                mListener.onFailure(databaseError.getMessage());
            }
        });

    }
}
