package ru.user32338.fbandvkauth;

import android.app.Application;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;

/**
 * Created on 20.03.17.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
