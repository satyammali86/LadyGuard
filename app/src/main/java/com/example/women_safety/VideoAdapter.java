package com.example.women_safety;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<String> videoUrls;

    public VideoAdapter(Context context, List<String> videoUrls) {
        this.context = context;
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoUrl = videoUrls.get(position);
        holder.webView.loadData(
                "<iframe width='100%' height='100%' src='" + videoUrl + "' frameborder='0' allowfullscreen></iframe>",
                "text/html",
                "utf-8"
        );
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.webview_video);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
        }
    }
}
