/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.KryoConfig;
import java.util.Random;

/**
 *
 * @author anton
 */
public class Lobby {
    public Senator senators[];
    public static final int LEVEL_PRICE = 100;
    public static final int NSENATORS = 100;
    private Random rnd;
    public int lastvote;

    public static class Senator {
        public Corporation aligment = null;
        public int level = 0;
        public int vote = -1;
        public Senator() {
        }
        public KryoConfig.SenatorDto getDTO(){
            KryoConfig.SenatorDto dto = new KryoConfig.SenatorDto();
            dto.vote = vote;
            dto.level = level;
            dto.corp = aligment == null ? "" : aligment.name;
            return dto;
        }
    }

    public Lobby() {
        rnd = new Random();
        senators = new Senator[NSENATORS];
        for (int i = 0; i < senators.length; i++) {
            senators[i] = new Senator();
        }
    }
    
    public boolean buySenator(int id, Corporation corp){
        if(!corp.getMoney((senators[id].level+1)*LEVEL_PRICE))
            return false;
        senators[id].level++;
        senators[id].aligment = corp;
        return true;
    }
    
    public void askSenatorsToVote(Corporation corp, boolean accept){
        for (int i = 0; i < senators.length; i++) {
            Lobby.Senator senator = senators[i];
            if(senator.aligment == corp){
                senator.vote = accept ? 1 : 0;
            }
        }
    }
    
    public boolean vote(){
        lastvote = 0;
        boolean ret = false;
        for (int i = 0; i < senators.length; i++) {
            Lobby.Senator senator = senators[i];
            if(senator.vote == 1){
                lastvote++;
            }else if(senator.vote == -1){
                if(rnd.nextBoolean()){
                    lastvote++;
                }
            }
        }
        if(lastvote > senators.length/2){
            ret = true;
        }
        for (int i = 0; i < senators.length; i++) {
            senators[i].vote = -1;
        }
        return ret;
    }
}
