package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.listeners.LoadLostsListener;
import asm.uabierta.responses.LostsResponse;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoadMyRecovered extends Job {
    private final String TAG = "LoadLosts Job";
    private LoadLostsListener mListener;
    private int id, limit, page;

    public LoadMyRecovered(LoadLostsListener listener, int id, int limit, int page) {
        super(new Params(1).requireNetwork());
        mListener = listener;
        this.id = id;
        this.limit = limit;
        this.page = page;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        if (mListener != null) {
            mListener.onInitLoad();
        }
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        LostsResponse lostsResponse = null;
        Service service = ServiceGenerator.createService(Service.class);
        Call<LostsResponse> call = service.LoadRecovered(id, limit, page);
        Response<LostsResponse> lostResponse = call.execute();

        if(lostResponse.isSuccess()) {
            lostsResponse = lostResponse.body();
            //for(Found found : foundsResponse.getData()) {
            //    Log.e("TITLE ",found.getTitle()+" "+found.getUser().getPrefix().getName());
            //}
        } else {
            Log.i("Error: ", lostResponse.errorBody().toString());
        }

        // TODO Notificar al listener
        if (mListener != null) {
            mListener.onFinishLoad(lostsResponse);
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        if (mListener != null) {
            mListener.onFinishLoad(null);
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface Service {
        @GET("users/{id}/recover")
        Call<LostsResponse> LoadRecovered(@Path("id") int id, @Query("limit") int limit, @Query("page") int page);
    }
}