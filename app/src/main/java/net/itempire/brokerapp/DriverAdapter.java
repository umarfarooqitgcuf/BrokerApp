package net.itempire.brokerapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverHistoryViewHolder> {

    private Context mContext;
    private List<DriverHistoryCustom> driveHistory;

    public DriverAdapter(Context mContext, List<DriverHistoryCustom> driveHistory) {
        this.mContext = mContext;
        this.driveHistory = driveHistory;
    }
    @NonNull
    @Override
    public DriverHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.custom_design_completed_service,null);
        return  new DriverHistoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DriverHistoryViewHolder holder, int position) {

        DriverHistoryCustom hitory = driveHistory.get(position);
        holder.textRideDate.setText(hitory.getRideDate());
        holder.textRideAmmount.setText(hitory.getRideAmmount());
        holder.textRideStarted.setText(hitory.getRideStarted());
        holder.textRideEnded.setText(hitory.getRideEnded());
        holder.ridername.setText(hitory.getRidername());
/*
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,DiscriptionActivity.class);
                mContext.startActivity(intent);
            }
        });*/

    }
    @Override
    public int getItemCount() {
        return driveHistory.size();
    }

    public class DriverHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView textRideDate,textRideAmmount,textRideStarted,textRideEnded,ridername;
        RelativeLayout relativeLayout;
        public DriverHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            /*textRideDate =itemView.findViewById(R.id.text_date);
            textRideAmmount =itemView.findViewById(R.id.text_amount);
            textRideStarted =itemView.findViewById(R.id.text_start_ride);
            textRideEnded =itemView.findViewById(R.id.text_end_ride);
            ridername =itemView.findViewById(R.id.text_rider_name);*/
/*
            relativeLayout = itemView.findViewById(R.id.layout_cardView);
*/
        }
    }
}
