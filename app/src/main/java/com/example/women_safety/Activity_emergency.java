package com.example.women_safety;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class Activity_emergency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency);
        RecyclerView helplineList = findViewById(R.id.helpline_list);
        List<Helpline> helplines = Arrays.asList(
                new Helpline("Women Helpline", "1091"),
                new Helpline("Police Emergency", "100"),
                new Helpline("Domestic Abuse", "181"),
                new Helpline("Child Helpline", "1098"),
                new Helpline("Senior Citizens", "14567")
        );

        HelplineAdapter adapter = new HelplineAdapter(this, helplines);
        helplineList.setLayoutManager(new LinearLayoutManager(this));
        helplineList.setAdapter(adapter);

    }
}