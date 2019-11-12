package com.lappungdev.jajankuy.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String databasePathSeller = "jajankuy_db/seller";
    private static final String broadcastAction = "android.location.PROVIDERS_CHANGED";
    private static final int accessFineLocationIntent = 3;
    private static final int requestCheckSettings = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static LatLng loctNow;
    private final String[] kec = {"Abung Barat, Lampung Utara", "Abung Kunang, Lampung Utara", "Abung Pekurun, Lampung Utara", "Abung Selatan, Lampung Utara", "Abung Semuli, Lampung Utara", "Abung Surakarta, Lampung Utara", "Abung Tengah, Lampung Utara", "Abung Timur, Lampung Utara", "Abung Tinggi, Lampung Utara", "Adiluwih, Pringsewu", "Air Hitam, Lampung Barat", "Air Naningan, Tanggamus", "Ambarawa, Pringsewu", "Anak Ratu Aji, Lampung Tengah", "Anak Tuha, Lampung Tengah", "Bahuga, Way Kanan", "Bakauheni, Lampung Selatan", "Balik Bukit, Lampung Barat", "Bandar Mataram, Lampung Tengah", "Bandar Negeri Semuong, Tanggamus", "Bandar Negeri Suoh, Lampung Barat", "Bandar Sribhawono, Lampung Timur", "Bandar Surabaya, Lampung Tengah", "Bangunrejo, Lampung Tengah", "Banjar Agung, Tulang Bawang", "Banjar Baru, Tulang Bawang", "Banjar Margo, Tulang Bawang", "Banjit, Way Kanan", "Banyumas, Pringsewu", "Baradatu, Way Kanan", "Batanghari Nuban, Lampung Timur", "Batanghari, Lampung Timur", "Batu Brak, Lampung Barat", "Batu Ketulis, Lampung Barat", "Bekri, Lampung Tengah", "Belalau, Lampung Barat", "Bengkunat Belimbing, Pesisir Barat", "Bengkunat, Pesisir Barat", "Blambangan Pagar, Lampung Utara", "Blambangan Umpu, Way Kanan", "Braja Slebah, Lampung Timur", "Buay Bahuga, Way Kanan", "Bukit Kemuning, Lampung Utara", "Bulok, Tanggamus", "Bumi Agung, Lampung Timur", "Bumi Agung, Way Kanan", "Bumi Nabung, Lampung Tengah", "Bumi Ratu Nuban, Lampung Tengah", "Bumi Waras, Bandar Lampung", "Bunga Mayang, Lampung Utara", "Candipuro, Lampung Selatan", "Cukuh Balak, Tanggamus", "Dente Teladas, Tulang Bawang", "Enggal, Bandar Lampung", "Gading Rejo, Pringsewu", "Gadingrejo, Pringsewu", "Gedong Tataan, Pesawaran", "Gedung Aji Baru, Tulang Bawang", "Gedung Aji, Tulang Bawang", "Gedung Meneng, Tulang Bawang", "Gedung Surian, Lampung Barat", "Gisting, Tanggamus", "Gunung Agung, Tulang Bawang Barat", "Gunung Labuhan, Way Kanan", "Gunung Pelindung, Lampung Timur", "Gunung Sugih, Lampung Tengah", "Gunung Terang, Tulang Bawang Barat", "Hulu Sungkai, Lampung Utara", "Jabung, Lampung Timur", "Jati Agung, Lampung Selatan", "Kalianda, Lampung Selatan", "Kalirejo, Lampung Tengah", "Karya Penggawa, Pesisir Barat", "Kasui, Way Kanan", "Katibung, Lampung Selatan", "Kebun Tebu, Lampung Barat", "Kedamaian, Bandar Lampung", "Kedaton, Bandar Lampung", "Kedondong, Pesawaran", "Kelumbayan Barat, Tanggamus", "Kelumbayan, Tanggamus", "Kemiling, Bandar Lampung", "Ketapang, Lampung Selatan", "Ketimbang", "Kota Agung Barat, Tanggamus", "Kota Agung Pusat, Tanggamus", "Kota Agung Timur, Tanggamus", "Kota Gajah, Lampung Tengah", "Kotaagung, Tanggamus", "Kotabumi Kota, Lampung Utara", "Kotabumi Selatan, Lampung Utara", "Kotabumi Utara, Lampung Utara", "Krui Selatan, Pesisir Barat", "Labuhan Maringgai, Lampung Timur", "Labuhan Ratu, Bandar Lampung", "Labuhan Ratu, Lampung Timur", "Lambu Kibang, Tulang Bawang Barat", "Langkapura, Bandar Lampung", "Lemong, Pesisir Barat", "Limau, Tanggamus", "Lumbok Seminung, Lampung Barat", "Marga Punduh, Pesawaran", "Marga Sekampung, Lampung Timur", "Margatiga, Lampung Timur", "Mataram Baru, Lampung Timur", "Melinting, Lampung Timur", "Menggala Timur, Tulang Bawang", "Menggala, Tulang Bawang", "Meraksa Aji, Tulang Bawang", "Merbau Mataram, Lampung Selatan", "Mesuji Timur, Mesuji", "Mesuji, Mesuji", "Metro Barat, Metro", "Metro Kibang, Lampung Timur", "Metro Pusat, Metro", "Metro Selatan, Metro", "Metro Timur, Metro", "Metro Utara, Metro", "Muara Sungkai, Lampung Utara", "Natar, Lampung Selatan", "Negara Batin, Way Kanan", "Negeri Agung, Way Kanan", "Negeri Besar, Way Kanan", "Negeri Katon, Pesawaran", "Ngambur, Pesisir Barat", "Padang Cermin, Pesawaran", "Padang Ratu, Lampung Tengah", "Pagar Dewa, Lampung Barat", "Pagar Dewa, Tulang Bawang Barat", "Pagelaran Utara, Pringsewu", "Pagelaran, Pringsewu", "Pakuan Ratu, Way Kanan", "Palas, Lampung Selatan", "Panca Jaya, Mesuji", "Panjang, Bandar Lampung", "Pardasuka, Pringsewu", "Pasir Sakti, Lampung Timur", "Pekalongan, Lampung Timur", "Pematang Sawa, Tanggamus", "Penawar Aji, Tulang Bawang", "Penawar Tama, Tulang Bawang", "Penengahan, Lampung Selatan", "Pesisir Selatan, Pesisir Barat", "Pesisir Tengah, Pesisir Barat", "Pesisir Utara, Pesisir Barat", "Pringsewu, Pringsewu", "Pubian, Lampung Tengah", "Pugung, Tanggamus", "Pulau Panggung, Tanggamus", "Pulau Pisang, Pesisir Barat", "Punduh Pidada, Pesawaran", "Punggur, Lampung Tengah", "Purbolinggo, Lampung Timur", "Putra Rumbia, Lampung Tengah", "Rajabasa, Bandar Lampung", "Rajabasa, Lampung Selatan", "Raman Utara, Lampung Timur", "Rawa Jitu Utara, Mesuji", "Rawa Pitu, Tulang Bawang", "Rawajitu Selatan, Tulang Bawang", "Rawajitu Timur, Tulang Bawang", "Rebang Tangkas, Way Kanan", "Rumbia, Lampung Tengah", "Sekampung Udik, Lampung Timur", "Sekampung, Lampung Timur", "Sekincau, Lampung Barat", "Selagai Lingga, Lampung Tengah", "Semaka, Tanggamus", "Sendang Agung, Lampung Tengah", "Seputih Agung, Lampung Tengah", "Seputih Banyak, Lampung Tengah", "Seputih Mataram, Lampung Tengah", "Seputih Raman, Lampung Tengah", "Seputih Surabaya, Lampung Tengah", "Sidomulyo, Lampung Selatan", "Simpang Pematang, Mesuji", "Sragi, Lampung Selatan", "Sukabumi, Bandar Lampung", "Sukadana, Lampung Timur", "Sukarame, Bandar Lampung", "Sukau, Lampung Barat", "Sukoharjo, Pringsewu", "Sumber Jaya, Lampung Barat", "Sumberejo, Tanggamus", "Sungkai Barat, Lampung Utara", "Sungkai Jaya, Lampung Utara", "Sungkai Selatan, Lampung Utara", "Sungkai Tengah, Lampung Utara", "Sungkai Utara, Lampung Utara", "Suoh, Lampung Barat", "Talang Padang, Tanggamus", "Tanjung Bintang, Lampung Selatan", "Tanjung Karang Barat, Bandar Lampung", "Tanjung Karang Pusat, Bandar Lampung", "Tanjung Karang Timur, Bandar Lampung", "Tanjung Raja, Lampung Utara", "Tanjung Raya, Mesuji", "Tanjung Senang, Bandar Lampung", "Tanjungsari, Lampung Selatan", "Tegineneng, Pesawaran", "Teluk Betung Barat, Bandar Lampung", "Teluk Betung Selatan, Bandar Lampung", "Teluk Betung Timur, Bandar Lampung", "Teluk Betung Utara, Bandar Lampung", "Way Halim, Bandar Lampung"};
    public String sellerUid = "";
    @BindView(R.id.llSeller)
    LinearLayout llSeller;
    @BindView(R.id.rlUser)
    RelativeLayout rlUser;
    @BindView(R.id.actSellerAddressState)
    AutoCompleteTextView actSellerAddressState;
    private DatabaseReference databaseReferenceSeller;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private LatLng sellerLoct;
    private List<LatLng> listLatLng = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Runnable sendUpdatesToUI = this::showSettingDialog;
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).matches(broadcastAction)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                }

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        initGoogleAPIClient();
        checkPermissions();
        ImageView ivLoctNow = view.findViewById(R.id.ivLoctNow);
        ivLoctNow.setOnClickListener(v -> animateToCurrentLocation(loctNow));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        sellerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("SLR - ")) {
            llSeller.setVisibility(View.VISIBLE);
            rlUser.setVisibility(View.GONE);
            new Handler().postDelayed(() -> updateSellerLocation(sellerUid), 2000);
        } else if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().contains("USR - ")) {
            getSeller();
            llSeller.setVisibility(View.GONE);
            rlUser.setVisibility(View.VISIBLE);

            bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.rlUser));
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }


                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                }
            });
        }

        ArrayAdapter<String> adapterKec = new ArrayAdapter<>(getActivity(), R.layout.dropdown, kec);
        actSellerAddressState.setThreshold(1);
        actSellerAddressState.setAdapter(adapterKec);

        return view;
    }

    private void getSeller() {
        Drawable dMarker = getResources().getDrawable(R.drawable.ic_marker);
        BitmapDescriptor bdMarker = getMarkerIconFromDrawable(dMarker);

        databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        Query orderQuery = databaseReferenceSeller.orderByChild("sellerLocation");
        orderQuery.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Seller sellerInfo = orderSnapshot.getValue(Seller.class);
                    if (sellerInfo != null) {
                        if (!sellerInfo.getSellerLocation().isEmpty()) {
                            String[] loc = sellerInfo.getSellerLocation().split(",");
                            sellerLoct = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));
                            listLatLng.add(sellerLoct);
                            MarkerOptions sellerMarker = new MarkerOptions().position(sellerLoct).title(sellerInfo.getSellerName()).snippet(sellerInfo.getSellerSale()).icon(bdMarker);
                            mMap.addMarker(sellerMarker);
                        }
                    }
                }
                new Handler().postDelayed(() -> zoomRoute(listLatLng), 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) {
            return;
        }
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute) {
            boundsBuilder.include(latLngPoint);
        }
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 300));
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void updateSellerLocation(String Id) {
        if (loctNow != null) {
            String sellerLocation = loctNow.latitude + "," + loctNow.longitude;
            databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
            databaseReferenceSeller.child(Id).child("sellerLocation").setValue(sellerLocation);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        Objects.requireNonNull(getActivity()), R.raw.map_style));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        //mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getContext()))
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(getActivity(), requestCheckSettings);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCheckSettings) {
            switch (resultCode) {
                case RESULT_OK:
                    break;
                case RESULT_CANCELED:
                    Objects.requireNonNull(getActivity()).finish();
                    break;
            }
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    accessFineLocationIntent);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    accessFineLocationIntent);
        }
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void animateToCurrentLocation(LatLng currentLocation) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getContext())).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        loctNow = new LatLng(location.getLatitude(), location.getLongitude());
        moveToCurrentLocation(loctNow);
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Objects.requireNonNull(getActivity()).registerReceiver(gpsLocationReceiver, new IntentFilter(broadcastAction));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (gpsLocationReceiver != null)
                Objects.requireNonNull(getActivity()).unregisterReceiver(gpsLocationReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
