package asm.uabierta.jobs;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.listeners.LogoutListener;
import asm.uabierta.responses.LogoutResponse;
import asm.uabierta.utils.ServiceGenerator;
import asm.uabierta.utils.UserPreferences;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Alex on 26/07/2016.
 */
public class LogoutJob extends Job {
    private final String TAG = "Logout Job";
    private LogoutListener mListener;
    private Context context;
    private Activity activity;
    private UserPreferences uP;

    public LogoutJob(LogoutListener listener, Context c, Activity act){
        super(new Params(1).requireNetwork());
        mListener = listener;
        context = c;
        activity = act;
        uP = new UserPreferences(context);
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        LogoutResponse logoutsResponse = null;
        Service service = ServiceGenerator.createService(Service.class);
        Call<LogoutResponse> call = service.Logout(uP.getToken());
        Response<LogoutResponse> logoutResponse = call.execute();

        if(logoutResponse.isSuccess()) {
            logoutsResponse = logoutResponse.body();

        } else {
            Log.i("Error: ", logoutResponse.errorBody().toString());
        }

        // TODO Notificar al listener
        if (mListener != null) {
            mListener.onLogoutFinish(logoutsResponse);
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        if (mListener != null) {
            mListener.onLogoutFinish(null);
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface Service {
        @POST("logout/mobile")
        Call<LogoutResponse> Logout(@Header("api-key") String token);
    }
}