package com.lappungdev.jajankuy.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.adapter.RecyclerViewAdapterCategory;
import com.lappungdev.jajankuy.model.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryActivity extends AppCompatActivity {

    private static final String databasePathMenu = "jajankuy_db/menu";
    @SuppressLint("StaticFieldLeak")
    public static RecyclerViewAdapterCategory adapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout svCategory;
    @BindView(R.id.pb)
    ProgressBar pbLoad;
    @BindView(R.id.llNotFound)
    LinearLayout llNotFound;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvCategoryTitle)
    TextView tvCategoryTitle;
    @BindView(R.id.tvMenuSum)
    TextView tvMenuSum;
    @BindView(R.id.spMenuSort)
    Spinner spMenuSort;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseReferenceMenu;
    private List<Menu> menuList = new ArrayList<>();
    private boolean itemSelected = false;
    private boolean closest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        Intent categoryIntent = getIntent();
        String categoryName = categoryIntent.getStringExtra("menuCategory");
        getMenu(categoryName);
        tvCategoryTitle.setText(categoryName);
        categoryIntent.removeExtra("menuCategory");
        svCategory = findViewById(R.id.svCategory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this, 2));


        swipeRefreshLayout.setOnRefreshListener(this::refreshItems);

        spMenuSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelect = parent.getItemAtPosition(position).toString();
                if (itemSelected) {
                    if (itemSelect.contains("Jarak Terdekat")) {
                        Collections.sort(menuList, (o1, o2) -> {
                            int sComp = o1.getMenuMeter() - o2.getMenuMeter();
                            if (sComp != 0) {
                                return sComp;
                            } else {
                                Long id1 = Long.valueOf(o1.getMenuZID());
                                Long id2 = Long.valueOf(o2.getMenuZID());
                                return id1.compareTo(id2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                        itemSelected = true;
                        closest = true;
                    }
                }
                if (itemSelect.contains("Harga Termurah")) {
                    Collections.sort(menuList, (o1, o2) -> Integer.parseInt(o1.getMenuPrice()) - Integer.parseInt(o2.getMenuPrice()));
                    adapter.notifyDataSetChanged();
                    itemSelected = true;
                    closest = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void refreshItems() {
        try {
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void getMenu(String name) {
        databaseReferenceMenu = FirebaseDatabase.getInstance().getReference(databasePathMenu);
        Query queryMenu = databaseReferenceMenu.orderByChild("menuCategory").equalTo(name);
        queryMenu.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    Menu menuInfo = menuSnapshot.getValue(Menu.class);
                    menuList.add(menuInfo);
                }
                adapter = new RecyclerViewAdapterCategory(getApplicationContext(), menuList);
                try {
                    recyclerView.setAdapter(adapter);
                    tvMenuSum.setText(String.format("%d Jajanan", menuList.size()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                pbLoad.setVisibility(View.GONE);
                if (menuList.isEmpty()) {
                    llNotFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void upScroll(View view) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getBaseContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(0);
        if (layoutManager != null) {
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }
}
