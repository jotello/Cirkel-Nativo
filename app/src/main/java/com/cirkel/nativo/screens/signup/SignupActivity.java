package com.cirkel.nativo.screens.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.cirkel.nativo.R;
import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.screens.login.LoginActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity implements SignupContract.View {

    // region fields
    @BindView(R.id.edit_text_email) EditText fieldEmail;
    @BindView(R.id.edit_text_pass) EditText fieldPassword;
    @BindView(R.id.edit_text_name) EditText fieldName;
    @BindView(R.id.edit_text_number) EditText fieldPhone;
    @BindView(R.id.btn_cancel) Button btnCancel;
    @BindView(R.id.btn_signup) Button btnSignup;
    @BindView(R.id.loader_signup) ProgressBar loaderSignup;
    // endregion fields

    private SignupContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mPresenter = new SignupPresenter(this);
    }

    @OnClick(R.id.btn_signup)
    void onClick_Signup(View view) {
        final String email = fieldEmail.getText().toString().trim();
        final String password = fieldPassword.getText().toString().trim();
        final String name = fieldName.getText().toString().trim();
        final String phone = fieldPhone.getText().toString().trim();
        if(mPresenter.isValidForm(email, password, name, phone)) {
            mPresenter.attemptSignup(email, password, name, phone);
        }
    }

    @OnClick(R.id.btn_cancel)
    void onClick_Cancel(View view) {
        onNavigateLogin();
    }

    @Override
    public void onNavigateLogin() {
        Core.newActivity(this, LoginActivity.class);
        finish();
    }

    @Override
    public void displayEmailError(String error) {
        fieldEmail.setError(error);
    }

    @Override
    public void displayPasswordError(String error) {
        fieldPassword.setError(error);
    }

    @Override
    public void displayPhoneError(String error) {
        fieldPhone.setError(error);
    }

    @Override
    public void displayNameError(String error) {
        fieldName.setError(error);
    }

    @Override
    public void displaySignupError(String error) {
        BaseError.showMessage( error, this);
    }

    @Override
    public void displayLoader(boolean loader) {
        int stateLoader = loader ? View.VISIBLE : View.GONE;
        loaderSignup.setVisibility(stateLoader);
    }

    @Override
    public void setEnabledView(boolean enable) {
        fieldEmail.setEnabled(enable);
        fieldPassword.setEnabled(enable);
        fieldName.setEnabled(enable);
        fieldPhone.setEnabled(enable);
        btnCancel.setEnabled(enable);
        btnSignup.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
