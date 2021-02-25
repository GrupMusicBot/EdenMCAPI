package com.kuebv.mcapi.mcapi.API;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.kuebv.mcapi.mcapi.MCAPI;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.slf4j.LoggerFactory;
import sun.security.ssl.Debug;

import javax.print.Doc;
import java.sql.Time;
import java.util.function.Consumer;

import static org.bukkit.Bukkit.getServer;

public class API {


    public static String APIVersion(){
        String APIVersion = "1.7";
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
        int play = (player.getStatistic(Statistic.PLAY_ONE_MINUTE));
        int Playtime = (play / 20) / 60; //This returns playtime in minutes

        int TotalDistance = (player.getStatistic(Statistic.WALK_ONE_CM) + player.getStatistic(Statistic.SPRINT_ONE_CM) + player.getStatistic(Statistic.BOAT_ONE_CM));

        Document found = (Document) coll.find(new Document ("_id", UUID)).first();

        if (found != null){
            coll.find(filter).forEach((Consumer<Document>) document -> {
                try{
                    Bson UpdateLogout = new Document("LogoutLocation", getLocation(player));
                    Bson BsonLogout = new Document("$set", UpdateLogout);

                    coll.updateOne(found, BsonLogout);
                }
                catch (MongoException e){
                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[API] ERROR : ISSUE WHEN UPDATING DOCUMENT ON METHOD HandleLeave ");
                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[API] ERROR : " + e.toString());
                }
            });
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Updating User Data on Leave");
        }
        else{
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Fresh User");
            Document user = new Document("_id", UUID);
            user.append("UUID", UUID.replaceAll("-", ""))
                    .append("LogoutLocation", "FirstJoin")
                    .append("Deaths", player.getStatistic(Statistic.DEATHS))
                    .append("MobKills", player.getStatistic(Statistic.MOB_KILLS))
                    .append("PlayerKills", player.getStatistic(Statistic.PLAYER_KILLS))
                    .append("DistanceTravelled", TotalDistance)
                    .append("Playtime", Playtime)
                    .append("Jumps", player.getStatistic(Statistic.JUMP))
                    .append("DamageDealt", player.getStatistic(Statistic.DAMAGE_DEALT))
                    .append("DamageTaken", player.getStatistic(Statistic.DAMAGE_TAKEN))
                    .append("FlowersPotted", player.getStatistic(Statistic.FLOWER_POTTED))
                    .append("TimeSinceLastDeath", player.getStatistic(Statistic.TIME_SINCE_DEATH))
                    .append("APIEnabled", true);
            coll.insertOne(user);
            getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[API] THIS IS NOT AN ERROR : USER LEFT DURING API UPDATE");
        }
        mongoClient.close();
    }

    public static void HandleJoin(Player player){
        CFG config = new CFG();
        // Runs everytime someone joins the server, more of a setup for the loop
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        MongoCollection<Document> coll = database.getCollection(config.MongoCollection);

        String UUID = player.getUniqueId().toString();
        Document filter = new Document("_id" , UUID);
        int DistanceTravelled = player.getStatistic(Statistic.WALK_ONE_CM);

        Document found = (Document) coll.find(new Document ("_id", UUID)).first();

        int Playtime = (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60; //This returns playtime in minutes

        int TotalDistance = (player.getStatistic(Statistic.WALK_ONE_CM) + player.getStatistic(Statistic.SPRINT_ONE_CM) + player.getStatistic(Statistic.BOAT_ONE_CM));

        if (found != null){
            Bson UpdateUsername = new Document("Username", player.getDisplayName());
            Bson UpdateIP = new Document("IP", player.getAddress().toString());

            Bson UpdateUserBson = new Document("$set", UpdateUsername);
            Bson UpdateIPBson = new Document("$set", UpdateIP);

            coll.updateMany(found, UpdateUserBson);
            coll.updateMany(found, UpdateIPBson);
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Updating User Data on Join");
        }
        else{
            Document user = new Document("_id", UUID);
            user.append("UUID", UUID.replaceAll("-", ""))
                    .append("LogoutLocation", "FirstJoin")
                    .append("Deaths", player.getStatistic(Statistic.DEATHS))
                    .append("MobKills", player.getStatistic(Statistic.MOB_KILLS))
                    .append("PlayerKills", player.getStatistic(Statistic.PLAYER_KILLS))
                    .append("DistanceTravelled", TotalDistance)
                    .append("Playtime", Playtime)
                    .append("Jumps", player.getStatistic(Statistic.JUMP))
                    .append("DamageDealt", player.getStatistic(Statistic.DAMAGE_DEALT))
                    .append("DamageTaken", player.getStatistic(Statistic.DAMAGE_TAKEN))
                    .append("FlowersPotted", player.getStatistic(Statistic.FLOWER_POTTED))
                    .append("TimeSinceLastDeath", player.getStatistic(Statistic.TIME_SINCE_DEATH))
                    .append("Username", player.getDisplayName())
                    .append("APIEnabled", true);
            coll.insertOne(user);
            DebugMessage(player, "[API] " + player.getDisplayName() + " is not found in the Database. Setting up Settings & Statistics");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Fresh User");
        }

        mongoClient.close();

    }


    // Thanks Ben for the idea of having the code loop every minute rather than just leave and join
    public static void LoopMinute() {
        //Reference this outside of the For Statement, because that would be hell
        CFG config = new CFG();
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        MongoCollection<Document> coll = database.getCollection(config.MongoCollection);
        for (Player p : getServer().getOnlinePlayers()){
            String UUID = p.getUniqueId().toString();
            int TotalDistance = (p.getStatistic(Statistic.WALK_ONE_CM) + p.getStatistic(Statistic.SPRINT_ONE_CM) + p.getStatistic(Statistic.BOAT_ONE_CM));
            int totalPlaytime = (p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60;
            Document found = (Document) coll.find(new Document("_id", UUID)).first();
            if (found != null){
                Bson Deaths = new Document("Deaths", p.getStatistic(Statistic.DEATHS));
                Bson SetDeaths = new Document("$set", Deaths);
                coll.updateOne(found, SetDeaths);

                Bson MobKills = new Document("MobKills", p.getStatistic(Statistic.MOB_KILLS));
                Bson SetMobKills = new Document("$set", MobKills);
                coll.updateOne(found, SetMobKills);

                Bson PlayerKills = new Document("PlayerKills", p.getStatistic(Statistic.PLAYER_KILLS));
                Bson SetPlayerKills = new Document("$set", PlayerKills);
                coll.updateOne(found, SetPlayerKills);

                Bson DistanceTravelled = new Document("DistanceTravelled", TotalDistance);
                Bson SetDistanceTravelled = new Document("$set", DistanceTravelled);
                coll.updateOne(found, SetDistanceTravelled);

                Bson Playtime = new Document("Playtime", totalPlaytime);
                Bson SetPlaytime = new Document("$set", Playtime);
                coll.updateOne(found, SetPlaytime);

                // I HAD TO REMOVE 5 FUNCTIONS FROM HERE
                // AHHHHHHHHHHHH

                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Updated " + p.getDisplayName() + "'s Document");

            }
            // There is no Else statement, because it should never happen. User's are entered on join and leave. Hence it will never run
        }
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] API Check Complete");

        mongoClient.close();
        // We don't want the MongoDB to be overloaded again.

    }


}
