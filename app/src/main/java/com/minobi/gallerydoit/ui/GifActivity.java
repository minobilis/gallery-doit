package com.minobi.gallerydoit.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.GetGifResponse;
import com.minobi.gallerydoit.di.module.NetworkModule;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GifActivity extends DaggerAppCompatActivity {

    @Inject
    NetworkModule.DoItRestClient doItRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

        String token = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Config.USER.AUTH_TOKEN, "");

        doItRestClient.getGif(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.isSuccessful()) {
                                GetGifResponse body = response.body();
                                if (body != null) {
                                    String gifUrl = body.getGif();
                                    ImageView gifView = findViewById(R.id.imgGif);
                                    Glide.with(this)
                                            .load(gifUrl)
                                            .into(gifView);
                                }
                            }
                        }, throwable -> Toast.makeText(this, "Error getting gif", Toast.LENGTH_SHORT).show()
                );
    }
}
