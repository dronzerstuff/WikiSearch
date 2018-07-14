package com.deepak.moneytap.retrifitUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by deepak on 7/14/2018.
 */

public class ResponseCachingInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Access-Control-Allow-Origin")
                .removeHeader("Vary")
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=60")
                .build();
    }
}
