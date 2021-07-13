package com.chrisvanry.weightlosscontest.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.data.Competition;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    private ArrayList<String> competitionsList;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> competitionsList) {
        this.mContext = mContext;
        this.competitionsList = competitionsList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.competition_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        // Textview - competition name
        holder.textView.setText(competitionsList.get(position));

        // onClick listener
        holder.parentLayout.setOnClickListener(v -> {
            Log.d(TAG, "onClick: clicked on: " + competitionsList.get(position));
            Toast.makeText(mContext, competitionsList.get(position), Toast.LENGTH_SHORT).show();
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
