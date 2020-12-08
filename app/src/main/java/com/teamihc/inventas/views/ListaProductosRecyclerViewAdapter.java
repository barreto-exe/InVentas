package com.teamihc.inventas.views;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.teamihc.inventas.R;
import com.teamihc.inventas.activities.CarritoActivity;
import com.teamihc.inventas.activities.CrearProductoActivity;
import com.teamihc.inventas.activities.MainActivity;
import com.teamihc.inventas.backend.entidades.Articulo;
import com.teamihc.inventas.fragments.ListaProductosVentaFragment;

import java.util.ArrayList;

public class ListaProductosRecyclerViewAdapter extends RecyclerView.Adapter<ListaProductosRecyclerViewAdapter.ListaProductosAdapter> implements View.OnClickListener
{
    
    //LayoutInflater inflater;
    private View.OnClickListener listener;
    private ArrayList<Articulo> listaArticulos;
    Dialog dialog;
    
    //constructor, en este se le debe pasar tambien la lista por parametro
    public ListaProductosRecyclerViewAdapter(ArrayList<Articulo> listaArticulos)
    {
        //this.inflater = LayoutInflater.from(context);
        this.listaArticulos = listaArticulos;
    }
    
    @NonNull
    @Override
    public ListaProductosAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_info_producto, parent, false);
        view.setOnClickListener(this);
        return new ListaProductosAdapter(view);
    }
    
    //Esto lo debes cambiar de acuerdo a tu lista, lo que está despues del ultimo punto representa el campo del nodo de la lista
    //ejemplo: holder.imagenProd.setImageResource(lista.get(position).fotoProducto); donde foto producto es la foto que está guardada en la base de datos
    //y que luego de ser consultada y vaciada en la lista se coloca ahí
    @Override
    public void onBindViewHolder(@NonNull ListaProductosAdapter holder, int position)
    {
        holder.asignarDatos(listaArticulos.get(position));
    }
    
    @Override
    public int getItemCount()
    {
        return listaArticulos.size();
    }
    
    //Esto es para que se pueda editar la cantidad del stock, se complementa con setOnClick()
    @Override
    public void onClick(View v)
    {
        TextView descripcion = (TextView) v.findViewById(R.id.descripcion);

        if (v.getContext() instanceof MainActivity)
        {
            MainActivity mainActivity = ((MainActivity) v.getContext());
            Intent intent = new Intent(mainActivity, CrearProductoActivity.class);
            intent.putExtra("descripcion", descripcion.getText().toString());
            mainActivity.startActivity(intent);
        } else{
            CarritoActivity carritoActivity = ((CarritoActivity) v.getContext());
            carritoActivity.hideFragment();
            carritoActivity.cargarArticulo(descripcion.getText().toString());

            //carritoActivity.getFragmentManager().beginTransaction().hide(new ListaProductosVentaFragment());
            //carritoActivity.getFragmentManager().beginTransaction().remove(new ListaProductosVentaFragment());
        }
        
    }
    
    public class ListaProductosAdapter extends RecyclerView.ViewHolder
    {
        
        CardView cardView;
        
        public ListaProductosAdapter(@NonNull View itemView)
        {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.info_producto);
        }
        
        public void asignarDatos(Articulo articulo)
        {
            ImageView imagenProd = (ImageView) cardView.findViewById(R.id.imagenProd);
            TextView descripcion = (TextView) cardView.findViewById(R.id.descripcion);
            TextView precioBsS = (TextView) cardView.findViewById(R.id.precioBsS);
            TextView cantidadStock = (TextView) cardView.findViewById(R.id.cantidadStock);
            TextView costoD = (TextView) cardView.findViewById(R.id.costoD);
            TextView precioD = (TextView) cardView.findViewById(R.id.precioD);
            
            //imagenProd.setImageResource();
            descripcion.setText(articulo.getDescripcion());
            precioBsS.setText("" + 0);
            cantidadStock.setText("" + articulo.getCantidad());
            costoD.setText("" + articulo.getCosto());
            precioD.setText("" + articulo.getPrecio());
        }
    }
}
