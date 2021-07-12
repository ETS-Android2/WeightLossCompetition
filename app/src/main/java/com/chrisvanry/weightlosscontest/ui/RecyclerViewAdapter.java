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

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mCompNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mCompNames, Context mContext) {
        this.mCompNames = mCompNames;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        holder.competitionListItem.setText(mCompNames.get(position));

        holder.parentLayout.setOnClickListener(v -> {
            Log.d(TAG, "onClick: clicked on: " + mCompNames.get(position));
            Toast.makeText(mContext, mCompNames.get(position), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mCompNames.size());
        return mCompNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView competitionListItem;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            competitionListItem = itemView.findViewById(R.id.competition_list_item);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
