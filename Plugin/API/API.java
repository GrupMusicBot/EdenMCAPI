package com.kuebv.mcapi.mcapi.API;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.kuebv.mcapi.mcapi.MCAPI;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static org.bukkit.Bukkit.getServer;

public class API {


    public static String APIVersion(){
        String APIVersion = "1.5";
        return APIVersion;
    }

    public static String Authors(){
        String Authors = "KuebV";
        return Authors;
    }

    public static void DebugMessage(Player player, String Message){
        player.sendMessage(ChatColor.BLUE + "[Debug] " + ChatColor.GRAY + Message);
    }

    public static String getLocation (Player player){
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        String Location = (x + "," + y + "," + z);
        return Location;
    }

    public static void HandleLeave(Player player){
        CFG config = new CFG();
        // We do on leave rather than live data because less data to transfer each time
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        MongoCollection<Document> coll = database.getCollection(config.MongoCollection);

        String UUID = player.getUniqueId().toString();
        Document filter = new Document("_id", UUID);

        int Kills = player.getStatistic(Statistic.PLAYER_KILLS);
        int MobKills  = player.getStatistic(Statistic.MOB_KILLS);
        int totalKills = Kills + MobKills;
        int Deaths = player.getStatistic(Statistic.DEATHS);
        int play = (player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        int DistanceTravelled = player.getStatistic(Statistic.WALK_ONE_CM);
        String Server = player.getWorld().toString();
        int Playtime = (play / 20) / 60; //This returns playtime in minutes
        try{
            Document data = new Document("_id" , UUID)
                    .append("Username", player.getDisplayName())
                    .append("IP", player.getAddress().toString())
                    .append("DiscordName", "None")
                    .append("DiscordID", 0)
                    .append("LogoutLocation", getLocation(player))
                    .append("AuthCode", "NotSetup")
                    .append("APIEnabled", true)
                    .append("TotalDeaths", Deaths)
                    .append("TotalKills", totalKills)
                    .append("Playtime", Playtime)
                    .append("Server", Server)
                    .append("TotalDistanceTravelledOnFoot", 0);
            coll.insertOne(data);
            mongoClient.close();
        }
        catch (MongoWriteException e){
            e.printStackTrace();
            if (e.getCode() == 11000){
                coll.find(filter).forEach((Consumer<Document>) document -> {
                    Bson UpdateLogout = new Document("LogoutLocation", getLocation(player));
                    Bson UpdateTotalDeaths = new Document("TotalDeaths", Deaths);
                    Bson UpdateKills = new Document("TotalKills", totalKills);
                    Bson UpdateBlocks = new Document("Playtime", Playtime);
                    Bson UpdateServer = new Document("Server", Server);
                    Bson UpdateWalking = new Document("TotalDistanceTravelledOnFoot", DistanceTravelled / 100);

                    Bson BsonLogout = new Document("$set", UpdateLogout);
                    Bson BsonDeaths = new Document("$set", UpdateTotalDeaths);
                    Bson BsonKills = new Document("$set", UpdateKills);
                    Bson BsonPlaytime = new Document("$set", UpdateBlocks);
                    Bson BsonServer = new Document("$set", UpdateServer);
                    Bson BsonWalking  = new Document("$set", UpdateWalking);

                    coll.updateOne(document, BsonLogout);
                    coll.updateOne(document, BsonDeaths);
                    coll.updateOne(document, BsonKills);
                    coll.updateOne(document, BsonPlaytime);
                    coll.updateOne(document, BsonServer);
                    coll.updateOne(document, BsonWalking);
                });
                mongoClient.close();
            }
        }
    }

    public static void HandleJoin(Player player){
        CFG config = new CFG();
        // We do on leave rather than live data because less data to transfer each time
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        MongoCollection<Document> coll = database.getCollection(config.MongoCollection);

        String UUID = player.getUniqueId().toString();
        Document filter = new Document("_id" , UUID);
        int DistanceTravelled = player.getStatistic(Statistic.WALK_ONE_CM);
        try{
            // First Time Join
            Document data = new Document("_id" , UUID)
                    .append("Username", player.getDisplayName())
                    .append("IP", player.getAddress().toString())
                    .append("DiscordName", "None")
                    .append("DiscordID", 0)
                    .append("LogoutLocation", "FirstJoin")
                    .append("AuthCode", "NotSetup")
                    .append("APIEnabled", true)
                    .append("TotalDeaths", 0)
                    .append("TotalKills", 0)
                    .append("Playtime", 0)
                    .append("Server", "Hub")
                    .append("TotalDistanceTravelledOnFoot", DistanceTravelled / 100);
            coll.insertOne(data);
            mongoClient.close();
            DebugMessage(player, "[API] " + player.getDisplayName() + " has joined for the first time. Setting up API for Player");
        }
        catch (MongoWriteException e){
            e.printStackTrace();
            if (e.getCode() == 11000){
                coll.find(filter).forEach((Consumer<Document>) document -> {
                    Bson UpdateUsername = new Document("Username", player.getDisplayName());
                    Bson UpdateIP = new Document("IP", player.getAddress().toString());

                    Bson UpdateUserBson = new Document("$set", UpdateUsername);
                    Bson UpdateIPBson = new Document("$set", UpdateIP);

                    coll.updateOne(document, UpdateUserBson);
                    coll.updateOne(document, UpdateIPBson);
                });
                mongoClient.close();
            }
        }

    }

}
