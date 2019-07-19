/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.model.Player;
import com.sigma.sigmagame.model.Corporation;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.sigma.sigmagame.*;
import java.util.LinkedList;
import java.util.Queue;
/**
 *
 * @author anton
 */
public class Market {
    public String what;
    public int price = 10;
    public double Q = 200;
    public double slope = -10;
    
    public double Q_cycle = 0;
    public LinkedList<Double> history;
    public State state;
   
    public Market(String what, State state, double Q0, double slope, int price) {
        this.Q = Q0;
        this.slope = slope;
        this.price = price;
        this.state = state;
        this.what = what;
        history = new LinkedList<Double>();
        history.add(slope*price+Q);
        history.add(slope*price+Q);
        history.add(slope*price+Q);
        history.add(slope*price+Q);
        history.add(slope*price+Q);
    }
    
    public KryoConfig.TransactionStatus buy(int q, Player buyer){
        KryoConfig.TransactionStatus ts = new KryoConfig.TransactionStatus();
        int c = q*price;
        if(!buyer.corp.getMoney(c)){
            ts.isSuccess = false;
            ts.error = "Недостаточно средств для покупки";
            return ts;
        }
        Q_cycle += q;
        buyer.putItem(q, what);
        state.putMoney(c);
        ts.isSuccess = true;
        return ts;
    }
    
    public KryoConfig.TransactionStatus sell(int q, Player seller){
        KryoConfig.TransactionStatus ts = new KryoConfig.TransactionStatus();
        int c = q*price;
        if(!seller.getItem(q, what)){
            ts.isSuccess = false;
            ts.error = "Недостаточно предметов для продажи";
            return ts;
        }
        if(!state.getMoney(c)){
            seller.putItem(q, what);
            ts.isSuccess = false;
            ts.error = "Нет желающих купить товар";
            return ts;
        }
        Q_cycle += q;
        seller.corp.putMoney(c);
        ts.isSuccess = true;
        return ts;
    }
    
    public void cycle(){
        history.remove();
        history.add(Q_cycle);
        double sum = 0;
        for (Double a : history) {
            sum += a;
        }
        sum /= history.size();
        price = (int)Math.round((sum-Q)/slope);
    }
}
