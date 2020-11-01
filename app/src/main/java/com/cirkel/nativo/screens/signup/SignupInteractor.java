package com.cirkel.nativo.screens.signup;

import androidx.annotation.NonNull;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
                            createUserInDB(email, name, cellphone);
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

    private void createUserInDB(String email, String name, String cellphone) {
        String userId = mAuth.getCurrentUser().getUid();
        User user = new User(email, name, cellphone);
        Map<String, Object> postValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + userId + "/", postValues);
        mDatabase.updateChildren(childUpdates);
        mAuth.signOut();
    }
}
