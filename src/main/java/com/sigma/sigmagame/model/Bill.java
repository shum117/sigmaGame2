/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

/**
 *
 * @author anton
 */
public class Bill {
    private int start;
    public static final int price = 100;
    public int id; 
    public static int lastid = 0;
    
    public Bill(int start) {
        this.start = start;
        id = ++lastid;
    }

    public int getPrice(int t) {
        return (int)Math.round(((t-start)*0.05+1)*price);
    }
    
    
}
