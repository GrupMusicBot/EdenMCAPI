package com.kuebv.mcapi.mcapi.Events;

import com.kuebv.mcapi.mcapi.API.API;
import com.kuebv.mcapi.mcapi.API.CFG;
import com.kuebv.mcapi.mcapi.MCAPI;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class OnJoin implements Listener {

    // To Do:
    // Check IP in DB and match with IP connecting to check for Alt Account or account sharing.
    // Make the above a config option

    @EventHandler
    public void OnJoin(PlayerJoinEvent ev){
        Player player = ev.getPlayer();
        API.HandleJoin(player);

        // Runs 10 Seconds after Player Joins the server.
//        getServer().getScheduler().runTaskLater((Plugin)this,  new Runnable() {
//            @Override
//            public void run(){
//                CFG config = new CFG();
//                MongoClient mongoClient = MongoClients.create(config.MongoURL);
//                MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
//                MongoCollection<Document> coll = database.getCollection(config.MongoCollection);
//
//                Document found = (Document) coll.find(new Document ("_id", player.getUniqueId().toString())).first();
//                if (found != null){
//                    // If plugin is installed on a BungeeCord / Waterfall server. The Plugin has a difficult time understanding the IP
//                    // The IP would show as the Host IP. If this happens, this function will stop it from happening
//                    Document getIp = new Document("IP", player.getAddress());
//                    Document setIP = new Document("$set", getIp);
//                    coll.updateOne(found, setIP);
//                    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] " + player.getDisplayName() + "'s IP was Set!");
//                }
//                else{
//                    // This is just purely an error code for the player and server
//                    // Error should only be thrown if the Player Document doesn't exist. Which should be impossible due to this being OnJoin . Hopefully 10 Seconds is enough time.
//                    API.DebugMessage(player, "ERROR : OnJoin was not run properly!");
//                    getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[API] " + player.getDisplayName() + "'s IP wasnt entered set properly. This should not be considered an error");
//                }
//
//                mongoClient.close();
//            }
//        }, 200L);
    }
}
