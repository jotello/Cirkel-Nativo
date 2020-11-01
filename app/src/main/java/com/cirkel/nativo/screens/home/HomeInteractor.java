package com.cirkel.nativo.screens.home;

import androidx.annotation.NonNull;

import com.cirkel.nativo.common.CompleteListener;
import com.cirkel.nativo.models.Alert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;
import static com.cirkel.nativo.network.FirebaseNetwork.getDatabaseReference;

public class HomeInteractor implements HomeContract.Interactor {

    private CompleteListener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public HomeInteractor(CompleteListener mListener) {
        this.mListener = mListener;
        this.mDatabase = getDatabaseReference();
        this.mAuth = getAuthInstance();
    }

    @Override
    public void performSendAlert() {
        //TODO : Agregar ubicacion y usuario que envia la alerta
        String mAlertKey = mDatabase.child("alerts").push().getKey();
        Alert alert = new Alert("userIdTest", "locationTest");
        Map<String, Object> postValues = alert.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("alerts/" + mAlertKey, postValues);
        mDatabase.updateChildren(childUpdates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mListener.onSuccess();
                } else {
                    mListener.onError();
                }
            }
        });
    }

    @Override
    public void performLogout() {
        try {
            mAuth.signOut();
            mListener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError();
        }
    }
}
