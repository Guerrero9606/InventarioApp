package com.example.inventarioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilCodigo, tilDescripcion, tilPrecio;
    private TextInputEditText etCodigo, etDescripcion, etPrecio;
    private Button btnRegistrar, btnBorrar, btnEditar, btnBuscar, btnVerTodos, btnCerrarSesion, btnGuardarTienda;

    private RecyclerView rvArticulos;
    private ArticuloAdapter adaptador;
    private List<Articulo> listaArticulos;
    private FirebaseFirestore db;
    private android.widget.ProgressBar pbCarga;
    private com.google.android.material.switchmaterial.SwitchMaterial swOferta;
    private com.google.firebase.firestore.ListenerRegistration listenerFirestore;
    private EditText etNombreTienda;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilCodigo = findViewById(R.id.tilCodigo);
        tilDescripcion = findViewById(R.id.tilDescripcion);
        tilPrecio = findViewById(R.id.tilPrecio);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        etNombreTienda = findViewById(R.id.etNombreTienda);

        rvArticulos = findViewById(R.id.rvArticulos);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnGuardarTienda = findViewById(R.id.btnGuardarTienda);

        pbCarga = findViewById(R.id.pbCarga);
        swOferta = findViewById(R.id.swOferta);
        btnVerTodos = findViewById(R.id.btnVerTodos);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences preferencias = getSharedPreferences("ConfiguracionApp", MODE_PRIVATE);

        String tiendaGuardada = preferencias.getString("nombre_tienda", "Mi inventario");

        etNombreTienda.setText(tiendaGuardada);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inventario: " + tiendaGuardada);
        }

        etPrecio.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    tilPrecio.setError("El precio no puede estar vacío");
                } else if (Double.parseDouble(s.toString()) <= 0) {
                    tilPrecio.setError("El precio debe ser mayor a cero");
                } else {
                    tilPrecio.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) { }
        });

        btnRegistrar.setOnClickListener(v -> {
            if (esFormularioValido()){
                registrarArticuloFirebase();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarArticuloFirebase();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarArticuloFirebase();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarArticuloFirebase();
            }
        });

        btnVerTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { filtrarSoloOfertas(); }
        });

        btnCerrarSesion.setOnClickListener(v -> {
            mAuth.signOut();

            Intent intencion = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intencion);

            finish();
        });

        btnGuardarTienda.setOnClickListener(v -> {
            String nombreTienda = etNombreTienda.getText().toString().trim();

            if (!nombreTienda.isEmpty()) {
                SharedPreferences preferences = getSharedPreferences("ConfiguracionApp", MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("nombre_tienda", nombreTienda);

                editor.apply(); //.commit() bloquea toda la app

                Toast.makeText(MainActivity.this, "Nombre guardado correctamente en el dispositivo", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
            }
        });



        rvArticulos.setLayoutManager(new GridLayoutManager(this, 2));

        cargarDatosEnTiempoReal();

    }

    private boolean esFormularioValido(){
        boolean esValido = true;

        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (codigo.isEmpty() || codigo.length() < 3){
            tilCodigo.setError("El codigo debe tener al menos 3 digitos");
            esValido = false;
        } else {
            tilCodigo.setErrorEnabled(false);
        }

        if (descripcion.isEmpty() || descripcion.length() < 5 ) {
            tilDescripcion.setError("Sea mas descriptivo con el articulo");
            esValido = false;
        } else {
            tilDescripcion.setErrorEnabled(false);
        }

        if (precio.isEmpty()) {
            tilPrecio.setError("El campo no puede estar vacio");
            esValido = false;
        } else {
            tilPrecio.setErrorEnabled(false);
        }

        return esValido;
    }
    private void registrarArticulo(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            //INSERT INTO articulos (codigo, descripcion, precio) VALUES (123423, "Teclado", 230000.00);
            baseDeDatos.insert("articulos", null, registro);

            cargarListaArticulos();

            if (adaptador != null){
                adaptador.notifyDataSetChanged();
            }

            //Cerrar conexion a la base de datos
            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            Toast.makeText(this, "Articulo registrado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarArticulo(){
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getReadableDatabase();

            android.database.Cursor fila = baseDeDatos.rawQuery("SELECT descripcion, precio FROM articulos WHERE codigo = " + codigo, null);

            if (fila.moveToFirst()){
                etDescripcion.setText(fila.getString(0));
                etPrecio.setText(fila.getString(1));
                Toast.makeText(this, "Articulo encontrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No existe un articulo con ese codigo", Toast.LENGTH_SHORT).show();
                etDescripcion.setText("");
                etPrecio.setText("");
            }

            baseDeDatos.close();
            fila.close();
        } else {
            Toast.makeText(this, "Debes ingresar el codigo del articulo a buscar", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarArticuloFirebase(){
        String codigo = etCodigo.getText().toString();

        if(codigo.isEmpty()){
            Toast.makeText(this, "Ingrese el codigo a buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("articulos").document(codigo).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String descripcion = documentSnapshot.getString("descripcion");
                        Double precio = documentSnapshot.getDouble("precio");
                        Boolean oferta = documentSnapshot.getBoolean("oferta");

                        etDescripcion.setText(descripcion);
                        etPrecio.setText(String.valueOf(precio));

                        if (oferta != null){
                            swOferta.setChecked(oferta);
                        } else {
                            swOferta.setChecked(false);
                        }

                        Toast.makeText(this, "Articulo encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "El articulo no existe", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                   Toast.makeText(this, "Error de conexion", Toast.LENGTH_SHORT).show();
                });
    }

    private void borrarArticulo(){
        String codigo = etCodigo.getText().toString();

        if (!codigo.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            int cantidadBorrados = baseDeDatos.delete("articulos", "codigo=" + codigo, null);

            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            if (cantidadBorrados == 1){
                Toast.makeText(this, "Articulo eliminado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El articulo no existe", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese el codigo del articulo a eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void borrarArticuloFirebase(){
        String codigo = etCodigo.getText().toString();

        if (codigo.isEmpty()){
            Toast.makeText(this, "Ingrese el codigo del articulo a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("articulos").document(codigo).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Articulo eliminado", Toast.LENGTH_SHORT).show();
                    etCodigo.setText("");
                    etDescripcion.setText("");
                    etPrecio.setText("");
                    swOferta.setChecked(false);
                })
                .addOnFailureListener(e -> {
                   Toast.makeText(this, "Error al borrar", Toast.LENGTH_SHORT).show();
                });
    }

    private void modificarArticulo(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
            SQLiteDatabase baseDeDatos = admin.getWritableDatabase();

            ContentValues registroNuevo = new ContentValues();
            registroNuevo.put("codigo", codigo);
            registroNuevo.put("descripcion", descripcion);
            registroNuevo.put("precio", precio);

            int cantidadActualizados = baseDeDatos.update("articulos", registroNuevo, "codigo=" + codigo, null);

            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");

            if (cantidadActualizados == 1){
                Toast.makeText(this, "Articulo actualizado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se encontro articulo para actualizar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void modificarArticuloFirebase(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();
        boolean oferta = swOferta.isChecked();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            db.collection("articulos").document(codigo)
                    .update(
                            "descripcion", descripcion,
                            "precio", Double.parseDouble(precio),
                            "oferta", oferta
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Articulo actualizado", Toast.LENGTH_SHORT).show();
                        etCodigo.setText("");
                        etDescripcion.setText("");
                        etPrecio.setText("");
                        swOferta.setChecked(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Llena todos los campos para editar", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarListaArticulos(){
        //SQLite
        //listaArticulos = new ArrayList<>();

        //AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
        //SQLiteDatabase bd = admin.getReadableDatabase();

        //android.database.Cursor fila = bd.rawQuery("SELECT codigo, descripcion, precio FROM articulos", null);

        //while (fila.moveToNext()){
        //    int codigo = fila.getInt(0);
        //    String descripcion = fila.getString(1);
        //    double precio = fila.getDouble(2);

        //    listaArticulos.add(new Articulo(codigo, descripcion, precio));
        //}

        //bd.close();
        //fila.close();

        //adaptador = new ArticuloAdapter(listaArticulos);

        //rvArticulos.setAdapter(adaptador);

        //Firebase
        listaArticulos = new ArrayList<>();

        db.collection("articulos")
                .get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()){
                        listaArticulos.clear();

                        for (QueryDocumentSnapshot documento : task.getResult()){
                            Articulo articulo = documento.toObject(Articulo.class);
                            listaArticulos.add(articulo);
                        }

                        adaptador = new ArticuloAdapter(listaArticulos);
                        rvArticulos.setAdapter(adaptador);
                    } else {
                        Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    }
                } );
    }

    private void cargarDatosEnTiempoReal(){
        listaArticulos = new ArrayList<>();
        adaptador = new ArticuloAdapter(listaArticulos);
        rvArticulos.setAdapter(adaptador);

        listenerFirestore = db.collection("articulos")
                .addSnapshotListener( (value, error) -> {
                    if(error != null){
                        Toast.makeText(this, "Fallo al escuchar los cambios", Toast.LENGTH_SHORT).show();
                    }

                    if (value != null){
                        listaArticulos.clear();

                        for (QueryDocumentSnapshot documento : value){
                            Articulo articulo = documento.toObject(Articulo.class);
                            listaArticulos.add(articulo);
                        }

                        adaptador.notifyDataSetChanged();
                    }
                } );
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(listenerFirestore != null) {
            listenerFirestore.remove();
        }
    }

    private void registrarArticuloFirebase(){
        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        boolean estaEnOferta = swOferta.isChecked();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            pbCarga.setVisibility(View.VISIBLE);
            btnRegistrar.setEnabled(false);

            Map<String, Object> articuloMap = new HashMap<>();
            articuloMap.put("codigo", Integer.parseInt(codigo));
            articuloMap.put("descripcion", descripcion);
            articuloMap.put("precio", Double.parseDouble(precio));
            articuloMap.put("oferta", estaEnOferta);

            db.collection("articulos").document(codigo)
                    .set(articuloMap)
                    .addOnSuccessListener(aVoid -> {
                        pbCarga.setVisibility(View.GONE);
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Guardado en la NUBE", Toast.LENGTH_SHORT).show();
                        etCodigo.setText("");
                        etDescripcion.setText("");
                        etPrecio.setText("");
                        swOferta.setChecked(false);
                    })
                    .addOnFailureListener(e -> {
                        pbCarga.setVisibility(View.GONE);
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Llena todos los campos del formulario", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarSoloOfertas() {
        db.collection("articulos")
                .whereEqualTo("oferta", true)
                .whereGreaterThan("precio", 100000)
                .addSnapshotListener( (value, error) -> {
                    if (error != null){
                        return;
                    }

                    if (value != null){
                        listaArticulos.clear();

                        for (QueryDocumentSnapshot documento: value){
                            listaArticulos.add(documento.toObject(Articulo.class));
                        }

                        adaptador.notifyDataSetChanged();
                    }
                } );
    }
}