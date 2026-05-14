package com.example.inventarioapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText etCodigo, etDescripcion, etPrecio;
    private Button btnRegistrar, btnBorrar, btnEditar, btnBuscar, btnVerTodos;

    private RecyclerView rvArticulos;
    private ArticuloAdapter adaptador;
    private List<Articulo> listaArticulos;
    private FirebaseFirestore db;
    private android.widget.ProgressBar pbCarga;
    private com.google.android.material.switchmaterial.SwitchMaterial swOferta;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.etCodigo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);

        rvArticulos = findViewById(R.id.rvArticulos);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);

        pbCarga = findViewById(R.id.pbCarga);
        swOferta = findViewById(R.id.swOferta);
        //btnVerTodos = findViewById(R.id.btnVerTodos);
        db = FirebaseFirestore.getInstance();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarArticuloFirebase();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarArticulo();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarArticulo();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarArticulo();
            }
        });

        /*btnVerTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { cargarListaArticulos(); }
        });*/

        rvArticulos.setLayoutManager(new LinearLayoutManager(this));

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

    private void cargarListaArticulos(){
        listaArticulos = new ArrayList<>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion.db", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        android.database.Cursor fila = bd.rawQuery("SELECT codigo, descripcion, precio FROM articulos", null);

        while (fila.moveToNext()){
            int codigo = fila.getInt(0);
            String descripcion = fila.getString(1);
            double precio = fila.getDouble(2);

            listaArticulos.add(new Articulo(codigo, descripcion, precio));
        }

        bd.close();
        fila.close();

        adaptador = new ArticuloAdapter(listaArticulos);

        rvArticulos.setAdapter(adaptador);
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
}