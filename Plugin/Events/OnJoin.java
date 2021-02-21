package com.kuebv.mcapi.mcapi.Events;

import com.kuebv.mcapi.mcapi.API.API;
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
