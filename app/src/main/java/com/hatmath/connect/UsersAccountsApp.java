package com.hatmath.connect;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class UsersAccountsApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Context getAppContext() {
        return context;
    }
}
