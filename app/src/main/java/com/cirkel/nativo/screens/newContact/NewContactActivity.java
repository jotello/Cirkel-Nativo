package com.cirkel.nativo.screens.newContact;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.models.Contact;
import com.cirkel.nativo.screens.contacts.ContactsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewContactActivity extends AppCompatActivity
        implements NewContactContract.View {

    // region fields
    @BindView(R.id.edit_text_contact_email) EditText fieldEmail;
    @BindView(R.id.edit_text_contact_name) EditText fieldName;
    @BindView(R.id.edit_text_contact_number) EditText fieldPhone;
    @BindView(R.id.btn_cancel_addContact) Button btnCancel;
    @BindView(R.id.btn_addContact) Button btnAddContact;
    @BindView(R.id.loader_add_contact) ProgressBar loaderAddContact;
    // endregion fields

    private NewContactContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        ButterKnife.bind(this);
        mPresenter = new NewContactPresenter(this);
    }

    @OnClick(R.id.btn_addContact)
    void onClick_addContact(View view) {
        final String email = fieldEmail.getText().toString().trim();
        final String name = fieldName.getText().toString().trim();
        final String phone = fieldPhone.getText().toString().trim();
        Contact contact = new Contact(email, name, phone);
        if(mPresenter.isValidForm(contact)) {
            mPresenter.attemptCreateContact(contact);
        }
    }

    @OnClick(R.id.btn_cancel_addContact)
    void onClick_cancel(View view) {
        onBackPressed();
    }

    @Override
    public void backToContacts() { Core.newActivity(this, ContactsActivity.class); }

    @Override
    public void displayEmailError(String error) { fieldEmail.setError(error); }

    @Override
    public void displayPhoneError(String error) { fieldPhone.setError(error); }

    @Override
    public void displayNameError(String error) { fieldName.setError(error); }

    @Override
    public void displayCreationError(String error) { BaseError.showMessage( error, this); }

    @Override
    public void displayLoader(boolean loader) {
        int stateLoader = loader ? View.VISIBLE : View.GONE;
        loaderAddContact.setVisibility(stateLoader);
    }

    @Override
    public void setEnabledView(boolean enable) {
        fieldEmail.setEnabled(enable);
        fieldName.setEnabled(enable);
        fieldPhone.setEnabled(enable);
        btnCancel.setEnabled(enable);
        btnAddContact.setEnabled(enable);
    }
}
