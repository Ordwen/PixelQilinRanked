package fr.pixelqilin.pixelqilinranked.top;

import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.core.RankedPlayer;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TopInventory {

    private final PixelQilinRanked plugin;

    private final Map<Integer, Integer> topSlots = new HashMap<>();
    private final Inventory base;

    public TopInventory(PixelQilinRanked plugin) {
        this.plugin = plugin;
        this.base = loadBase();
        initSlots();
    }

    /**
     * Initialize the base of the inventory with glass panes.
     *
     * @return base inventory.
     */
    private Inventory loadBase() {
        final Inventory base = Bukkit.createInventory(null, 54, "§6Top 12 des joueurs");

        final ItemStack purpleGlass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        final ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        final ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        // set purple glass
        base.setItem(0, purpleGlass);
        base.setItem(1, purpleGlass);
        base.setItem(2, purpleGlass);
        base.setItem(6, purpleGlass);
        base.setItem(7, purpleGlass);
        base.setItem(8, purpleGlass);
        base.setItem(9, purpleGlass);
        base.setItem(10, purpleGlass);
        base.setItem(16, purpleGlass);
        base.setItem(17, purpleGlass);

        // set black glass
        base.setItem(3, blackGlass);
        base.setItem(4, blackGlass);
        base.setItem(5, blackGlass);
        base.setItem(11, blackGlass);
        base.setItem(15, blackGlass);
        base.setItem(18, blackGlass);
        base.setItem(19, blackGlass);
        base.setItem(20, blackGlass);
        base.setItem(24, blackGlass);
        base.setItem(25, blackGlass);
        base.setItem(26, blackGlass);
        base.setItem(29, blackGlass);
        base.setItem(33, blackGlass);
        base.setItem(39, blackGlass);
        base.setItem(40, blackGlass);
        base.setItem(41, blackGlass);

        // set white glass
        base.setItem(27, whiteGlass);
        base.setItem(28, whiteGlass);
        base.setItem(34, whiteGlass);
        base.setItem(35, whiteGlass);
        base.setItem(36, whiteGlass);
        base.setItem(37, whiteGlass);
        base.setItem(38, whiteGlass);
        base.setItem(42, whiteGlass);
        base.setItem(43, whiteGlass);
        base.setItem(44, whiteGlass);

        return base;
    }

    /**
     * Initialize the slots to display the top players.
     */
    private void initSlots() {
        topSlots.put(1, 13);
        topSlots.put(2, 21);
        topSlots.put(3, 23);
        topSlots.put(4, 45);
        topSlots.put(5, 46);
        topSlots.put(6, 47);
        topSlots.put(7, 48);
        topSlots.put(8, 49);
        topSlots.put(9, 50);
        topSlots.put(10, 51);
        topSlots.put(11, 52);
        topSlots.put(12, 53);
    }

    /**
     * Open the top inventory to the player.
     *
     * @param sender player who will receive the inventory.
     */
    public void openTop(Player sender) {
        sender.openInventory(getTopInventory());
    }

    /**
     * Get the top inventory.
     *
     * @return top inventory.
     */
    public Inventory getTopInventory() {
        final List<RankedPlayer> players = plugin.getSqlManager().getSqlTop().getTopPlayers();
        final Inventory inventory = base;

        for (int i = 0; i < players.size(); i++) {
            final RankedPlayer player = players.get(i);
            final int slot = topSlots.get(i + 1);

            inventory.setItem(slot, getRankedPlayerItem(i + 1, player));
        }

        return inventory;
    }

    /**
     * Get the head item of a ranked player.
     *
     * @param rankedPlayer ranked player.
     * @return item.
     */
    private ItemStack getRankedPlayerItem(int top, RankedPlayer rankedPlayer) {
        final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        final var meta = (SkullMeta) item.getItemMeta();

        if (meta == null) {
            PluginLogger.error("An error occurred while getting the item meta.");
            return item;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rankedPlayer.getUuid()));

        meta.setDisplayName(ChatColor.GOLD + "Top " + top + " - " + player.getName());
        meta.setLore(List.of(
                ChatColor.BLUE + "Rang: " + ChatColor.GOLD + rankedPlayer.getRank(),
                ChatColor.YELLOW + "Elo: " + ChatColor.GOLD + rankedPlayer.getElo(),
                ChatColor.GREEN + "Victoires: " + ChatColor.GOLD + rankedPlayer.getWins(),
                ChatColor.RED + "Défaites: " + ChatColor.GOLD + rankedPlayer.getLosses()
        ));
        meta.setOwningPlayer(player);

        item.setItemMeta(meta);

        return item;
    }
}
