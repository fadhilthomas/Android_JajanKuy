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
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.model.Seller;

import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterSellerActivity extends AppCompatActivity {

    @BindView(R.id.acbCheckEmail) AppCompatButton acbCheckEmail;
    @BindView(R.id.actSellerAddressState) AutoCompleteTextView actKec;
    @BindView(R.id.btRemovePlace) ImageButton btRemovePlace;
    @BindView(R.id.btRemoveSeller) ImageButton btRemoveSeller;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etRePassword) EditText etRePassword;
    @BindView(R.id.etSellerEmail) EditText etSellerEmail;
    @BindView(R.id.etSellerLicenseID) EditText etSellerLicenseID;
    @BindView(R.id.etSellerName) EditText etSellerName;
    @BindView(R.id.etSellerNIK) EditText etSellerNIK;
    @BindView(R.id.etSellerPhone) EditText etSellerPhone;
    @BindView(R.id.ivPlace) ImageView ivPlace;
    @BindView(R.id.ivSeller) ImageView ivSeller;
    @BindView(R.id.llInfoAkun) LinearLayout llInfoAkun;
    @BindView(R.id.llInfoToko) LinearLayout llInfoToko;
    @BindView(R.id.svDaftar) ScrollView svDaftar;
    @BindView(R.id.tvEmail) TextView tvEmail;
    private DatabaseReference databaseReferenceSeller;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private StorageReference storageReferenceSeller;
    private String address;
    private String imgURLPlace;
    private String imgURLSeller;
    private String loc;
    private String storagePathSeller = "seller/";
    private Typeface custom_font;
    private boolean emailClicked = false;
    private boolean emailValid = true;
    private boolean placePhoto = false;
    private boolean sellerPhoto = false;
    private static final String databasePathSeller = "jajankuy_db/seller";
    private static final int cameraRequestCode = 100;
    private static final int imageRequestCodePlace = 7;
    private String[] kec = {"Abung Barat, Lampung Utara","Abung Kunang, Lampung Utara","Abung Pekurun, Lampung Utara","Abung Selatan, Lampung Utara","Abung Semuli, Lampung Utara","Abung Surakarta, Lampung Utara","Abung Tengah, Lampung Utara","Abung Timur, Lampung Utara","Abung Tinggi, Lampung Utara","Adiluwih, Pringsewu","Air Hitam, Lampung Barat","Air Naningan, Tanggamus","Ambarawa, Pringsewu","Anak Ratu Aji, Lampung Tengah","Anak Tuha, Lampung Tengah","Bahuga, Way Kanan","Bakauheni, Lampung Selatan","Balik Bukit, Lampung Barat","Bandar Mataram, Lampung Tengah","Bandar Negeri Semuong, Tanggamus","Bandar Negeri Suoh, Lampung Barat","Bandar Sribhawono, Lampung Timur","Bandar Surabaya, Lampung Tengah","Bangunrejo, Lampung Tengah","Banjar Agung, Tulang Bawang","Banjar Baru, Tulang Bawang","Banjar Margo, Tulang Bawang","Banjit, Way Kanan","Banyumas, Pringsewu","Baradatu, Way Kanan","Batanghari Nuban, Lampung Timur","Batanghari, Lampung Timur","Batu Brak, Lampung Barat","Batu Ketulis, Lampung Barat","Bekri, Lampung Tengah","Belalau, Lampung Barat","Bengkunat Belimbing, Pesisir Barat","Bengkunat, Pesisir Barat","Blambangan Pagar, Lampung Utara","Blambangan Umpu, Way Kanan","Braja Slebah, Lampung Timur","Buay Bahuga, Way Kanan","Bukit Kemuning, Lampung Utara","Bulok, Tanggamus","Bumi Agung, Lampung Timur","Bumi Agung, Way Kanan","Bumi Nabung, Lampung Tengah","Bumi Ratu Nuban, Lampung Tengah","Bumi Waras, Bandar Lampung","Bunga Mayang, Lampung Utara","Candipuro, Lampung Selatan","Cukuh Balak, Tanggamus","Dente Teladas, Tulang Bawang","Enggal, Bandar Lampung","Gading Rejo, Pringsewu","Gadingrejo, Pringsewu","Gedong Tataan, Pesawaran","Gedung Aji Baru, Tulang Bawang","Gedung Aji, Tulang Bawang","Gedung Meneng, Tulang Bawang","Gedung Surian, Lampung Barat","Gisting, Tanggamus","Gunung Agung, Tulang Bawang Barat","Gunung Labuhan, Way Kanan","Gunung Pelindung, Lampung Timur","Gunung Sugih, Lampung Tengah","Gunung Terang, Tulang Bawang Barat","Hulu Sungkai, Lampung Utara","Jabung, Lampung Timur","Jati Agung, Lampung Selatan","Kalianda, Lampung Selatan","Kalirejo, Lampung Tengah","Karya Penggawa, Pesisir Barat","Kasui, Way Kanan","Katibung, Lampung Selatan","Kebun Tebu, Lampung Barat","Kedamaian, Bandar Lampung","Kedaton, Bandar Lampung","Kedondong, Pesawaran","Kelumbayan Barat, Tanggamus","Kelumbayan, Tanggamus","Kemiling, Bandar Lampung","Ketapang, Lampung Selatan","Ketimbang","Kota Agung Barat, Tanggamus","Kota Agung Pusat, Tanggamus","Kota Agung Timur, Tanggamus","Kota Gajah, Lampung Tengah","Kotaagung, Tanggamus","Kotabumi Kota, Lampung Utara","Kotabumi Selatan, Lampung Utara","Kotabumi Utara, Lampung Utara","Krui Selatan, Pesisir Barat","Labuhan Maringgai, Lampung Timur","Labuhan Ratu, Bandar Lampung","Labuhan Ratu, Lampung Timur","Lambu Kibang, Tulang Bawang Barat","Langkapura, Bandar Lampung","Lemong, Pesisir Barat","Limau, Tanggamus","Lumbok Seminung, Lampung Barat","Marga Punduh, Pesawaran","Marga Sekampung, Lampung Timur","Margatiga, Lampung Timur","Mataram Baru, Lampung Timur","Melinting, Lampung Timur","Menggala Timur, Tulang Bawang","Menggala, Tulang Bawang","Meraksa Aji, Tulang Bawang","Merbau Mataram, Lampung Selatan","Mesuji Timur, Mesuji","Mesuji, Mesuji","Metro Barat, Metro","Metro Kibang, Lampung Timur","Metro Pusat, Metro","Metro Selatan, Metro","Metro Timur, Metro","Metro Utara, Metro","Muara Sungkai, Lampung Utara","Natar, Lampung Selatan","Negara Batin, Way Kanan","Negeri Agung, Way Kanan","Negeri Besar, Way Kanan","Negeri Katon, Pesawaran","Ngambur, Pesisir Barat","Padang Cermin, Pesawaran","Padang Ratu, Lampung Tengah","Pagar Dewa, Lampung Barat","Pagar Dewa, Tulang Bawang Barat","Pagelaran Utara, Pringsewu","Pagelaran, Pringsewu","Pakuan Ratu, Way Kanan","Palas, Lampung Selatan","Panca Jaya, Mesuji","Panjang, Bandar Lampung","Pardasuka, Pringsewu","Pasir Sakti, Lampung Timur","Pekalongan, Lampung Timur","Pematang Sawa, Tanggamus","Penawar Aji, Tulang Bawang","Penawar Tama, Tulang Bawang","Penengahan, Lampung Selatan","Pesisir Selatan, Pesisir Barat","Pesisir Tengah, Pesisir Barat","Pesisir Utara, Pesisir Barat","Pringsewu, Pringsewu","Pubian, Lampung Tengah","Pugung, Tanggamus","Pulau Panggung, Tanggamus","Pulau Pisang, Pesisir Barat","Punduh Pidada, Pesawaran","Punggur, Lampung Tengah","Purbolinggo, Lampung Timur","Putra Rumbia, Lampung Tengah","Rajabasa, Bandar Lampung","Rajabasa, Lampung Selatan","Raman Utara, Lampung Timur","Rawa Jitu Utara, Mesuji","Rawa Pitu, Tulang Bawang","Rawajitu Selatan, Tulang Bawang","Rawajitu Timur, Tulang Bawang","Rebang Tangkas, Way Kanan","Rumbia, Lampung Tengah","Sekampung Udik, Lampung Timur","Sekampung, Lampung Timur","Sekincau, Lampung Barat","Selagai Lingga, Lampung Tengah","Semaka, Tanggamus","Sendang Agung, Lampung Tengah","Seputih Agung, Lampung Tengah","Seputih Banyak, Lampung Tengah","Seputih Mataram, Lampung Tengah","Seputih Raman, Lampung Tengah","Seputih Surabaya, Lampung Tengah","Sidomulyo, Lampung Selatan","Simpang Pematang, Mesuji","Sragi, Lampung Selatan","Sukabumi, Bandar Lampung","Sukadana, Lampung Timur","Sukarame, Bandar Lampung","Sukau, Lampung Barat","Sukoharjo, Pringsewu","Sumber Jaya, Lampung Barat","Sumberejo, Tanggamus","Sungkai Barat, Lampung Utara","Sungkai Jaya, Lampung Utara","Sungkai Selatan, Lampung Utara","Sungkai Tengah, Lampung Utara","Sungkai Utara, Lampung Utara","Suoh, Lampung Barat","Talang Padang, Tanggamus","Tanjung Bintang, Lampung Selatan","Tanjung Karang Barat, Bandar Lampung","Tanjung Karang Pusat, Bandar Lampung","Tanjung Karang Timur, Bandar Lampung","Tanjung Raja, Lampung Utara","Tanjung Raya, Mesuji","Tanjung Senang, Bandar Lampung","Tanjungsari, Lampung Selatan","Tegineneng, Pesawaran","Teluk Betung Barat, Bandar Lampung","Teluk Betung Selatan, Bandar Lampung","Teluk Betung Timur, Bandar Lampung","Teluk Betung Utara, Bandar Lampung","Way Halim, Bandar Lampung"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seller);
        ButterKnife.bind(this);
        
        checkPermission();
        
        databaseReferenceSeller = FirebaseDatabase.getInstance().getReference(databasePathSeller);
        storageReferenceSeller = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/font.ttf");
        etSellerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<String> adapterKec = new ArrayAdapter<>(this, R.layout.dropdown, kec);
        actKec.setThreshold(1);
        actKec.setAdapter(adapterKec);

        acbCheckEmail.setOnClickListener(v -> {
            emailClicked = true;
            checkEmailProcess(etSellerEmail.getText().toString());
        });
    }

    private void checkEmailProcess(String email){
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                emailValid = Objects.requireNonNull(task.getResult()).getSignInMethods().size() == 0;
                if (emailValid) {
                    tvEmail.setText(R.string.email_tersedia);
                } else {
                    tvEmail.setText(R.string.email_sudah_digunakan);
                }
            }
        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, cameraRequestCode);
        }
    }

    private void registerProcess() {
        if (validateAccount()) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.proses));
            progressDialog.setCancelable(false);
            progressDialog.show();
            String email = etSellerEmail.getText().toString();
            String password = etPassword.getText().toString();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            photoUpload();
                        }
                    });
        }
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName("SLR - " + WordUtils.capitalizeFully(etSellerName.getText().toString()))
                .setPhotoUri(Uri.parse(imgURLSeller))
                .build();
        user.updateProfile(addProfileName);
    }

    private void photoUpload() {
        if (sellerPhoto) {
            Bitmap bitmapOne = ((BitmapDrawable) ivSeller.getDrawable()).getBitmap();
            ByteArrayOutputStream imageOneBytes = new ByteArrayOutputStream();
            bitmapOne.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes);
            byte[] dataOne = imageOneBytes.toByteArray();

            final StorageReference filepathOne = storageReferenceSeller.child(storagePathSeller + System.currentTimeMillis() + ".jpg");
            UploadTask uploadTask = filepathOne.putBytes(dataOne);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return filepathOne.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        imgURLSeller = downloadUri.toString();
                        if (placePhoto) {
                            Bitmap bitmapOne1 = ((BitmapDrawable) ivPlace.getDrawable()).getBitmap();
                            ByteArrayOutputStream imageOneBytes1 = new ByteArrayOutputStream();
                            bitmapOne1.compress(Bitmap.CompressFormat.JPEG, 100, imageOneBytes1);
                            byte[] dataOne1 = imageOneBytes1.toByteArray();

                            final StorageReference filepathOne1 = storageReferenceSeller.child(storagePathSeller + System.currentTimeMillis() + ".jpg");
                            UploadTask uploadTask1 = filepathOne1.putBytes(dataOne1);

                            uploadTask1.continueWithTask(task12 -> {
                                if (!task12.isSuccessful()) {
                                    throw Objects.requireNonNull(task12.getException());
                                }
                                return filepathOne1.getDownloadUrl();
                            }).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Uri downloadUri1 = task1.getResult();
                                    if (downloadUri1 != null) {
                                        imgURLPlace = downloadUri1.toString();
                                        createFirebaseUserProfile(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
                                        registerSeller(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                        sentVerificationEmail();
                                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RegisterSellerActivity.this, R.style.AppCompatAlertDialogStyle);
                                        try {
                                            alertDialogBuilder
                                                    .setTitle("Verifikasi Email")
                                                    .setMessage("Kami telah mengirimkan email ke " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + " untuk  memverifikasi alamat email kamu. Silahkan klik link di email tersebut untuk dapat masuk ke akun.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Ok", (dialog, id) -> {
                                                        onSignupSuccess();
                                                        progressDialog.dismiss();
                                                    });
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();

                                        TextView msg = alertDialog.findViewById(android.R.id.message);
                                        msg.setTextSize(15);
                                        msg.setTypeface(custom_font);

                                        Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                        b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        b.setTypeface(custom_font);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void registerSeller(String id){
        String sellerName = WordUtils.capitalizeFully(etSellerName.getText().toString());
        String sellerID = String.format("SLR-%s", Long.toHexString(System.currentTimeMillis()).toUpperCase());
        String sellerEmail = etSellerEmail.getText().toString();
        String sellerPhone = etSellerPhone.getText().toString();
        String sellerNIK = etSellerNIK.getText().toString();
        String sellerLicenseID = etSellerLicenseID.getText().toString();
        String sellerAddressState = WordUtils.capitalizeFully(actKec.getText().toString());
        Seller seller = new Seller(sellerAddressState, sellerEmail, sellerID, sellerLicenseID, sellerNIK, sellerName, sellerPhone, imgURLSeller, imgURLSeller, id, 0);
        databaseReferenceSeller.child(id).setValue(seller);
    }

    private void sentVerificationEmail(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(svDaftar,"Email verifikasi berhasil dikirim.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onSignupSuccess() {
        FirebaseAuth.getInstance().signOut();
        Snackbar.make(svDaftar,"Daftar berhasil.", Snackbar.LENGTH_SHORT).show();
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validatePass(String password) {
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        return hasLetter.find() && hasDigit.find();
    }

    private boolean validateAccount() {
        boolean valid = true;
        String email = etSellerEmail.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etSellerEmail.setError("Alamat email tidak valid");
            Snackbar.make(svDaftar,"Alamat email tidak valid.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etSellerEmail.setError(null);
        }

        if ((validatePass(password))&&(password.length() > 8)){
            etPassword.setError(null);
        } else {
            etPassword.setError("Kata sandi harus lebih dari 8 karakter dan alfanumerik");
            Snackbar.make(svDaftar,"Kata sandi harus lebih dari 8 karakter dan alfanumerik.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        if (rePassword.isEmpty() || !(rePassword.equals(password))) {
            etRePassword.setError("Kata sandi tidak cocok");
            Snackbar.make(svDaftar,"Kata sandi tidak cocok.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etRePassword.setError(null);
        }

        if(!emailValid){
            valid = false;
            Snackbar.make(svDaftar,"Email sudah terdaftar, gunakan email yang lain.", Snackbar.LENGTH_SHORT).show();
        }
        if(!emailClicked){
            valid = false;
            Snackbar.make(svDaftar,"Klik tombol periksa email untuk memeriksa email valid.", Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    private boolean validateSeller() {
        boolean valid = true;
        String name = WordUtils.capitalizeFully(etSellerName.getText().toString());
        String phone = etSellerPhone.getText().toString();
        String state = actKec.getText().toString();
        String nik = etSellerNIK.getText().toString();


        if (name.isEmpty()) {
            etSellerName.setError("Nama Lengkap belum diisi");
            Snackbar.make(svDaftar,"Nama Lengkap belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etSellerName.setError(null);
        }

        if (phone.isEmpty()) {
            etSellerPhone.setError("Nomor Ponsel belum diisi");
            Snackbar.make(svDaftar,"Nomor Ponsel belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etSellerPhone.setError(null);
        }
        if(!sellerPhoto){
            valid = false;
            Snackbar.make(svDaftar,"Foto Diri belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }

        if(!placePhoto){
            valid = false;
            Snackbar.make(svDaftar,"Foto Tempat Usaha belum dipilih.", Snackbar.LENGTH_SHORT).show();
        }

        if (state.isEmpty()) {
            actKec.setError("Kecataman belum diisi");
            Snackbar.make(svDaftar,"Kecataman belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            actKec.setError(null);
        }
        if (nik.isEmpty()) {
            etSellerNIK.setError("NIK KTP belum diisi");
            Snackbar.make(svDaftar,"NIK KTP belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etSellerNIK.setError(null);
        }
        return valid;
    }

    public void uploadSeller(View view) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(R.string.upload_foto_diri)
                .setCancelable(true)
                .setPositiveButton("Kamera", (dialog, id) -> takeCameraSeller());
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        TextView msg = alertDialog.findViewById(android.R.id.message);
        msg.setTextSize(14);
        msg.setTypeface(custom_font);
    }

    private void takeCameraSeller() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    private void takeCameraPlace() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        byte[] compImg;
        ByteArrayOutputStream baos;
        if (resultCode == RESULT_OK && data != null && data.getData() != null && (requestCode == imageRequestCodePlace)) {

            Uri filePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                baos = new ByteArrayOutputStream();
                bitmap = getResizedBitmap(bitmap, 640);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                compImg = baos.toByteArray();
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);

                if (requestCode == imageRequestCodePlace) {
                    ivPlace.setImageBitmap(bitmap2);
                    ivPlace.requestLayout();
                    ivPlace.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    btRemovePlace.setVisibility(View.VISIBLE);
                    placePhoto = true;
                }

                Snackbar.make(svDaftar,"Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if ((requestCode == 1) || (requestCode == 2)) {
            try {
                Bitmap bitmap = null;
                if (data != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }
                baos = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap = getResizedBitmap(bitmap, 640);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    compImg = baos.toByteArray();
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(compImg, 0, compImg.length);
                    if (requestCode == 1) {
                        ivSeller.setImageBitmap(bitmap2);
                        ivSeller.requestLayout();
                        ivSeller.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        btRemoveSeller.setVisibility(View.VISIBLE);
                        sellerPhoto = true;
                    } else {
                        ivPlace.setImageBitmap(bitmap2);
                        ivPlace.requestLayout();
                        ivPlace.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        btRemovePlace.setVisibility(View.VISIBLE);
                        placePhoto = true;
                    }
                    Snackbar.make(svDaftar, "Foto berhasil dipilih.", Snackbar.LENGTH_SHORT).show();
                }
            }catch (Exception e){
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

    public void registerSeller(View view) {
        registerProcess();
    }

    public void berikutnyaDaftar(View view) {
        if(validateSeller()) {
            llInfoToko.setVisibility(View.GONE);
            llInfoAkun.setVisibility(View.VISIBLE);
        }
    }

    public void sebelumnyaDaftar(View view) {
        llInfoToko.setVisibility(View.VISIBLE);
        llInfoAkun.setVisibility(View.GONE);
    }

    public void removePlace(View view) {
        ivPlace.setImageResource(R.drawable.ic_add_img);
        ivPlace.requestLayout();
        ivPlace.setScaleType(ImageView.ScaleType.CENTER);
        btRemovePlace.setVisibility(View.GONE);
        placePhoto = false;
    }

    public void removeSeller(View view) {
        ivSeller.setImageResource(R.drawable.ic_add_img);
        ivSeller.requestLayout();
        ivSeller.setScaleType(ImageView.ScaleType.CENTER);
        btRemoveSeller.setVisibility(View.GONE);
        sellerPhoto = false;
    }

    public void uploadPlace(View view) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Upload Foto Tempat Usaha")
                .setCancelable(true)
                .setNegativeButton("Galeri", (dialog, id) -> {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), imageRequestCodePlace);
                })
                .setPositiveButton("Kamera", (dialog, id) -> takeCameraPlace());

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
}