package com.cirkel.nativo.screens.home;

public interface HomeContract {

    interface View {
        void displayAlertError(String error);
        void displayLoader(boolean loader);
        void setEnabledView(boolean enable);
        void onNavigateLogin();
    }

    interface Presenter {
        void attempSendAlert();
        void onDestroy();
        void attempLogout();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performSendAlert();
        void performLogout();
    }

}
