package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.listeners.LoadFoundsListener;
import asm.uabierta.listeners.LoadLostsListener;
import asm.uabierta.models.Found;
import asm.uabierta.models.Lost;
import asm.uabierta.responses.FoundsResponse;
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
public class LoadSimilars extends Job {
    private final String TAG = "LoadFounds Job";
    private LoadFoundsListener foundListener;
    private LoadLostsListener lostListener;
    private int id, type;

    public LoadSimilars(LoadFoundsListener listener, int id, int type) {
        super(new Params(1).requireNetwork());
        foundListener = listener;
        this.id = id;
        this.type = type;
    }

    public LoadSimilars(LoadLostsListener listener, int id, int type) {
        super(new Params(1).requireNetwork());
        lostListener = listener;
        this.id = id;
        this.type = type;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        if (foundListener != null) {
            foundListener.onInitLoad();
        }
        if (lostListener != null) {
            lostListener.onInitLoad();
        }
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        switch (type){
            case 0:
                LostsResponse lostsResponse = null;
                ServiceLost servL = ServiceGenerator.createService(ServiceLost.class);
                Call<LostsResponse> callL = servL.LoadLostSimilar(id);
                Response<LostsResponse> lostResponse = callL.execute();

                if(lostResponse.isSuccess()) {
                    lostsResponse = lostResponse.body();
                    //for(Lost lost : lostsResponse.getData()) {
                        //    Log.e("TITLE ",lost.getTitle()+" "+lost.getUser().getPrefix().getName());
                    //}
                } else {
                    Log.i("Error: ", lostResponse.errorBody().toString());
                    Log.i("Error: ", lostResponse.code()+"");
                }

                // TODO Notificar al listener
                if (lostListener != null) {
                    lostListener.onFinishLoad(lostsResponse);
                }

            break;
            case 1:
                FoundsResponse foundsResponse = null;
                ServiceFound servF = ServiceGenerator.createService(ServiceFound.class);
                Call<FoundsResponse> callF = servF.LoadFoundSimilar(id);
                Response<FoundsResponse> foundResponse = callF.execute();

                if(foundResponse.isSuccess()) {
                    foundsResponse = foundResponse.body();
                    //for(Found found : foundsResponse.getData()) {
                    //    Log.e("TITLE ",found.getTitle()+" "+found.getUser().getPrefix().getName());
                    //}
                } else {
                    Log.i("Error: ", foundResponse.errorBody().toString());
                    Log.i("Error: ", foundResponse.code()+"");
                }

                // TODO Notificar al listener
                if (foundListener != null) {
                    foundListener.onFinishLoad(foundsResponse);
                }
            break;
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        if (foundListener != null) {
            foundListener.onFinishLoad(null);
        }
        if (lostListener != null) {
            lostListener.onFinishLoad(null);
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface ServiceFound {
        @GET("lost/{id}/similar")
        Call<FoundsResponse> LoadFoundSimilar(@Path("id") int id);
    }

    interface ServiceLost {
        @GET("found/{id}/similar")
        Call<LostsResponse> LoadLostSimilar(@Path("id") int id);
    }
}