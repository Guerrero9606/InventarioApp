package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextView tvIrRegistro;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvIrRegistro = findViewById(R.id.tvIrRegistro);
        btnLogin = findViewById(R.id.btnLogin);

        tvIrRegistro.setOnClickListener(v -> {
            Intent intencion = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intencion);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intencion = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intencion);

            finish();
        });
    }
}