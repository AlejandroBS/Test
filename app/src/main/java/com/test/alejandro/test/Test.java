package com.test.alejandro.test;

import java.io.Serializable;

/**
 * Created by Alejandro on 03/12/2014.
 */public class Test implements Serializable {


   private static final long serialVersionUID = -7846396081980575608L;
    private String nombreTest = "";
    private Pregunta[] preguntas = null;
    private boolean testVacio = true;
    private int numPreguntasMaximas = 0;
    private int numPregInsertadas = 0;
    private int version = 0;

    public Test(int numPreguntasMaximas){
        this.numPreguntasMaximas = numPreguntasMaximas;

        preguntas = new Pregunta[numPreguntasMaximas];

        for(int i = 0;i<numPreguntasMaximas;i++){
            preguntas[i] = null;
        }
        System.out.println("tamaño preguntas: "+preguntas.length+"--"+nombreTest);

    }

    public Test(String nombreTest, int numPreguntasMaximas){
        this.nombreTest = nombreTest;
        this.numPreguntasMaximas=numPreguntasMaximas;
        testVacio = true;
    }
    /*
    public void add(Pregunta pregunta, int indice){
        testVacio = false;
        if(indice<numPreguntasMaximas){
            if(preguntas[indice]==null){
                System.out.println("era nula, se inserta nueva pregunta");
                setNumPregInsertadas(getNumPregInsertadas() + 1);
            }
            if(pregunta == null){
                setNumPregInsertadas(getNumPregInsertadas() - 1);
            }
            preguntas[indice] = pregunta;
        }
    }*/
    public Pregunta getPregunta(int indice){
        return preguntas[indice];
    }
    public Pregunta[] getPreguntas(){
        return preguntas;
    }
    public int getNumPregMax(){
        return numPreguntasMaximas;
    }
    public String getNombreTest(){
        return nombreTest;
    }
    public int getNumPregInsertadas(){
		/*int num = 0;
		for(int i = 0; i < numPreguntasMaximas ; i++){
			if(preguntas[i]!=null){
				num++;
			}
		}*/
        return numPregInsertadas;
    }

	/*public boolean getTestVacio(){
		return testVacio;
	}*/
/*
	public void setNumPregInsertadas(int numPregInsertadas) {
		this.numPregInsertadas = numPregInsertadas;
	}
	*//*
	public void aumentarVersion(){
		version++;
	}*/

    public int getVersion(){
        return version;
    }
}
