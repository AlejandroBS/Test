package com.test.alejandro.test;

/**
 * Created by Alejandro on 24/11/2014.
 */
public class ItemTest {

    private String nombreTest;
    private String nPreguntas;
    private String estado;
    private int ultimoResultado;

    public ItemTest(String nombreTest, String nPreguntas, String estado, int ultimoResultado){
        this.nombreTest=nombreTest;
        this.nPreguntas=nPreguntas;
        this.estado =estado;
        this.ultimoResultado=ultimoResultado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstado() {

        return estado;
    }

    public String getnPreguntas() {

        return nPreguntas;
    }

    public void setnPreguntas(String nPreguntas) {
        this.nPreguntas = nPreguntas;
    }

    public String getTitulo() {
    
        return nombreTest;
    }

    public void setTitulo(String nombreTest) {
        this.nombreTest = nombreTest;
    }

    public int getUltimoResultado() {
        return ultimoResultado;
    }

    public void setUltimoResultado(int ultimoResultado) {
        this.ultimoResultado = ultimoResultado;
    }


}
