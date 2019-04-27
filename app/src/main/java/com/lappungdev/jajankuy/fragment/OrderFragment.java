package com.lappungdev.jajankuy.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.adapter.OrderClick;
import com.lappungdev.jajankuy.adapter.RecyclerViewOrderAdapter;
import com.lappungdev.jajankuy.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderFragment extends Fragment implements OrderClick {

    private static final String databasePathSeller = "jajankuy_db/seller";
    private static final String databasePathOrder = "jajankuy_db/order";
    private static final String databasePathUser = "boyexpress_db/user";
    private final List<Order> list = new ArrayList<>();
    @BindView(R.id.lavNoResult)
    LottieAnimationView lavNoResult;
    @BindView(R.id.llNoOrderResult)
    LinearLayout llNoOrderResult;
    @BindView(R.id.recyclerViewOrder)
    RecyclerView recyclerView;
    @BindView(R.id.shimmer_order_view_container)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String dbQuery = "";
    private String originID = "";
    private DatabaseReference databaseReferenceOrder;
    private RecyclerViewOrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        ButterKnife.bind(this, view);

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("SLR - ")) {
            dbQuery = "orderSellerID";
        } else if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("USR - ")) {
            dbQuery = "orderCustomerID";
        }

        adapter = new RecyclerViewOrderAdapter(getContext(), list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReferenceOrder = FirebaseDatabase.getInstance().getReference(databasePathOrder);
        originID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        lavNoResult.setAnimation("result.json");
        lavNoResult.setRepeatMode(LottieDrawable.RESTART);
        lavNoResult.setRepeatCount(LottieDrawable.INFINITE);

        getOrder(originID);

        swipeRefreshLayout.setOnRefreshListener(() -> getOrder(originID));

        return view;
    }

    private void getOrder(String originID) {
        recyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        llNoOrderResult.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmerAnimation();
        Query orderQuery = databaseReferenceOrder.orderByChild(dbQuery).equalTo(originID);
        orderQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order orderInfo = orderSnapshot.getValue(Order.class);
                    list.add(orderInfo);
                    try {
                        adapter = new RecyclerViewOrderAdapter(getContext(), list);
                        recyclerView.setAdapter(adapter);
                        adapter.setOrderClickListener(OrderFragment.this);
                        adapter.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                new Handler().postDelayed(() -> {
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (list.isEmpty()) {
                        llNoOrderResult.setVisibility(View.VISIBLE);
                        lavNoResult.playAnimation();
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        llNoOrderResult.setVisibility(View.GONE);
                        lavNoResult.cancelAnimation();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClicked(boolean status) {
        if (status) {
            getOrder(originID);
        }
    }
}