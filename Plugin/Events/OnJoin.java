package com.peanutlab.api.peanutlabapi.Events;

import com.peanutlab.api.peanutlabapi.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    @EventHandler
    public void OnJoin(PlayerJoinEvent ev){
        Player player = ev.getPlayer();
        API.HandleJoin(player);
    }
}
