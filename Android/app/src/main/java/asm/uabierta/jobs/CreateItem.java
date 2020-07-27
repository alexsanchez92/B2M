package asm.uabierta.jobs;


import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import asm.uabierta.events.CreateFoundEvent;
import asm.uabierta.events.CreateLostEvent;
import asm.uabierta.events.SimpleResponseEvent;
import asm.uabierta.responses.SingleIdResponse;
import asm.uabierta.utils.BusProvider;
import asm.uabierta.utils.ServiceGenerator;
import asm.uabierta.utils.ServiceUploadGenerator;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PartMap;

/**
 * Created by alex on 17/11/15.
 */
public class CreateItem extends Job {
    private final String TAG = "Create Item Job";

    private String token, photoUri, title, description, placeDetails, foundDate, property, startDate, endDate, havePlaceDetails;
    private Integer hasPhoto, placeId, haveIt, havePlaceId;
    private int type;

    public CreateItem(String token, String photoUri, String title, String description,
                      Integer placeId, String placeDetails, String foundDate, String property, Integer havePlaceId, String havePlaceDetails){
        super(new Params(1).requireNetwork().persist());
        this.type = 0;
        this.token = token;
        this.photoUri = photoUri;
        this.hasPhoto = (this.photoUri!=null) ? 1 : 0;
        this.title = title;
        this.description = description;
        this.placeId = placeId;
        this.placeDetails = placeDetails;
        this.foundDate = foundDate;
        this.property = property;
        this.haveIt = 0;
        this.havePlaceId = havePlaceId;
        this.havePlaceDetails = havePlaceDetails;
    }

    public CreateItem(String token, String photoUri, String title, String description,
                      Integer placeId, String placeDetails, String foundDate, String property){
        super(new Params(1).requireNetwork().persist());
        this.type = 0;
        this.token = token;
        this.photoUri = photoUri;
        this.hasPhoto = (this.photoUri!=null) ? 1 : 0;
        this.title = title;
        this.description = description;
        this.placeId = placeId;
        this.placeDetails = placeDetails;
        this.foundDate = foundDate;
        this.property = property;
        this.haveIt = 1;
        this.havePlaceId = null;
        this.havePlaceDetails = null;
    }

    public CreateItem(String token, String photoUri, String title, String description,
                      Integer placeId, String placeDetails, String startDate, String endDate, String flag){
        super(new Params(1).requireNetwork().persist());
        this.type = 1;
        this.token = token;
        this.photoUri = photoUri;
        this.hasPhoto = (this.photoUri!=null) ? 1 : 0;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.placeDetails = placeDetails;
    }

    @Override
    public void onAdded() {
        Log.d(TAG, "Job Added");
        switch (type){
            case 1:
                BusProvider.getInstance().post("lost");
            break;
            case 0:
                BusProvider.getInstance().post("found");
            break;
        }
    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "Job Run");

