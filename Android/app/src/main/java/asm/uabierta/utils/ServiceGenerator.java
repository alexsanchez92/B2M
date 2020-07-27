package asm.uabierta.utils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by alex on 19/02/16.
 */
public class ServiceGenerator {

    private static OkHttpClient httpClient = new OkHttpClient();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ServerConnection.urlApi)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public static RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), value);
        //RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
}