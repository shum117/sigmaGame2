/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anton
 */
public class Corporation {
    public ArrayList<Player> members;
    public String name;
    public int account;
    public int labour;
    public int labourRegen = 100;
    public Model m;

    public Corporation(String name, Model m) {
        this.m = m;
        this.members = new ArrayList<>();
        this.name = name;
        this.account = 9000;
        this.labour = 100;
    }
    
    public void cycle(){
        labour = labourRegen;
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
