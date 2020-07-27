package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.responses.BuildingsResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.GET;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoadBuildings extends Job {
    private final String TAG = "LoadBuildings Job";

    public LoadBuildings() {
        super(new Params(1).requireNetwork());
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        Service service = ServiceGenerator.createService(Service.class);
        Call<BuildingsResponse> call = service.LoadBuildings();
        Response<BuildingsResponse> buildingResponse = call.execute();

        if(buildingResponse.isSuccess()) {
            BusProvider.getInstance().post(buildingResponse.body());
            //for(Found found : foundsResponse.getData()) {
            //    Log.e("TITLE ",found.getTitle()+" "+found.getUser().getPrefix().getName());
            //}
        } else {
            Log.i("Error: ", buildingResponse.errorBody().toString());
            BusProvider.getInstance().post(new SimpleResponseEvent(false, ""));
        }

    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(new SimpleResponseEvent(false, ""));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface Service {
        @GET("buildings")
        Call<BuildingsResponse> LoadBuildings();
    }
}