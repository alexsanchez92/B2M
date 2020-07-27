package asm.uabierta.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.responses.UserSingleResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.MD5;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoginJob extends Job {
    private final String TAG = "Login Job";
    private String email, password, token;
    private Integer id=null;

    public LoginJob(String email, String password){
        super(new Params(1).requireNetwork());
        this.email = email;
        try {
            this.password = MD5.createMd5(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoginJob(String token, Integer id){
        super(new Params(1).requireNetwork());
        this.token = token;
        this.id = id;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post("login");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        if(id!=null) {
            ServiceLoginId service = ServiceGenerator.createService(ServiceLoginId.class);
            Call<UserSingleResponse> call = service.Login(token, id);
            Response<UserSingleResponse> loginResponse = call.execute();

            if(loginResponse.isSuccess()) {
                BusProvider.getInstance().post((loginResponse.body()));
            } else {
                Log.i("Error: ", loginResponse.errorBody().toString());
                BusProvider.getInstance().post(loginResponse.code());
            }
        }
        else{
            ServiceLogin service = ServiceGenerator.createService(ServiceLogin.class);
            Call<UserSingleResponse> call = service.Login(email, password);
            Response<UserSingleResponse> loginResponse = call.execute();

            if(loginResponse.isSuccess()) {
                BusProvider.getInstance().post((loginResponse.body()));
            } else {
                Log.i("Error: ", loginResponse.errorBody().toString());
                BusProvider.getInstance().post(loginResponse.code());
            }
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(0);
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        BusProvider.getInstance().post(0);
        return false;
    }

    interface ServiceLogin {
        @FormUrlEncoded
        @POST("login/mobile")
        Call<UserSingleResponse> Login(@Field("email") String email, @Field("password") String password);
    }

    interface ServiceLoginId {
        @PUT("login/{id}")
        Call<UserSingleResponse> Login(@Header("api-key") String token, @Path("id") int id);
    }
}