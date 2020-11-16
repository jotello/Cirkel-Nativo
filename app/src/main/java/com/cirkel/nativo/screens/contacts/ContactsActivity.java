package com.cirkel.nativo.screens.contacts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.models.Contact;
import com.cirkel.nativo.screens.adapters.ContactAdapter;
import com.cirkel.nativo.screens.home.HomeActivity;
import com.cirkel.nativo.screens.newContact.NewContactActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactsActivity extends AppCompatActivity
        implements ContactsContract.View {

    // region fields
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader_contacts)
    ProgressBar loaderContacts;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_view_contacts)
    RecyclerView recyclerViewContacts;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_add_contact)
    Button btnAddContact;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.bottom_navigation_contacts)
    BottomNavigationView bottomNavigationView;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.toolbar_contacts)
    Toolbar toolbar;
    // endregion fields

    // region variables
    private ContactsContract.Presenter mPresenter;
    // endregion variables

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        setVariables();
    }

    private void setVariables() {
        mPresenter = new ContactsPresenter(this);
        mPresenter.attempGetContacts();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewContacts.setLayoutManager(layoutManager);

        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this,
                R.color.menuBarBackground));
        bottomNavigationView.setSelectedItemId(R.id.action_contacts);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Core.newActivity(ContactsActivity.this, HomeActivity.class);
                        break;
                    case R.id.action_contacts:
                        break;
                }
                return true;
            }
        });
    }

    @OnClick(R.id.btn_add_contact)
    void onClick(View view) {
        Core.newActivity(this, NewContactActivity.class);
    }

    @Override
    public void onGetDataSuccess(List<Contact> list) {
        ContactAdapter contactAdapter = new ContactAdapter(list);
        recyclerViewContacts.setAdapter(contactAdapter);
        recyclerViewContacts.setVisibility(View.VISIBLE);
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
