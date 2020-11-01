package com.cirkel.nativo.common;

import android.content.Context;
import android.widget.Toast;

public class BaseError {

    public static final String EMAIL_FORMAT_ERROR = "Email ingresado no válido";
    public static final String PHONE_NUMBER_FORMAT_ERROR = "Teléfono ingresado no válido";
    public static final String NAME_NULL_ERROR = "Debe ingresar un nombre";
    public static final String PASSWORD_LENGTH_ERROR = "Su contraseña debe poseer más de 6 carácteres";
    public static final String LOGIN_GENERAL_ERROR = "Error al ingresar, intente más tarde";

    public static final String SEND_ALERT_GENERAL_ERROR = "Error al enviar alerta";
    public static final String CREATE_CONTACT_GENERAL_ERROR = "Error al crear contacto";
    public static final String CREATE_USER_GENERAL_ERROR = "Error al crear usuario";

    public static void showMessage(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
