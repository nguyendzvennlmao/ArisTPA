package me.aris.aristpa.gui;

import me.aris.aristpa.ArisTPA;
import me.aris.aristpa.models.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.Arrays;
import java.util.List;

public class TPAGUI implements Listener {
    private ArisTPA plugin;
    
    public TPAGUI(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    public void openRequestGUI(Player sender, Player target) {
        if (!plugin.getConfigManager().isGUIEnabled()) return;
        if (!plugin.getTPAManager().isGUIEnabled(sender)) return;
        
        Inventory gui = Bukkit.createInventory(null, 27, "§8Confirm Request");
        
        ItemStack cancelIcon = createGuiItem(Material.RED_STAINED_GLASS_PANE, "§4Cancel", 
            Arrays.asList("§7Click to cancel this request"));
        gui.setItem(10, cancelIcon);
        
        ItemStack locationIcon = createGuiItem(Material.COMPASS, "§aLocation", 
            Arrays.asList("§f" + target.getWorld().getName(), 
                "§fX: " + target.getLocation().getBlockX(),
                "§fY: " + target.getLocation().getBlockY(),
                "§fZ: " + target.getLocation().getBlockZ()));
        gui.setItem(12, locationIcon);
        
        ItemStack playerIcon = createPlayerHead(target, "§aPlayer", 
            Arrays.asList("§f" + target.getName()));
        gui.setItem(13, playerIcon);
        
        String flyingStatus = target.isFlying() || target.isGliding() ? "§aYes" : "§cNo";
        ItemStack flyIcon = createGuiItem(Material.FEATHER, "§aFlying", 
            Arrays.asList("§f" + flyingStatus));
        gui.setItem(14, flyIcon);
        
        ItemStack confirmIcon = createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§aConfirm", 
            Arrays.asList("§7Click to send a teleport request", "§7to §f" + target.getName()));
        gui.setItem(16, confirmIcon);
        
        sender.openInventory(gui);
    }
    
    public void openAcceptGUI(Player target, TeleportRequest request) {
        if (!plugin.getConfigManager().isGUIEnabled()) return;
        if (!plugin.getTPAManager().isGUIEnabled(target)) return;
        
        Inventory gui = Bukkit.createInventory(null, 27, "§8Accept Request");
        
        ItemStack cancelIcon = createGuiItem(Material.RED_STAINED_GLASS_PANE, "§4Deny", 
            Arrays.asList("§7Click to deny this request"));
        gui.setItem(10, cancelIcon);
        
        Player sender = request.getSender();
        ItemStack locationIcon = createGuiItem(Material.COMPASS, "§aLocation", 
            Arrays.asList("§f" + sender.getWorld().getName(),
                "§fX: " + sender.getLocation().getBlockX(),
                "§fY: " + sender.getLocation().getBlockY(),
                "§fZ: " + sender.getLocation().getBlockZ()));
        gui.setItem(12, locationIcon);
        
        ItemStack playerIcon = createPlayerHead(sender, "§aPlayer", 
            Arrays.asList("§f" + sender.getName()));
        gui.setItem(13, playerIcon);
        
        String flyingStatus = sender.isFlying() || sender.isGliding() ? "§aYes" : "§cNo";
        ItemStack flyIcon = createGuiItem(Material.FEATHER, "§aFlying", 
            Arrays.asList("§f" + flyingStatus));
        gui.setItem(14, flyIcon);
        
        ItemStack confirmIcon = createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§aAccept", 
            Arrays.asList("§7Click to accept teleport request", "§7from §f" + sender.getName()));
        gui.setItem(16, confirmIcon);
        
        target.openInventory(gui);
    }
    
    private ItemStack createGuiItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createPlayerHead(Player player, String name, List<String> lore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(name);
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (title.equals("§8Confirm Request") || title.equals("§8Accept Request")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            if (title.equals("§8Confirm Request")) {
                if (event.getSlot() == 16) {
                    player.closeInventory();
                    player.performCommand("tpa " + getTargetNameFromGUI(event));
                } else if (event.getSlot() == 10) {
                    player.closeInventory();
                }
            } else if (title.equals("§8Accept Request")) {
                if (event.getSlot() == 16) {
                    player.closeInventory();
                    player.performCommand("tpaccept " + getSenderNameFromGUI(event));
                } else if (event.getSlot() == 10) {
                    player.closeInventory();
                    player.performCommand("tpdeny " + getSenderNameFromGUI(event));
                }
            }
        }
    }
    
    private String getTargetNameFromGUI(InventoryClickEvent event) {
        ItemStack item = event.getInventory().getItem(13);
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            if (lore != null && !lore.isEmpty()) {
                return lore.get(0).replace("§f", "");
            }
        }
        return "";
    }
    
    private String getSenderNameFromGUI(InventoryClickEvent event) {
        ItemStack item = event.getInventory().getItem(13);
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            if (lore != null && !lore.isEmpty()) {
                return lore.get(0).replace("§f", "");
            }
        }
        return "";
    }
                                               }
