package com.cirkel.nativo.screens.login;

import android.util.Patterns;

import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.CompleteListener;
import com.cirkel.nativo.common.Constants;

public class LoginPresenter implements LoginContract.Presenter, CompleteListener {

    private LoginContract.View mView;
    private LoginContract.Interactor mInteractor;

    public LoginPresenter(LoginContract.View view) {
        this.mView = view;
        this.mInteractor = new LoginInteractor(this);
    }

    @Override
    public void attemptLogin(String email, String password) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performLogin(email, password);
    }

    @Override
    public boolean isValidForm(String email, String password) {
        boolean isValid = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            mView.displayEmailError(BaseError.EMAIL_FORMAT_ERROR);
        } else if (password.length() <= Constants.PASSWORD_LENGTH) {
            isValid = false;
            mView.displayPasswordError(BaseError.PASSWORD_LENGTH_ERROR);
        }
        return isValid;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onSuccess() {
        if(mView != null) {
            setLoader(false);
            mView.onNavigateHome();
        }
    }

    @Override
    public void onError() {
        if(mView != null) {
            setLoader(false);
            mView.displayLoginError(BaseError.LOGIN_GENERAL_ERROR);
        }
    }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }
}
