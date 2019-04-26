package com.lappungdev.jajankuy.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.adapter.NumberTextWatcherForThousand;
import com.lappungdev.jajankuy.model.Menu;

import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMenuActivity extends AppCompatActivity {

    private static final String databasePathMenu = "jajankuy_db/menu";
    private static final String databasePathSeller = "jajankuy_db/seller";
    private static final int cameraRequestCode = 100;
    private static final int imageRequestCodeMenu = 7;
    private final String storagePathMenu = "menu/";
    @BindView(R.id.etMenuName)
    EditText etMenuName;
    @BindView(R.id.etMenuPrice)
    EditText etMenuPrice;
    @BindView(R.id.spMenuCategory)
    Spinner spMenuCategory;
    @BindView(R.id.btRemoveMenu)
    ImageButton btRemoveMenu;
    @BindView(R.id.ivMenu)
    ImageView ivMenu;
    @BindView(R.id.svMenu)
    ScrollView svMenu;
    private DatabaseReference databaseReferenceMenu;
    private DatabaseReference databaseReferenceSeller;
    private ProgressDialog progressDialog;
    private StorageReference storageReferenceMenu;
    private String imgURLMenu;
    private String sellerZID;
    private String menuState;
    private String menuSellerName;
    private boolean menuPhoto;
    private Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        ButterKnife.bind(this);

        checkPermission();
        getSellerInfo();

        sellerZID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etMenuPrice.addTextChangedListener(new NumberTextWatcherForThousand(etMenuPrice));
        databaseReferenceMenu = FirebaseDatabase.getInstance().getReference(databasePathMenu);
        storageReferenceMenu = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
    }

    private boolean validateMenu() {
        boolean valid = true;
        String menuName = WordUtils.capitalizeFully(etMenuName.getText().toString());
        String menuPrice = etMenuPrice.getText().toString().replace(".", "").trim();
        String menuCategory = spMenuCategory.getSelectedItem().toString();

        if (menuName.isEmpty()) {
            etMenuName.setError("Nama Menu belum diisi");
            Snackbar.make(svMenu, "Nama Menu belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etMenuName.setError(null);
        }

        if (menuPrice.isEmpty()) {
            etMenuPrice.setError("Harga Menu belum diisi");
            Snackbar.make(svMenu, "Harga Menu belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etMenuPrice.setError(null);
        }

        if (menuCategory.isEmpty()) {
            Snackbar.make(svMenu, "Kategori Menu belum dipilih.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }

        if (!menuPhoto) {
            valid = false;
            Snackbar.make(svMenu, "Foto Menu belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, cameraRequestCode);
        }
    }

    public void uploadMenu(View view) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto Menu")
                .setCancelable(true)
                .setNegativeButton("Galeri", (dialog, id) -> {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), imageRequestCodeMenu);
                })
                .setPositiveButton("Kamera", (dialog, id) -> takeCameraMenu());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView msg = alertDialog.findViewById(android.R.id.message);
        msg.setTextSize(14);
        msg.setTypeface(custom_font);

        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        b.setTypeface(custom_font);

        Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        c.setTypeface(custom_font);
    }

    private void takeCameraMenu() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    public void removeMenu(View view) {
        ivMenu.setImageResource(R.drawable.ic_add_img);
        ivMenu.requestLayout();
        ivMenu.setScaleType(ImageView.ScaleType.CENTER);
        btRemoveMenu.setVisibility(View.GONE);
        menuPhoto = false;
    }

    public void pasangMenu(View view) {
        if (validateMenu()) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Proses ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (menuPhoto) {
                Bitmap bitmap = ((BitmapDrawable) ivMenu.getDrawable()).getBitmap();
                ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageBytes);
                byte[] dataByte = imageBytes.toByteArray();

                storageReferenceMenu = storageReferenceMenu.child(storagePathMenu + System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = storageReferenceMenu.putBytes(dataByte);

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return storageReferenceMenu.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            imgURLMenu = downloadUri.toString();
                            saveMenuDB();
                        }
                    }
                });
            }
        }
    }

    private void saveMenuDB() {
        String menuID = String.format("MNU-%s", Long.toHexString(System.currentTimeMillis()).toUpperCase());
        String menuTimestamp = String.valueOf(System.currentTimeMillis());
        String menuZID = String.valueOf((9999999999999L + (-1 * Long.valueOf(menuTimestamp))));
        String menuName = WordUtils.capitalizeFully(etMenuName.getText().toString());
        String menuPrice = etMenuPrice.getText().toString().replace(".", "").trim();
        String menuCategory = spMenuCategory.getSelectedItem().toString();

        Menu menu = new Menu(menuCategory, menuID, menuName, imgURLMenu, menuPrice, menuSellerName, sellerZID, menuState, menuTimestamp, menuZID, 0);
        databaseReferenceMenu.child(menuID).setValue(menu)
                .addOnSuccessListener(aVoid -> new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddMenuActivity.this, R.style.AppCompatAlertDialogStyle);
                    alertDialogBuilder
                            .setMessage("Penambahan menu berhasil.")
                            .setCancelable(false)
                            .setPositiveButton("Oke", (dialog, id) -> finish());
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    TextView msg = alertDialog.findViewById(android.R.id.message);
                    if (msg != null) {
                        msg.setTextSize(15);
                        msg.setTypeface(custom_font);
                    }

                    Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setTypeface(custom_font);
                    b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }, 2000)).addOnFailureListener(e -> {
        });
    }

    private void getSellerInfo() {
        databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = databaseReferenceSeller.child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuState = snapshot.child("sellerAddressState").getValue(String.class);
                menuSellerName = snapshot.child("sellerName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        byte[] compImg;
        ByteArrayOutputStream baos;
        if (resultCode == RESULT_OK && data != null && data.getData() != null && (requestCode == imageRequestCodeMenu)) {

            Uri filePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);

                ivMenu.setImageBitmap(bitmap2);
                ivMenu.requestLayout();
                ivMenu.setScaleType(ImageView.ScaleType.CENTER_CROP);
                btRemoveMenu.setVisibility(View.VISIBLE);
                menuPhoto = true;

                Snackbar.make(svMenu, "Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 1) {
            try {
                Bitmap bitmap = null;
                if (data != null) {
                    bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                }
                baos = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap = getResizedBitmap(bitmap, 640);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    compImg = baos.toByteArray();
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);
                    ivMenu.setImageBitmap(bitmap2);
                    ivMenu.requestLayout();
                    ivMenu.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    btRemoveMenu.setVisibility(View.VISIBLE);
                    menuPhoto = true;
                    Snackbar.make(svMenu, "Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}