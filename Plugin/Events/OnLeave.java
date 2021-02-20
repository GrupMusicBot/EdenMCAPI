package com.peanutlab.api.peanutlabapi.Events;

import com.peanutlab.api.peanutlabapi.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnLeave implements Listener {
    @EventHandler
    public void OnLeave(PlayerQuitEvent ev){
        Player player = ev.getPlayer();
        API.HandleLeave(player);
    }
}
