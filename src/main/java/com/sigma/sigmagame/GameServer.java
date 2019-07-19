/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.sigmagame;

import com.sigma.sigmagame.model.Market;
import com.sigma.sigmagame.model.Player;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Listener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.sigma.sigmagame.KryoConfig.*;
import com.sigma.sigmagame.model.Item;
import com.sigma.sigmagame.model.Lobby;
import com.sigma.sigmagame.model.StateOrder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
/**
 *
 * @author anton
 */

public class GameServer extends Listener{
    
    
    
    public Server kryoserver;
    public Model m;
    public ArrayList<Connection> broadcast;
    private File backup;
    private PrintWriter pw;
    private Gson gson;
    
    public GameServer(Model m, String filename){
        try {
            gson = new GsonBuilder().setLenient().create();
            broadcast = new ArrayList<>();
            this.m = m;
            backup = new File(filename);
            if(backup.exists()){
                readBackup();
                try {
                    pw = new PrintWriter(new FileWriter(backup, true));
                } catch (IOException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }else{
                try {
                    pw = new PrintWriter(backup);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
            }
            kryoserver = new Server();
            kryoserver.addListener(this);
            KryoConfig.register(kryoserver);
            try {
                kryoserver.bind(54555, 54777);
            } catch (IOException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(1);
            }
            kryoserver.start();
            org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
            WebSocketHandler wsHandler = new WebSocketHandler() {
                
                
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(MyWebSocketHandler.class);
                }
            };
            server.setHandler(wsHandler);
            server.start();
        } catch (Exception ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    private List<ProductData> getProducts(HashMap<String,Integer> items){
        ArrayList<ProductData> ret = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            if(m.items.get(key).couldBeProduced)
            ret.add(new ProductData(val, key));
        }
        return ret;
    }
    
    private List<ResourceData> getResourses(HashMap<String,Integer> items){
        ArrayList<ResourceData> ret = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String key = entry.getKey();
            Integer val = entry.getValue();
            if(!m.items.get(key).couldBeProduced)
            ret.add(new ResourceData(val, key));
        }
        return ret;
    }
    
    @Override
    public void received(Connection cnctn, Object o) {
        try{
            if(!(o instanceof FrameworkMessage.KeepAlive)){
                System.out.println(o);
                if(cnctn != null){
                    pw.println(o.getClass().getTypeName());
                    pw.println(gson.toJson(o));
                    pw.flush();
                }
            }
            if(o instanceof RequestResourceListDto){
                ResourceListDto ret = new ResourceListDto();
                ret.resources = new ArrayList<>();
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    String type = entry.getKey();
                    Market market = entry.getValue();
                    if(!m.items.get(type).couldBeProduced){
                        ResourceData rd = new ResourceData(market.price, type);
                        ret.resources.add(rd);
                    }
                }
                System.out.println(ret);
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof RequestProductListDto){
                ProductListDto ret = new ProductListDto();
                ret.products = new ArrayList<>();
                for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                    String type = entry.getKey();
                    Market market = entry.getValue();
                    if(m.items.get(type).couldBeProduced){
                        ProductData rd = new ProductData(market.price, type);
                        ret.products.add(rd);
                    }
                }
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof ResourceBuyDto){
                ResourceBuyDto msg = (ResourceBuyDto)o;
                
                if(m.getPlayer(msg.id) == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = m.markets.get(msg.resource.name).buy(msg.amount, m.getPlayer(msg.id));
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof ProductSellDto){
                ProductSellDto msg = (ProductSellDto)o;
                if(m.getPlayer(msg.id) == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = m.markets.get(msg.product.name).sell(msg.amount, m.getPlayer(msg.id));
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof RequestPlayerInformation){
                Identifier id = ((RequestPlayerInformation)o).id;
                PlayerInformation ret = new PlayerInformation();
                Player pl = m.getPlayer(id);
                if(pl == null){
                    System.out.println("Player not found: "+id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                ret.money = pl.corp.account;
                ret.name = pl.corp.name;
                ret.power = pl.corp.labour;
                ret.products = getProducts(pl.items);
                ret.resources = getResourses(pl.items);
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof ProductTransferDto){
                ProductTransferDto msg = (ProductTransferDto)o;
                Player pl1 = m.getPlayer(msg.firstPlayer);
                if(pl1 == null){
                    System.out.println("Player1 not found: "+msg.firstPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок1 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                Player pl2 = m.getPlayer(msg.secondPlayer);
                if(pl2 == null){
                    System.out.println("Player2 not found: "+msg.secondPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок2 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = pl1.getItem(msg.amount, msg.product.name);
                if(ts.isSuccess){
                    pl2.putItem(msg.amount, msg.product.name);
                }else{
                    ts.error = "Нет указанного количества предметов";
                }
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof ResourceTransferDto){
                ResourceTransferDto msg = (ResourceTransferDto)o;
                Player pl1 = m.getPlayer(msg.firstPlayer);
                if(pl1 == null){
                    System.out.println("Player1 not found: "+msg.firstPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок1 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                Player pl2 = m.getPlayer(msg.secondPlayer);
                if(pl2 == null){
                    System.out.println("Player2 not found: "+msg.secondPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок2 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = pl1.getItem(msg.amount, msg.resource.name);
                if(ts.isSuccess){
                    pl2.putItem(msg.amount, msg.resource.name);
                }else{
                    ts.error = "Нет указанного количества предметов";
                }
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof MoneyTransferDto){
                MoneyTransferDto msg = (MoneyTransferDto)o;
                Player pl1 = m.getPlayer(msg.firstPlayer);
                if(pl1 == null){
                    System.out.println("Player1 not found: "+msg.firstPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок1 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                Player pl2 = m.getPlayer(msg.secondPlayer);
                if(pl2 == null){
                    System.out.println("Player2 not found: "+msg.secondPlayer);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок2 не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = pl1.corp.getMoney(msg.amount);
                if(ts.isSuccess){
                    pl2.corp.putMoney(msg.amount);
                }else{
                    ts.error = "Недостаточно средств";
                }
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof RequestSenatorsListDto){
                SenatorsListDto ret = new SenatorsListDto();
                ret.senators = new ArrayList<>();
                for (int i = 0; i < m.lobby.senators.length; i++) {
                    Lobby.Senator senator = m.lobby.senators[i];
                    ret.senators.add(senator.getDTO());
                }
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof BuySenatorDto){
                BuySenatorDto msg = (BuySenatorDto)o;
                Player pl = m.getPlayer(msg.player);
                if(pl == null){
                    System.out.println("Player not found: "+msg.player);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = m.lobby.buySenator(msg.senator, pl.corp);
                if(!ts.isSuccess){
                    ts.error = "Недостаточно средств";
                }
                if(cnctn != null) cnctn.sendTCP(ts);
                if(ts.isSuccess){
                    SenatorsListDto ret = new SenatorsListDto();
                    ret.senators = new ArrayList<>();
                    for (int i = 0; i < m.lobby.senators.length; i++) {
                        Lobby.Senator senator = m.lobby.senators[i];
                        ret.senators.add(senator.getDTO());
                    }
                    for (Connection connection : broadcast) {
                        connection.sendTCP(ret);
                    }
                }
            }
            else if(o instanceof AskSenatorsToVoteDto){
                AskSenatorsToVoteDto msg = (AskSenatorsToVoteDto)o;
                Player pl = m.getPlayer(msg.player);
                if(pl == null){
                    System.out.println("Player not found: "+msg.player);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                m.lobby.askSenatorsToVote(pl.corp,msg.vote);
            }
            else if(o instanceof StartNewCycle){
                m.cycle();
                for (Connection connection : broadcast) {
                    ResourceListDto retr = new ResourceListDto();
                    retr.resources = new ArrayList<>();
                    ProductListDto retp = new ProductListDto();
                    retp.products = new ArrayList<>();
                    for (Map.Entry<String, Market> entry : m.markets.entrySet()) {
                        String key = entry.getKey();
                        Market value = entry.getValue();
                        if(!m.items.get(key).couldBeProduced){
                            ResourceData rd = new ResourceData(value.price, key);
                            retr.resources.add(rd);
                        }
                        if(m.items.get(key).couldBeProduced){
                            ProductData rd = new ProductData(value.price, key);
                            retp.products.add(rd);
                        }
                    }
                    connection.sendTCP(retr);
                    connection.sendTCP(retp);
                    GameCycleDto t = new GameCycleDto();
                    t.cycle = m.t;
                    connection.sendTCP(t);
                }
            }
            else if(o instanceof ProductionDto){
                ProductionDto msg = (ProductionDto)o;
                if(m.getPlayer(msg.id) == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = m.getPlayer(msg.id).produce(msg.amount, msg.product.name);
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof RequestProductionListDto){
                ProductListDto ret = new ProductListDto();
                ret.products = new ArrayList<>();
                for (Map.Entry<String, Item> entry : m.items.entrySet()) {
                    String type = entry.getKey();
                    Item item = entry.getValue();
                    if(m.items.get(type).couldBeProduced){
                        ProductData rd = new ProductData(item.labour, type);
                        ret.products.add(rd);
                    }
                }
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof VexelCashingDto){
                VexelCashingDto msg = (VexelCashingDto)o;
                if(m.getPlayer(msg.id) == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                TransactionStatus ts = m.state.payBill(msg.vexelId,m.getPlayer(msg.id).corp);
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof RequestGameCycle){
                GameCycleDto ret = new GameCycleDto();
                ret.cycle = m.t;
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof BankTransaction){
                BankTransaction msg = (BankTransaction)o;
                Player pl = m.getPlayer(msg.id);
                if(pl == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                if(msg.amount > 0){
                    if(!pl.corp.getMoney(msg.amount)){
                        TransactionStatus ts = new TransactionStatus();
                        ts.isSuccess = false;
                        ts.error = "Игрок не найден";
                        if(cnctn != null) cnctn.sendTCP(ts);
                        return;
                    }
                }else{
                    pl.corp.putMoney(-msg.amount);
                } 
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = true;
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            else if(o instanceof RequestStateOrderListDto){
                StateOrderListDto ret = new StateOrderListDto();
                ret.stateOrderList = new ArrayList<>();
                for (Map.Entry<Integer, StateOrder> entry : m.orders.entrySet()) {
                    Integer id = entry.getKey();
                    StateOrder so = entry.getValue();
                    ret.stateOrderList.add(so.getDTO());
                }
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof ResolveStateOrder){
                ResolveStateOrder msg = (ResolveStateOrder)o;
                Player pl = m.getPlayer(msg.id);
                if(pl == null){
                    System.out.println("Player not found: "+msg.id);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок не найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                StateOrder ord = m.orders.get(msg.orderId);
                TransactionStatus ts = ord.fulfill(pl);
                if(cnctn != null) cnctn.sendTCP(ts);
                if(ord.payByBill && ts.isSuccess){
                    VexelListDto ret = new VexelListDto();
                    ret.vexelIdList = ord.bills;
                    if(cnctn != null) cnctn.sendTCP(ret);
                }
            }
            else if(o instanceof RequestCompanyDataListDto){
                CompanyDataListDto ret = new CompanyDataListDto();
                ret.companyDataList = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : m.getScores(true).entrySet()) {
                    String key = entry.getKey();
                    int val = entry.getValue();
                    ret.companyDataList.add(new CompanyData(val, key));
                }
                if(cnctn != null) cnctn.sendTCP(ret);
            }
            else if(o instanceof AddPlayer){
                AddPlayer msg = (AddPlayer)o;
                Player pl1 = m.playerByPlain.get(msg.identifier.plain);
                Player pl2 = m.playerByRFID.get(msg.identifier.rfid);
                if(pl1 != null || pl2 != null){
                    System.out.println("Player found: "+msg.identifier);
                    TransactionStatus ts = new TransactionStatus();
                    ts.isSuccess = false;
                    ts.error = "Игрок найден";
                    if(cnctn != null) cnctn.sendTCP(ts);
                    return;
                }
                m.registerPlayer(
                        "#"+msg.identifier.plain, 
                        msg.identifier.rfid,
                        msg.identifier.plain, 
                        msg.company.name);
                TransactionStatus ts = new TransactionStatus();
                ts.isSuccess = true;
                if(cnctn != null) cnctn.sendTCP(ts);
            }
            
            
        }catch(Exception ex){
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void connected(Connection cnctn) {
        System.out.println("connected "+cnctn);
        broadcast.add(cnctn);
    }

    @Override
    public void disconnected(Connection cnctn) {
        System.out.println("disconnected "+cnctn);
        if(cnctn != null) broadcast.remove(cnctn);
    }

    public void readBackup(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(backup));
            String s;
            while((s = br.readLine()) != null){
                System.out.println(s);
                Class<?> c = Class.forName(s);
                s = br.readLine();
                System.out.println(s);
                Object o = gson.fromJson(s,c);
                received(null, o);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