        Call<SingleIdResponse> call;
        if(type==1){
            ServiceLost service = ServiceUploadGenerator.createService(ServiceLost.class);
            Map<String, RequestBody> map = new HashMap<>();

            if(photoUri!=null) {
                File imgFile = new File(photoUri);
                RequestBody photoFileRequest = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                map.put("image\"; filename=\"" + imgFile.getName() + "\"", photoFileRequest);
            }

            map.put("title", ServiceGenerator.toRequestBody(title));
            map.put("description", ServiceGenerator.toRequestBody(description));
            map.put("hasPhoto", ServiceGenerator.toRequestBody(Integer.toString(hasPhoto)));
            if(placeId!=null) {
                map.put("placeId", ServiceGenerator.toRequestBody(Integer.toString(placeId)));
                map.put("placeDetails", ServiceGenerator.toRequestBody(placeDetails));
            }
            map.put("startDate", ServiceGenerator.toRequestBody(startDate));
            map.put("endDate", ServiceGenerator.toRequestBody(endDate));

            /*for (Map.Entry<String,RequestBody> entry : map.entrySet()) {
                String key = entry.getKey();
                RequestBody value = entry.getValue();

                Log.e("KEYMAP: ", key+"  "+value);
            }*/

            call = service.createLost(token, map);
            call.enqueue(new Callback<SingleIdResponse>() {
                @Override
                public void onResponse(Response<SingleIdResponse> response, Retrofit retrofit) {
                    //Log.e("Upload", "Request data " + new Gson().toJson(response));
                    Log.e("CREATE LOST ID",response.code()+"");
                    if(response.isSuccess())
                        BusProvider.getInstance().post(new CreateLostEvent(false, response.body(), ""));
                    else
                        BusProvider.getInstance().post(new CreateLostEvent(true, "JobFailed"));
                }

                @Override
                public void onFailure(Throwable t) {
                    BusProvider.getInstance().post(new CreateLostEvent(true, "JobFailed"));
                }
            });
        }
        else{
            ServiceFound service = ServiceUploadGenerator.createService(ServiceFound.class);
            Map<String, RequestBody> map = new HashMap<>();

            if(photoUri!=null) {
                File imgFile = new File(photoUri);
                RequestBody photoFileRequest = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                map.put("image\"; filename=\"" + imgFile.getName() + "\"", photoFileRequest);
            }

            map.put("title", ServiceGenerator.toRequestBody(title));
            map.put("description", ServiceGenerator.toRequestBody(description));
            map.put("hasPhoto", ServiceGenerator.toRequestBody(Integer.toString(hasPhoto)));
            if(placeId!=null) {
                map.put("placeId", ServiceGenerator.toRequestBody(Integer.toString(placeId)));
                map.put("placeDetails", ServiceGenerator.toRequestBody(placeDetails));
            }
            map.put("foundDate", ServiceGenerator.toRequestBody(foundDate));
            map.put("property", ServiceGenerator.toRequestBody(property));
            map.put("haveIt", ServiceGenerator.toRequestBody(Integer.toString(haveIt)));
            if(haveIt==0 && havePlaceId!=null) {
                map.put("havePlaceId", ServiceGenerator.toRequestBody(Integer.toString(havePlaceId)));
                map.put("havePlaceDetails", ServiceGenerator.toRequestBody(havePlaceDetails));
            }

            /*for (Map.Entry<String,RequestBody> entry : map.entrySet()) {
                String key = entry.getKey();
                RequestBody value = entry.getValue();

                Log.e("KEYMAP: ", key+"  "+value);
            }*/

            call = service.createFound(token, map);
            call.enqueue(new Callback<SingleIdResponse>() {
                @Override
                public void onResponse(Response<SingleIdResponse> response, Retrofit retrofit) {
                    //Log.e("Upload", "Request data " + new Gson().toJson(response));
                    if(response.isSuccess())
                        BusProvider.getInstance().post(new CreateFoundEvent(false, response.body(), ""));
                    else
                        BusProvider.getInstance().post(new CreateFoundEvent(true, "JobFailed"));
                }

                @Override
                public void onFailure(Throwable t) {
                    BusProvider.getInstance().post(new CreateFoundEvent(true, "JobFailed"));
                }
            });
        }
    }

    @Override
    protected void onCancel() {
        Log.d(TAG, "Job Cancelled");
        BusProvider.getInstance().post(new SimpleResponseEvent(true, "JobFailed"));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.e(TAG, "Failed", throwable);
        return false;
    }

    interface ServiceFound {
        @Multipart
        @POST("found")
        Call<SingleIdResponse> createFound(@Header("api-key") String token, @PartMap Map<String, RequestBody> request);
    }

    interface ServiceLost {
        @Multipart
        @POST("lost")
        Call<SingleIdResponse> createLost(@Header("api-key") String token, @PartMap Map<String, RequestBody> request);
    }
}