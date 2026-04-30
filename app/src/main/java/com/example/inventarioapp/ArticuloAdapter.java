package com.example.inventarioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
    }

    @Override
    public int getItemCount(){
        return listaArticulos.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        TextView tvCodigo, tvDescripcion, tvPrecio;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigo = itemView.findViewById(R.id.tvItemCodigo);
            tvDescripcion = itemView.findViewById(R.id.tvItemDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvItemPrecio);
        }
    }
}
