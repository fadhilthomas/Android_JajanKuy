package com.lappungdev.jajankuy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.adapter.RecyclerViewAdapterKatalog;
import com.lappungdev.jajankuy.model.Menu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KatalogActivity extends AppCompatActivity {

    private DatabaseReference databaseReferenceMenu;
    private DatabaseReference databaseReferenceSeller;
    private List<Menu> menuList = new ArrayList<>();
    @BindView(R.id.pb) ProgressBar pbLoad;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private String sellerName = "";
    private static final String databasePathMenu = "jajankuy_db/menu";
    private static final String databasePathSeller = "jajankuy_db/seller";

    @SuppressLint("StaticFieldLeak")
    public static RecyclerViewAdapterKatalog adapter ;

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout svKatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);

        ButterKnife.bind(this);

        svKatalog = findViewById(R.id.svKatalog);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(KatalogActivity.this, 2));

        getSellerInfo();
    }

    private void getSellerInfo(){
        databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = databaseReferenceSeller.child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sellerName = snapshot.child("sellerName").getValue(String.class);
                getSellerMenu(sellerName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getSellerMenu(String name){
        databaseReferenceMenu = FirebaseDatabase.getInstance().getReference(databasePathMenu);
        Query queryMenu = databaseReferenceMenu.orderByChild("menuSellerName").equalTo(name);
        queryMenu.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot menuSnapshot: dataSnapshot.getChildren()){
                    Menu menuInfo = menuSnapshot.getValue(Menu.class);
                    menuList.add(menuInfo);
                }
                adapter = new RecyclerViewAdapterKatalog(getApplicationContext(), menuList);
                try {
                    recyclerView.setAdapter(adapter);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                pbLoad.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
