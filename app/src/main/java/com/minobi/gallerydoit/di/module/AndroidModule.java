package com.minobi.gallerydoit.di.module;

import com.minobi.gallerydoit.ui.AddImageFragment;
import com.minobi.gallerydoit.ui.GalleryFragment;
import com.minobi.gallerydoit.ui.GifActivity;
import com.minobi.gallerydoit.ui.MainActivity;
import com.minobi.gallerydoit.ui.SignInFragment;
import com.minobi.gallerydoit.ui.SignUpFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AndroidModule {
    @ContributesAndroidInjector
    abstract MainActivity mainActivityInjector();

    @ContributesAndroidInjector
    abstract GifActivity gifctivityInjector();

    @ContributesAndroidInjector
    abstract SignInFragment signInFragmentInjector();

    @ContributesAndroidInjector
    abstract SignUpFragment signUpFragmentInjector();

    @ContributesAndroidInjector
    abstract GalleryFragment galleryFragmentInjector();

    @ContributesAndroidInjector
    abstract AddImageFragment addImageFragmentInjector();
}