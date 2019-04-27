package com.lappungdev.jajankuy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.model.Menu;
import com.lappungdev.jajankuy.model.Order;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static com.lappungdev.jajankuy.fragment.HomeFragment.loctNow;


public class RecyclerViewAdapterCategory extends RecyclerView.Adapter<RecyclerViewAdapterCategory.ViewHolder> {

    private DatabaseReference databaseReferenceMenu;
    private DatabaseReference databaseReferenceOrder;
    private DatabaseReference databaseReferenceSeller;
    private String menuDistance = "-";
    private ProgressDialog progressDialog;
    private Typeface custom_font;
    private String menuLocation;
    private Context context;
    private List<Menu> menuList;

    public RecyclerViewAdapterCategory(Context context, List<Menu> TempList) {
        this.menuList = TempList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        String databasePathMenu = "jajankuy_db/menu";
        String databasePathSeller = "jajankuy_db/seller";
        String databasePathOrder = "jajankuy_db/order";
        databaseReferenceMenu = FirebaseDatabase.getInstance().getReference(databasePathMenu);
        databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference(databasePathOrder);
        custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_category, parent, false);
        return new ViewHolder(view);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Menu menuInfo = menuList.get(position);

        holder.tvMenuName.setText(menuInfo.getMenuName());
        holder.tvMenuState.setText(menuInfo.getMenuState());
        holder.tvSellerName.setText(menuInfo.getMenuSellerName());
        holder.tvMenuPrice.setText(String.format("Rp. %s", getMoney(String.valueOf(menuInfo.getMenuPrice()))));
        Query query = databaseReferenceSeller.child(menuInfo.getMenuSellerZID());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuLocation = snapshot.child("sellerLocation").getValue(String.class);
                if (!menuLocation.isEmpty()) {
                    String[] lok = menuLocation.split(",");
                    Double bLong = Double.parseDouble(lok[1]);
                    Double bLat = Double.parseDouble(lok[0]);
                    DecimalFormat kl = new DecimalFormat("#.#");
                    kl.setRoundingMode(RoundingMode.CEILING);
                    Double kilo = haversine(bLat, bLong, Double.parseDouble(String.valueOf(loctNow.latitude)), Double.parseDouble(String.valueOf(loctNow.longitude)));
                    int menuMeter = (int) (kilo * 1000);
                    if (menuMeter > 100) {
                        menuDistance = kl.format(kilo) + " km";
                    } else {
                        menuDistance = menuMeter + " m";
                    }
                    menuInfo.setMenuMeter(menuMeter);
                }
                holder.tvMenuMeter.setText(menuDistance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Glide.with(context).load(menuInfo.getMenuPhoto())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.ivMenuLoad.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.ivMenuLoad.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivMenuPhoto);
    }

    @Override
    public int getItemCount() {

        return menuList.size();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6378.16;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMenuPhoto, ivMenuLoad;
        TextView tvMenuName, tvMenuPrice, tvSellerName, tvMenuState, tvMenuMeter;
        Button btBeli;

        ViewHolder(final View itemView) {
            super(itemView);

            ivMenuLoad = itemView.findViewById(R.id.ivMenuLoad);
            ivMenuPhoto = itemView.findViewById(R.id.ivMenuPhoto);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvMenuState = itemView.findViewById(R.id.tvMenuState);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            tvMenuMeter = itemView.findViewById(R.id.tvMenuMeter);
            btBeli = itemView.findViewById(R.id.btBeli);

            btBeli.setOnClickListener(v -> {
                context = itemView.getContext();
                progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Proses ...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String orderCustomerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String orderCustomerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String orderFoodID = menuList.get(getAdapterPosition()).getMenuID();
                String orderFoodName = menuList.get(getAdapterPosition()).getMenuName();
                String orderFoodPrice = menuList.get(getAdapterPosition()).getMenuPrice();
                String orderID = String.format("ORD-%s", Long.toHexString(System.currentTimeMillis()).toUpperCase());
                String orderSellerID = menuList.get(getAdapterPosition()).getMenuSellerZID();
                String orderSellerName = menuList.get(getAdapterPosition()).getMenuSellerName();
                String orderStatus = "Proses";
                String orderTimestamp = String.valueOf(System.currentTimeMillis());
                String orderZID = String.valueOf((9999999999999L + (-1 * Long.valueOf(orderTimestamp))));


                Order order = new Order(orderCustomerID, orderCustomerName, orderFoodID, orderFoodName, orderFoodPrice, orderID, orderSellerID, orderSellerName, orderStatus, orderTimestamp, orderZID);
                databaseReferenceOrder.child(orderID).setValue(order)
                        .addOnSuccessListener(aVoid -> new Handler().postDelayed(() -> {
                            progressDialog.dismiss();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                            alertDialogBuilder
                                    .setMessage("Pemesanan berhasil.")
                                    .setCancelable(false)
                                    .setPositiveButton("Oke", (dialog, id) -> dialog.dismiss());
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            TextView msg = alertDialog.findViewById(android.R.id.message);
                            if (msg != null) {
                                msg.setTextSize(15);
                                msg.setTypeface(custom_font);
                            }

                            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setTypeface(custom_font);
                            b.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }, 2000)).addOnFailureListener(e -> {
                });
            });
        }
    }
}
