package com.minobi.gallerydoit.ui;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.AddImageResponse;
import com.minobi.gallerydoit.di.module.NetworkModule;
import com.minobi.gallerydoit.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class AddImageFragment extends DaggerFragment {
    @Inject
    NetworkModule.DoItRestClient doItRestClient;

    private OnFragmentInteractionListener mListener;
    private EditText etImageDescription;
    private EditText etImageHashTag;
    private ImageView imgImageToUpload;
    private Uri imageUri;

    public AddImageFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_add_image, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_image, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_image_done) {
            if (mListener != null) {
                String description = etImageDescription.getText().toString();
                String hashTag = etImageHashTag.getText().toString();


                if (TextUtils.isEmpty(description)) {
                    showErrorMessage(etImageDescription, "User name is required");

                } else if (TextUtils.isEmpty(hashTag)) {
                    showErrorMessage(etImageDescription, "Email is required");

                } else if (imageUri == null) {
                    showErrorMessage(etImageDescription, "Avatar file required");

                } else {
                    String type = getActivity().getContentResolver().getType(imageUri);

                    if (type != null) {
                        File originalFile = FileUtils.getFile(getActivity(), imageUri);
                        RequestBody filePart = RequestBody.create(MediaType.parse(type), originalFile);
                        MultipartBody.Part file = MultipartBody.Part.createFormData("image", originalFile.getName(), filePart);

                        String token = PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .getString(Config.USER.AUTH_TOKEN, "");

                        float latitude = 0;
                        float longitude = 0;
                        ExifInterface exif;
                        try {
                            exif = new ExifInterface(originalFile.getAbsolutePath());
                            float[] latLong = new float[2];
                            boolean hasLatLong = exif.getLatLong(latLong);
                            if (hasLatLong) {
                                latitude = latLong[0];
                                longitude = latLong[1];
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        doItRestClient.uploadImage(token, description, hashTag, longitude, latitude, file)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(addImageResponse -> {
                                            if (addImageResponse.isSuccessful()) {
                                                AddImageResponse body = addImageResponse.body();
                                                if (body != null) {

                                                }
                                                mListener.onAddImageDone();

                                            } else {
                                                describeErrors(addImageResponse, etImageDescription);
                                            }
                                        },

                                        throwable -> showErrorMessage(etImageDescription, "Error trying to add image"));
                    }
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextInputLayout etImageDescriptionHinLayout = view.findViewById(R.id.etImageDescriptionHinLayout);
        TextInputLayout etImageHashTagHinLayout = view.findViewById(R.id.etImageHashTagHinLayout);
        etImageDescriptionHinLayout.setHint("image description");
        etImageHashTagHinLayout.setHint("hash tag");

        imgImageToUpload = view.findViewById(R.id.imgUploadImage);
        imgImageToUpload.setOnClickListener(v -> mListener.selectUploadImage());

        etImageDescription = view.findViewById(R.id.etImageDescription);
        etImageHashTag = view.findViewById(R.id.etImageHashTag);

        ConstraintLayout parentLayoutSignUp = view.findViewById(R.id.parentLayoutAddImage);
        parentLayoutSignUp.setOnTouchListener((v, event) -> {
            if (etImageDescription.isFocused()) {
                etImageDescription.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else if (etImageHashTag.isFocused()) {
                etImageHashTag.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void describeErrors(Response<AddImageResponse> signInResponse, View view) {
        ResponseBody errorBody = signInResponse.errorBody();
        if (errorBody != null) {
            try {
                JSONObject error = new JSONObject(errorBody.string());
                JSONObject children = error.getJSONObject("children");

                JSONObject image = children.getJSONObject("image");
                if (image != null && image.names() != null && image.names().length() != 0) {
                    JSONArray errorMessages = image.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }

                JSONObject description = children.getJSONObject("description");
                if (description != null && description.names() != null && description.names().length() != 0) {
                    JSONArray errorMessages = description.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }

                JSONObject hashtag = children.getJSONObject("hashtag");
                if (hashtag != null && hashtag.names() != null && hashtag.names().length() != 0) {
                    JSONArray errorMessages = hashtag.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }

                JSONObject latitude = children.getJSONObject("longitude");
                if (latitude != null && latitude.names() != null && latitude.names().length() != 0) {
                    JSONArray errorMessages = latitude.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }

                JSONObject longitude = children.getJSONObject("longitude");
                if (longitude != null && longitude.names() != null && longitude.names().length() != 0) {
                    JSONArray errorMessages = longitude.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }


            } catch (Exception e) {
                showErrorMessage(view, e.getMessage());
            }
        }
    }

    private void showErrorMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setImageUri(Uri uri) {
        this.imageUri = uri;
        showSelectedImage();
    }

    private void showSelectedImage() {
        Glide.with(this)
                .load(new File(getRealPathFromURI(getActivity(), imageUri)))
                .into(imgImageToUpload);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public interface OnFragmentInteractionListener {

        void onAddImageDone();

        void selectUploadImage();
    }
}
