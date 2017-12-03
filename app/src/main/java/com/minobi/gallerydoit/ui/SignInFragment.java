package com.minobi.gallerydoit.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.minobi.gallerydoit.Config;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.SignInResponse;
import com.minobi.gallerydoit.di.module.NetworkModule;

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SignInFragment extends DaggerFragment {
    @Inject
    NetworkModule.DoItRestClient doItRestClient;

    private OnFragmentInteractionListener mListener;

    public SignInFragment() {}

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
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout etEmailHinLayout = view.findViewById(R.id.etEmailHinLayout);
        TextInputLayout etPasswordHinLayout = view.findViewById(R.id.etPasswordHinLayout);
        etEmailHinLayout.setHint("email");
        etPasswordHinLayout.setHint("password");

        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        Button btnLogin = view.findViewById(R.id.btnSignUp);
        btnLogin.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            if (mListener != null) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(btnLogin, "Email can't be empty", Snackbar.LENGTH_LONG).show();

                } else if (TextUtils.isEmpty(password)) {
                    Snackbar.make(btnLogin, "Password can't be empty", Snackbar.LENGTH_LONG).show();

                } else {
                    btnLogin.setEnabled(false);
                    doItRestClient.signIn(email, password)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    signInResponse -> {
                                        if (signInResponse.isSuccessful()) {
                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                            SignInResponse body = signInResponse.body();
                                            if (body != null) {
                                                preferences.edit()
                                                        .putString(Config.USER.AUTH_TOKEN, body.getAuthToken())
                                                        .putString(Config.USER.USER_AVATAR_URL, body.getAvatarUrl())
                                                        .putBoolean(Config.USER.USER_LOGGED_IN, true)
                                                        .putString(Config.USER.USER_EMAIL, email)
                                                        .apply();
                                            }
                                            mListener.onSignInSuccess();

                                        } else {
                                            describeErrors(signInResponse, view);
                                        }
                                        btnLogin.setEnabled(true);
                                    },
                                    throwable -> {
                                        showErrorMessage(view, "Error trying to log in");
                                        btnLogin.setEnabled(true);
                                    });
                }
            }
        });

        TextView tvGoToSignUpScreen = view.findViewById(R.id.tvGoToSignUpScreen);
        tvGoToSignUpScreen.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.showSignUpFragment();
            }
        });

        ConstraintLayout parentLayoutSignIn = view.findViewById(R.id.parentLayoutSignIn);
        parentLayoutSignIn.setOnTouchListener((v, event) -> {
            if(etEmail.isFocused()){
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

    private void describeErrors(Response<SignInResponse> signInResponse, View view) {
        ResponseBody errorBody = signInResponse.errorBody();
        if (errorBody != null) {
            try {
                JSONObject error = new JSONObject(errorBody.string());
                String errorMessage = error.getString("error");
                if (errorMessage != null && errorMessage.length() != 0) {
                    showErrorMessage(view, errorMessage);
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

    public interface OnFragmentInteractionListener {

        void onSignInSuccess();

        void showSignUpFragment();
    }
}
