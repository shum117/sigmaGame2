package com.sigma.sigmagame;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

/**
 * Created by profy on 13.07.2018.
 */

public class KryoConfig {

    static final int SERVER_PORT = 54555;
    static final int SERVER_PORT_UDP = 54777;
    static final String ADDRESS = "192.168.1.2";//"192.168.43.232";


    static void register(EndPoint point) {
        Kryo kryo = point.getKryo();
        kryo.register(String.class);
        kryo.register(int[].class);
        kryo.register(double[].class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Identifier.class);
        kryo.register(Entity.class);
        kryo.register(ResourceData.class);
        kryo.register(MoneyData.class);
        kryo.register(ProductData.class);
        kryo.register(RequestResourceListDto.class);
        kryo.register(ResourceListDto.class);
        kryo.register(ResourceBuyDto.class);
        kryo.register(RequestProductListDto.class);
        kryo.register(ProductListDto.class);
        kryo.register(ProductSellDto.class);
        kryo.register(RequestPlayerInformation.class);
        kryo.register(PlayerInformation.class);
        kryo.register(ProductTransferDto.class);
        kryo.register(ResourceTransferDto.class);
        kryo.register(MoneyTransferDto.class);
        kryo.register(TransactionStatus.class);
        kryo.register(RequestSenatorsListDto.class);
        kryo.register(SenatorDto.class);
        kryo.register(SenatorsListDto.class);
        kryo.register(AskSenatorsToVoteDto.class);
        kryo.register(BuySenatorDto.class);
        kryo.register(RequestProductionListDto.class);
        kryo.register(VexelCashingDto.class);
        kryo.register(RequestGameCycle.class);
        kryo.register(GameCycleDto.class);
        kryo.register(BankTransaction.class);
        kryo.register(StartNewCycle.class);
        kryo.register(RequestStateOrderListDto.class);
        kryo.register(StateOrderDto.class);
        kryo.register(StateOrderListDto.class);
        kryo.register(ResolveStateOrder.class);
        kryo.register(CompanyData.class);
        kryo.register(RequestCompanyDataListDto.class);
        kryo.register(CompanyDataListDto.class);
        kryo.register(AddPlayer.class);
        kryo.register(VexelListDto.class);
    }

    public static class Entity implements Serializable {
        public int amount;
        public String name;

        public Entity(int amount, String name) {
            this.amount = amount;
            this.name = name;
        }

        public Entity() {
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ResourceData extends Entity implements Serializable {

        public ResourceData(int cost, String name) {
            super(cost, name);
        }

        public ResourceData() {
        }


    }

    public static class MoneyData extends Entity implements Serializable {

        public MoneyData(int amount, String name) {
            super(amount, name);
        }

        public MoneyData() {
            super();
        }


    }

    public static class Identifier implements Serializable {
        public boolean byRFID;
        public String rfid = "";
        public int plain;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identifier that = (Identifier) o;
            return byRFID == that.byRFID &&
                    plain == that.plain &&
                    Objects.equals(rfid, that.rfid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(byRFID, rfid, plain);
        }

        @Override
        public String toString() {
            if (byRFID) {
                return rfid;
            } else {
                return "" + plain;
            }
        }
    }


    public static class RequestResourceListDto implements Serializable {

    }

    public static class ResourceListDto implements Serializable {
        public List<ResourceData> resources;
    }

    public static class ResourceBuyDto implements Serializable {
        public Identifier id;
        public int amount;
        public ResourceData resource;
    }

    public static class ProductData extends Entity implements Serializable {

        public ProductData(int cost, String name) {
            super(cost, name);
        }

        public ProductData() {
        }
    }

    public static class RequestProductListDto implements Serializable {

    }

    public static class ProductListDto implements Serializable {
        public List<ProductData> products;
    }

    public static class ProductSellDto implements Serializable {
        public Identifier id;
        public int amount;
        public ProductData product;
    }

    public static class RequestPlayerInformation implements Serializable {
        public Identifier id;
    }

    public static class PlayerInformation implements Serializable {
        public String name;
        public int money;
        public int power;
        public List<ProductData> products;
        public List<ResourceData> resources;
    }

    public static class ProductTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ProductData product;
        public int amount;
    }

    public static class ResourceTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public ResourceData resource;
        public int amount;
    }

    public static class MoneyTransferDto implements Serializable {
        public Identifier firstPlayer;
        public Identifier secondPlayer;
        public int amount;
    }

    public static class TransactionStatus implements Serializable {
        public boolean isSuccess;
        public String error;
    }

    public static class RequestSenatorsListDto implements Serializable{

    }

    public static class SenatorDto implements Serializable{
        public String corp;
        public int level = 0;
        public int vote = -1;
    }

    public static class SenatorsListDto implements Serializable{
        ArrayList<SenatorDto> senators;
    }

    public static class BuySenatorDto implements Serializable{
        public Identifier player;
        public int senator;
    }

    public static class AskSenatorsToVoteDto implements Serializable{
        public Identifier player;
        public boolean vote;
    }

    public static class ProductionDto implements Serializable {
        public Identifier id;
        public ProductData product;
        public int amount;
    }

    public static class RequestProductionListDto implements Serializable {

    }

    public static class VexelCashingDto implements Serializable {
        public Identifier id;
        public int vexelId;
    }

    public static class RequestGameCycle implements Serializable {

    }

    public static class GameCycleDto implements Serializable {
        public int cycle;
    }

    public static class BankTransaction implements Serializable {
        public Identifier id;
        public int amount;
    }
    
    public static class StartNewCycle implements Serializable {

    }
    
    public static class RequestStateOrderListDto implements Serializable {

    }

    public static class StateOrderDto implements Serializable {
        public int id;
        public int moneyAmount;
        public ProductData productData;
        public boolean payByVexel;

        public StateOrderDto(int id, int moneyAmount, ProductData productData, boolean payByVexel) {
            this.id = id;
            this.moneyAmount = moneyAmount;
            this.productData = productData;
            this.payByVexel = payByVexel;
        }

        public StateOrderDto() {
        }
    }

    public static class StateOrderListDto implements Serializable {
        public List<StateOrderDto> stateOrderList;
    }

    public static class ResolveStateOrder implements Serializable {
        public Identifier id;
        public int orderId;
    }
    
    public static class CompanyData extends Entity implements Serializable {

        public CompanyData(int amount, String name) {
            super(amount, name);
        }

        public CompanyData() {
        }
    }

    public static class RequestCompanyDataListDto implements Serializable {

    }

    public static class CompanyDataListDto implements Serializable {
        public List<CompanyData> companyDataList;
    }

    public static class AddPlayer implements Serializable {
        public Identifier identifier;
        public CompanyData company;
    }
    
    public static class VexelListDto implements Serializable {
        public List<Integer> vexelIdList;
    }
}