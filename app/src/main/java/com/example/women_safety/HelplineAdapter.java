package com.example.women_safety;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HelplineAdapter extends RecyclerView.Adapter<HelplineAdapter.HelplineViewHolder> {

    private Context context;
    private List<Helpline> helplineList;

    public HelplineAdapter(Context context, List<Helpline> helplineList) {
        this.context = context;
        this.helplineList = helplineList;
    }

    @NonNull
    @Override
    public HelplineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.helpline_item, parent, false);
        return new HelplineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelplineViewHolder holder, int position) {
        Helpline helpline = helplineList.get(position);

        holder.name.setText(helpline.getName());
        holder.number.setText(helpline.getNumber());

        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + helpline.getNumber()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return helplineList.size();
    }

    public static class HelplineViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        ImageView icon, callButton;
        CardView card;

        public HelplineViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.helpline_name);
            number = itemView.findViewById(R.id.helpline_number);
            icon = itemView.findViewById(R.id.helpline_icon);
            callButton = itemView.findViewById(R.id.call_button);
            card = (CardView) itemView;
        }
    }
}
