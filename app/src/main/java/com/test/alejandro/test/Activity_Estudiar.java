package com.test.alejandro.test;


 import android.app.Activity;
 import android.app.ProgressDialog;
 import android.content.ClipData;
 import android.content.Context;
 import android.content.Intent;
 import android.os.AsyncTask;
import android.os.Bundle;
 import android.os.Environment;
 import android.util.Log;
 import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;

public class Activity_Estudiar extends Activity {

    ItemTest[] datos;
    ArrayList<String> items ;
    AsyncDescargarFichero asyncDescarga;
    Context context;
    public static String _DIRECTORIO_TEST_;
    ProgressDialog dialogoProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout__estudiar);
        Bundle bundle = getIntent().getExtras();
        ListView lista = (ListView) findViewById(R.id.listView);
        context=this;
        dialogoProgreso = new ProgressDialog(context);
        _DIRECTORIO_TEST_ =  Environment.getExternalStorageDirectory().toString()+File.separator+"aprendetest"+File.separator+"data";
        //getFilesDir().getAbsolutePath().toString()+File.separator+"aprendetest"+File.separator+"data";
        if(bundle.getString("conexion")=="si"){
            items = (ArrayList<String>)bundle.getStringArrayList("items");

            datos = new ItemTest[items.size()];

            new File(_DIRECTORIO_TEST_).mkdirs();

            File[] f = new File(_DIRECTORIO_TEST_).listFiles();




            for(int i =0;i <items.size();i++){

                File file = new File(_DIRECTORIO_TEST_+File.separator+items.get(i));

                if(file.exists()==true){
                    datos[i]=new ItemTest(items.get(i),"","Realizar", i);
                }
                else{
                    datos[i]=new ItemTest(items.get(i),"","Descargar", i);
                }

            }
        }
        else{
            File f = new File(_DIRECTORIO_TEST_);
            File[] ficheros = f.listFiles();
            datos = new ItemTest[ficheros.length];
            int j=0;

            for(int i = 0;i<ficheros.length;i++){
                File fch = new File(_DIRECTORIO_TEST_+File.separator+ficheros[i].getName()+File.separator+ficheros[i].getName()+".test");
                if(fch.exists()){
                    j++;
                }
            }
            datos = new ItemTest[j];
            j=0;
            for(int i = 0;i<ficheros.length;i++){
                File fch = new File(_DIRECTORIO_TEST_+File.separator+ficheros[i].getName()+File.separator+ficheros[i].getName()+".test");
                if(fch.exists()){
                    datos[j]=new ItemTest(ficheros[i].getName(),"","Realizar", 0);
                    j++;
                }
            }

        }

        AdapterTest adapter = new AdapterTest(this,datos);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemTest item = datos[position];
                File fichero = new File(_DIRECTORIO_TEST_+File.separator+item.getTitulo()+File.separator+item.getTitulo()+".test");
                if(fichero.exists()){
                    Toast.makeText(Activity_Estudiar.this, "CARGANDO...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Activity_Estudiar.this,realizarTest.class);
                    intent.putExtra("nombre",item.getTitulo());
                    startActivity(intent);
                    finish();
                }
                else{
                    asyncDescarga = new AsyncDescargarFichero();
                    asyncDescarga.execute(item.getTitulo());
                }
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
        protected void onPreExecute(){
            super.onPreExecute();
            dialogoProgreso.setTitle("Descargando");
            dialogoProgreso.setMessage("Por favor espere...");
            dialogoProgreso.setCancelable(false);
            dialogoProgreso.setProgress(0);
            dialogoProgreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //dialogoProgreso.show();
        }

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
                publishProgress(0,0);
                recibirFichero(params[0]);

                publishProgress(50,1);
                descomprimirfichero(params[0]);

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private boolean descomprimirfichero(String fichero){

            File f = new File(_DIRECTORIO_TEST_+File.separator+ fichero+".zip");

            Decompressor decomp;
            decomp = new Decompressor(fichero + ".zip", File.separator);
            decomp.unZip();
            f.delete();
            return true;
        }

        private boolean recibirFichero(String fichero){
            try {
                File f = new File(_DIRECTORIO_TEST_ + File.separator + fichero+".zip");
                f.getParentFile().mkdirs();
                f.createNewFile();
                //Log.d("path",f.getAbsolutePath().toString());
                int tamano = entrada.readInt();
                long tamano_fichero = entrada.readLong();
                long bytesRecibidos = 0;
                byte[] buffer = new byte[tamano];
                int leido = 0;
                FileOutputStream fos = new FileOutputStream(f);

                while((leido=entrada.readInt())>0 ){
                    publishProgress(new Integer((int) (bytesRecibidos/tamano_fichero*100)), 0);
                    try {
                        bytesRecibidos+=leido;
                        buffer = (byte[]) entrada.readObject();
                        fos.write(buffer,0,leido);
                        fos.flush();
                        salida.writeBoolean(true);
                        salida.flush();


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
            if(integer[1]==0){
                dialogoProgreso.setProgress(integer[0]);
                dialogoProgreso.setMessage("Por favor espere...");
            }
            if(integer[1]==1){
                dialogoProgreso.setTitle("Descomprimiendo");
                dialogoProgreso.setProgress(integer[0]);
                dialogoProgreso.setMessage("Por favor espere...");
            }

        }

        @Override
        protected void onPostExecute(Boolean param){
            dialogoProgreso.hide();
            dialogoProgreso = null;

            if(param==true) {
                //Toast.makeText(Activity_Estudiar.this,"NO ERROR",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Activity_Estudiar.this, ActivityLoadingListarTest.class);
                startActivity(intent);
                try {
                    asyncDescarga.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                finish();
            }
            else{
                //Toast.makeText(Activity_Estudiar.this,"ERROR",Toast.LENGTH_SHORT).show();
            }
        }

    }
    class Decompressor {

        private String INPUT_ZIP_FILE = "";
        private String OUTPUT_FOLDER = "";

        public Decompressor(String zipFileName, String outputFolder) {
            this.INPUT_ZIP_FILE = zipFileName;
            this.OUTPUT_FOLDER = outputFolder;
        }


        public void unZip() {

            byte[] buffer = new byte[1024];

            try {

                FileInputStream fis = new FileInputStream(_DIRECTORIO_TEST_+File.separator+INPUT_ZIP_FILE);
                        //new FileInputStream(getFilesDir().getAbsolutePath().toString()+File.separator+INPUT_ZIP_FILE);
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {

                    System.out.println("Extracting: " + OUTPUT_FOLDER+entry.getName());
                    File f = new File(_DIRECTORIO_TEST_+OUTPUT_FOLDER.replace('\\','/')+entry.getName().replace('\\','/'));

                    f.getParentFile().mkdirs();


                    int count;
                    byte data[] = new byte[1024];
                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(_DIRECTORIO_TEST_+OUTPUT_FOLDER.replace('\\','/')+entry.getName().replace('\\','/'));
                    BufferedOutputStream dest = new BufferedOutputStream(fos, 1024);
                    while ((count = zis.read(data, 0, 1024)) != -1) {
                        dest.write(data, 0, count);
                        Log.d("escribe",_DIRECTORIO_TEST_+OUTPUT_FOLDER.replace('\\','/')+entry.getName().replace('\\','/'));
                        data = new byte[1024];
                    }
                    dest.flush();
                    dest.close();
                }
                zis.close();
                fis.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
