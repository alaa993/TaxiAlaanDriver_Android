package com.taxialaan.drivers.Api.utils;


import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorUtils {

    public static APIError parseError(Retrofit retrofit, Response<?> response) {
        Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
        APIError error;
        try {
            error = converter.convert(response.errorBody());
            if (error != null) {
                error.setStatusCode(response.code());
            }
        } catch (IOException e) {
            return new APIError();
        }
        return error;
    }
}