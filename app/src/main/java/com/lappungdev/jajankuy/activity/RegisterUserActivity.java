package com.lappungdev.jajankuy.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lappungdev.jajankuy.R;
import com.lappungdev.jajankuy.model.User;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String databasePathUser = "jajankuy_db/user";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    @BindView(R.id.acbCheckEmail)
    AppCompatButton acbCheckEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etRePassword)
    EditText etRePassword;
    @BindView(R.id.etUserEmail)
    EditText etUserEmail;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etUserPhone)
    EditText etUserPhone;
    @BindView(R.id.llInfoAkun)
    LinearLayout llInfoAkun;
    @BindView(R.id.llInfoUser)
    LinearLayout llInfoUser;
    @BindView(R.id.svDaftar)
    ScrollView svDaftar;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    private DatabaseReference databaseReferenceUser;
    private ProgressDialog progressDialog;
    private Typeface custom_font;
    private boolean emailClicked = false;
    private boolean emailValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.bind(this);

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(databasePathUser);
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailClicked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        acbCheckEmail.setOnClickListener(v -> {
            emailClicked = true;
            checkEmailProcess(etUserEmail.getText().toString());
        });
    }

    private void checkEmailProcess(String email) {
        firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                emailValid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getSignInMethods()).size() == 0;
                if (emailValid) {
                    tvEmail.setText(R.string.email_tersedia);
                } else {
                    tvEmail.setText(R.string.email_sudah_digunakan);
                }
            }
        });
    }

    private void registerProcess() {
        if (validateAccount()) {
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.proses));
            progressDialog.setCancelable(false);
            progressDialog.show();
            String email = etUserEmail.getText().toString();
            String password = etPassword.getText().toString();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            createFirebaseUserProfile(Objects.requireNonNull(firebaseAuth.getCurrentUser()));
                            registerUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            sentVerificationEmail();
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RegisterUserActivity.this, R.style.AppCompatAlertDialogStyle);
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
                    });
        }
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName("USR - " + WordUtils.capitalizeFully(etUserName.getText().toString()))
                .build();
        user.updateProfile(addProfileName);
    }

    private void registerUser(String id) {
        String userName = WordUtils.capitalizeFully(etUserName.getText().toString());
        String userID = String.format("USR-%s", Long.toHexString(System.currentTimeMillis()).toUpperCase());
        String userEmail = etUserEmail.getText().toString();
        String userPhone = etUserPhone.getText().toString();
        User User = new User(userEmail, userID, userName, userPhone, id, 0);
        databaseReferenceUser.child(id).setValue(User);
    }

    private void sentVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(svDaftar, "Email verifikasi berhasil dikirim.", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onSignupSuccess() {
        FirebaseAuth.getInstance().signOut();
        Snackbar.make(svDaftar, "Daftar berhasil.", Snackbar.LENGTH_SHORT).show();
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
        String email = etUserEmail.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etUserEmail.setError("Alamat email tidak valid");
            Snackbar.make(svDaftar, "Alamat email tidak valid.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etUserEmail.setError(null);
        }

        if ((validatePass(password)) && (password.length() > 8)) {
            etPassword.setError(null);
        } else {
            etPassword.setError("Kata sandi harus lebih dari 8 karakter dan alfanumerik");
            Snackbar.make(svDaftar, "Kata sandi harus lebih dari 8 karakter dan alfanumerik.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        if (rePassword.isEmpty() || !(rePassword.equals(password))) {
            etRePassword.setError("Kata sandi tidak cocok");
            Snackbar.make(svDaftar, "Kata sandi tidak cocok.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etRePassword.setError(null);
        }

        if (!emailValid) {
            valid = false;
            Snackbar.make(svDaftar, "Email sudah terdaftar, gunakan email yang lain.", Snackbar.LENGTH_SHORT).show();
        }
        if (!emailClicked) {
            valid = false;
            Snackbar.make(svDaftar, "Klik tombol periksa email untuk memeriksa email valid.", Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }

    private boolean validateUser() {
        boolean valid = true;
        String name = WordUtils.capitalizeFully(etUserName.getText().toString());
        String phone = etUserPhone.getText().toString();

        if (name.isEmpty()) {
            etUserName.setError("Nama Lengkap belum diisi");
            Snackbar.make(svDaftar, "Nama Lengkap belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etUserName.setError(null);
        }

        if (phone.isEmpty()) {
            etUserPhone.setError("Nomor Ponsel belum diisi");
            Snackbar.make(svDaftar, "Nomor Ponsel belum diisi.", Snackbar.LENGTH_SHORT).show();
            valid = false;
        } else {
            etUserPhone.setError(null);
        }

        return valid;
    }

    public void daftarUser(View view) {
        registerProcess();
    }

    public void berikutnyaDaftar(View view) {
        if (validateUser()) {
            llInfoUser.setVisibility(View.GONE);
            llInfoAkun.setVisibility(View.VISIBLE);
        }
    }

    public void sebelumnyaDaftar(View view) {
        llInfoUser.setVisibility(View.VISIBLE);
        llInfoAkun.setVisibility(View.GONE);
    }
}