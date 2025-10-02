package com.example.women_safety;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Activity_selfdefence extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<String> videoUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfdefence);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoUrls = new ArrayList<>();
        loadVideos();

        videoAdapter = new VideoAdapter(this, videoUrls);
        recyclerView.setAdapter(videoAdapter);
    }

    private void loadVideos() {
        // Add YouTube video URLs (embedded format)
        videoUrls.add("https://www.youtube.com/embed/KVpxP3ZZtAc"); // 5 Self-Defense Moves Every Woman Should Know
        videoUrls.add("https://www.youtube.com/embed/WCn4GBcs84s"); // Self Defence for Women - The most Effective Techniques
        videoUrls.add("https://www.youtube.com/embed/SSnnte5cVIo"); // 8 Self-defense techniques every woman should know
        videoUrls.add("https://www.youtube.com/embed/R_IVjAvnEZc"); // Most Common Women's Self Defense - Krav Maga for Beginners
        videoUrls.add("https://www.youtube.com/embed/m2uKwkaa6Vw"); // 5 EASY Self Defence Moves Every Woman MUST Learn

    }
}
