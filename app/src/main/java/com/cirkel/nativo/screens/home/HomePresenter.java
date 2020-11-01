package com.cirkel.nativo.screens.home;

import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.CompleteListener;

public class HomePresenter implements HomeContract.Presenter,
        CompleteListener {

    private HomeContract.View mView;
    private HomeContract.Interactor mInteractor;

    public HomePresenter(HomeContract.View view) {
        this.mView = view;
        this.mInteractor = new HomeInteractor(this);
    }

    @Override
    public void attempSendAlert() {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performSendAlert();
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void attempLogout() {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performLogout();
    }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }

    @Override
    public void onSuccess() {
        if(mView != null) {
            setLoader(false);
            //TODO: Implementar un metodo para cancelar alerta
            //TODO: Implementar un metodo para finalizar alerta
        }
    }

    @Override
    public void onError() {
        if(mView != null) {
            setLoader(false);
            mView.displayAlertError(BaseError.SEND_ALERT_GENERAL_ERROR);
        }
    }
}
