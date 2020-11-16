package com.cirkel.nativo.screens.home;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.models.Alert;
import com.cirkel.nativo.screens.contacts.ContactsActivity;
import com.cirkel.nativo.screens.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends Activity
        implements HomeContract.View, LocationListener,
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener {

    // region fields
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_alert) Button btnAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_cancel_alert) Button btnCancelAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_finish_alert) Button btnFinishAlert;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader_home) ProgressBar loaderHome;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_navigation_home)
    public BottomNavigationView bottomNavigationView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar_home)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.drawer_layout_home)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    TextView navHeaderUsername;
    TextView navHeaderEmail;
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
        setVariables();
        runtimePermissions();
        checkActiveAlert();
        checkFirebaseUser();
    }

    @SuppressLint("CommitPrefEdits")
    private void setVariables() {
        mPresenter = new HomePresenter(this);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this,
                R.color.menuBarBackground));
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        break;
                    case R.id.action_contacts:
                        Core.newActivity(HomeActivity.this,ContactsActivity.class);
                        break;
                }
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        navHeaderUsername = headerView.findViewById(R.id.nav_header_username);
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_alert)
    void onClick_getLocation(View view) {
        Log.i("INIT_ALERT", String.valueOf(Calendar.getInstance().getTime()));
        getLocation();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_cancel_alert)
    void onClick_cancelAlert(View view){
        removeLocationUpdates();
        mPresenter.attempEndAlert(alertId, new Alert(lastAddress, lastLocation, "canceled"));
    }

    @SuppressLint("NonConstantResourceId")
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
        btnCancelAlert.setEnabled(enable);
        btnFinishAlert.setEnabled(enable);
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
            navHeaderUsername.setText(user.getDisplayName());
            navHeaderEmail.setText(user.getEmail());
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

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mPresenter.attempLogout();
                setAlertInSharedPref("");
                onNavigateLogin();
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}