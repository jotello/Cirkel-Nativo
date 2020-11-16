package com.cirkel.nativo.screens.signup;

import androidx.annotation.NonNull;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;
import static com.cirkel.nativo.network.FirebaseNetwork.getAuthInstance;
import static com.cirkel.nativo.network.FirebaseNetwork.getDatabaseReference;

public class SignupInteractor implements SignupContract.Interactor {

    private SignupContract.Listener mListener;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public SignupInteractor(SignupContract.Listener listener) {
        this.mListener = listener;
        this.mAuth = getAuthInstance();
        this.mDatabase = getDatabaseReference();
    }

    @Override
    public void performSignup(String email, String password, String name, String cellphone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUserInDB(user, email, name, cellphone);
                            updateUserAuth(user, name);
                            mAuth.signOut();
                            mListener.onSuccess();
                        } else {
                            mListener.onError(BaseError.CREATE_USER_GENERAL_ERROR);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mListener.onError(e.getMessage());
                    }
                });
    }

    private void updateUserAuth(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileUpdates);
    }

    private void createUserInDB(FirebaseUser userFirebase, String email, String name, String cellphone) {
        User user = new User(email, name, cellphone);
        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userFirebase.getUid() + "/", postValues);
        mDatabase.updateChildren(childUpdates);
    }
}
