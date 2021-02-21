package com.kuebv.mcapi.mcapi.Events;

import com.kuebv.mcapi.mcapi.API.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnLeave implements Listener {

    @EventHandler
    public void OnLeave(PlayerQuitEvent ev){
        Player player = ev.getPlayer();
        API.HandleLeave(player);
    }
}
