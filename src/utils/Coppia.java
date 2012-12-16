/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author tosco
 */
public class Coppia<E,F>{
    private E first;
    private F second;

    public Coppia(E first, F second){
        this.first = first;
        this.second = second;
    }

    public E getFirst(){return first;}
    public F getSecond(){return second;}

    public String toString(){
        return "("+ first + "," + second + ")";
    }
}

