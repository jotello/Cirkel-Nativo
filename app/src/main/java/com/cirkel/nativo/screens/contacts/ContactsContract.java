package com.cirkel.nativo.screens.contacts;

import com.cirkel.nativo.models.Contact;
import java.util.List;

public interface ContactsContract {

    interface View {
        void onGetDataSuccess(List<Contact> list);
        void onGetDataFailure(String message);
        void displayLoader(boolean loader);
        void setEnabledView(boolean enable);
    }

    interface Presenter {
        void attempGetContacts();
        void onDestroy();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performGetContacts();
    }

    interface onGetDataListener{
        void onSuccess(List<Contact> list);
        void onFailure(String message);
    }
}
