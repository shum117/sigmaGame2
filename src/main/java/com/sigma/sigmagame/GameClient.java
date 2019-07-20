/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import static com.sigma.sigmagame.KryoConfig.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.util.List;
import java.util.Random;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.swing.SwingUtilities;

/**
 *
 * @author anton
 */
public class GameClient extends Listener{
    public String ip;
    public Client cli;
    public LobbyWindow lw;
    
    public ResourceListDto resList = null;
    
    public GameClient(String ip) throws IOException {
        this.ip = ip;
        cli = new Client();
        KryoConfig.register(cli);
        cli.start();
        cli.connect(5000, ip, 54555, 54777);
        cli.addListener(this);
    }
    
    
    
    public static Identifier getId(BufferedReader stdin) throws IOException{
        System.out.print("player: ");
        System.out.flush();
        Identifier id = new Identifier();
        String resp = stdin.readLine();
        if(resp.isEmpty()){
            id.byRFID = true;
            try {
                TerminalFactory factory = TerminalFactory.getDefault();
                List<CardTerminal> terminals = factory.terminals().list();
                System.out.println("Terminals: " + terminals);
                // get the first terminal
                CardTerminal terminal = terminals.get(0);
                // establish a connection with the card
                Card card = terminal.connect("T=1");
                System.out.println("card: " + card);
                CardChannel channel = card.getBasicChannel();
                byte[] arr = {(byte)0xFF,(byte)0xCA,(byte)0x00,(byte)0x00,(byte)0x00};
                ResponseAPDU r = channel.transmit(new CommandAPDU(arr));
                StringBuilder sb = new StringBuilder(r.getBytes().length * 2);
                for(byte b: r.getBytes())
                    sb.append(String.format("%02x", b));
                System.out.println("response: " + sb.toString());
                id.rfid = sb.toString();
                // disconnect
                card.disconnect(false);
            } catch (CardException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex){
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("try another method");
                Process p = Runtime.getRuntime().exec("./nfc_read");
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                id.rfid = br.readLine();
                System.out.println(id.rfid);
                if(id.rfid.isEmpty()){
                    System.out.println("Cannot use nfc, enter plain num");
                    System.out.print("player: ");
                    System.out.flush();
                    id.byRFID = false;
                    id.plain = Integer.parseInt(resp);
                }
            }
        }else{
            id.byRFID = false;
            id.plain = Integer.parseInt(resp);
        }
        return id;
    }
    
    private static BufferedReader stdin;
    
    public static void main(String[] args) throws IOException{
        stdin = new BufferedReader(new InputStreamReader(System.in));
        String s;
        
        System.out.print("ip: ");
        System.out.flush();
        String ip = KryoConfig.ADDRESS;
        
        GameClient gc = new GameClient(ip);
        System.out.print("command : ");
        System.out.flush();
        while((s = stdin.readLine()) != null){
            //String[] ss = s.split(" ");
            if(s.equalsIgnoreCase("exit")){
                return;
            }else if(s.equalsIgnoreCase("list")){
                gc.resList();
                System.out.println("list size:" + gc.resList.resources.size());
                for (ResourceData resource : gc.resList.resources) {
                    System.out.println(resource.name + " " + resource.amount);
                }
            }else if(s.equalsIgnoreCase("lobby")){
                SwingUtilities.invokeLater(gc.lw = new LobbyWindow(gc));
            }else if(s.equalsIgnoreCase("cycle")){
                gc.cli.sendTCP(new StartNewCycle());
            }else{
                System.out.println("Unknown command");
            }
            System.out.print("command : ");
            System.out.flush();
        }
    }
    
    @Override
    public void received(Connection cnctn, Object o) {
        if(!(o instanceof FrameworkMessage.KeepAlive))System.out.println(o);
        if(o instanceof ResourceListDto){
            synchronized(this){
                resList = (ResourceListDto) o;
                this.notifyAll();
            }
        }
        else if(o instanceof SenatorsListDto){
            synchronized(this){
                if(lw != null){
                    lw.update((SenatorsListDto) o);
                }
                this.notifyAll();
            }
        }
        else if(o instanceof TransactionStatus){
            synchronized(this){
                if(((TransactionStatus) o).isSuccess){
                    System.out.println("Success");
                }else{
                    System.out.println("Error ("+((TransactionStatus) o).error+")");
                }
                this.notifyAll();
            }
        }
        
    }

    private void resList() {
        synchronized(this){
            cli.sendTCP(new RequestResourceListDto());
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void lobbyList() {
        synchronized(this){
            cli.sendTCP(new RequestSenatorsListDto());
            try {
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void buySenator(int id) {
        BuySenatorDto msg = new BuySenatorDto();
        try {
            msg.player = getId(stdin);
            msg.senator = id;
            synchronized(this){
                cli.sendTCP(msg);
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void askToVote(boolean vote) {
        AskSenatorsToVoteDto msg = new AskSenatorsToVoteDto();
        try {
            msg.player = getId(stdin);
            msg.vote = vote;
            synchronized(this){
                cli.sendTCP(msg);
            }
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
