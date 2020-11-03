package com.cirkel.nativo.screens.home;

import android.location.Location;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.models.Alert;

public class HomePresenter implements HomeContract.Presenter,
        HomeContract.Listener {

    private HomeContract.View mView;
    private HomeContract.Interactor mInteractor;

    public HomePresenter(HomeContract.View view) {
        this.mView = view;
        this.mInteractor = new HomeInteractor(this);
    }

    @Override
    public void attempSendAlert(String address, Location location) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performSendAlert(address, location);
    }

    @Override
    public void attempEditAlert(String alertId, Alert alert) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performEditAlert(alertId, alert);
    }

    @Override
    public void attempEndAlert(String alertId, Alert alert) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performEndAlert(alertId, alert);
    }

    @Override
    public void attempLogout() {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performLogout();
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onSuccess_sendAlert(String alertId) {
        if(mView != null) {
            setLoader(false);
            mView.setEnabledView_afterInitAlert(alertId);
        }
    }

    @Override
    public void onError_sendAlert() {
        if(mView != null) {
            setLoader(false);
            mView.displayError(BaseError.SEND_ALERT_GENERAL_ERROR);
        }
    }

    @Override
    public void onSuccess_editAlert() {
        if(mView != null) {
            setLoader(false);
        }
    }

    @Override
    public void onError_editAlert() {
        if(mView != null) {
            setLoader(false);
            mView.displayError(BaseError.EDIT_ALERT_GENERAL_ERROR);
        }
    }

    @Override
    public void onSuccess_endAlert() {
        if(mView != null) {
            setLoader(false);
            mView.setEnabledView_afterEndAlert();
        }
    }

    @Override
    public void onError_endAlert() {
        if(mView != null) {
            setLoader(false);
            mView.displayError(BaseError.END_ALERT_GENERAL_ERROR);
        }
    }

    @Override
    public void onSuccess_logout() {
        if(mView != null) {
            setLoader(false);
        }
    }

    @Override
    public void onError_logout() {
        if(mView != null) {
            setLoader(false);
            mView.displayError(BaseError.LOGOUT_GENERAL_ERROR);
        }
    }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }
}
