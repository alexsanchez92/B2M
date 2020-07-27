package asm.uabierta.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.RecoverEvent;
import asm.uabierta.responses.SingleIdResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.Header;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Alex on 26/07/2016.
 */
public class RecoverItem extends Job {
    private final String TAG = "Delete Found Job";

    private String token;
    private int id, type;

    public RecoverItem(String token, int id, int type){
        super(new Params(2).requireNetwork().persist());
        this.token = token;
        this.id = id;
        this.type = type;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post(Constants.recover);
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        switch (type){
            case 0:
                ServiceFound serviceF = ServiceGenerator.createService(ServiceFound.class);
                Call<SingleIdResponse> callF = serviceF.recoverFound(token, id);
                Response<SingleIdResponse> respF = callF.execute();

                if(respF.isSuccess()) {
                    BusProvider.getInstance().post(new RecoverEvent(true, respF.body(), ""));
                } else {
                    Log.i("Error: ", respF.errorBody().toString());
                    BusProvider.getInstance().post(new RecoverEvent(false, ""));
                }
            break;
            case 1:
                ServiceLost serviceL = ServiceGenerator.createService(ServiceLost.class);
                Call<SingleIdResponse> callL = serviceL.recoverLost(token, id);
                Response<SingleIdResponse> respL = callL.execute();

                if(respL.isSuccess()) {
                    BusProvider.getInstance().post(new RecoverEvent(true, respL.body(), ""));
                } else {
                    Log.i("Error: ", respL.errorBody().toString());
                    BusProvider.getInstance().post(new RecoverEvent(false, ""));
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

    interface ServiceFound {
        @PUT("found/{id}/recover")
        Call<SingleIdResponse> recoverFound(@Header("api-key") String token, @Path("id") int id);
    }

    interface ServiceLost {
        @PUT("lost/{id}/recover")
        Call<SingleIdResponse> recoverLost(@Header("api-key") String token, @Path("id") int id);
    }
}