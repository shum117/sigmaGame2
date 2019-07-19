/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame.model;

import com.sigma.sigmagame.KryoConfig;
import com.sigma.sigmagame.Model;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author anton
 */
public class Item {
    public String type;
    public boolean couldBeProduced;
    public HashMap<String,Integer> recipe;
    public int labour;

    public Item(String type) {
        this.type = type;
        recipe = new HashMap<>();
        couldBeProduced = false;
        switch(type){
            case "Car":
                couldBeProduced = true;
                recipe.put("Iron", 2);
                recipe.put("Copper", 1);
                labour = 1;
                break;
            case "Locomotive":
                couldBeProduced = true;
                recipe.put("Iron", 6);
                recipe.put("Copper", 1);
                labour = 2;
                break;
            case "Plane":
                couldBeProduced = true;
                recipe.put("Iron", 2);
                recipe.put("Copper", 1);
                recipe.put("Wood", 3);
                labour = 2;
                break;
            case "Rifles":
                couldBeProduced = true;
                recipe.put("Iron", 1);
                recipe.put("Copper", 0);
                recipe.put("Wood", 1);
                labour = 1;
                break;
            case "Ship":
                couldBeProduced = true;
                recipe.put("Iron", 4);
                recipe.put("Copper", 4);
                recipe.put("Wood", 2);
                labour = 3;
                break;
            case "Tank":
                couldBeProduced = true;
                recipe.put("Iron", 4);
                recipe.put("Copper", 2);
                labour = 2;
                break;
        }
    }
    
    
}
