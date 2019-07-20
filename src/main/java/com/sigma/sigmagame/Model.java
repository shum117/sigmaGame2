/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame;

import com.sigma.sigmagame.model.Market;
import com.sigma.sigmagame.model.Player;
import com.sigma.sigmagame.model.Corporation;

import java.util.Date;
import java.util.Map;

import static com.sigma.sigmagame.KryoConfig.*;

import com.sigma.sigmagame.model.Item;
import com.sigma.sigmagame.model.Lobby;
import com.sigma.sigmagame.model.Scenario;
import com.sigma.sigmagame.model.State;
import com.sigma.sigmagame.model.StateOrder;

import java.util.HashMap;

/**
 * @author anton
 */


public class Model {
    public static final String[] CORPORATIONS = {
            "ГазХром", "СмотриМасло", "ТиньВыкл", "Теле1.5", "Шестёрочка", "Гошан",
            "Мындекс", "СберТанк", "СпортГрандмастер", "Взрослый мир"
    };
    public static final String[] ITEMS = {
            "Углепластик", "Уран", "Квантовый процессор",
            "Аккумулятор", "Электромотор", "Тостер", "Машина Делориан", "Межгалактический корабль",
            "Ядерное оружие", "Пушка Гаусса"
    };
    public HashMap<String, Player> playerByRFID;
    public HashMap<Integer, Player> playerByPlain;
    public HashMap<String, Corporation> corporationByName;
    public HashMap<String, Market> markets;
    public HashMap<String, Item> items;
    public HashMap<Integer, StateOrder> orders;
    public State state;
    public Lobby lobby;
    public Scenario sc;
    public int t = 0;


    public Model() {
        playerByPlain = new HashMap<>();
        playerByRFID = new HashMap<>();
        items = new HashMap<>();
        for (int i = 0; i < ITEMS.length; i++) {
            items.put(ITEMS[i], new Item(ITEMS[i]));
        }
        state = new State(this);
        lobby = new Lobby();
        markets = new HashMap<>();
        orders = new HashMap<>();
        for (int i = 0; i < ITEMS.length; i++) {
            switch(ITEMS[i]){
                case "Углепластик":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,107,0.12,359));
                    break;
                case "Уран":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,138,1.8,90));
                    break;
                case "Квантовый процессор":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,88,1.5,75));
                    break;
                case "Аккумулятор":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,20,0.15,187));
                    break;
                case "Электромотор":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,58,1,42));
                    break;
                case "Тостер":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,466,-1,67));
                    break;
                case "Машина Делориан":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,1042,-5,28));
                    break;
                case "Межгалактический корабль":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,1970,-20,13));
                    break;
                case "Ядерное оружие":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,3000,-40,10));
                    break;
                case "Пушка Гаусса":
                    markets.put(ITEMS[i], new Market(ITEMS[i],state,770,-3,40));
                    break;
                default:
                    System.out.println("unknown market");
                    System.out.println("\""+ITEMS[i]+"\"");
                    System.exit(1);
            }
                
            
        }
        corporationByName = new HashMap<>();
        for (String name : CORPORATIONS) {
            corporationByName.put(name, new Corporation(name, this));
        }
        sc = new Scenario(this);
    }

    public Player registerPlayer(String name, String rfid, int plain, String corp) {
        Player p = new Player((rfid.length() < 8 ? rfid.toUpperCase() : rfid.substring(0, 8).toUpperCase()), name, plain, corporationByName.get(corp), this);
        playerByPlain.put(plain, p);
        playerByRFID.put((rfid.length() < 8 ? rfid.toUpperCase() : rfid.substring(0, 8).toUpperCase()), p);
        corporationByName.get(corp).members.add(p);
        return p;
    }

    public void cycle() {
        t += 1;
        sc.cycle();
        for (Map.Entry<String, Corporation> corp : corporationByName.entrySet()) {
            corp.getValue().cycle();
        }
        for (Map.Entry<String, Market> market : markets.entrySet()) {
            market.getValue().cycle();
        }
        String suffix = "_period_" + t + " " + new Date().toString();
    }


    public Player getPlayer(Identifier id) {
        if (id.byRFID && id.rfid != null) {
            //System.out.println("getPlayer "+(id.rfid.length() < 8 ? id.rfid.toUpperCase() : id.rfid.substring(0,8).toUpperCase()));
            String s = (id.rfid.length() < 8 ? id.rfid.toUpperCase() : id.rfid.substring(0, 8).toUpperCase());
            return playerByRFID.get(s);
        } else {
            return playerByPlain.get(id.plain);
        }
    }

    public HashMap<String, Integer> getScores(boolean silent) {
        HashMap<String, Integer> ret = new HashMap<>();
        if (!silent) System.out.println("Scores for period " + t);
        for (Map.Entry<String, Corporation> entry : corporationByName.entrySet()) {
            String key = entry.getKey();
            Corporation val = entry.getValue();
            int score = 0;
            score += val.account;
            for (Player member : val.members) {
                for (Map.Entry<String, Integer> entry1 : member.items.entrySet()) {
                    String resource = entry1.getKey();
                    Integer amount = entry1.getValue();
                    score += markets.get(resource).price * amount;
                }
            }
            if (!silent) System.out.println(val.name + ": " + score);
            ret.put(val.name, score);
        }
        return ret;
    }

}
