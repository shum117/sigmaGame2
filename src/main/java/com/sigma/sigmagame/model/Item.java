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
 * @author anton
 */
public class Item {
    public String type;
    public boolean couldBeProduced;
    public HashMap<String, Integer> recipe;
    public int labour;

    public Item(String type) {
        this.type = type;
        recipe = new HashMap<>();
        couldBeProduced = false;
        switch (type) {
            case "Межгалактический корабль":
                couldBeProduced = true;
                recipe.put("Углепластик", 6);
                recipe.put("Квантовый процессор", 2);
                recipe.put("Электромотор", 1);
                labour = 15;
                break;
            case "Машина Делориан":
                couldBeProduced = true;
                recipe.put("Углепластик", 3);
                recipe.put("Квантовый процессор", 1);
                recipe.put("Электромотор", 1);
                labour = 7;
                break;
            case "Пушка Гаусса":
                couldBeProduced = true;
                recipe.put("Углепластик", 1);
                recipe.put("Аккумулятор", 3);
                recipe.put("Уран", 1);
                labour = 5;
                break;
            case "Тостер":
                couldBeProduced = true;
                recipe.put("Углепластик", 2);
                recipe.put("Аккумулятор", 1);
                labour = 3;
                break;
            case "Ядерное оружие":
                couldBeProduced = true;
                recipe.put("Углепластик", 2);
                recipe.put("Уран", 5);
                recipe.put("Квантовый процессор", 2);
                labour = 20;
                break;
        }
    }


}
