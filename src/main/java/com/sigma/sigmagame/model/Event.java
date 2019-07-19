/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.Model;

/**
 *
 * @author anton
 */
public class Event {
    Model m;

    public Event(Model m) {
        this.m = m;
    }
    
    public boolean doEvent(int t){
        return true;
    }
}
