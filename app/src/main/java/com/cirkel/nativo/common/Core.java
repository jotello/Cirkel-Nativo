package com.cirkel.nativo.common;

import android.content.Context;
import android.content.Intent;

public class Core {

    public static void newActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }
}
