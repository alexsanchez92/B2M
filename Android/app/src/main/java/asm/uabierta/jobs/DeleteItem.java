package asm.uabierta.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.DeleteEvent;
import asm.uabierta.responses.SingleIdResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.Constants;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.DELETE;
import retrofit.http.Header;
import retrofit.http.Path;

/**
 * Created by Alex on 26/07/2016.
 */
public class DeleteItem extends Job {
    private final String TAG = "Delete Found Job";

    private String token;
    private int id, type;

    public DeleteItem(String token, int id, int type){
        super(new Params(2).requireNetwork().persist());
        this.token = token;
        this.id = id;
        this.type = type;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post(Constants.delete);
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        /*
        Call<Response<Void>> call = null;
        if(type==1) {
            ServiceLost service = ServiceGenerator.createService(ServiceLost.class);
            call = service.deleteLost(token, id);
        }
        else {
            ServiceFound service = ServiceGenerator.createService(ServiceFound.class);
            call = service.deleteFound(token, id);
        }

        try{
            call.execute();
        }catch (ProtocolException e){
            SingleIdResponse deletesResponse = new SingleIdResponse();
            deletesResponse.setError(0);
            deletesResponse.setSuccess(1);
            BusProvider.getInstance().post(new SimpleResponseEvent(true, deletesResponse, ""));
        }
        BusProvider.getInstance().post(new SimpleResponseEvent(false, ""));
        */

        switch (type){
            case 0:
                ServiceFound serviceF = ServiceGenerator.createService(ServiceFound.class);
                Call<SingleIdResponse> callF = serviceF.deleteFound(token, id);
                Response<SingleIdResponse> respF = callF.execute();

                if(respF.isSuccess()) {
                    BusProvider.getInstance().post(new DeleteEvent(true, respF.body(), ""));
                } else {
                    Log.i("Error: ", respF.errorBody().toString());
                    BusProvider.getInstance().post(new DeleteEvent(false, ""));
                }
            break;
            case 1:
                ServiceLost serviceL = ServiceGenerator.createService(ServiceLost.class);
                Call<SingleIdResponse> callL = serviceL.deleteLost(token, id);
                Response<SingleIdResponse> respL = callL.execute();

                if(respL.isSuccess()) {
                    BusProvider.getInstance().post(new DeleteEvent(true, respL.body(), ""));
                } else {
                    Log.i("Error: ", respL.errorBody().toString());
                    BusProvider.getInstance().post(new DeleteEvent(false, ""));
                }
            break;
            default:
                BusProvider.getInstance().post(new DeleteEvent(false, ""));
            break;
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(new DeleteEvent(false, ""));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        BusProvider.getInstance().post(new DeleteEvent(false, ""));
        return false;
    }

    interface ServiceFound {
        @DELETE("found/{id}")
        Call<SingleIdResponse> deleteFound(@Header("api-key") String token, @Path("id") int id);
    }

    interface ServiceLost {
        @DELETE("lost/{id}")
        Call<SingleIdResponse> deleteLost(@Header("api-key") String token, @Path("id") int id);
    }
}