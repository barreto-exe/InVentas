package com.teamihc.inventas.activities;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.teamihc.inventas.R;
import com.teamihc.inventas.backend.entidades.Articulo;
import com.teamihc.inventas.backend.entidades.Tasa;
import com.teamihc.inventas.dialogs.ConfirmarEliminacionDialogFragment;
import com.teamihc.inventas.dialogs.SobreescribirDialogFragment;

import java.util.ArrayList;
import java.util.Date;

public class CrearProductoActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private String descripcion_original = "";
    private int cantidad_original = 0;
    private TextInputEditText descripcionProdView;
    private TextInputEditText costoView;
    private TextInputEditText precioView;
    private TextView precioBsView;
    private TextInputEditText codigoView;
    private TextInputEditText cantidadView;
    private boolean modoEdicion;
    private ImageView imagenProd;

    //request code to pick image
    private static final int PICK_IMAGES_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_producto);
        toolbar = findViewById(R.id.crear_bar);
        
        descripcionProdView = findViewById(R.id.descripcionProd);
        costoView = findViewById(R.id.costo);
        precioView = findViewById(R.id.precio);
        precioBsView = findViewById(R.id.precioBs);
        codigoView = findViewById(R.id.codTxt);
        cantidadView = findViewById(R.id.cantidad);
        imagenProd = (ImageView) findViewById(R.id.imagenProd);

        toolbar = findViewById(R.id.crearArticuloToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.articulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        modoEdicion = getIntent().getExtras() != null;
        if (modoEdicion)
        {
            llenarFormulario();
            bloquearCampos();
        }
        
        descripcion_original = ((TextView) findViewById(R.id.descripcionProd)).getText().toString();
        agregarListeners();
    }
    
    private void agregarListeners()
    {
        //Actualizar equivalencia Dolar/Bs
        precioView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            
            }
            
            @Override
            public void afterTextChanged(Editable s)
            {
                float precioBs = 0;
                try
                {
                    precioBs = Float.parseFloat(precioView.getText().toString());
                    precioBs *= Tasa.obtenerTasa().getMonto();
                }
                catch (Exception ex)
                {
                }
                precioBsView.setText(String.valueOf(precioBs));
            }
        });
        
        //Salir de la ventana
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crear_bar, menu);
        
        if (!modoEdicion)
        {
            menu.setGroupVisible(R.id.group_edicion, false);
        }
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_eliminar:
            {
                eliminarArticulo();
                break;
            }
            case R.id.nav_editar:
            {
                permitirEdicion();
                break;
            }
            case R.id.nav_guardar:
            {
                salvarDatos();
                break;
            }
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void bloquearCampos()
    {
        descripcionProdView.setEnabled(false);
        costoView.setEnabled(false);
        precioView.setEnabled(false);
        codigoView.setEnabled(false);
        cantidadView.setEnabled(false);
    }
    
    private void permitirEdicion()
    {
        descripcionProdView.setEnabled(true);
        costoView.setEnabled(true);
        precioView.setEnabled(true);
        codigoView.setEnabled(true);
        cantidadView.setEnabled(true);
    }
    
    private void llenarFormulario()
    {
        Articulo articulo = Articulo.obtenerInstancia(getIntent().getExtras().getString("descripcion"));
        
        descripcionProdView.setText(articulo.getDescripcion());
        costoView.setText(articulo.getCosto() + "");
        precioView.setText(articulo.getPrecio() + "");
        precioBsView.setText(articulo.getPrecioBs() + "");
        codigoView.setText(articulo.getCodigo());
        cantidadView.setText(articulo.getCantidad() + "");
        
        cantidad_original = articulo.getCantidad();
    }
    
    public boolean validarDatos()
    {
        boolean todoBien = true;
        String error = "No puede estar vacío.";
        
        if (descripcionProdView.getText().toString().equals(""))
        {
            descripcionProdView.setError(error);
            todoBien = false;
        }
        if (costoView.getText().toString().equals(""))
        {
            costoView.setError(error);
            todoBien = false;
        }
        if (precioView.getText().toString().equals(""))
        {
            precioView.setError(error);
            todoBien = false;
        }
        if (cantidadView.getText().toString().equals("") ||
                Integer.parseInt(cantidadView.getText().toString()) < 0)
        {
            cantidadView.setError("Revise este campo.");
            todoBien = false;
        }
        
        return todoBien;
    }
    
    public void eliminarArticulo()
    {
        Articulo articulo = Articulo.obtenerInstancia(descripcion_original);
        new ConfirmarEliminacionDialogFragment(this, articulo).show(getSupportFragmentManager(), null);
    }
    
    public void salvarDatos()
    {
        if (!validarDatos())
        {
            return;
        }
        
        String descripcion = descripcionProdView.getText().toString();
        float costo = Float.parseFloat(costoView.getText().toString());
        float precio = Float.parseFloat(precioView.getText().toString());
        int cantidad = Integer.parseInt(cantidadView.getText().toString());
        String codigo = codigoView.getText().toString();
        
        Articulo articulo = new Articulo(descripcion, costo, precio, cantidad, codigo);
        int cambio_stock = cantidad - cantidad_original;
        
        //si se modifico la descripcion
        if (!descripcion_original.equals(articulo.getDescripcion()))
        {
            //la descripcion es unica??
            if (articulo.obtenerId() == -1)
            {
                //es un articulo nuevo?
                if (descripcion_original.equals(""))
                {
                    articulo.registrar();
                    Toast.makeText(getApplicationContext(), "Articulo registrado con exito", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    //Si se cambia la descripcion, eliminar articulo y registrar uno nuevo
                    Articulo articulo_eliminar = Articulo.obtenerInstancia(descripcion_original);
                    articulo_eliminar.eliminar();
                    articulo.registrar();
                    finish();
                }
                //Desea sobreescribir??
            }
            else
            {
                new SobreescribirDialogFragment(this, articulo, cambio_stock).show(getSupportFragmentManager(), null);
            }
        }
        else
        {
            //estamos en modo edicion, actualizar
            actualizarArticulo(articulo, cambio_stock);
        }
    }
    
    public void actualizarArticulo(Articulo articulo, int cambio_stock)
    {
        articulo.actualizar();
        if (cambio_stock != 0)
        {
            articulo.agregarStock(cambio_stock, new Date());
        }
        finish();
    }

    //<-------------------------------Metodos para capturar una foto------------------------------->
    public void obtenerImagen(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        //intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE && resultCode == Activity.RESULT_OK
            && data.getClipData() == null){
                imagenProd.setImageURI(data.getData());
        }
    }
    //<-------------------------------Metodos para capturar una foto------------------------------->

    public void salir(View view)
    {
        finish();
    }
}