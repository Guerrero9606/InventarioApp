package com.example.inventarioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private TextView tvVolverLogin;
    private Button btnRegistrarUsuario;

    private com.google.android.material.textfield.TextInputEditText etRegistrarNombre, etRegistrarCorreo, etRegistrarPassword, etRegistrarPasswordConf;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tvVolverLogin = findViewById(R.id.tvVolverLogin);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        etRegistrarNombre = findViewById(R.id.etRegistrarNombre);
        etRegistrarCorreo = findViewById(R.id.etRegistrarCorreo);
        etRegistrarPassword = findViewById(R.id.etRegistrarPassword);
        etRegistrarPasswordConf = findViewById(R.id.etRegistrarPasswordConf);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvVolverLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegistrarUsuario.setOnClickListener(v -> crearCuentaFirebase());
    }

    private void crearCuentaFirebase(){
        String nombre = etRegistrarNombre.getText().toString().trim();
        String correo = etRegistrarCorreo.getText().toString().trim();
        String password = etRegistrarPassword.getText().toString().trim();
        String confirmacion = etRegistrarPasswordConf.getText().toString().trim();

        //&& AND, || OR
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || confirmacion.isEmpty()){
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6){
            Toast.makeText(this, "La contraseña debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmacion)){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegistrarUsuario.setEnabled(false);
        btnRegistrarUsuario.setText("Creando Cuenta...");

        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(task -> {
                    btnRegistrarUsuario.setEnabled(true);
                    btnRegistrarUsuario.setText("REGISTRARME");

                    if (task.isSuccessful()) {
                        String uidUsuario = mAuth.getCurrentUser().getUid();

                        Map<String, Object> perfilUsuario = new HashMap<>();
                        perfilUsuario.put("nombre", nombre);
                        perfilUsuario.put("correo", correo);
                        perfilUsuario.put("rol", "Administrador");

                        db.collection("usuario").document(uidUsuario).set(perfilUsuario)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(RegistroActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                                            finish();
                                        });
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}