package com.peanutlab.api.peanutlabapi;

import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import sun.security.ssl.Debug;

import javax.print.Doc;
import javax.swing.event.DocumentEvent;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {

    public static String APIVersion(){
        String APIVersion = "1.2";
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
        // We do on leave rather than live data because less data to transfer each time
        MongoClient mongoClient = MongoClients.create("MongoURL");
        MongoDatabase database = mongoClient.getDatabase("MC");
        MongoCollection<Document> coll = database.getCollection("API");

        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        String UUID = player.getUniqueId().toString();
        Document filter = new Document("_id", UUID);

        int Kills = player.getStatistic(Statistic.PLAYER_KILLS);
        int MobKills  = player.getStatistic(Statistic.MOB_KILLS);
        int totalKills = Kills + MobKills;
        int Deaths = player.getStatistic(Statistic.DEATHS);
        int play = (player.getStatistic(Statistic.PLAY_ONE_MINUTE));
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
                    .append("Server", Server);
            coll.insertOne(data);
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

                    Bson BsonLogout = new Document("$set", UpdateLogout);
                    Bson BsonDeaths = new Document("$set", UpdateTotalDeaths);
                    Bson BsonKills = new Document("$set", UpdateKills);
                    Bson BsonBlocks = new Document("$set", UpdateBlocks);
                    Bson BsonServer = new Document("$set", UpdateServer);

                    coll.updateOne(document, BsonLogout);
                    coll.updateOne(document, BsonDeaths);
                    coll.updateOne(document, BsonKills);
                    coll.updateOne(document, BsonBlocks);
                    coll.updateOne(document, BsonServer);
                });
            }
        }
    }


    public static void HandleJoin(Player player){
        MongoClient mongoClient = MongoClients.create("MongoURL");
        MongoDatabase database = mongoClient.getDatabase("MC");
        MongoCollection<Document> coll = database.getCollection("API");

        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        String UUID = player.getUniqueId().toString();
        Document filter = new Document("_id" , UUID);
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
                    .append("Server", "Hub");
            coll.insertOne(data);
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
            }
        }

    }
}
