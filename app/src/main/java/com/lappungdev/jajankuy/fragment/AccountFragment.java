package com.lappungdev.jajankuy.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.activity.LoginActivity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private static final String databasePathSeller = "jajankuy_db/seller";
    private static final String databasePathUser = "jajankuy_db/user";
    private String sellerPhone;
    private String sellerName;
    private String sellerState;
    private String userPhone;
    private String userName;
    private Typeface custom_font;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        custom_font = Typeface.createFromAsset(Objects.requireNonNull(getContext()).getAssets(), "fonts/font.ttf");
        DatabaseReference databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference(databasePathUser);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            TextView tvSellerEmail = view.findViewById(R.id.tvSellerEmail);
            TextView tvSellerPhone = view.findViewById(R.id.tvSellerPhone);
            TextView tvSellerName = view.findViewById(R.id.tvSellerName);
            TextView tvSellerState = view.findViewById(R.id.tvSellerState);
            CircleImageView ivProfPrict = view.findViewById(R.id.ivProfPict);
            LinearLayout llState = view.findViewById(R.id.llState);
            String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).replace("Shop - ", "");
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            tvSellerName.setText(name);
            tvSellerEmail.setText(email);

            ivProfPrict.setBorderWidth(12);
            ivProfPrict.setBorderColor(Color.parseColor("#818181"));

            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("SLR - ")) {
                try {
                    Glide.with(getContext()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivProfPrict);
                    loadSellerInfo();
                    String shopId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (sellerPhone.length() > 0) {
                        tvSellerPhone.setText(sellerPhone);
                        tvSellerName.setText(sellerName);
                        tvSellerState.setText(sellerState);
                    } else {
                        Query query = databaseReferenceSeller.child(shopId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                sellerPhone = snapshot.child("sellerPhone").getValue(String.class);
                                sellerName = snapshot.child("sellerName").getValue(String.class);
                                sellerState = snapshot.child("sellerAddressState").getValue(String.class);
                                tvSellerPhone.setText(sellerPhone);
                                tvSellerName.setText(sellerName);
                                tvSellerState.setText(sellerState);
                                saveSellerInfo();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/jajankuy-ina.appspot.com/o/user%2Fuser.png?alt=media&token=bd0832fa-6e8a-4a1e-910a-90bc87f4faf3").diskCacheStrategy(DiskCacheStrategy.ALL).into(ivProfPrict);
                    loadUserInfo();
                    llState.setVisibility(View.GONE);
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (userPhone.length() > 0) {
                        tvSellerPhone.setText(userPhone);
                        tvSellerName.setText(userName);
                    } else {
                        Query query = databaseReferenceUser.child(userId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userPhone = snapshot.child("userPhone").getValue(String.class);
                                userName = snapshot.child("userName").getValue(String.class);
                                tvSellerPhone.setText(userPhone);
                                tvSellerName.setText(userName);
                                saveUserInfo();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            LinearLayout llKeluar = view.findViewById(R.id.llKeluar);
            llKeluar.setOnClickListener(v -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                alertDialogBuilder
                        .setMessage("Apakah kamu ingin keluar?")
                        .setCancelable(true)
                        .setPositiveButton("Keluar", (dialog, id) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                TextView msg = alertDialog.findViewById(android.R.id.message);
                if (msg != null) {
                    msg.setTextSize(15);
                    msg.setTypeface(custom_font);
                }

                Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                c.setTextColor(getResources().getColor(R.color.colorHitam));
            });
        }

        return view;
    }

    private void loadSellerInfo() {
        try {
            SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences("sellerInfo", 0);
            sellerPhone = sharedPref.getString("sellerPhone", "");
            sellerName = sharedPref.getString("sellerName", "");
            sellerState = sharedPref.getString("sellerState", "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void saveSellerInfo() {
        try {
            SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences("sellerInfo", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sellerPhone", sellerPhone);
            editor.putString("sellerName", sellerName);
            editor.putString("sellerState", sellerState);
            editor.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void loadUserInfo() {
        try {
            SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences("userInfo", 0);
            userPhone = sharedPref.getString("userPhone", "");
            userName = sharedPref.getString("userName", "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void saveUserInfo() {
        try {
            SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences("userInfo", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userPhone", userPhone);
            editor.putString("userName", userName);
            editor.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}