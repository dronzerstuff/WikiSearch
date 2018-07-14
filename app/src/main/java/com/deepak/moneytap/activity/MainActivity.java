package com.deepak.moneytap.activity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.claudiodegio.msv.BaseMaterialSearchView;
import com.claudiodegio.msv.OnSearchViewListener;
import com.claudiodegio.msv.SuggestionMaterialSearchView;
import com.deepak.moneytap.R;
import com.deepak.moneytap.adapter.ResultsAdapter;
import com.deepak.moneytap.models.Page;
import com.deepak.moneytap.models.SearchResults;
import com.deepak.moneytap.retrifitUtils.API;
import com.deepak.moneytap.retrifitUtils.ResponseCachingInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnSearchViewListener {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.search)
    AutoCompleteTextView search;

    Handler handler;
    private ResultsAdapter adapter;

    List<Page> pages;
    @BindView(R.id.sv)
    BaseMaterialSearchView mSearchView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    List<String> searchHistory = new ArrayList<>();
    SuggestionMaterialSearchView cast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        pages = new ArrayList<>();
        handler = new Handler();
        cast = (SuggestionMaterialSearchView) mSearchView;
        cast.setSuggestion(searchHistory);
        mSearchView.setOnSearchViewListener(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.showSearch(true);
                ((SuggestionMaterialSearchView) mSearchView).showSuggestion();
            }
        });


    }


    private void getSearchResults(String query) {
        progressBar.setVisibility(View.VISIBLE);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        int cacheSize = 10 * 1024 * 1024;
        Cache cache = new Cache(getCacheDir(), cacheSize);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new ResponseCachingInterceptor())
                .addInterceptor(new OfflineResponseCacheInterceptor())
                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        API apicall = retrofit.create(API.class);
        Map<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("format", "json");
        params.put("formatversion", "2");
        params.put("prop", "pageimages|pageterms");
        params.put("generator", "prefixsearch");
        params.put("piprop", "thumbnail");
        params.put("pithumbsize", "500");
        params.put("pilimit", "200");
        params.put("gpssearch", query);
        params.put("gpslimit", "50");


        apicall.getSearchResults(params).enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SearchResults> call, Response<SearchResults> response) {
                progressBar.setVisibility(View.GONE);
                SearchResults searchResults = response.body();
                if (searchResults != null && searchResults.getQuery() != null
                        && searchResults.getQuery().getPages() != null &&
                        !searchResults.getQuery().getPages().isEmpty() && mRecyclerView != null) {

                    pages.clear();
                    pages.addAll(searchResults.getQuery().getPages());
                    adapter = new ResultsAdapter(MainActivity.this, pages);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Results found...", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {


                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }

    @Override
    public boolean onQueryTextSubmit(final String s) {
        System.out.println("data:" + s);
        searchHistory.add(s);
        cast.setSuggestion(searchHistory);
        mSearchView.closeSearch();
        search.setText(s);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSearchResults(s);
            }
        }, 600);
        return true;
    }

    @Override
    public void onQueryTextChange(String s) {

    }

    class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            NetworkInfo networkInfo = ((ConnectivityManager)
                    (getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
            if (networkInfo == null) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Access-Control-Allow-Origin")
                        .removeHeader("Vary")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale= 60")
                        .build();
            }
            return chain.proceed(request);
        }
    }


}
