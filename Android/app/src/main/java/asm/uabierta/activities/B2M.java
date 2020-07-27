package asm.uabierta.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import asm.uabierta.R;
import asm.uabierta.jobs.LoginJob;
import asm.uabierta.models.User;
import asm.uabierta.responses.UserSingleResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.ConnectionDetector;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.UserPreferences;
import asm.uabierta.utils.UtilsFunctions;

public class B2M extends Activity{

    private String TAG ="LOADING";
    private UserPreferences up;
    private Intent intentIndex = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int milisec = 5000;
        contador(milisec);
        setContentView(R.layout.main);

        UtilsFunctions.createFolders();

        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        JobManager jobManager = new JobManager(getApplicationContext());
        up = new UserPreferences(getApplicationContext());

        if(!up.getId().equals("id") && !up.getId().equals("")){
            if(cd.isOnline()) {
                jobManager.addJobInBackground(new LoginJob(up.getToken(), Integer.parseInt(up.getId())));
            }
            else{
                intentIndex = new Intent(this, IndexActivity.class);
                intentIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }else{
            Intent login = new Intent(getApplicationContext(), IndexActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
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
    public void onLoginFinish(final UserSingleResponse loginResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loginResponse!=null) {
                    if(loginResponse.isSuccess()==1){
                        User user = loginResponse.getData();
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
                    }
                }
                intentIndex = new Intent(getApplicationContext(), IndexActivity.class);
                intentIndex.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        });
    }

    /*
    /**
     * Espera y cierra la aplicación tras los milisegundos indicados
     * @param milisegundos
     */
    /*public void contador(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(map!=null) {
                    startActivity(map);
                    finish();
                }
                else
                    milisec = 3000;

            }
        }, milisegundos);
    }*/
    public void contador(int milisegundos) {
        final int[] milsec = {milisegundos};
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(intentIndex!=null) {
                    startActivity(intentIndex);
                    finish();
                }
                else {
                    milsec[0] = 3000;
                }

            }
        }, milsec[0]);
    }
}