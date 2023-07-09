package com.akuh.twiliootp.interfaces;

import com.akuh.twiliootp.model.OTPModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

        @Headers({"Content-Type: application/json"})
        @POST("api/verify")
        Call<OTPModel> postVerifyCode(
                @Body OTPModel body
        );

        @Headers({"Content-Type: application/json"})
        @POST("api/request")
        Call<OTPModel> postRequestCode(
                @Body OTPModel body
        );



    }

