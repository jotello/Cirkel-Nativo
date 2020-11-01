package com.cirkel.nativo.screens.login;

public interface LoginContract {

    interface View {
        void onNavigateHome();
        void displayEmailError(String error);
        void displayPasswordError(String error);
        void displayLoginError(String error);
        void displayLoader(boolean loader);
        void setEnabledView(boolean enable);
    }

    interface Presenter {
        void attemptLogin(String email, String password);
        boolean isValidForm(String email, String password);
        void onDestroy();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performLogin(String email, String password);
    }
}
