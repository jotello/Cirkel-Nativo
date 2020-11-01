package com.cirkel.nativo.screens.contacts;

import com.cirkel.nativo.models.Contact;
import java.util.List;

public class ContactsPresenter implements ContactsContract.Presenter,
        ContactsContract.onGetDataListener {

    private ContactsContract.View mView;
    private ContactsContract.Interactor mInteractor;

    public ContactsPresenter(ContactsContract.View mView) {
        this.mView = mView;
        this.mInteractor = new ContactsInteractor(this);
    }

    @Override
    public void attempGetContacts() {
        if(mView != null) { setLoader(true); }
        mInteractor.performGetContacts();
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onSuccess(List<Contact> contactList) {
        if(mView != null) {
            setLoader(false);
            mView.onGetDataSuccess(contactList);
        }
    }

    @Override
    public void onFailure(String message) {
        if(mView != null) {
            setLoader(false);
            mView.onGetDataFailure(message);
        }
    }

    @Override
    public void setLoader(boolean state) {
        mView.setEnabledView(!state);
        mView.displayLoader(state);
    }

}
