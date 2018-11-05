package com.haoruigang.cniao5play;

import android.app.Application;
import android.content.Context;

import com.haoruigang.cniao5play.di.component.AppComponent;
import com.haoruigang.cniao5play.di.component.DaggerAppComponent;
import com.haoruigang.cniao5play.di.module.AppModule;
import com.haoruigang.cniao5play.di.module.HttpModule;

public class AppApplication extends Application {

    public AppComponent mAppComponent;

    public static AppApplication get(Context context) {
        return (AppApplication) context.getApplicationContext();
    }

    public AppComponent getmAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .httpModule(new HttpModule())
                .build();
    }
}
