package com.cirkel.nativo.screens.login;

import androidx.annotation.NonNull;

import com.cirkel.nativo.common.CompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;

public class LoginInteractor implements LoginContract.Interactor {

    private CompleteListener mListener;
    private FirebaseAuth mAuth;

    public LoginInteractor(CompleteListener listener) {
        this.mListener = listener;
        this.mAuth = getAuthInstance();
    }

    @Override
    public void performLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            mListener.onSuccess();
                        } else {
                            mListener.onError();
                        }
                    }
                });
    }
}
