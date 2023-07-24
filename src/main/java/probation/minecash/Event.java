package probation.minecash;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Event implements Listener {
    Random rand = new Random();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void MineBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        Set<String> materials = MineCash.getInstance().getConfig().getConfigurationSection("blocks").getKeys(false);
        List<String> mines = MineCash.getInstance().getConfig().getStringList("regions");
        List<String> worlds = MineCash.getInstance().getConfig().getStringList("worlds");
        if (worlds.contains(p.getWorld().getName())) {
            if (materials.contains(block.getType().toString())) {
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
                ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()));
                for (ProtectedRegion region : regions) {
                    if (mines.contains(region.getId())) {
                        EconomyResponse r = MineCash.getEconomy().depositPlayer(p, MineCash.getInstance().getConfig().getDouble("blocks." + block.getType().toString() + ".money") + MineCash.getInstance().getConfig().getDouble("blocks." + block.getType().toString() + ".rand") * rand.nextInt(5));
                        if (r.transactionSuccess()) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Вы получили " + r.amount + "$"));
                        } else {
                            p.sendMessage(String.format("An error occured: %s", r.errorMessage));
                        }
                        break;
                    }
                }
            }
        }
    }
}