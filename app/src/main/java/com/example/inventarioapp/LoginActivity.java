package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView tvIrRegistro;
    private Button btnLogin;
    private com.google.android.material.textfield.TextInputEditText etLoginCorreo, etLoginPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvIrRegistro = findViewById(R.id.tvIrRegistro);
        btnLogin = findViewById(R.id.btnLogin);
        etLoginCorreo = findViewById(R.id.etLoginCorreo);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            Intent intencion = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intencion);
            finish();
            return;
        }

        tvIrRegistro.setOnClickListener(v -> {
            Intent intencion = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intencion);
        });

        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion(){
        String correo = etLoginCorreo.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Verificando...");

        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {
                   btnLogin.setEnabled(true);
                   btnLogin.setText("INICIAR SESION");

                   if (task.isSuccessful()) {
                       Intent intencion = new Intent(LoginActivity.this, MainActivity.class);
                       startActivity(intencion);
                       finish();
                   } else {
                       Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}