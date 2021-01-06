package co.ciao.bluclub.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AccountVerificationAPI {
    @POST("")
    @FormUrlEncoded
    Call<Success> verify(@Field("email") String email, @Field("country") String country);
}
