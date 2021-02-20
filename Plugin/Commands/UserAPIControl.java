package com.peanutlab.api.peanutlabapi.Commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.peanutlab.api.peanutlabapi.API;
import com.peanutlab.api.peanutlabapi.CodeGenerator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class UserAPIControl implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://EdenAdmin:j2NmOEmDLb@discordcluster.lxrev.mongodb.net/DiscordBot?retryWrites=true&w=majority");
        MongoDatabase database = mongoClient.getDatabase("MC");
        MongoCollection<Document> coll = database.getCollection("API");

        if (label.equalsIgnoreCase("api")){
            if (!(commandSender instanceof Player)){
                commandSender.sendMessage(ChatColor.RED + "You can only execute this command as a Player!");
                return true;
            }
            Player player = (Player) commandSender;
            String UUID = player.getUniqueId().toString();
            Document updatefilter = new Document("_id", UUID);
            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "Incorrect Syntax : Do /api help for more help");
            }
            if (args.length >= 1){
                if (args[0].equalsIgnoreCase("link")){
                    player.sendMessage(ChatColor.YELLOW + "Please Wait while the code is generating...");
                    String authToken = CodeGenerator.GetCode();
                    coll.find(updatefilter).forEach((Consumer<Document>) document -> {
                        Bson LinkAccount = new Document("AuthCode" , authToken);
                        Bson UpdateOperator_One = new Document("$set", LinkAccount);
                        coll.updateOne(document, UpdateOperator_One);
                    });
                    player.sendMessage(ChatColor.GREEN + "Generated Code : Your Code is in the Debug Message Below");
                    API.DebugMessage(player, authToken);
                }
                if (args[0].equalsIgnoreCase("disable")){
                    player.sendMessage(ChatColor.BLUE + "[Debug] " + ChatColor.GRAY + "Disabling API");

                    coll.find(updatefilter).forEach((Consumer<Document>) document -> {
                        Bson UpdateUserName = new Document("APIEnabled" , false);
                        Bson UpdateOperator_One = new Document("$set", UpdateUserName);
                        coll.updateOne(document, UpdateOperator_One);
                    });
                    player.sendMessage(ChatColor.RED + "Disabled your API Settings");
                }
                if (args[0].equalsIgnoreCase("enable")){
                    player.sendMessage(ChatColor.BLUE + "[Debug] " + ChatColor.GRAY + "Enabling API");

                    coll.find(updatefilter).forEach((Consumer<Document>) document -> {
                        Bson UpdateUserName = new Document("APIEnabled" , true);
                        Bson UpdateOperator_One = new Document("$set", UpdateUserName);
                        coll.updateOne(document, UpdateOperator_One);
                    });
                    player.sendMessage(ChatColor.RED + "Enabled your API Settings");
                }
                if (args[0].equalsIgnoreCase("about")){
                    player.sendMessage(ChatColor.GREEN + "[API] This plugin was created and developed by KuebV\nThis plugin will track your statistics, and information about your session" + ChatColor.RED + "\nDo /api disable to disable the API");
                }
                if (args[0].equalsIgnoreCase("help")){
                    player.sendMessage(ChatColor.GREEN + "_______________________________");
                    player.sendMessage(ChatColor.BLUE + "/api about - Displays information about the plugin\n" +
                            "/api disable - Disable your API for the public\n" +
                            "/api enable - Enable your API for the public\n" +
                            "/api link <Code> - Do $api link in the discord to link your account to the API");
                    player.sendMessage(ChatColor.GREEN + "_______________________________");
                }
            }
        }
        return false;
    }
}
