package com.lappungdev.jajankuy.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.activity.EditMenuActivity;
import com.lappungdev.jajankuy.activity.KatalogActivity;
import com.lappungdev.jajankuy.model.Menu;

import java.util.List;
import java.util.Objects;


public class RecyclerViewAdapterKatalog extends RecyclerView.Adapter<RecyclerViewAdapterKatalog.ViewHolder> {

    private DatabaseReference databaseReferenceMenu;

    private Context context;
    private List<Menu> menuList;

    public RecyclerViewAdapterKatalog(Context context, List<Menu> TempList) {
        this.menuList = TempList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        String databasePathMenu = "jajankuy_db/menu";
        databaseReferenceMenu = FirebaseDatabase.getInstance().getReference(databasePathMenu);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items_katalog, parent, false);
        return new ViewHolder(view);
    }

    private String getMoney(String str2){
        StringBuilder str = new StringBuilder(str2);
        int idx = str.length()-3;

        while(idx > 0){
            str.insert(idx,".");
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMenuPhoto, ivMenuLoad;
        TextView tvMenuName, tvMenuPrice, tvSellerName, tvMenuState;
        Button btUbah, btHapus;

        ViewHolder(final View itemView) {
            super(itemView);

            ivMenuLoad = itemView.findViewById(R.id.ivMenuLoad);
            ivMenuPhoto = itemView.findViewById(R.id.ivMenuPhoto);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            tvMenuState = itemView.findViewById(R.id.tvMenuState);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
            btUbah = itemView.findViewById(R.id.btUbahPromo);
            btHapus = itemView.findViewById(R.id.btHapusPromo);

            btUbah.setOnClickListener(v -> passDataUbah(getAdapterPosition()));

            btHapus.setOnClickListener(v -> {
                context = itemView.getContext();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                alertDialogBuilder
                        .setMessage("Apakah kamu yakin ingin menghapus produk ini? ")
                        .setCancelable(true)
                        .setPositiveButton("Hapus", (dialog, id) -> {
                            String menuID = menuList.get(getAdapterPosition()).getMenuID();
                            databaseReferenceMenu.child(menuID).removeValue();
                            menuList.remove(getAdapterPosition());
                            removeFile(menuList.get(getAdapterPosition()).getMenuPhoto());
                            KatalogActivity.adapter.notifyItemRemoved(getAdapterPosition());
                            KatalogActivity.adapter.notifyItemRangeChanged(getAdapterPosition(), getItemCount());
                            Snackbar.make(KatalogActivity.svKatalog, "Produk berhasil dihapus.", Snackbar.LENGTH_SHORT).show();
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
            });
        }
    }

    private void passDataUbah(int position){
        Intent i = new Intent(context, EditMenuActivity.class);
        i.putExtra("menuName", menuList.get(position).getMenuName());
        i.putExtra("menuPrice", menuList.get(position).getMenuPrice());
        i.putExtra("menuCategory", menuList.get(position).getMenuCategory());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void removeFile(String url){
        if(!url.contains("null")) {
            System.out.println(url);
            String urlStr = url.split("menu%2F")[1];
            System.out.println(urlStr);
            String urlStr2 = urlStr.split("\\?")[0];
            System.out.println(urlStr2);
            StorageReference fileDelete = FirebaseStorage.getInstance().getReference().child("menu/" + urlStr2);
            fileDelete.delete().addOnSuccessListener(aVoid -> {

            });
        }
    }
}
