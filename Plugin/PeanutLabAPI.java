package com.peanutlab.api.peanutlabapi;

import com.peanutlab.api.peanutlabapi.Commands.UserAPIControl;
import com.peanutlab.api.peanutlabapi.Commands.UserAPIControlTab;
import com.peanutlab.api.peanutlabapi.Events.OnJoin;
import com.peanutlab.api.peanutlabapi.Events.OnLeave;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class PeanutLabAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[PeanutsLabAPI] API has been loaded");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[PeanutsLabAPI] Version " + API.APIVersion());
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[PeanutsLabAPI] Authors " + API.Authors());
        getCommand("api").setExecutor(new UserAPIControl());
        getCommand("api").setTabCompleter(new UserAPIControlTab());
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new OnLeave(), this);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[PeanutsLabAPI] API has been unloaded");
    }
}
