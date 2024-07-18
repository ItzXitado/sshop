package io.github.xitadoo.sshop.commands;

import io.github.xitadoo.sshop.Main;
import io.github.xitadoo.sshop.gui.HistoryInventory;
import io.github.xitadoo.sshop.gui.SpawnerInventory;
import io.github.xitadoo.sshop.manager.SpawnerManager;
import io.github.xitadoo.sshop.manager.UserManager;
import io.github.xitadoo.sshop.util.Players;
import io.github.xitadoo.sshop.util.Sort;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends Command {
    

    private final UserManager userManager;
    private final SpawnerManager spawnerManager;
    
    public ShopCommand(UserManager userManager, SpawnerManager spawnerManager) {
        super("sshop");
        this.userManager = userManager;
        this.spawnerManager = spawnerManager;
    }

    @Override
    public boolean execute(CommandSender s, String label, String[] args) {
        if (!(s instanceof Player)) {
            return false;
        }

        Player player = (Player) s;
        if (args.length < 1) {
            s.sendMessage("§cUsage: /sshop (buy/history)");
            return false;
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "buy":
                    new SpawnerInventory(spawnerManager.getSpawnerList(), player); //fix new and instance methods
                    break;
                case "history":
                    new HistoryInventory(userManager
                            .getSortedList(userManager.fetchUserWithId(player.getUniqueId()).getPlayerHistory(), Sort.AMOUNT_REVERSED) , player, player.getUniqueId());
                    break;
                default:
                    s.sendMessage("§cUsage: /sshop (buy/history)");
                    break;

            }
            return false;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("history")) {
                if (Bukkit.getPlayer(args[1]) != null) {
                    new HistoryInventory(userManager.getSortedList(userManager.fetchUserWithId(Bukkit.getPlayer(args[1]).getUniqueId()).getPlayerHistory(), Sort.AMOUNT_REVERSED), player, Bukkit.getPlayer(args[1]).getUniqueId());
                }
                if (Players.fetchPlayerUniqueId(args[1]) == null) {
                    s.sendMessage("§cThat player does not exist.");
                    return false;
                }
                if (userManager.fetchUserWithId(Players.fetchPlayerUniqueId(args[1])) == null) {
                    //Player is not in the cache lets search for him in the database , Null verificator is done, toString() will never be null
                    Main.getInstance().mongoDB.fetchUser(Players.fetchPlayerUniqueId(args[1]).toString()).thenAccept(user -> {
                        if (user == null) {
                            //User is not in database
                            s.sendMessage("§cThere are no avaliable data for that user's history");
                        } else {
                            //loading user to cache and opening the menu
                            s.sendMessage("§aOpening " + Players.getOfflinePlayer(user.getPlayerId()).getName() + "'s history");
                            userManager.register(user);
                            new HistoryInventory(userManager.getSortedList(user.getPlayerHistory(), Sort.AMOUNT_REVERSED), player, user.getPlayerId());
                        }
                    });
                    return false;
                } else  {
                    //is in database
                    new HistoryInventory(userManager.getSortedList(userManager.fetchUserWithId(Players.fetchPlayerUniqueId(args[1])).getPlayerHistory(), Sort.AMOUNT_REVERSED), player, Players.fetchPlayerUniqueId(args[1]));
                }
            } else {
                s.sendMessage("§cUsage: /sshop history (player)");
            }
            return false;
        }
        return false;
    }
}
