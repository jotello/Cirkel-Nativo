package com.cirkel.nativo.screens.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    // region constants
    final String EMAIL = "email_example@gmail.com";
    final String PASSWORD = "asdf1234";

    // endregion constans

    // region helper fields
    @Mock LoginContract.View mView;

    // endregion helper fields

    LoginPresenter SUT;

    @Before
    public void setup() throws Exception {
        //MockitoAnnotations.initMocks(this);
        //SUT = new LoginPresenter(mView);
    }

    // onSuccess on attemptLogin
    // onFailure on attemptLogin
    // onSuccess when isValidForm

    @Test
    public void loginPresenter_success_onValidFormGiven() {
        //Arrange

        //Act
        //boolean result = SUT.isValidForm(EMAIL, PASSWORD);
        //assertThat(result, true);
        //Assert
    }

    // onFailure when form is not valid



    // region helper methods

    // endregion helper methods

    // region helper classes

    // endregion helper classes

}