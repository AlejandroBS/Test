package com.test.alejandro.test;


 import android.app.Activity;
import android.content.ClipData;
 import android.content.Intent;
 import android.os.AsyncTask;
import android.os.Bundle;
 import android.util.Log;
 import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Activity_Estudiar extends Activity {

    ItemTest[] datos;
    ArrayList<String> items ;
    AsyncDescargarFichero asyncDescarga;

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
                datos[i]=new ItemTest(items.get(i),"","Realizar", i);
            }
            else{
                datos[i]=new ItemTest(items.get(i),"","Descargar", i);
            }
        }
        AdapterTest adapter = new AdapterTest(this,datos);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemTest item = datos[position];
                Toast.makeText(Activity_Estudiar.this, item.getTitulo(),Toast.LENGTH_SHORT).show();

                asyncDescarga = new AsyncDescargarFichero();
                asyncDescarga.execute(item.getTitulo());


            }
        });


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
    class AsyncDescargarFichero extends AsyncTask<String,Integer,Boolean> {
        Socket socket ;
        ObjectOutputStream salida ;
        ObjectInputStream entrada ;


        @Override
        protected Boolean doInBackground(String... params) {
            try {
                socket = new Socket(DatosConexion.IP,DatosConexion.port);
                salida = new ObjectOutputStream(socket.getOutputStream());
                entrada = new ObjectInputStream(socket.getInputStream());

                if(!socket.isConnected()){
                    return false;
                }

                salida.writeInt(1);
                salida.flush();
                // le decimos que queremos descargar un fichero
                salida.writeInt(2);
                salida.flush();

                salida.writeUTF(params[0]);
                salida.flush();

                recibirFichero(params[0]);

                descomprimirfichero(params[0]);
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean descomprimirfichero(String fichero){
            File f = new File(getFilesDir(),"data" + File.separator + fichero+".zip");

            Decompressor decomp;
            decomp = new Decompressor("data" + File.separator + fichero + ".zip", "data"+File.separator);
            decomp.unZip();
            f.delete();
            return true;
        }

        private boolean recibirFichero(String fichero){
            try {
                File f = new File(getFilesDir(),"data" + File.separator + fichero+".zip");
                int tamano = entrada.readInt();
                byte[] buffer = new byte[tamano];
                int leido = 0;
                FileOutputStream fos = new FileOutputStream(f);

                while((leido=entrada.readInt())>0 ){
                    try {
                        buffer = (byte[]) entrada.readObject();
                        fos.write(buffer,0,leido);
                        fos.flush();
                        salida.writeBoolean(true);
                        salida.flush();
                        Log.d("fichero", String.valueOf(leido));
                        buffer = new byte[tamano];
                    } catch (ClassNotFoundException ex) {
                        return false;
                    }
                }
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... integer){


        }

        @Override
        protected void onPostExecute(Boolean param){
            Intent intent = new Intent(Activity_Estudiar.this,ActivityLoadingListarTest.class);
            startActivity(intent);
            try {
                asyncDescarga.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            finish();
        }

    }
}
