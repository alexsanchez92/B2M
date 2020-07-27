package asm.uabierta.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.RecoverEvent;
import asm.uabierta.events.SimpleResponseEvent;
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
public class ChangeItemBuilding extends Job {
    private final String TAG = "Change Building Job";

    private String token, description;
    private int id;
    private Integer building;

    public ChangeItemBuilding(String token, int id, Integer building, String description){
        super(new Params(2).requireNetwork().persist());
        this.token = token;
        this.id = id;
        this.building = building;
        this.description = description;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        BusProvider.getInstance().post("");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        Service serv = ServiceGenerator.createService(Service.class);
        Call<SingleStringResponse> call = serv.updateBuilding(token, id, building, description);
        Response<SingleStringResponse> resp = call.execute();

        if(resp.isSuccess()) {
            BusProvider.getInstance().post(new SimpleResponseEvent(true, resp.body(), ""));
        } else {
            Log.i("Error: ", resp.errorBody().toString());
            BusProvider.getInstance().post(new SimpleResponseEvent(false, resp.code()));
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

    interface Service {
        @FormUrlEncoded
        @PUT("found/{id}/place")
        Call<SingleStringResponse> updateBuilding(@Header("api-key") String token, @Path("id") int id
                , @Field("placeId") Integer placeId, @Field("placeDetails") String placeDetails);
    }
}