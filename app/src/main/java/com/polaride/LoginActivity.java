package com.polaride;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import io.fabric.sdk.android.Fabric;


public class LoginActivity extends AppCompatActivity {
    public class LoginPOJO {

        private String loginMethod;
        private String userId;
        private String userToken;

        public String getLoginMethod() { return this.loginMethod; }
        public void setLoginMethod( String loginMethod ) { this.loginMethod = loginMethod; }

        public String getUserId() { return this.userId; }
        public void setUserId( String userId ) { this.userId = userId; }

        public String getUserToken() { return this.userToken; }
        public void setUserToken( String userToken ) { this.userToken = userToken; }

    }

    private static Context context;
    static CallbackManager mFacebookCallbackManager;
    static LoginButton mFacebookSignInButton;
    static AccessToken mFacebookAccessToken;

    static TwitterLoginButton mTwitterSignInButton;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Lp3tvQitZbjwrhwCVxIN1w6Gz";
    private static final String TWITTER_SECRET = "nxt7PkXJabMY3IQQetjmbhoCUTJtQC2ejX2H3kkNdXdrzftiVp";


    LoginPOJO loginPOJO = new LoginPOJO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivity.context = getApplicationContext();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_login);

        mFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions("email");

        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //TODO: Use the Profile class to get information about the current user.
                        mFacebookAccessToken = loginResult.getAccessToken();

                        loginPOJO.setLoginMethod("facebook");
                        loginPOJO.setUserId(mFacebookAccessToken.getUserId());
                        loginPOJO.setUserToken(mFacebookAccessToken.getToken());

                        sendTokenToServer(loginPOJO);
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

        mTwitterSignInButton = (TwitterLoginButton) findViewById(R.id.twitter_sign_in_button);
        mTwitterSignInButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                loginPOJO.setLoginMethod("twitter");
                loginPOJO.setUserId(String.valueOf(result.data.getUserId()));
                loginPOJO.setUserToken(result.data.getAuthToken().toString());

                sendTokenToServer(loginPOJO);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.d(LoginActivity.class.getCanonicalName(), exception.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        if(TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE == requestCode) {
            mTwitterSignInButton.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Context getAppContext() {
        return LoginActivity.context;
    }

    static void sendTokenToServer(LoginPOJO loginPOJO) {
        RequestQueue queue = Volley.newRequestQueue(getAppContext());
        String url = "http://private-861e1e-remoraapi.apiary-mock.com/user/mobile/login";

        Gson gson = new Gson();
        String loginJSON = gson.toJson(loginPOJO);
        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject(loginJSON);
            Log.d("json_object", jsonObj.toString());
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("json_response", response.toString());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", "I got an error", error);
                }
            });

            queue.add(jsonRequest);
        } catch (JSONException e) {
            Log.e("ERROR", "I got an error", e);
        }

    }
}
