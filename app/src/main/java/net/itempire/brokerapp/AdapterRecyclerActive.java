package net.itempire.brokerapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterRecyclerActive extends RecyclerView.Adapter<AdapterRecyclerActive.AdapterRecyclerViewHolder>
{

    Context pendingAdapterContext;
    List<AdapterGetterSetter> pandingList;

    public AdapterRecyclerActive(Context mContext, List<AdapterGetterSetter> pandingList) {
        this.pendingAdapterContext = mContext;
        this.pandingList = pandingList;
    }

    @NonNull
    @Override
    public AdapterRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(pendingAdapterContext);
        View view = inflater.inflate(R.layout.custom_design_active_services,null);
        return  new AdapterRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerViewHolder holder, int position) {

        AdapterGetterSetter hitory = pandingList.get(position);
        holder.nameServiceProvider.setText(hitory.getNameServiceProvider());
        holder.locationServiceProvider.setText(hitory.getLocationServiceProvider());
        holder.serviceServiceProvider.setText("Active");
        holder.request_date_time.setText(hitory.getAmountServiceProvider());
        holder.accet_date_time.setText(hitory.getReject_data_time());
    }

    @Override
    public int getItemCount() {
        return pandingList.size();
    }

    public class AdapterRecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameServiceProvider,locationServiceProvider,serviceServiceProvider,request_date_time,accet_date_time;

        public AdapterRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameServiceProvider = itemView.findViewById(R.id.history_user_name_place);
            locationServiceProvider = itemView.findViewById(R.id.order_address);
            serviceServiceProvider = itemView.findViewById(R.id.status_place);
            request_date_time = itemView.findViewById(R.id.history_order_date_place);
            accet_date_time = itemView.findViewById(R.id.history_order_time_place);
        }
    }
}