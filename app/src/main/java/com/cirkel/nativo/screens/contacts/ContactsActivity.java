package com.cirkel.nativo.screens.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.models.Contact;
import com.cirkel.nativo.screens.adapters.ContactAdapter;
import com.cirkel.nativo.screens.home.HomeActivity;
import com.cirkel.nativo.screens.newContact.NewContactActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactsActivity extends AppCompatActivity
        implements ContactsContract.View {

    // region fields
    @BindView(R.id.loader_contacts) ProgressBar loaderContacts;
    @BindView(R.id.recycler_view_contacts) RecyclerView recyclerViewContacts;
    @BindView(R.id.btn_add_contact) FloatingActionButton btnAddContact;
    // endregion fields

    // region variables
    private ContactsContract.Presenter mPresenter;
    // endregion variables

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        mPresenter = new ContactsPresenter(this);
        mPresenter.attempGetContacts();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewContacts.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.btn_add_contact)
    void onClick(View view) {
        Core.newActivity(this, NewContactActivity.class);
    }

    @Override
    public void onGetDataSuccess(List<Contact> list) {
        ContactAdapter contactAdapter = new ContactAdapter(list);
        recyclerViewContacts.setAdapter(contactAdapter);
    }

    @Override
    public void onGetDataFailure(String message) {
        BaseError.showMessage(message, this);
    }

    @Override
    public void displayLoader(boolean loader) {
        int stateLoader = loader ? View.VISIBLE : View.GONE;
        loaderContacts.setVisibility(stateLoader);
    }

    @Override
    public void setEnabledView(boolean enable) {
        recyclerViewContacts.setEnabled(enable);
        btnAddContact.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
