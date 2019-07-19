/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;
import com.sigma.sigmagame.model.Corporation;
import com.sigma.sigmagame.*;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author anton
 */
public class Player {
    public String rfid;
    public int plain;
    public String name;
    public HashMap<String,Integer> items;
    public Corporation corp;
    public Model m;
    public Player(String rfid, String name, int plain, Corporation corp, Model m){
        this.m = m;
        this.rfid = rfid;
        this.name = name;
        this.plain = plain;
        this.corp = corp;
        items = new HashMap<>();
        for (String string : m.items.keySet()) {
            items.put(string, 0);
        }
    }
    
    public void putItem(int n, String what){
        items.put(what,items.get(what)+n);
    }
    
    public boolean getItem(int n, String what){
        if(items.get(what) < n)
            return false;
        items.put(what,items.get(what)-n);
        return true;
    }
    
    public KryoConfig.TransactionStatus produce(int n, String what){
        KryoConfig.TransactionStatus ts = new KryoConfig.TransactionStatus();
        Item a = m.items.get(what);
        if(!a.couldBeProduced){
            ts.isSuccess = false;
            ts.error = "Это нельзя произвести";
            return ts;
        }
        if(corp.labour < a.labour*n){
            ts.isSuccess = false;
            ts.error = "Недостаточно производственной мощности";
            return ts;
        }
        for (Map.Entry<String, Integer> entry : a.recipe.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            if(items.get(key) < val*n){
                ts.isSuccess = false;
                ts.error = "Нехватает ресурсов ("+ key +")";
                return ts;
            }
        }
        for (Map.Entry<String, Integer> entry : a.recipe.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            items.put(key,items.get(key) - val*n);
        }
        items.put(what,items.get(what)+n);
        corp.labour -= a.labour*n;
        ts.isSuccess = true;
        return ts;
    }
}
