package com.polaride;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static Context context;
    static CallbackManager mFacebookCallbackManager;
    static LoginButton mFacebookSignInButton;
    static AccessToken mFacebookAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivity.context = getApplicationContext();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        mFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions("email");

        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //TODO: Use the Profile class to get information about the current user.
                        mFacebookAccessToken = loginResult.getAccessToken();

                        Log.d( "token", mFacebookAccessToken.getToken());
                        Log.d( "userID", mFacebookAccessToken.getUserId());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                    }
                }
        );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static Context getAppContext() {
        return LoginActivity.context;
    }

    static void sendFacebookTokenToServer() {
        RequestQueue queue = Volley.newRequestQueue(getAppContext());
        String url = "https://private-861e1e-remoraapi.apiary-mock.com/mobile/user";

        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Come on here", "got it");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("That didnt work!", "very well");
                    }
        });

        queue.add(jsonRequest);
    }
}
