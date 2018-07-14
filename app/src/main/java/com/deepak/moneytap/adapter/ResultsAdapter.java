package com.deepak.moneytap.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.deepak.moneytap.R;
import com.deepak.moneytap.activity.WebviewActivity;
import com.deepak.moneytap.models.Page;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private List<Page> pages;
    private Context context;

    public ResultsAdapter(Context context, List<Page> pages) {
        this.context = context;
        this.pages = pages;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewTitle;
        TextView mTextViewInfo;
        ImageView photo;

        public ViewHolder(View v) {
            super(v);
            mTextViewTitle = v.findViewById(R.id.title);
            mTextViewInfo = v.findViewById(R.id.description);
            photo = v.findViewById(R.id.image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Page page = pages.get(position);
        String title = page.getTitle();
        if (page.getTerms() != null) {
            holder.mTextViewInfo.setText(page.getTerms().getDescription());

        }

        holder.mTextViewTitle.setText(title);
        title = title.replaceAll(" ", "_");
        final String link = "https://en.wikipedia.org/wiki/" + title;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchListing(link);
            }
        });
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .fitCenter();
        if (page.getThumbnail() != null) {
            Glide.with(context).load(page.getThumbnail().getSource())
                    .apply(requestOptions)
                    .thumbnail(0.1f)

                    .into(holder.photo);
        } else {

            Glide.with(context).load(R.drawable.noimage)
                    .apply(requestOptions)
                    .into(holder.photo);
        }
    }


    private void showSearchListing(String link) {
        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra("URL", link);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

}
