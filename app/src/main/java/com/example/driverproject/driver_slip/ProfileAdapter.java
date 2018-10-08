package com.example.driverproject.driver_slip;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewer> {
    private LayoutInflater inflater;
    private Context ctx;
    private List<Vehicle> vehicleList;

    public ProfileAdapter(Context ctx, List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ProfileViewer onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.layout_rv, null);
        ProfileViewer holder = new ProfileViewer(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewer profileViewer, int i) {
        profileViewer.textViewS.setText("Slip");
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    class ProfileViewer extends RecyclerView.ViewHolder {

        TextView textViewS;
        Button buttonE, buttonD;


        public ProfileViewer(@NonNull View itemView) {
            super(itemView);

            textViewS = (TextView) itemView.findViewById(R.id.textViewS);
            buttonD = (Button) itemView.findViewById(R.id.buttonD);
            buttonE = (Button) itemView.findViewById(R.id.buttonE);
        }
    }
}
