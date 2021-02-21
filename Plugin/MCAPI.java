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
import org.bukkit.plugin.java.JavaPlugin;
import sun.awt.Win32GraphicsConfig;

public final class MCAPI extends JavaPlugin {


    @Override
    public void onEnable() {

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] API has been loaded");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Version " + API.APIVersion());
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[API] Authors :" + API.Authors());

        getCommand("api").setExecutor(new UserAPIControls());
        getCommand("api").setTabCompleter(new UserAPIControlsTab());
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnLeave(), this);
    }

    @Override
    public void onDisable() {
        CFG config = new CFG();
        MongoClient mongoClient = MongoClients.create(config.MongoURL);
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[API] API has been unloaded");
        mongoClient.close();
    }
}
