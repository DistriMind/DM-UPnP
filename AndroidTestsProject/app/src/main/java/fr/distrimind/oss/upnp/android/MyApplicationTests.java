package fr.distrimind.oss.upnp.android;

import android.app.Application;

import fr.distrimind.oss.flexilogxml.android.ContextProvider;

public class MyApplicationTests extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ContextProvider.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ContextProvider.applicationClosed();
    }
}
