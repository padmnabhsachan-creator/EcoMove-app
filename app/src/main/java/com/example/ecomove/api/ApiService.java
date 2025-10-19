package com.example.ecomove.api;

import com.example.ecomove.travel.model.TravelVerification;
import com.example.ecomove.travel.model.TravelVerificationRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.example.ecomove.models.Voucher;

import java.util.List;

public interface ApiService {
    @POST("users/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("users/signup")
    Call<Void> signup(@Body SignupRequest request);

    @POST("users/logout")
    Call<ResponseBody> logout(@Header("Authorization") String token);

    @GET("users/{userId}")
    Call<UserResponse> getUserById(@Path("userId") String userId);

    @POST("users/{userId}/addCredits")
    Call<Void> addCredits(@Path("userId") String userId, @Body CreditRequest request);

    @POST("redemptions")
    Call<VoucherRedemption> redeemVoucher(@Body VoucherRedemption redemption);

    @GET("vouchers/user/{userId}")
    Call<List<Voucher>> getVouchersByUser(@Path("userId") String userId);

    @Headers("Content-Type: application/json")
    @POST("/travel/verify")
    Call<Void> verifyTravel(@Body TravelVerificationRequest request);

    @Headers("Content-Type: text/plain")
    @POST("travel/verify/qr")
    Call<TravelVerification> verifyViaQr(@Body String qrContent);

    @GET("travel/user/{userId}")
    Call<List<TravelVerification>> getTravelHistory(@Path("userId") String userId);


}