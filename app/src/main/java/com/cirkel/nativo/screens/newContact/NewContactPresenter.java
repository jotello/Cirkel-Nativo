package com.cirkel.nativo.screens.newContact;

import android.util.Patterns;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.CompleteListener;
import com.cirkel.nativo.models.Contact;

public class NewContactPresenter implements
        NewContactContract.Presenter, CompleteListener {

    private NewContactContract.View mView;
    private NewContactContract.Interactor mInteractor;

    public NewContactPresenter(NewContactContract.View view){
        this.mView = view;
        this.mInteractor = new NewContactInteractor(this);
    }

    @Override
    public void onSuccess() {
        if(mView != null) {
            setLoader(false);
            mView.backToContacts();
            mView.displayCreationError("¡Contacto añadido con éxito!");
        }
    }

    @Override
    public void onError() {
        if(mView != null) {
            setLoader(false);
            mView.displayCreationError(BaseError.CREATE_CONTACT_GENERAL_ERROR);
        }
    }

    @Override
    public void attemptCreateContact(Contact contact) {
        if(mView != null) {
            setLoader(true);
        }
        mInteractor.performCreateContact(contact);
    }

    @Override
    public boolean isValidForm(Contact contact) {
        boolean isValid = true;
        if(!Patterns.EMAIL_ADDRESS.matcher(contact.getEmail()).matches()) {
            isValid = false;
            mView.displayEmailError(BaseError.EMAIL_FORMAT_ERROR);
        } else if (contact.getName().equals("")) {
            isValid = false;
            mView.displayNameError(BaseError.NAME_NULL_ERROR);
        } else if (contact.getPhoneNumber().equals("") ||
                !Patterns.PHONE.matcher(contact.getPhoneNumber()).matches()) {
            isValid = false;
            mView.displayPhoneError(BaseError.PHONE_NUMBER_FORMAT_ERROR);
        }
        return isValid;
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }
}
