package com.cirkel.nativo.screens.login;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cirkel.nativo.common.BaseError;
import com.cirkel.nativo.common.Core;
import com.cirkel.nativo.screens.home.HomeActivity;
import com.cirkel.nativo.R;
import com.cirkel.nativo.screens.signup.SignupActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    // region fields
    @BindView(R.id.edit_text_email) EditText fieldEmail;
    @BindView(R.id.edit_text_pass) EditText fieldPassword;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_signup) Button btnSignup;
    @BindView(R.id.loader_login) ProgressBar loaderLogin;
    // endregion fields

    private LoginContract.Presenter mPresenter;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter = new LoginPresenter(this);
        btnLogin.setBackground(getDrawable(R.drawable.btn_login_background));
    }

    @OnClick(R.id.btn_login)
    void onClick_Login(View view) {
        final String email = fieldEmail.getText().toString().trim();
        final String password = fieldPassword.getText().toString().trim();
        if(mPresenter.isValidForm(email, password)) {
            mPresenter.attemptLogin(email, password);
        }
    }

    @OnClick(R.id.btn_signup)
    void onClick_Signup(View view) { Core.newActivity(this, SignupActivity.class); }

    @Override
    public void onNavigateHome() {
        Core.newActivity(this, HomeActivity.class);
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
    public void displayLoginError(String error) {
        BaseError.showMessage(error, this);
    }

    @Override
    public void displayLoader(boolean loader) {
        int stateLoader = loader ? View.VISIBLE : View.GONE;
        loaderLogin.setVisibility(stateLoader);
    }

    @Override
    public void setEnabledView(boolean enable) {
        fieldEmail.setEnabled(enable);
        fieldPassword.setEnabled(enable);
        btnLogin.setEnabled(enable);
        btnSignup.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
