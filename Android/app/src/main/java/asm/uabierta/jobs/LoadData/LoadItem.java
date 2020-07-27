package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.listeners.LoadItemFoundListener;
import asm.uabierta.listeners.LoadItemLostListener;
import asm.uabierta.responses.FoundItemResponse;
import asm.uabierta.responses.LostItemResponse;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoadItem extends Job {
    private final String TAG = "Create Found Job";
    private LoadItemLostListener lostListener;
    private LoadItemFoundListener foundListener;
    private int type,id;

    public LoadItem(LoadItemLostListener listener, int id){
        super(new Params(1).requireNetwork());
        lostListener = listener;
        this.id = id;
        this.type = 1;
    }

    public LoadItem(LoadItemFoundListener listener, int id){
        super(new Params(1).requireNetwork());
        foundListener = listener;
        this.id = id;
        this.type = 0;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        if (foundListener != null) {
            foundListener.onInitGetFound();
        }
        if (lostListener != null) {
            lostListener.onInitGetLost();
        }
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        if(type==1){
            LostItemResponse lostssResponse = null;
            ServiceLost service = ServiceGenerator.createService(ServiceLost.class);
            Call<LostItemResponse> call = service.getLost(id);
            Response<LostItemResponse> createResponse = call.execute();

            if(createResponse.isSuccess()) {
                lostssResponse = createResponse.body();
            } else {
                Log.i("Error: ", createResponse.errorBody().toString());
            }

            // TODO Notificar al listener
            if (lostListener != null) {
                lostListener.onFinishGetLost(lostssResponse);
            }
        }
        else{
            FoundItemResponse foundsResponse = null;
            ServiceFound service = ServiceGenerator.createService(ServiceFound.class);
            Call<FoundItemResponse> call = service.getFound(id);
            Response<FoundItemResponse> foundResponse = call.execute();

            if(foundResponse.isSuccess()) {
                foundsResponse = foundResponse.body();
            } else {
                Log.i("Error: ", foundResponse.errorBody().toString());
            }

            // TODO Notificar al listener
            if (foundListener != null) {
                foundListener.onFinishGetFound(foundsResponse);
            }
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        if (foundListener != null) {
            foundListener.onFinishGetFound(null);
        }
        if (lostListener != null) {
            lostListener.onFinishGetLost(null);
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface ServiceFound {
        @GET("found/{id}")
        Call<FoundItemResponse> getFound(@Path("id") int id);
    }

    interface ServiceLost {
        @GET("lost/{id}")
        Call<LostItemResponse> getLost(@Path("id") int id);
    }
}