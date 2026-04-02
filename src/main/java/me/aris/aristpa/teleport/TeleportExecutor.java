package me.aris.aristpa.teleport;

import me.aris.aristpa.ArisTPA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportExecutor {
    private ArisTPA plugin;
    private Map<UUID, TeleportTask> activeTeleports;
    
    public TeleportExecutor(ArisTPA plugin) {
        this.plugin = plugin;
        this.activeTeleports = new HashMap<>();
    }
    
    public void startTeleport(Player player, Location targetLocation, Runnable onComplete) {
        startTeleport(player, targetLocation, onComplete, null);
    }
    
    public void startTeleport(Player player, Location targetLocation, Runnable onComplete, Runnable onCancel) {
        if (activeTeleports.containsKey(player.getUniqueId())) {
            cancelTeleport(player);
        }
        
        TeleportTask task = new TeleportTask(plugin, player, targetLocation, onComplete, onCancel);
        activeTeleports.put(player.getUniqueId(), task);
        task.start();
    }
    
    public void cancelTeleport(Player player) {
        TeleportTask task = activeTeleports.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    public boolean isTeleporting(Player player) {
        return activeTeleports.containsKey(player.getUniqueId());
    }
    
    public Location findSafeLocation(Location location) {
        if (!plugin.getConfigManager().isSafeTeleport()) {
            return location;
        }
        
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        for (int checkY = y; checkY < world.getMaxHeight() && checkY > world.getMinHeight(); checkY++) {
            Block feetBlock = world.getBlockAt(x, checkY, z);
            Block headBlock = world.getBlockAt(x, checkY + 1, z);
            Block groundBlock = world.getBlockAt(x, checkY - 1, z);
            
            if (isSafeBlock(feetBlock.getType()) && isSafeBlock(headBlock.getType()) && isSolidBlock(groundBlock.getType())) {
                return new Location(world, x + 0.5, checkY, z + 0.5);
            }
        }
        
        return location;
    }
    
    private boolean isSafeBlock(Material material) {
        return material == Material.AIR || !material.isSolid() || material.name().contains("WATER") || material.name().contains("LAVA");
    }
    
    private boolean isSolidBlock(Material material) {
        return material.isSolid() && material != Material.LAVA && !material.name().contains("WATER");
    }
    
    public void teleportPlayer(Player player, Location targetLocation) {
        Location safeLocation = findSafeLocation(targetLocation);
        player.teleportAsync(safeLocation).thenAccept(success -> {
            if (success) {
                plugin.getSoundManager().playTeleport(player);
                plugin.getMessageManager().sendTeleportSuccess(player);
            } else {
                plugin.getMessageManager().sendTeleportFailed(player);
            }
        });
    }
    
    public void teleportToPlayer(Player requester, Player target) {
        Location targetLocation = target.getLocation();
        teleportPlayer(requester, targetLocation);
    }
    
    public void teleportPlayerTo(Player requester, Player target) {
        Location requesterLocation = requester.getLocation();
        teleportPlayer(target, requesterLocation);
    }
          }
