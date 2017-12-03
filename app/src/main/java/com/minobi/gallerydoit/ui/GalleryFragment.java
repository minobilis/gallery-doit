package com.minobi.gallerydoit.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.GetAllImagesResponse;
import com.minobi.gallerydoit.data.Image;
import com.minobi.gallerydoit.di.module.NetworkModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GalleryFragment extends DaggerFragment {
    @Inject
    NetworkModule.DoItRestClient doItRestClient;

    private ArrayList<Image> imageList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private GridAdapter adapter;

    public GalleryFragment() {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_image) {
            mListener.onAddImage();
            return true;
        } else if (item.getItemId() == R.id.action_play_gif) {
            mListener.playGif();
            return true;
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        adapter = new GridAdapter(imageList, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycleViewGallery);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateGallery();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateGallery() {
        String token = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(Config.USER.AUTH_TOKEN, "");

        doItRestClient.getImages(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccessful()) {
                                GetAllImagesResponse body = response.body();
                                if (body != null) {
                                    List<Image> images = body.getImages();
                                    imageList.clear();
                                    imageList.addAll(images);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        },

                        throwable -> Toast.makeText(getActivity(), "Error while getting list of images", Toast.LENGTH_LONG).show());
    }

    public interface OnFragmentInteractionListener {

        void onAddImage();

        void playGif();
    }
}
