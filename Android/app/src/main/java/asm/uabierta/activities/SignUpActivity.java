package asm.uabierta.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import asm.uabierta.R;
import asm.uabierta.fragments.TermsB2M;
import asm.uabierta.jobs.LoadData.LoadCountries;
import asm.uabierta.jobs.SignupJob;
import asm.uabierta.models.Country;
import asm.uabierta.models.User;
import asm.uabierta.responses.CountriesResponse;
import asm.uabierta.responses.UserSingleResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class SignUpActivity extends AppCompatActivity{

    private EditText tvName, tvEmail, tvPassword, tvPhone, tvRepeatPassword;
    private TextView tvError;
    private Button btnSignup;
    private JobManager jobManager;
    private Spinner spinnerCountry;
    private Integer prefix = null;
    private ProgressDialog pDialog;
    private CoordinatorLayout coordinatorLayout;
    private View focusView;
    static CheckBox accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        jobManager  = new JobManager(getApplicationContext());

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        spinnerCountry = (Spinner) findViewById(R.id.spinnerCountry);
        jobManager.addJobInBackground(new LoadCountries());

        TelephonyManager tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        tvName = (EditText) findViewById(R.id.name);
        tvEmail = (EditText) findViewById(R.id.email);
        tvPhone = (EditText) findViewById(R.id.phone);
        tvPassword = (EditText) findViewById(R.id.password);
        tvRepeatPassword = (EditText) findViewById(R.id.repeat_password);
        tvError = (TextView) findViewById(R.id.tvError);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        accept = (CheckBox) findViewById(R.id.acceptCheck);

        tvName.setText("Alex Sanchez");
        tvEmail.setText("alex@alex.es");
        try {
            tvPhone.setText(tMgr.getLine1Number());
        }catch (NullPointerException ignored){

        }
        tvPassword.setText("123");
        tvRepeatPassword.setText("123");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public void clickTerms(View v) {
        switch (v.getId()) {
            case R.id.buttonTerms:
                Intent i = new Intent(this, TermsB2M.class);
                startActivity(i);
                break;
        }
    }

    public static void acceptTerms(){
        accept.setChecked(true);
    }

    //JOB EVENTS
    @Subscribe
    public void onInit(final String caseInit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog = ProgressDialog.show(SignUpActivity.this, "", "Enviando información", true);
                btnSignup.setEnabled(false);
            }
        });
    }

    @Subscribe
    public void onFinisLoadCountries(final CountriesResponse countriesResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (countriesResponse != null) {
                    if (countriesResponse.getData() != null) {
                        ArrayAdapter<Country> dataAdapter = new ArrayAdapter<Country>(SignUpActivity.this,
                                android.R.layout.simple_list_item_1, countriesResponse.getData());
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCountry.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();

                        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                Country c = (Country) spinnerCountry.getSelectedItem();
                                prefix = c.getId();
                                //Log.e("PAIS ", c.getPrefix()+" "+c.getName());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        });
                    }
                }
            }
        });
    }

    @Subscribe
    public void onFinishSignUp(final UserSingleResponse signupResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                btnSignup.setEnabled(true);
                if (signupResponse != null) {
                    if (signupResponse.getData() != null) {
                        User user = signupResponse.getData();
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
            }
        });
    }

    @Subscribe
    public void onFinishSignUpError(final Integer code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                btnSignup.setEnabled(true);
                switch (code){
                    case 1:
                        tvEmail.setError(getString(R.string.email_exist));
                        focusView = tvEmail;
                        focusView.requestFocus();
                        break;
                    case 2:
                        tvPhone.setError(getString(R.string.phone_exist));
                        focusView = tvPhone;
                        focusView.requestFocus();
                        break;
                    case 409:
                        tvEmail.setError(getString(R.string.email_phone_exist));
                        tvPhone.setError(getString(R.string.email_phone_exist));
                        //Snackbar.make(coordinatorLayout, getString(R.string.email_phone_exist), Snackbar.LENGTH_LONG).show();
                        break;
                    case 400:
                        Snackbar.make(coordinatorLayout, "Error en el registro", Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        Snackbar.make(coordinatorLayout, "Error en el registro", Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    public void startLogin(View v){
        try {
            tvEmail.setError(null);
            tvPassword.setError(null);
            tvPhone.setError(null);
            accept.setError(null);
            tvError.setVisibility(View.INVISIBLE);
            boolean cancel = false;
            focusView = null;

            String name = tvName.getText().toString();
            String email = tvEmail.getText().toString();
            String password = tvPassword.getText().toString();
            String repeatPassword = tvRepeatPassword.getText().toString();
            String phone = tvPhone.getText().toString();

            if (TextUtils.isEmpty(password)) {
                tvPassword.setError(getString(R.string.error_invalid_password));
                focusView = tvPassword;
                cancel = true;
            }

            if (!password.equals(repeatPassword)) {
                tvRepeatPassword.setError(getString(R.string.error_password_not_match));
                focusView = tvRepeatPassword;
                cancel = true;
            }

            if (!UtilsFunctions.isEmailValid(email)) {
                tvEmail.setError(getString(R.string.error_invalid_email));
                focusView = tvEmail;
                cancel = true;
            }

            if (!UtilsFunctions.isPhoneValid(phone)) {
                tvPhone.setError(getString(R.string.error_invalid_phone));
                focusView = tvPhone;
                cancel = true;
            }

            if(!accept.isChecked()) {
                accept.setError(getString(R.string.error_accept_terms));
                focusView = accept;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                jobManager.addJobInBackground(new SignupJob(name, email, password, prefix, phone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

