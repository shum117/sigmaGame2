/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.KryoConfig;
import com.sigma.sigmagame.Model;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author anton
 */
public class StateOrder {
    public String item;
    public int amount;
    public boolean payByBill;
    public int price;
    private Model m;
    public int id;
    public static int lastid = 0;
    public ArrayList<Integer> bills;
    
    public StateOrder(String item, int amount, boolean payByBill, int price, Model m) {
        this.item = item;
        this.amount = amount;
        this.payByBill = payByBill;
        this.price = price;
        this.m = m;
        bills = new ArrayList<>();
        id = ++lastid;
        m.orders.put(id, this);
    }

    public KryoConfig.StateOrderDto getDTO(){
        KryoConfig.ProductData pd = new KryoConfig.ProductData(amount, item);
        KryoConfig.StateOrderDto dto = new KryoConfig.StateOrderDto(id,price,pd,payByBill);
        return dto;
    }
    
    public KryoConfig.TransactionStatus fulfill(Player pl){
        KryoConfig.TransactionStatus ts = new KryoConfig.TransactionStatus();
        if(!pl.getItem(amount, item)){
            ts.isSuccess = false;
            ts.error = "Недостаточно предметов для продажи";
            return ts;
        }
        if(payByBill){
            pl.corp.putMoney(price%Bill.price);
            for (int i = 0; i < price/Bill.price; i++) {
                bills.add(m.state.emitBill());
            }
        }else{
            pl.corp.putMoney(price);
        }
        ts.isSuccess = true;
        return ts;
    }
}
