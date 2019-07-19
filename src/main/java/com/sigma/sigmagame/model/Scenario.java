/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.Model;
import com.sigma.sigmagame.MyWebSocketHandler;
import java.util.ArrayList;

/**
 *
 * @author anton
 */
public class Scenario {
    private Model m;

    public Scenario(Model m) {
        this.m = m;
        Market market = m.markets.get("Углепластик");
        market.Q = 359,047619;
    }
    
    public void cycle() {
        MyWebSocketHandler.NewsDto news = new MyWebSocketHandler.NewsDto();
        news.period = m.t;
        news.news = new ArrayList<>();
        switch (m.t) {
        case 0:
            
            
            break;
        case 1:
            break;
        case 2:
            break;
        case 3:
            break;
        case 4:
            break;
        case 5:
            break;
        case 6:
            break;
        case 7:
            break;
        case 8:
            break;
        case 9:
            break;
        case 10:
            break;
        case 11:
            break;
        case 12:
            break;
        case 13:
            break;
        case 14:
            break;
        case 15:
            break;
        default:
            break;
        }
        MyWebSocketHandler.broadcast(news);
        
    }
}
