package asm.uabierta.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.RecoverEvent;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.responses.SingleIdResponse;
import asm.uabierta.responses.SingleStringResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Alex on 26/07/2016.
 */
public class UpdateUser extends Job {
    private final String TAG = "Delete Found Job";

    private String caseService, token, value, currentPass, newPass;
    private int id;
    private Integer prefix;

    public UpdateUser(String caseService, String token, int id, String value){
        super(new Params(2).requireNetwork().persist());
        this.caseService = caseService;
        this.token = token;
        this.id = id;
        this.value = value;
    }

    public UpdateUser(String caseService, String token, int id, String value, Integer prefix){
        super(new Params(2).requireNetwork().persist());
        this.caseService = caseService;
        this.token = token;
        this.id = id;
        this.value = value;
        this.prefix = prefix;
    }

    public UpdateUser(String caseService, String token, int id, String currentPass, String newPass){
        super(new Params(2).requireNetwork().persist());
        this.caseService = caseService;
        this.token = token;
        this.id = id;
        this.currentPass = currentPass;
        this.newPass = newPass;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post(caseService);
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        switch (caseService){
            case "name":
                ServiceName servName = ServiceGenerator.createService(ServiceName.class);
                Call<SingleStringResponse> callName = servName.updateName(token, id, value);
                Response<SingleStringResponse> respName = callName.execute();

                if(respName.isSuccess()) {
                    BusProvider.getInstance().post(new SimpleResponseEvent(true, respName.body(), ""));
                } else {
                    Log.i("Error: ", respName.errorBody().toString());
                    BusProvider.getInstance().post(new SimpleResponseEvent(false, respName.code()));
                }
                break;
            case "email":
                ServiceEmail servEmail = ServiceGenerator.createService(ServiceEmail.class);
                Call<SingleStringResponse> callEmail = servEmail.updateEmail(token, id, value);
                Response<SingleStringResponse> respEmail = callEmail.execute();

                if(respEmail.isSuccess()) {
                    BusProvider.getInstance().post(new SimpleResponseEvent(true, respEmail.body(), ""));
                } else {
                    Log.i("Error: ", respEmail.code()+" "+respEmail.errorBody().toString());
                    BusProvider.getInstance().post(new SimpleResponseEvent(false, respEmail.code()));
                }
                break;
            case "phone":
                ServicePhone servPhone = ServiceGenerator.createService(ServicePhone.class);
                Call<SingleStringResponse> callPhone = servPhone.updatePhone(token, id, value, prefix);
                Response<SingleStringResponse> respPhone = callPhone.execute();

                if(respPhone.isSuccess()) {
                    BusProvider.getInstance().post(new SimpleResponseEvent(true, respPhone.body(), ""));
                } else {
                    Log.i("Error: ", respPhone.code()+" "+respPhone.errorBody().toString());
                    BusProvider.getInstance().post(new SimpleResponseEvent(false, respPhone.code()));
                }
                break;
            case "password":
                ServicePassword servPass = ServiceGenerator.createService(ServicePassword.class);
                Call<SingleStringResponse> callPass = servPass.updatePassword(token, id, currentPass, newPass);
                Response<SingleStringResponse> respPass = callPass.execute();

                if(respPass.isSuccess()) {
                    BusProvider.getInstance().post(new SimpleResponseEvent(true, respPass.body(), ""));
                } else {
                    Log.i("Error: ", respPass.code()+" "+respPass.errorBody().toString());
                    BusProvider.getInstance().post(new SimpleResponseEvent(false, respPass.code()));
                }
                break;
            default:
                BusProvider.getInstance().post(new RecoverEvent(false, ""));
            break;
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(new RecoverEvent(false, ""));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        BusProvider.getInstance().post(new RecoverEvent(false, ""));
        return false;
    }

    interface ServiceName {
        @FormUrlEncoded
        @PUT("users/{id}/name")
        Call<SingleStringResponse> updateName(@Header("api-key") String token, @Path("id") int id, @Field("value") String value);
    }

    interface ServicePhone {
        @FormUrlEncoded
        @PUT("users/{id}/phone")
        Call<SingleStringResponse> updatePhone(@Header("api-key") String token, @Path("id") int id, @Field("value") String value
                , @Field("prefix") Integer prefix);
    }

    interface ServiceEmail {
        @FormUrlEncoded
        @PUT("users/{id}/email")
        Call<SingleStringResponse> updateEmail(@Header("api-key") String token, @Path("id") int id, @Field("value") String value);
    }

    interface ServicePassword {
        @FormUrlEncoded
        @PUT("users/{id}/password")
        Call<SingleStringResponse> updatePassword(@Header("api-key") String token, @Path("id") int id,
                                                  @Field("currentPassword") String currentPassword , @Field("password") String password);
    }
}