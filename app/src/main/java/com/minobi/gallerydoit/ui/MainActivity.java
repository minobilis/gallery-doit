package com.minobi.gallerydoit.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;

import dagger.android.support.DaggerAppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends DaggerAppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SignInFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener,
        AddImageFragment.OnFragmentInteractionListener,
        SignUpFragment.OnFragmentInteractionListener {

    private static final int GET_AVATAR_FROM_GALLERY = 0;
    private static final int GET_IMAGE_FROM_GALLERY = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SignInFragment signInFragment;
    private SignUpFragment signUpFragment;
    private GalleryFragment galleryFragment;
    private AddImageFragment addImageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (isUserLoggedIn()) {
            unlockUI();
            showGalleryFragment();
        } else {
            lockUI();
            showSignInFragment();
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(Config.USER.USER_LOGGED_IN, false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_log_out) {
            initiateLogOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initiateLogOut() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.dialog_log_out_confirm)
                .setPositiveButton(R.string.ok, (dialog, id) -> onLogOutConfirmed())
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public void onSignInSuccess() {
        unlockUI();
        showGalleryFragment();
    }

    @Override
    public void onSignUpSuccess() {
        unlockUI();
        showGalleryFragment();
    }

    private void onLogOutConfirmed() {
        lockUI();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .remove(Config.USER.AUTH_TOKEN)
                .remove(Config.USER.USER_AVATAR_URL)
                .remove(Config.USER.USER_EMAIL)
                .putBoolean(Config.USER.USER_LOGGED_IN, false)
                .apply();

        showSignInFragment();
    }

    private void unlockUI() {
        getSupportActionBar().show();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        String avatarUrl = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Config.USER.USER_AVATAR_URL, "");

        CircleImageView imgDrawerAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgDrawerHeader);
        Glide.with(this)
                .load(avatarUrl)
                .into(imgDrawerAvatar);
        imgDrawerAvatar.setBorderWidth(4);

        TextView tvDrawerUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvDrawerUserEmail);
        String email = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Config.USER.USER_EMAIL, "");
        tvDrawerUserEmail.setText(email);
    }

    private void lockUI() {
        getSupportActionBar().hide();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        String avatarUrl = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Config.USER.USER_AVATAR_URL, "");

        CircleImageView imgDrawerAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgDrawerHeader);
        Glide.with(this)
                .load(avatarUrl)
                .into(imgDrawerAvatar);
        imgDrawerAvatar.setBorderWidth(4);

        TextView tvDrawerUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvDrawerUserEmail);
        String email = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(Config.USER.USER_EMAIL, "");
        tvDrawerUserEmail.setText(email);
    }

    private void showGalleryFragment() {
        if (galleryFragment == null) {
            galleryFragment = new GalleryFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, galleryFragment)
                .commit();
    }

    @Override
    public void showSignUpFragment() {
        if (signUpFragment == null) {
            signUpFragment = new SignUpFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, signUpFragment)
                .commit();
    }

    @Override
    public void showSignInFragment() {
        if (signInFragment == null) {
            signInFragment = new SignInFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, signInFragment)
                .commit();
    }

    @Override
    public void selectAvatarImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_AVATAR_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_AVATAR_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri avatarUri = data.getData();
            if (signUpFragment != null) {
                signUpFragment.setAvatarUri(avatarUri);
            }

        } else if (requestCode == GET_IMAGE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri avatarUri = data.getData();
            if (addImageFragment != null) {
                addImageFragment.setImageUri(avatarUri);
            }
        }
    }

    @Override
    public void onAddImage() {
        if (addImageFragment == null) {
            addImageFragment = new AddImageFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, addImageFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAddImageDone() {
        onBackPressed();
    }

    @Override
    public void selectUploadImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_IMAGE_FROM_GALLERY);
    }

    @Override
    public void playGif() {
        startActivity(new Intent(this, GifActivity.class));
    }
}
