package com.kuebv.mcapi.mcapi.API;

import com.kuebv.mcapi.mcapi.MCAPI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class APIUpdates {

    //If Server Operator runs the command /api update.
    public static void UpdateDatabase(Player player){
        CFG config = new CFG();
        // We do on leave rather than live data because less data to transfer each time
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        MongoCollection<Document> coll = database.getCollection(config.MongoCollection);

        try{
            Document filter = new Document();
            Document updateDoc = new Document().append("$set", new Document().append("DiamondsMined", 0).append("StoneMined", 0));
            coll.updateMany(filter, updateDoc);

            player.sendMessage(ChatColor.GREEN + "Database has been updated");
            mongoClient.close();
        }
        catch (Exception e){
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Database was unable to update");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] [Error] " + ChatColor.YELLOW + e.toString());
            player.sendMessage(ChatColor.RED + "Unable to update Database, Check Console for Errors");
            mongoClient.close();
        }
    }


}
