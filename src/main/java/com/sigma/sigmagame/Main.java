package com.sigma.sigmagame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        //if(args.length > 0 && args[0].equals("-server")){
            Model m = new Model();
            GameServer gs = new GameServer(m,"log.json");
        /*}else{
            GameClient.main(args);
        }*/
        //m.unload("_start");
    }
    
}
