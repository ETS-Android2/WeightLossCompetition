package com.chrisvanry.weightlosscompetition.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chrisvanry.weightlosscompetition.R;
import com.chrisvanry.weightlosscompetition.data.Competition;

import java.util.ArrayList;

public class CompListRecyclerViewAdapter extends RecyclerView.Adapter<CompListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CompListRecycler";
    private Context mContext;
    private ArrayList<Competition> competitionsList;

    public CompListRecyclerViewAdapter(Context mContext, ArrayList<Competition> competitionsList) {
        this.mContext = mContext;
        this.competitionsList = competitionsList;
    }

    @NonNull
    @Override
    public CompListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.competition_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompListRecyclerViewAdapter.ViewHolder holder, int position) {

        String competitionId = competitionsList.get(position).getCompetitionId();

        // Textview - competition name
        holder.textView.setText(competitionsList.get(position).getName());

        // onClick listener
        holder.parentLayout.setOnClickListener(v -> {
            Log.d(TAG, "onClick: compID: " + competitionsList.get(position).getCompetitionId());
            Intent intent = new Intent(v.getContext(), CompDetails.class);
            intent.putExtra("comp_id", competitionId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return competitionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // widgets
        RelativeLayout parentLayout;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }

    }

}
