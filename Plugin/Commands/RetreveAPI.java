package com.peanutlab.api.peanutlabapi.Commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class RetreveAPI {

//    public static void getAPIInfo(Player player){
//        MongoClient mongoClient = MongoClients.create("MongoURL");
//        MongoDatabase database = mongoClient.getDatabase("MC");
//        MongoCollection<Document> coll = database.getCollection("API");
//
//        Document filter = new Document("_id" , player.getUniqueId());
//        coll.find(filter).forEach((Consumer<Document>) document -> {
//            String Kills = (String) document.get("TotalKills");
//            String Deaths = document.get("TotalDeaths").toString();
//            String Playtime = document.get("Playtime").toString();
//            String APIEnabled = document.get("APIEnabled").toString();
//            String DiscordName = document.get("DiscordName").toString();
//
//            player.sendMessage(ChatColor.BLUE +
//                    "---------------------------------------\n" +
//                    ChatColor.GRAY + "Username : " + player.getDisplayName() + "\nUUID : " + player.getUniqueId() + "\nDiscord Username : " + DiscordName + "\nTotal Kills : " + Kills + "\nDeaths : " + Deaths + "\nPlaytime : " + Playtime + "\nAPI Enabled? : " + APIEnabled +
//                    ChatColor.BLUE + "---------------------------------------");
//
//        });
//
//
//    }
}
