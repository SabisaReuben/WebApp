package co.ciao.bluclub.network;



import com.google.gson.Gson;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionService {
    private static final String BASE_URL = "https://bluclub.ciao.co.ke";
    private static OkHttpClient CLIENT = new OkHttpClient();
    private static final Gson GSON = new Gson();
    private static final GsonConverterFactory FACTORY = GsonConverterFactory.create(GSON);


    private static final Retrofit RETROFIT = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(FACTORY)
            .client(CLIENT)
            .build();

    public static void addInterceptor(Interceptor interceptor) {
        if (!CLIENT.interceptors().contains(interceptor)) {
            CLIENT = CLIENT.newBuilder().addInterceptor(interceptor).build();
        }
    }

    public static <T> T create(Class<T> tClass) {
        return RETROFIT.create(tClass);
    }

}
