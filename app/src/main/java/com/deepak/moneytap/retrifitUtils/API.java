package com.deepak.moneytap.retrifitUtils;


import com.deepak.moneytap.models.SearchResults;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;


public interface API {

    //get apollo promo codes
    @GET("w/api.php")
    Call<SearchResults> getSearchResults(@QueryMap Map<String, String> params);
}
