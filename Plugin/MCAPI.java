package com.kuebv.mcapi.mcapi;

import com.kuebv.mcapi.mcapi.API.API;
import com.kuebv.mcapi.mcapi.API.CFG;
import com.kuebv.mcapi.mcapi.Commands.UserAPIControls;
import com.kuebv.mcapi.mcapi.Commands.UserAPIControlsTab;
import com.kuebv.mcapi.mcapi.Events.OnJoin;
import com.kuebv.mcapi.mcapi.Events.OnLeave;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import sun.awt.Win32GraphicsConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getServer;

public final class MCAPI extends JavaPlugin {
    private CFG config = new CFG();
    private MongoCollection<Document> coll;

    @Override
    public void onEnable() {
        //Lets shut the MongoDB up
        System.setProperty("DEBUG.GO", "true");
        System.setProperty("DB.TRACE", "true");
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);

        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        MongoDatabase database = mongoClient.getDatabase(config.MongoDatabase);
        coll = database.getCollection(config.MongoCollection);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] API has been loaded");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Version " + API.APIVersion());
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Authors : " + API.Authors()); // It took me 4 full updates for me to fix fucking formatting

        getCommand("api").setExecutor(new UserAPIControls());
        getCommand("api").setTabCompleter(new UserAPIControlsTab());
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnLeave(), this);

        // This accidentally acts as a OnReload. Task Failed Successfully
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
            @Override
            public void run() {
                API.LoopMinute();
            }
        }, 0, 20 * 60 );
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[API] API has been unloaded");
    }

}
