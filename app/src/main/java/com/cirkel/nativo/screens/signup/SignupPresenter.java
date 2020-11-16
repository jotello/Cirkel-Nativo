package com.cirkel.nativo.screens.signup;

import android.util.Patterns;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Constants;

public class SignupPresenter implements SignupContract.Presenter, SignupContract.Listener {

    private SignupContract.View mView;
    private SignupContract.Interactor mInteractor;

    public SignupPresenter(SignupContract.View view) {
        this.mView = view;
        this.mInteractor = new SignupInteractor(this);
    }

    @Override
    public void attemptSignup(String email, String password,
                              String name, String cellphone) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performSignup(email, password, name, cellphone);
    }

    @Override
    public boolean isValidForm(String email, String password,
                               String name, String cellphone,
                               String confirmPassword) {
        boolean isValid = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            mView.displayEmailError(BaseError.EMAIL_FORMAT_ERROR);
        } else if (password.length() <= Constants.PASSWORD_LENGTH) {
            isValid = false;
            mView.displayPasswordError(BaseError.PASSWORD_LENGTH_ERROR);
        } else if (!password.equals(confirmPassword)) {
            isValid = false;
            mView.displayPasswordError(BaseError.PASSWORD_NOT_EQUALS_ERROR);
        } else if (name.equals("")) {
            isValid = false;
            mView.displayNameError(BaseError.NAME_NULL_ERROR);
        } else if (cellphone.equals("") || !Patterns.PHONE.matcher(cellphone).matches()) {
            isValid = false;
            mView.displayPhoneError(BaseError.PHONE_NUMBER_FORMAT_ERROR);
        }
        return isValid;
    }

    @Override
    public void onSuccess() {
        if(mView != null) {
            setLoader(false);
            mView.displaySignupError("¡Cuenta creada con éxito!");
            mView.onNavigateLogin();
        }
    }

    @Override
    public void onError(String message) {
        if(mView != null) {
            setLoader(false);
            mView.displaySignupError(message);
        }
    }

    @Override
    public void onDestroy() { mView = null; }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }
}
