package com.test.alejandro.test;

import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Activity_Estudiar extends Activity {

    ItemTest[] datos;
    ArrayList<String> items ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout__estudiar);
        Bundle bundle = getIntent().getExtras();
        ListView lista = (ListView) findViewById(R.id.listView);

        items = (ArrayList<String>)bundle.getStringArrayList("items");
        datos = new ItemTest[items.size()];



        for(int i =0;i <items.size();i++){
            File f = new File(getFilesDir(),"data"+ File.separator+items.get(i));
            if(f.exists()){
                datos[i]=new ItemTest(items.get(i),"","Realizar");
            }
            else{
                datos[i]=new ItemTest(items.get(i),"","Descargar");
            }
        }
        AdapterTest adapter = new AdapterTest(this,datos);
        lista.setAdapter(adapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity__estudiar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
