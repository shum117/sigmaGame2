/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.KryoConfig;
import com.sigma.sigmagame.Model;
import java.util.Hashtable;

/**
 *
 * @author anton
 */
public class State {
    private Hashtable<Integer,Bill> debts;
    public double account = 10000000;
    private Model m;
    
    public State(Model m) {
        this.m = m;
        debts = new Hashtable<>();
    }
    
    public KryoConfig.TransactionStatus payBill(int id, Corporation corp){
        KryoConfig.TransactionStatus ts = new KryoConfig.TransactionStatus();
        Bill b = debts.get(id);
        if(b == null){
            ts.isSuccess = false;
            ts.error = "Номер векселя не найден";
            return ts;
        }
        if(account < b.getPrice(m.t)){
            ts.isSuccess = false;
            ts.error = "Денег нет, но вы держитесь";
            return ts;
        }
        corp.putMoney(b.getPrice(m.t));
        debts.remove(id);
        ts.isSuccess = true;
        return ts;
    }
    
    public int emitBill(){
        Bill b = new Bill(m.t);
        debts.put(b.id, b);
        return b.id;
    }
    
    public void putMoney(int n){
        account+=n;
    }
    
    public boolean getMoney(int n){
        if(account < n)
            return false;
        account-=n;
        return true;
    }
}
