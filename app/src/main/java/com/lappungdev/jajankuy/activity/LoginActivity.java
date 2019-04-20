package com.lappungdev.jajankuy.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lappungdev.jajankuy.R;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.cvForgot) CardView cvForgot;
    @BindView(R.id.cvLogin) CardView cvLogin;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etEmailForgot) EditText etEmailForgot;
    @BindView(R.id.etPassword) TextInputEditText etPassword;
    @BindView(R.id.tilEmail) TextInputLayout tilEmail;
    @BindView(R.id.tilPass) TextInputLayout tilPass;
    @BindView(R.id.transContainer) ViewGroup transContainer;
    @BindView(R.id.tvForgot) TextView tvForgot;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String password;
    private Typeface custom_font;
    private boolean emailValid;
    private boolean visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/font.ttf");

        initFirebase();
        boolean loggedIn = isLoggedIn();

        if (loggedIn) {
            gotoMain();
        }

        loadLogin();
        if(!email.isEmpty() && !password.isEmpty()){
            etEmail.setText(email);
            etPassword.setText(password);
        }
        tilEmail.setTypeface(custom_font);
        tilPass.setTypeface(custom_font);
    }

    public void login(View view) {
        email = etEmail.getText().toString().trim();
        password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        if(validateInput()) loginProcess(email, password);
    }

    private boolean validateInput() {
        boolean valid = true;
        String email = etEmail.getText().toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(findViewById(android.R.id.content), "Alamat email tidak boleh kosong", Snackbar.LENGTH_LONG).show();
            valid = false;
        } else if ((!validatePass(password)) && (password.length() < 8)) {
            Snackbar.make(findViewById(android.R.id.content), "Kata sandi tidak boleh kosong", Snackbar.LENGTH_LONG).show();
            valid = false;
        }
        return valid;
    }

    private boolean validatePass(String password) {
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");
        Matcher hasLetter = letter.matcher(password);
        Matcher hasDigit = digit.matcher(password);
        return hasLetter.find() && hasDigit.find();
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    private void gotoForgot() {
        cvForgot = findViewById(R.id.cvForgot);
        cvLogin = findViewById(R.id.cvLogin);
        tvForgot = findViewById(R.id.tvForgot);
        transContainer = findViewById(R.id.transContainer);
        new Handler().postDelayed(() -> {
            TransitionManager.beginDelayedTransition(transContainer);
            visible = !visible;
            cvForgot.setVisibility(visible ? View.VISIBLE : View.GONE);
            cvLogin.setVisibility(visible ? View.GONE : View.VISIBLE);
            tvForgot.setText(R.string.masuk);
        },500);
        visible = false;
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void gotoLogin() {
        new Handler().postDelayed(() -> {
            TransitionManager.beginDelayedTransition(transContainer);
            visible = !visible;
            cvLogin.setVisibility(visible ? View.VISIBLE : View.GONE);
            cvForgot.setVisibility(visible ? View.GONE : View.VISIBLE);
            tvForgot.setText(R.string.lupa_password);
        },500);
        visible = false;
    }

    public void forgot(View view) {
        if(tvForgot.getText().toString().contains("Lupa")){
            gotoForgot();
        }else{
            gotoLogin();
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterSellerActivity.class));
    }

    private boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(findViewById(android.R.id.content), "Verifikasi email berhasil dikirim.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void sendResetPassword(String email){
        try {
            final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Proses ...");
            progressDialog.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Link reset password berhasil dikirim ke email kamu.", Snackbar.LENGTH_LONG).show();
                }
            });
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void checkEmailProcess(String emailC){
        firebaseAuth.fetchSignInMethodsForEmail(emailC).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                try {
                    emailValid = Objects.requireNonNull(task.getResult().getSignInMethods()).size() == 0;
                    if (!emailValid) {
                        sendResetPassword(emailC);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Alamat email tidak terdaftar.", Snackbar.LENGTH_LONG).show();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void loginProcess(final String username, final String password) {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Proses ...");
        progressDialog.show();
        try {
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    saveLogin();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            gotoMain();
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
                            alertDialogBuilder
                                    .setTitle("Verifikasi Email")
                                    .setMessage("Kami telah mengirimkan email ke " + user.getEmail() + " untuk  memverifikasi alamat email kamu. Silahkan klik link di email tersebut untuk dapat masuk ke akun.")
                                    .setCancelable(false)
                                    .setPositiveButton("Keluar", (dialog, id) -> {
                                        progressDialog.dismiss();
                                        FirebaseAuth.getInstance().signOut();
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("Kirim Ulang", (dialog, id) -> {
                                        sendVerificationEmail();
                                        FirebaseAuth.getInstance().signOut();
                                        dialog.dismiss();
                                        progressDialog.dismiss();
                                    });
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                            TextView pesan = alertDialog.findViewById(android.R.id.message);
                            if (pesan != null) {
                                pesan.setTextSize(15);
                                pesan.setTypeface(custom_font);
                            }

                            Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            b.setTypeface(custom_font);
                            b.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                            Button c = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            c.setTypeface(custom_font);
                            c.setTextColor(getResources().getColor(R.color.colorHitam));
                        }
                    }

                } else {
                    showMessageBox("Proses gagal. Alamat email atau kata sandi salah");
                    progressDialog.hide();
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void showMessageBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Masuk");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Oke", (dialogInterface, i) -> dialogInterface.dismiss());
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView pesan = alertDialog.findViewById(android.R.id.message);
        if (pesan != null) {
            pesan.setTextSize(15);
            pesan.setTypeface(custom_font);
        }

    }

    private void saveLogin(){
        email = etEmail.getText().toString();
        password = Objects.requireNonNull(etPassword.getText()).toString();
        SharedPreferences sharedPref= getSharedPreferences("login", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void loadLogin(){
        SharedPreferences sharedPref = getSharedPreferences("login", 0);
        email = sharedPref.getString("email","");
        password = sharedPref.getString("password","");
    }

    public void sendReset(View view) {
        String emailForgot = etEmailForgot.getText().toString();
        if(!emailForgot.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailForgot).matches()){
            checkEmailProcess(emailForgot);
        }else{
            Snackbar.make(findViewById(android.R.id.content), "Alamat email tidak boleh kosong.", Snackbar.LENGTH_LONG).show();
        }
    }
}