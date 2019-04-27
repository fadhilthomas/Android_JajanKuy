package com.lappungdev.jajankuy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.model.Order;

import java.util.List;
import java.util.Objects;


public class RecyclerViewOrderAdapter extends RecyclerView.Adapter<RecyclerViewOrderAdapter.ViewHolder> {

    private List<Order> orderList;
    private OrderClick listener;
    private DatabaseReference databaseReferenceOrder;
    private Context context;

    public RecyclerViewOrderAdapter(Context context, List<Order> TempList) {
        this.orderList = TempList;
        this.context = context;
    }

    public void setOrderClickListener(OrderClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        String databasePathOrder = "jajankuy_db/order";
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference(databasePathOrder);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Order orderInfo = orderList.get(position);
        if (orderInfo.getOrderStatus().contains("Selesai")) {
            holder.ivOrderStatus.setImageResource(R.drawable.ic_order_received);
            holder.tvOrderStatus.setTextColor(Color.parseColor("#D43644"));
        } else {
            holder.ivOrderStatus.setImageResource(R.drawable.ic_order_process);
            holder.tvOrderStatus.setTextColor(Color.parseColor("#0f8fe8"));
        }
        holder.tvOrderStatus.setText(orderInfo.getOrderStatus());
        holder.ivOrderStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.tvOrderSellerName.setText(orderInfo.getOrderSellerName());
        holder.tvOrderFoodName.setText(orderInfo.getOrderFoodName());
        holder.tvOrderFoodPrice.setText(String.format("Rp. %s", getMoney(String.valueOf(orderInfo.getOrderFoodPrice()))));
    }

    private String getMoney(String str2) {
        StringBuilder str = new StringBuilder(str2);
        int idx = str.length() - 3;

        while (idx > 0) {
            str.insert(idx, ".");
            idx = idx - 3;
        }

        return str.toString();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderSellerName, tvOrderFoodName, tvOrderFoodPrice, tvOrderStatus;
        ImageView ivOrderStatus;
        CardView cvOrder;

        ViewHolder(final View itemView) {
            super(itemView);

            tvOrderSellerName = itemView.findViewById(R.id.tvOrderDestinationName);
            tvOrderFoodName = itemView.findViewById(R.id.tvOrderDestinationAddress);
            tvOrderFoodPrice = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            ivOrderStatus = itemView.findViewById(R.id.ivOrderStatus);
            cvOrder = itemView.findViewById(R.id.cvOrder);

            itemView.setOnClickListener(v -> {
                if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("SLR - ")) {
                    context = itemView.getContext();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder
                            .setMessage("Apakah kamu yakin ingin mengubah status pemesanan ini? ")
                            .setCancelable(true)
                            .setPositiveButton("Selesai", (dialog, id) -> {
                                String orderID = orderList.get(getAdapterPosition()).getOrderID();
                                databaseReferenceOrder.child(orderID).child("orderStatus").setValue("Selesai");
                                listener.onClicked(true);
                            })
                            .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    TextView pesan = alertDialog.findViewById(android.R.id.message);
                    Objects.requireNonNull(pesan).setTextSize(15);

                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

                    Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    c.setTextColor(ContextCompat.getColor(context, R.color.colorHitam));
                }
            });
        }
    }
}
