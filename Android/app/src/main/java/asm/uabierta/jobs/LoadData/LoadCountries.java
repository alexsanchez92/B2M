package asm.uabierta.jobs.LoadData;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.responses.CountriesResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.ServiceGenerator;
import retrofit.Call;
import retrofit.Response;
import retrofit.http.GET;

/**
 * Created by Alex on 26/07/2016.
 */
public class LoadCountries extends Job {
    private final String TAG = "LoadCountries Job";

    public LoadCountries() {
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
        Call<CountriesResponse> call = service.LoadCountries();
        Response<CountriesResponse> countryResponse = call.execute();

        if(countryResponse.isSuccess()) {
            BusProvider.getInstance().post(countryResponse.body());
            //for(Found found : foundsResponse.getData()) {
            //    Log.e("TITLE ",found.getTitle()+" "+found.getUser().getPrefix().getName());
            //}
        } else {
            Log.i("Error: ", countryResponse.errorBody().toString());
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
        @GET("countries")
        Call<CountriesResponse> LoadCountries();
    }
}