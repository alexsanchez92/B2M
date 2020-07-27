package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.listeners.LoadFoundsListener;
import asm.uabierta.responses.FoundsResponse;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoadMyFounds extends Job {
    private final String TAG = "LoadFounds Job";
    private LoadFoundsListener mListener;
    private int id, limit, page;

    public LoadMyFounds(LoadFoundsListener listener, int id, int limit, int page) {
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

        FoundsResponse foundsResponse = null;
        Service service = ServiceGenerator.createService(Service.class);
        Call<FoundsResponse> call = service.LoadFounds(id, limit, page);
        Response<FoundsResponse> foundResponse = call.execute();

        if(foundResponse.isSuccess()) {
            foundsResponse = foundResponse.body();
            //for(Found found : foundsResponse.getData()) {
            //    Log.e("TITLE ",found.getTitle()+" "+found.getUser().getPrefix().getName());
            //}
        } else {
            Log.i("Error: ", foundResponse.errorBody().toString());
        }

        // TODO Notificar al listener
        if (mListener != null) {
            mListener.onFinishLoad(foundsResponse);
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
        @GET("users/{id}/found")
        Call<FoundsResponse> LoadFounds(@Path("id") int id, @Query("limit") int limit, @Query("page") int page);
    }
}