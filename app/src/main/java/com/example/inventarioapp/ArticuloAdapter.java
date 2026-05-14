package com.example.inventarioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;

    public ArticuloAdapter(List<Articulo> listaArticulos){
        this.listaArticulos = listaArticulos;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int posicion){
        Articulo articuloActual = listaArticulos.get(posicion);

        holder.tvCodigo.setText(String.valueOf(articuloActual.getCodigo()));
        holder.tvDescripcion.setText(articuloActual.getDescripcion());
        holder.tvPrecio.setText("$ " + articuloActual.getPrecio());

        if(articuloActual.getPrecio() >= 100000){
            holder.tvEstado.setText("PREMIUM");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#FFC107"));
        } else {
            holder.tvEstado.setText("ESTANDAR");
            holder.tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Seleccionaste: " + articuloActual.getDescripcion(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return listaArticulos.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvDescripcion, tvPrecio, tvEstado;
        CardView cvContenedor;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
            tvDescripcion = itemView.findViewById(R.id.tvItemDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvItemPrecio);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
            cvContenedor = itemView.findViewById(R.id.cvContenedor);
        }
    }
}
