package com.cirkel.nativo.screens.home;

import android.location.Location;

import com.cirkel.nativo.models.Alert;

public interface HomeContract {

    interface View {
        void displayError(String error);
        void displayLoader(boolean loader);
        void setEnabledView(boolean state);
        void setEnabledView_afterInitAlert(String alertId);
        void setEnabledView_afterEndAlert();
        void onNavigateLogin();
    }

    interface Presenter {
        void attempSendAlert(String address, Location location);
        void attempEditAlert(String alertId, Alert alert);
        void attempEndAlert(String alertId, Alert alert);
        void onDestroy();
        void attempLogout();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performSendAlert(String address, Location location);
        void performEditAlert(String alertId, Alert alert);
        void performEndAlert(String alertId, Alert alert);
        void performLogout();
    }

    interface Listener {
        void onSuccess_sendAlert(String alertId);
        void onError_sendAlert();
        void onSuccess_editAlert();
        void onError_editAlert();
        void onSuccess_endAlert();
        void onError_endAlert();
        void onSuccess_logout();
        void onError_logout();
    }

}
