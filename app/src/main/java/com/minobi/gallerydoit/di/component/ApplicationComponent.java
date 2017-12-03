package com.minobi.gallerydoit.di.component;

import com.minobi.gallerydoit.App;
import com.minobi.gallerydoit.di.module.AndroidModule;
import com.minobi.gallerydoit.di.module.NetworkModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules = {
        NetworkModule.class,
        AndroidSupportInjectionModule.class,
        AndroidModule.class})
public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);
        ApplicationComponent build();
    }
}