package asm.uabierta.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.Arrays;

import asm.uabierta.R;
import asm.uabierta.jobs.LoginJob;
import asm.uabierta.models.User;
import asm.uabierta.responses.UserSingleResponse;
import asm.uabierta.utils.AnalyticsTracker;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class LoginActivity extends AppCompatActivity{

    private String TAG ="LOGIN";
    private static final int FB_SIGN_IN = 64206;

    private EditText mEmailView, mPasswordView;
    private TextView tvError;
    private Button btnLogin;
    private JobManager jobManager;
    private ProgressDialog pDialog;

    private CallbackManager callbackManager;
    private LoginButton btnFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AnalyticsTracker(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        jobManager  = new JobManager(getApplicationContext());

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        tvError = (TextView) findViewById(R.id.tvError);

        mEmailView.setText("alex@alex.es");
        mPasswordView.setText("123");

        //FACEBOOK
        btnFacebookLogin = (LoginButton) findViewById(R.id.facebook_button);
        btnFacebookLogin.setReadPermissions(Arrays.asList("email,public_profile"));
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Log.e(TAG, AccessToken.getCurrentAccessToken().toString());
                //Log.e(TAG, AccessToken.getCurrentAccessToken().getToken().toString());
                //Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        //Log.e(TAG, AccessToken.getCurrentAccessToken().toString());
                        //Log.e(TAG, Profile.getCurrentProfile()+"");
                        if (user!=null){
                            if (user.optString("email") != null && !TextUtils.isEmpty(user.optString("email"))) {
                                Log.e(TAG + " LOGINFB", user.toString());
                                //jobManager.addJobInBackground(new LoginRegister("loginFb", user.optString("email"), user.optString("name"), user.optString("id")));
                                jobManager.addJobInBackground(new LoginJob("alex@alex.es", "123"));
                            }
                            else
                                onCancel();
                        }
                        else
                            onCancel();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                Log.e(TAG, "FUERA");
            }

            @Override
            public void onError(FacebookException exception) {
                //Log.e("ERROR ", exception.getCause().toString());
                LoginManager.getInstance().logOut();
                Toast.makeText(LoginActivity.this, getString(R.string.error_login_facebook), Toast.LENGTH_LONG).show();
                Log.e(TAG, "ERROR FB");
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mEmailView.setError(null);
                    mPasswordView.setError(null);
                    tvError.setVisibility(View.INVISIBLE);
                    boolean cancel = false;
                    View focusView = null;

                    // Store values at the time of the login attempt.
                    String email = mEmailView.getText().toString();
                    String password = mPasswordView.getText().toString();

                    if (TextUtils.isEmpty(password)) {
                        mPasswordView.setError(getString(R.string.error_invalid_password));
                        focusView = mPasswordView;
                        cancel = true;
                    }

                    // Check for a valid email address.
                    if (!UtilsFunctions.isEmailValid(email)) {
                        mEmailView.setError(getString(R.string.error_invalid_email));
                        focusView = mEmailView;
                        cancel = true;
                    }

                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {
                        // Show a progress spinner, and kick off a background task to
                        // perform the user login attempt.
                        jobManager.addJobInBackground(new LoginJob(email, password));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FB_SIGN_IN) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    //JOB EVENTS
    @Subscribe
    public void onInitLogin(final String caseInit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnLogin.setEnabled(false);
                pDialog = ProgressDialog.show(LoginActivity.this, "", "Cargando", true);
            }
        });
    }

    @Subscribe
    public void onFinishLogin(final UserSingleResponse loginResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnLogin.setEnabled(true);
                pDialog.dismiss();

                if(loginResponse!=null) {
                    if(loginResponse.isSuccess()==0 || loginResponse.getData()==null) {
                        tvError.setVisibility(View.VISIBLE);
                    }
                    else{
                        User user = loginResponse.getData();
                        UserPreferences up = new UserPreferences(getApplicationContext());
                        up.setId(Integer.toString(user.getId()));
                        up.setName(user.getName());
                        up.setEmail(user.getEmail());
                        up.setPhone(user.getPhone());
                        up.setToken(user.getToken());
                        if(user.getPrefix()!=null) {
                            up.setPrefix(user.getPrefix().getPrefix());
                            up.setPrefixId(user.getPrefix().getId());
                            up.setPrefixName(user.getPrefix().getName());
                        }
                        up.logUserPreferences();

                        Intent indexLogged = new Intent(getApplicationContext(), IndexActivity.class);
                        indexLogged.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(indexLogged);
                        finish();
                    }
                }
                else{
                    Intent indexLogged = new Intent(getApplicationContext(), IndexActivity.class);
                    indexLogged.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(indexLogged);
                    finish();
                }
            }
        });
    }

    @Subscribe
    public void onFinishLoginError(final Integer code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnLogin.setEnabled(true);
                pDialog.dismiss();

                switch (code){
                    case 404:
                        tvError.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

