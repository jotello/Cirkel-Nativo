package com.cirkel.nativo.screens.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.screens.contacts.ContactsActivity;
import com.cirkel.nativo.screens.login.LoginActivity;
import com.cirkel.nativo.screens.newContact.NewContactActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements HomeContract.View {

    // region fields
    @BindView(R.id.btn_alert) Button btnAlert;
    @BindView(R.id.btn_contacts) Button btnContacts;
    @BindView(R.id.btn_logout) Button btnLogout;
    @BindView(R.id.loader_home) ProgressBar loaderHome;
    // endregion fields

    private HomeContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkFirebaseUser();
        ButterKnife.bind(this);
        mPresenter = new HomePresenter(this);
    }

    @OnClick(R.id.btn_alert)
    void onClick_SendAlert(View view) {
        mPresenter.attempSendAlert();
    }

    @OnClick(R.id.btn_contacts)
    void onClick_goToContacts(View view) { Core.newActivity(this,ContactsActivity.class); }

    @OnClick(R.id.btn_logout)
    void onClick_Logout(View view) {
        mPresenter.attempLogout();
        onNavigateLogin();
    }

    @Override
    public void displayAlertError(String error) {
        BaseError.showMessage( error, this);
    }

    @Override
    public void displayLoader(boolean loader) {
        int stateLoader = loader ? View.VISIBLE : View.GONE;
        loaderHome.setVisibility(stateLoader);
    }

    @Override
    public void setEnabledView(boolean enable) {
        btnAlert.setEnabled(enable);
        btnContacts.setEnabled(enable);
        btnLogout.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    private void checkFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            //TODO: Agregar datos del usuario en sharedPreferences y menu
            Log.i("USER_ID", user.getUid());
            Log.i("USER_EMAIL", user.getEmail());
        } else {
            onNavigateLogin();
        }
    }

    @Override
    public void onNavigateLogin() {
        Core.newActivity(this, LoginActivity.class);
        finish();
    }
}