/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.Model;
import com.sigma.sigmagame.MyWebSocketHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author anton
 */
public class Scenario {
    private Model m;
    private Random rnd;

    public Scenario(Model m) {
        this.m = m;
        rnd = new Random();
    }
    
    public void cycle() {
        MyWebSocketHandler.NewsDto news = new MyWebSocketHandler.NewsDto();
        news.current_period = m.t;
        news.news = new ArrayList<>();
        MyWebSocketHandler.News msg1 = new MyWebSocketHandler.News();
        msg1.additional_params = new HashMap<>();
        MyWebSocketHandler.News msg2 = new MyWebSocketHandler.News();
        msg2.additional_params = new HashMap<>();
        
        MyWebSocketHandler.News law = new MyWebSocketHandler.News();
        law.additional_params = new HashMap<>();
        
        switch (m.t) {
        case 0:
            law.id = 25;
            law.additional_params.put("law_name", "Постройка нового жилищного комплекска");
            news.news.add(law);
            break;
        case 1:
            if(m.lobby.vote()){
                m.markets.get("Машина Делориан").P0 += 30;
                law.id = 26;
                law.additional_params.put("law_name", "Постройка нового жилищного комплекска");
                news.news.add(law);
            }else{
                m.markets.get("Машина Делориан").P0 -= 5;
                law.id = 27;
                law.additional_params.put("law_name", "Постройка нового жилищного комплекска");
                news.news.add(law);
            }
            m.markets.get("Уран").P0 = 128;
            m.markets.get("Уран").slope = 1.8;
            msg1.id = 1;
            news.news.add(msg1);
            break;
        case 2:
            m.markets.get("Углепластик").P0 = 9.7;
            m.markets.get("Углепластик").slope = 0.12;
            msg1.id = 2;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen = 120;
            }
            msg2.id = 3;
            news.news.add(msg2);
            break;
        case 3:
            law.id = 25;
            law.additional_params.put("law_name", "Увеличить инвестиции в угольную промышленность? ");
            news.news.add(law);
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().slope *= 0.99;
                    entry.getValue().P0 *= 0.99;
                }
            }
            msg1.id = 4;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen = 120;
            }
            msg2.id = 5;
            news.news.add(msg2);
            break;
        case 4:
            if(m.lobby.vote()){
                m.markets.get("Углепластик").P0 +=1;
                law.id = 26;
                law.additional_params.put("law_name", "Увеличить инвестиции в угольную промышленность? ");
                news.news.add(law);
            }else{
                law.id = 27;
                law.additional_params.put("law_name", "Увеличить инвестиции в угольную промышленность? ");
                news.news.add(law);
            }
            m.markets.get("Тостер").P0 += 50;
            msg1.id = 6;
            news.news.add(msg1);
            break;
        case 5:
            law.id = 25;
            law.additional_params.put("law_name", "Увеличение/уменьшение экспорта кораблей ");
            news.news.add(law);
            new StateOrder("Ядерное оружие", 3, true, 9000, m);
            msg1.id = 7;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen += 20;
            }
            msg2.id = 8;
            news.news.add(msg2);
            break;
        case 6:
            if(m.lobby.vote()){
                m.markets.get("Межгалактический корабль").P0 += 5;
                law.id = 26;
                law.additional_params.put("law_name", "Увеличение/уменьшение экспорта кораблей ");
                news.news.add(law);
            }else{
                m.markets.get("Межгалактический корабль").P0 -= 5;
                law.id = 27;
                law.additional_params.put("law_name", "Увеличение/уменьшение экспорта кораблей ");
                news.news.add(law);
            }
            msg1.id = 9;
            news.news.add(msg1);
            
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 += 100;
                }
            }
            msg2.id = 10;
            news.news.add(msg2);
            break;
        case 7:
            law.id = 25;
            law.additional_params.put("law_name", "Увеличение трудового дня до 10 часов");
            news.news.add(law);
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen -= 5;
            }
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 += 50;
                }
            }
            msg2.id = 11;
            news.news.add(msg2);
            break;
        case 8:
            if(m.lobby.vote()){
                for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                    entry.getValue().labourRegen += 10;
                }
                law.id = 26;
                law.additional_params.put("law_name", "Увеличение трудового дня до 10 часов");
                news.news.add(law);
            }else{
                law.id = 27;
                law.additional_params.put("law_name", "Увеличение трудового дня до 10 часов");
                news.news.add(law);
            }
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen -= 10;
            }
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(!m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().slope *= 0.99;
                    entry.getValue().P0 *= 0.99;
                }
            }
            msg1.id = 12;
            news.news.add(msg1);
            break;
        case 9:
            law.id = 25;
            law.additional_params.put("law_name", "Посадить ли коррупционера Овального на 5 лет?");
            news.news.add(law);
            new StateOrder("Межгалактический корабль", 10, true, 20000, m);
            msg1.id = 13;
            news.news.add(msg1);
            break;
        case 10:
            if(m.lobby.vote()){
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    if(m.items.get(entry.getValue().what).couldBeProduced){
                        entry.getValue().slope *= 1.01;
                        entry.getValue().P0 *= 1.01;
                    }
                }
                law.id = 26;
                law.additional_params.put("law_name", "Посадить ли коррупционера Овального на 5 лет?");
                news.news.add(law);
            }else{
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    if(m.items.get(entry.getValue().what).couldBeProduced){
                        entry.getValue().slope *= 0.99;
                        entry.getValue().P0 *= 0.99;
                    }
                }
                law.id = 27;
                law.additional_params.put("law_name", "Посадить ли коррупционера Овального на 5 лет?");
                news.news.add(law);
            }
            m.markets.get("Машина Делориан").P0 += 200;
            msg1.id = 14;
            news.news.add(msg1);
            
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(!m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 -= 10;
                }
            }
            msg2.id = 15;
            news.news.add(msg2);
            break;
        case 11:
            law.id = 25;
            law.additional_params.put("law_name", "Отмена статьи 228");
            news.news.add(law);
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(!m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 += 20;
                }
            }
            msg1.id = 16;
            news.news.add(msg1);
            
            m.markets.get("Пушка Гаусса").P0 += 200;
            msg2.id = 17;
            news.news.add(msg2);
            break;
        case 12:
            if(m.lobby.vote()){
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    if(m.items.get(entry.getValue().what).couldBeProduced){
                        entry.getValue().P0 -= 5;
                    }
                }
                law.id = 26;
                law.additional_params.put("law_name", "Отмена статьи 228");
                news.news.add(law);
            }else{
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    if(m.items.get(entry.getValue().what).couldBeProduced){
                        entry.getValue().P0 += 5;
                    }
                }
                law.id = 27;
                law.additional_params.put("law_name", "Отмена статьи 228");
                news.news.add(law);
            }
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(!m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 -= 5;
                }
            }
            msg1.id = 18;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen -= 10;
            }
            msg2.id = 19;
            news.news.add(msg2);
            break;
        case 13:
            law.id = 25;
            law.additional_params.put("law_name", "Разрешние на ношение пневматического оружия с собой ");
            news.news.add(law);
            m.markets.get("Углепластик").P0 -= 10;
            msg1.id = 20;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen -= 10;
            }
            msg2.id = 21;
            news.news.add(msg2);
            break;
        case 14:
            if(m.lobby.vote()){
                m.markets.get("Пушка Гаусса").P0 += 30;
                law.id = 26;
                law.additional_params.put("law_name", "Разрешние на ношение пневматического оружия с собой ");
                news.news.add(law);
            }else{
                m.markets.get("Пушка Гаусса").P0 -= 10;
                law.id = 27;
                law.additional_params.put("law_name", "Разрешние на ношение пневматического оружия с собой ");
                news.news.add(law);
            }
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 -= 10;
                }
            }
            msg1.id = 22;
            news.news.add(msg1);
            
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen += 20;
            }
            msg2.id = 23;
            news.news.add(msg2);
            break;
        case 15:
            for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                if(!m.items.get(entry.getValue().what).couldBeProduced){
                    entry.getValue().P0 -= 20;
                }
            }
            for (Map.Entry<String, Corporation> entry : m.corporationByName.entrySet()) {
                entry.getValue().labourRegen += 20;
            }
            msg2.id = 24;
            news.news.add(msg2);
            break;
        default:
            break;
        }
        
        ArrayList<String> prod = new ArrayList<>();
        for (Map.Entry<String, Item> entry : m.items.entrySet()) {
            if(entry.getValue().couldBeProduced){
                prod.add(entry.getKey());
            }
        }
        String item = prod.get(rnd.nextInt(prod.size()));
        int q = (int)Math.round(m.markets.get(item).history.get(3)/5);
        int price = (int)Math.round(m.markets.get(item).price*1.4);
        if(q > 0) 
            new StateOrder(item, q, m.t > 2, price, m);
        MyWebSocketHandler.broadcast(news);
        
    }
}
