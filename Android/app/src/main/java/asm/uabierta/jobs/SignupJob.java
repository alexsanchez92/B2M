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
import retrofit.http.POST;

/**
 * Created by Alex on 26/07/2016.
 */
public class SignupJob extends Job {
    private final String TAG = "Login Job";
    private String name, email, password, phone;
    private Integer prefix;

    public SignupJob(String name, String email, String password, Integer prefix, String phone){
        super(new Params(1).requireNetwork());
        this.name = name;
        this.email = email;
        this.prefix = prefix;
        this.phone = phone;
        try {
            this.password = MD5.createMd5(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post("signup");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        Service service = ServiceGenerator.createService(Service.class);
        Call<UserSingleResponse> call = service.Signup(name, email, password, prefix, phone);
        Response<UserSingleResponse> signupResponse = call.execute();

        if(signupResponse.isSuccess()) {
            BusProvider.getInstance().post(signupResponse.body());
        } else {
            Log.i("Error: ", signupResponse.code()+" "+signupResponse.errorBody().toString());
            BusProvider.getInstance().post(signupResponse.code());
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(400);
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        BusProvider.getInstance().post(400);
        return false;
    }

    interface Service {
        @FormUrlEncoded
        @POST("signup")
        Call<UserSingleResponse> Signup(@Field("name") String name, @Field("email") String email, @Field("password") String password
                , @Field("prefix") Integer prefix, @Field("phone") String phone);
    }
}