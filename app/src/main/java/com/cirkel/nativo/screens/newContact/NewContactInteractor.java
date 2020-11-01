package com.cirkel.nativo.screens.newContact;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cirkel.nativo.common.CompleteListener;
import com.cirkel.nativo.models.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;
import static com.cirkel.nativo.network.FirebaseNetwork.getDatabaseReference;

public class NewContactInteractor implements
        NewContactContract.Interactor {

    private CompleteListener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public NewContactInteractor(CompleteListener mListener) {
        this.mListener = mListener;
        this.mAuth = getAuthInstance();
        this.mDatabase = getDatabaseReference();
    }

    @Override
    public void performCreateContact(Contact contact) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> postValues = contact.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        String url = "users/" + userId + "/contacts/" + contact.getPhoneNumber() + "/";
        childUpdates.put(url, postValues);
        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.i("ERROR", "Data could not be saved " + databaseError.getMessage());
                    mListener.onError();
                } else {
                    mListener.onSuccess();
                }
            }
        });
    }
}
