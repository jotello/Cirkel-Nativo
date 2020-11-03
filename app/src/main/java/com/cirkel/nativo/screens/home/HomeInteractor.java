package com.cirkel.nativo.screens.home;

import android.location.Location;
import androidx.annotation.NonNull;
import com.cirkel.nativo.common.Constants;
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

    private HomeContract.Listener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String userId;

    public HomeInteractor(HomeContract.Listener mListener) {
        this.mListener = mListener;
        this.mDatabase = getDatabaseReference();
        this.mAuth = getAuthInstance();
    }

    @Override
    public void performSendAlert(String address, Location location) {
        userId = mAuth.getCurrentUser().getUid();
        String url = "users/" + userId + "/alerts";
        String mAlertKey = mDatabase.child(url).push().getKey();
        Alert alert = new Alert(address, location, Constants.ACTIVE_STATE);
        Map<String, Object> postValues = alert.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(url + "/" + mAlertKey, postValues);
        mDatabase.updateChildren(childUpdates)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mListener.onSuccess_sendAlert(mAlertKey);
                } else {
                    mListener.onError_sendAlert();
                }
            }
        });
    }

    @Override
    public void performEditAlert(String alertId, Alert alert) {
        userId = mAuth.getCurrentUser().getUid();
        String url = "users/" + userId + "/alerts/" + alertId;
        Map<String, Object> postValues = alert.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(url, postValues);
        mDatabase.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mListener.onSuccess_editAlert();
                        } else {
                            mListener.onError_editAlert();
                        }
                    }
                });
    }

    @Override
    public void performEndAlert(String alertId, Alert alert) {
        userId = mAuth.getCurrentUser().getUid();
        String url = "users/" + userId + "/alerts/" + alertId;
        Map<String, Object> postValues = alert.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(url, postValues);
        mDatabase.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mListener.onSuccess_endAlert();
                        } else {
                            mListener.onError_endAlert();
                        }
                    }
                });
    }

    @Override
    public void performLogout() {
        try {
            mAuth.signOut();
            mListener.onSuccess_logout();
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError_logout();
        }
    }
}
