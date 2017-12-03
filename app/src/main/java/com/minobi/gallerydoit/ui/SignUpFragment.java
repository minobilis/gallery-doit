package com.minobi.gallerydoit.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.SignUpResponse;
import com.minobi.gallerydoit.di.module.NetworkModule;
import com.minobi.gallerydoit.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SignUpFragment extends DaggerFragment {
    @Inject
    NetworkModule.DoItRestClient doItRestClient;

    private OnFragmentInteractionListener mListener;
    private CircleImageView imgAvatar;
    private Uri avatarUri;

    public SignUpFragment() {}

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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout etUserNameHinLayout = view.findViewById(R.id.etUserNameHinLayout);
        TextInputLayout etEmailHinLayout = view.findViewById(R.id.etEmailHinLayout);
        TextInputLayout etPasswordHinLayout = view.findViewById(R.id.etPasswordHinLayout);
        etUserNameHinLayout.setHint("user name");
        etEmailHinLayout.setHint("email");
        etPasswordHinLayout.setHint("password");

        imgAvatar = view.findViewById(R.id.imgAvatarSelect);
        imgAvatar.setOnClickListener(v -> mListener.selectAvatarImage());

        if (avatarUri != null) {
            showSelectedAvatar();
        }

        EditText etUserName = view.findViewById(R.id.etUserName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mListener != null) {
                String username = etUserName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    showErrorMessage(view, "User name is required");

                } else if (TextUtils.isEmpty(email)) {
                    showErrorMessage(view, "Email is required");

                } else if (TextUtils.isEmpty(password)) {
                    showErrorMessage(view, "Password is required");

                } else if (avatarUri == null) {
                    showErrorMessage(view, "Avatar file required");

                } else {
                    String type = getActivity().getContentResolver().getType(avatarUri);

                    if (type != null) {
                        File originalFile = FileUtils.getFile(getActivity(), avatarUri);
                        RequestBody filePart = RequestBody.create(MediaType.parse(type), originalFile);
                        MultipartBody.Part file = MultipartBody.Part.createFormData("avatar", originalFile.getName(), filePart);

                        btnSignUp.setEnabled(false);
                        doItRestClient.signUp(username, email, password, file)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        signUpResponse -> {
                                            if (signUpResponse.isSuccessful()) {
                                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                SignUpResponse body = signUpResponse.body();
                                                if (body != null) {
                                                    preferences.edit()
                                                            .putString(Config.USER.AUTH_TOKEN, body.getAuthToken())
                                                            .putString(Config.USER.USER_AVATAR_URL, body.getAvatarUrl())
                                                            .putBoolean(Config.USER.USER_LOGGED_IN, true)
                                                            .putString(Config.USER.USER_NAME, username)
                                                            .apply();
                                                }
                                                mListener.onSignUpSuccess();

                                            } else {
                                                describeErrors(signUpResponse, view);
                                            }
                                            btnSignUp.setEnabled(true);
                                        },

                                        throwable -> {
                                            showErrorMessage(view, "Error trying to log in");
                                            btnSignUp.setEnabled(true);
                                        });
                    }
                }
            }
        });

        TextView tvGoToSignInScreen = view.findViewById(R.id.tvGoToSignInScreen);
        tvGoToSignInScreen.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.showSignInFragment();
            }
        });

        ConstraintLayout parentLayoutSignUp = view.findViewById(R.id.parentLayoutSignUp);
        parentLayoutSignUp.setOnTouchListener((v, event) -> {
            if(etUserName.isFocused()){
                etUserName.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else if(etEmail.isFocused()){
                etEmail.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } else if (etPassword.isFocused()) {
                etPassword.clearFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return false;
        });
    }

    private void describeErrors(Response<SignUpResponse> signUpResponse, View view) {
        ResponseBody errorBody = signUpResponse.errorBody();
        if (errorBody != null) {
            try {
                JSONObject error = new JSONObject(errorBody.string());
                JSONObject children = error.getJSONObject("children");
                JSONObject username = children.getJSONObject("username");
                if (username != null && username.names() != null && username.names().length() != 0) {
                    JSONArray errorMessages = username.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }
                JSONObject email = children.getJSONObject("email");
                if (email != null && email.names() != null && email.names().length() != 0) {
                    JSONArray errorMessages = email.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }
                JSONObject password = children.getJSONObject("password");
                if (password != null && password.names() != null && password.names().length() != 0) {
                    JSONArray errorMessages = password.getJSONArray("errors");

                    for (int i = 0; i < errorMessages.length(); i++) {
                        String message = errorMessages.getString(i);
                        showErrorMessage(view, message);
                    }
                }
                JSONObject avatar = children.getJSONObject("avatar");
                if (avatar != null && avatar.names() != null && avatar.names().length() != 0) {
                    JSONArray errorMessages = avatar.getJSONArray("errors");

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

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
        showSelectedAvatar();
    }

    private void showSelectedAvatar() {
        Glide.with(this)
                .load(new File(getRealPathFromURI(getActivity(), avatarUri)))
                .into(imgAvatar);
        imgAvatar.setBorderWidth(4);
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

        void onSignUpSuccess();

        void showSignInFragment();

        void selectAvatarImage();
    }
}
