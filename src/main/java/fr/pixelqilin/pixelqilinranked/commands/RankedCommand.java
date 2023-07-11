package fr.pixelqilin.pixelqilinranked.commands;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankedCommand implements CommandExecutor {

    private final PixelQilinRanked plugin;

    public RankedCommand(PixelQilinRanked plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean isAdmin = sender.hasPermission("pixelqilinranked.admin");
        boolean isPlayer = sender instanceof Player;

        if (args.length == 0) {
            usage(isAdmin, sender);
            return true;
        }

        switch (args[0]) {
            case "join" -> {
                if (isPlayer) {
                    Player player = (Player) sender;
                    join(player);
                } else
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            }
            case "leave" -> {
                if (isPlayer) {
                    Player player = (Player) sender;
                    leave(player);
                } else
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            }
            case "me" -> {
                if (isPlayer) {
                    Player player = (Player) sender;
                    me(player);
                } else
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            }
            case "accept" -> {
                if (isPlayer) {
                    Player player = (Player) sender;
                    accept(player);
                } else
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            }
            case "decline" -> {
                if (isPlayer) {
                    Player player = (Player) sender;
                    decline(player);
                } else
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            }
            case "see" -> {
                if (isAdmin) {
                    final Player target = plugin.getServer().getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage("§cLe joueur n'est pas connecté.");
                        return true;
                    }

                    if (args.length == 2)
                        see(sender, target);
                    else
                        sender.sendMessage("§cUsage: /ranked see <player>");
                } else
                    sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            }
            case "setzone" -> {
                if (!isPlayer) {
                    sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
                    return true;
                }
                if (isAdmin)
                    setZone((Player) sender);
                else
                    sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            }
            case "add" -> {
                if (isAdmin) {
                    if (args.length == 3) {
                        final Player target = plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage("§cLe joueur n'est pas connecté.");
                            return true;
                        }

                        int elo;
                        try {
                            elo = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§cL'elo doit être un nombre.");
                            return true;
                        }

                        add(sender, target, elo);
                    } else
                        sender.sendMessage("§cUsage: /ranked add <player> <elo>");
                } else
                    sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            }
            case "remove" -> {
                if (isAdmin) {
                    if (args.length == 3) {
                        final Player target = plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage("§cLe joueur n'est pas connecté.");
                            return true;
                        }

                        int elo;
                        try {
                            elo = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§cL'elo doit être un nombre.");
                            return true;
                        }

                        remove(sender, target, elo);
                    } else
                        sender.sendMessage("§cUsage: /ranked remove <player>");
                } else
                    sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            }
            case "set" -> {
                if (isAdmin) {
                    if (args.length == 3) {
                        final Player target = plugin.getServer().getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage("§cLe joueur n'est pas connecté.");
                            return true;
                        }

                        int elo;
                        try {
                            elo = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§cL'elo doit être un nombre.");
                            return true;
                        }

                        set(sender, target, elo);
                    } else
                        sender.sendMessage("§cUsage: /ranked set <player> <elo>");
                } else
                    sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            }
            default -> usage(isAdmin, sender);
        }

        return true;
    }

    /**
     * Display the usage of the command.
     * @param isAdmin if the sender have the permission to use admin commands
     * @param sender the sender of the command
     */
    private void usage(boolean isAdmin, CommandSender sender) {
        if (isAdmin)
            sender.sendMessage("§cUsage: /ranked <join|leave|me|see|add|remove|set> [player] [elo]");
        else
            sender.sendMessage("§cUsage: /ranked <join|leave|me>");
    }

    /**
     * Add the player to the ranked queue.
     * @param player the player to add
     */
    private void join(Player player) {
        plugin.getRankedQueue().addToQueue(player);
    }

    /**
     * Remove the player from the ranked queue.
     * @param player the player to remove
     */
    private void leave(Player player) {
        plugin.getRankedQueue().removeFromQueue(player);
    }

    /**
     * Accept the duel.
     * @param player the player who accept the duel
     */
    private void accept(Player player) {
        plugin.getRankedQueue().accept(player);
    }

    /**
     * Decline the duel.
     * @param player the player who decline the duel
     */
    private void decline(Player player) {
        plugin.getRankedQueue().decline(player);
    }

    /**
     * Display the elo of the player.
     * @param player the player to display the elo
     */
    private void me(Player player) {
        plugin.getRankedQueue().checkEloSelf(player);
    }

    /**
     * Display the elo of the target.
     * @param sender the sender who execute the command
     * @param target the target to display the elo
     */
    private void see(CommandSender sender, Player target) {
        plugin.getRankedQueue().checkEloOther(sender, target);
    }


    /**
     * Set the zone for the duels.
     * @param player player who execute the command
     */
    private void setZone(Player player) {
        final Location location = player.getLocation();
        plugin.getConfig().set("fight-location", location);
        plugin.saveConfig();

        player.sendMessage("§aLa zone de combat a été définie.");
    }

    /**
     * Add elo to the target.
     * @param sender the sender who execute the command
     * @param target the target to add elo
     * @param amount the amount of elo to add
     */
    private void add(CommandSender sender, Player target, int amount) {
        plugin.getRankedQueue().addElo(sender, target, amount);
    }

    /**
     * Remove elo to the target.
     * @param sender the sender who execute the command
     * @param target the target to remove elo
     * @param amount the amount of elo to remove
     */
    private void remove(CommandSender sender, Player target, int amount) {
        plugin.getRankedQueue().removeElo(sender, target, amount);
    }

    /**
     * Set the elo of the target.
     * @param sender the sender who execute the command
     * @param target the target to set elo
     * @param amount the amount of elo to set
     */
    private void set(CommandSender sender, Player target, int amount) {
        plugin.getRankedQueue().setElo(sender, target, amount);
    }
}
