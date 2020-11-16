package com.cirkel.nativo.screens.signup;

public interface SignupContract {

    interface View {
        void onNavigateLogin();
        void displayEmailError(String error);
        void displayPasswordError(String error);
        void displayPhoneError(String error);
        void displayNameError(String error);
        void displaySignupError(String error);
        void displayLoader(boolean loader);
        void setEnabledView(boolean enable);
    }

    interface Presenter {
        void attemptSignup(String email, String password,
                           String name, String cellphone);
        boolean isValidForm(String email, String password,
                            String name, String cellphone, String confirmPassword);
        void onDestroy();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performSignup(String email, String password,
                          String name, String cellphone);
    }

    interface Listener {
        void onSuccess();
        void onError(String message);
    }

}
