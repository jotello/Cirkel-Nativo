package com.cirkel.nativo.screens.newContact;

import com.cirkel.nativo.models.Contact;

public interface NewContactContract {

    interface View {
        void backToContacts();
        void displayEmailError(String error);
        void displayPhoneError(String error);
        void displayNameError(String error);
        void displayCreationError(String error);
        void displayLoader(boolean loader);
        void setEnabledView(boolean enable);
    }

    interface Presenter {
        void attemptCreateContact(Contact contact);
        boolean isValidForm(Contact contact);
        void onDestroy();
        void setLoader(boolean state);
    }

    interface Interactor {
        void performCreateContact(Contact contact);
    }
}
