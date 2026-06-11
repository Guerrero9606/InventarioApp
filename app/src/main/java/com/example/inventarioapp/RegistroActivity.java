package com.example.inventarioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private TextView tvVolverLogin;
    private Button btnRegistrarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tvVolverLogin = findViewById(R.id.tvVolverLogin);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);

        tvVolverLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegistrarUsuario.setOnClickListener(v -> {
            Toast.makeText(RegistroActivity.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
        });
    }
}