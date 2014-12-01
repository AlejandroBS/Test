
package com.test.alejandro.test;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by Alejandro on 01/12/2014.
 */
public class Decompressor {

    private String INPUT_ZIP_FILE = "";
    private String OUTPUT_FOLDER = "";

    public Decompressor(String zipFileName, String outputFolder) {
        this.INPUT_ZIP_FILE = zipFileName;
        this.OUTPUT_FOLDER = outputFolder;
    }


    public void unZip() {

        byte[] buffer = new byte[1024];

        try {
            /*
             //create output directory is not exists
             File folder = new File(OUTPUT_FOLDER);
             if (!folder.exists()) {
             folder.mkdir();
             }

             //get the zip file content
             ZipInputStream zis = new ZipInputStream(new FileInputStream(INPUT_ZIP_FILE));
             //get the zipped file list entry
             ZipEntry ze = zis.getNextEntry();

             while (ze != null) {
             try {
             String fileName = ze.getName();
             File newFile = new File(OUTPUT_FOLDER + File.separator + fileName);

                    

             //System.out.println("file unzip : "+ newFile.getAbsoluteFile());
             //create all non exists folders
             //else you will hit FileNotFoundException for compressed folder
             System.out.println(OUTPUT_FOLDER + File.separator + fileName);

             FileOutputStream fos = new FileOutputStream(newFile);

             int len = 0;
             while ((len = zis.read(buffer)) > 0) {
             fos.write(buffer, 0, len);
             }

             fos.close();

             ze = zis.getNextEntry();
             System.out.println("close");

             } catch (IOException ex) {
                    
             }
             }
             zis.close();*/

            FileInputStream fis = new FileInputStream(INPUT_ZIP_FILE);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                
                System.out.println("Extracting: " + OUTPUT_FOLDER+File.separator+entry.getName());
                File f = new File(OUTPUT_FOLDER+File.separator+entry.getName());
                f.getParentFile().mkdirs();
                int count;
                byte data[] = new byte[1024];
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream( OUTPUT_FOLDER+File.separator+entry.getName());
                BufferedOutputStream dest = new BufferedOutputStream(fos, 1024);
                while ((count = zis.read(data, 0, 1024)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
            zis.close();
            fis.close();
            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
