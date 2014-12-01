package com.test.alejandro.test;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Alejandro on 24/11/2014.
 */
public class AdapterTest extends ArrayAdapter<ItemTest>{
    Activity context;
    ItemTest[] datos;
    public AdapterTest(Activity context, ItemTest[] datos){
        super(context, R.layout.item_test,datos);
        this.context=context;
        this.datos = datos;
    }

    public View getView(int posicion, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.item_test,null);

        TextView titulo = (TextView) item.findViewById(R.id.titulo);
        titulo.setText(datos[posicion].getTitulo());
/*
        TextView numPreguntas = (TextView) item.findViewById(R.id.numpreguntas);
        numPreguntas.setText(datos[posicion].getnPreguntas());
*/
        ImageView image = (ImageView) item.findViewById(R.id.imageView);
        if(datos[posicion].getEstado().equals("Realizar")){
            image.setImageResource(R.drawable.pencil_s);
        }
        if(datos[posicion].getEstado().equals("Descargar")){
            image.setImageResource(R.drawable.download);
        }
        ImageView image2 = (ImageView) item.findViewById(R.id.imageView3);
        if(datos[posicion].getUltimoResultado() == 0) {
            image2.setImageResource(R.drawable.prov_aprobado);
        }
        else{
            image2.setImageResource(R.drawable.prov_suspenso);
        }

        return item;
    }
}
