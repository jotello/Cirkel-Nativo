package com.cirkel.nativo.screens.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.models.Alert;
import com.cirkel.nativo.screens.contacts.ContactsActivity;
import com.cirkel.nativo.screens.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements HomeContract.View, LocationListener {

    // region fields
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_alert) Button btnAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_cancel_alert) Button btnCancelAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_finish_alert) Button btnFinishAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_contacts) Button btnContacts;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_logout) Button btnLogout;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader_home) ProgressBar loaderHome;
    // endregion fields

    // region variables
    private HomeContract.Presenter mPresenter;
    LocationManager locationManager;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String alertId;
    String lastAddress;
    Location lastLocation;
    // endregion variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        checkFirebaseUser();
        init_variables();
        runtimePermissions();
        checkActiveAlert();
    }

    @OnClick(R.id.btn_alert)
    void onClick_getLocation(View view) {
        getLocation();
    }

    @OnClick(R.id.btn_contacts)
    void onClick_goToContacts(View view) { Core.newActivity(this,ContactsActivity.class); }

    @OnClick(R.id.btn_logout)
    void onClick_Logout(View view) {
        mPresenter.attempLogout();
        onNavigateLogin();
    }

    @OnClick(R.id.btn_cancel_alert)
    void onClick_cancelAlert(View view){
        removeLocationUpdates();
        mPresenter.attempEndAlert(alertId, new Alert(lastAddress, lastLocation, "canceled"));
    }

    @OnClick(R.id.btn_finish_alert)
    void onClick_finishAlert(View view){
        removeLocationUpdates();
        mPresenter.attempEndAlert(alertId, new Alert(lastAddress, lastLocation, "finished"));
    }

    @Override
    public void displayError(String error) {
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
    public void setEnabledView_afterInitAlert(String alertIdReturned) {
        stateOfActiveAlert_inView(true);
        alertId = alertIdReturned;
        setAlertInSharedPref(alertId);
    }

    @Override
    public void setEnabledView_afterEndAlert() {
        stateOfActiveAlert_inView(false);
        setAlertInSharedPref("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onNavigateLogin() {
        Core.newActivity(this, LoginActivity.class);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),1);
            lastAddress = addresses.get(0).getAddressLine(0);
            lastLocation = location;
            boolean alertExists = sharedPref.getBoolean(getString(R.string.active_alert_exists), false);
            if(alertExists) {
                String alertId = sharedPref.getString(getString(R.string.active_alert_id), "");
                mPresenter.attempEditAlert(alertId, new Alert(lastAddress, location, "active"));
            } else {
                mPresenter.attempSendAlert(lastAddress, location);
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.i("ERROR", String.valueOf(e));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,1, this);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR_GET_LOCATION", String.valueOf(e));
        }

    }

    private void checkFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            //TODO: Agregar datos del usuario en menu
            Log.i("USER_ID", user.getUid());
            Log.i("USER_EMAIL", user.getEmail());
        } else {
            onNavigateLogin();
        }
    }

    private void checkActiveAlert() {
        boolean alertExists = sharedPref.getBoolean(getString(R.string.active_alert_exists), false);
        if (alertExists) {
            stateOfActiveAlert_inView(true);
            alertId = sharedPref.getString(getString(R.string.active_alert_id), "");
        } else {
            stateOfActiveAlert_inView(false);
            alertId = "";
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void init_variables() {
        mPresenter = new HomePresenter(this);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    private void runtimePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }
    }

    private void setAlertInSharedPref(String alertId) {
        editor.putString(getString(R.string.active_alert_id), alertId);
        editor.putBoolean(getString(R.string.active_alert_exists), !alertId.equals(""));
        editor.commit();
        if (alertId.equals("")) {
            lastAddress = "";
            lastLocation = null;
        }
    }

    public void stateOfActiveAlert_inView(boolean state) {
        btnAlert.setEnabled(!state);
        btnAlert.setVisibility(state ? View.GONE : View.VISIBLE);
        btnCancelAlert.setEnabled(state);
        btnCancelAlert.setVisibility(state ? View.VISIBLE : View.GONE);
        btnFinishAlert.setEnabled(state);
        btnFinishAlert.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void removeLocationUpdates() {
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }
}