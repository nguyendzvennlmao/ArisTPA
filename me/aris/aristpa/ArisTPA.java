package me.aris.aristpa;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ArisTPA extends JavaPlugin implements CommandExecutor {

    private final Map<UUID, UUID> tpaReq = new HashMap<>();
    private final Map<UUID, UUID> tpaHereReq = new HashMap<>();
    private final Set<UUID> tpAuto = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("tpa").setExecutor(this);
        getCommand("tpahere").setExecutor(this);
        getCommand("tpaccept").setExecutor(this);
        getCommand("tpdeny").setExecutor(this);
        getCommand("tpauto").setExecutor(this);
        getCommand("dtpa").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        switch (cmd.getName().toLowerCase()) {
            case "tpa" -> handleReq(p, args, false);
            case "tpahere" -> handleReq(p, args, true);
            case "tpaccept" -> accept(p);
            case "tpdeny" -> deny(p);
            case "tpauto" -> toggleAuto(p);
            case "dtpa" -> {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    long start = System.currentTimeMillis();
                    reloadConfig();
                    sendMsg(p, "reload-success", "%ms%", String.valueOf(System.currentTimeMillis() - start));
                }
            }
        }
        return true;
    }

    private void handleReq(Player p, String[] args, boolean here) {
        if (args.length == 0) { sendMsg(p, here ? "tpahere-usage" : "tpa-usage"); return; }
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) { sendMsg(p, "player-not-found", "%player%", args[0]); return; }
        if (target == p) { sendMsg(p, "self-teleport"); return; }

        if (tpAuto.contains(target.getUniqueId())) {
            sendMsg(target, "tp-auto-accepted", "%player%", p.getName());
            startTeleport(here ? target : p, here ? p : target);
        } else {
            if (here) tpaHereReq.put(target.getUniqueId(), p.getUniqueId());
            else tpaReq.put(target.getUniqueId(), p.getUniqueId());
            
            sendMsg(p, here ? "sent-here-request" : "sent-request", "%player%", target.getName());
            sendMsg(target, here ? "receive-here-request" : "receive-request", "%player%", p.getName());
        }
    }

    private void accept(Player p) {
        UUID sID = tpaReq.remove(p.getUniqueId());
        boolean isHere = false;
        if (sID == null) { sID = tpaHereReq.remove(p.getUniqueId()); isHere = true; }
        
        if (sID == null) { sendMsg(p, "no-requests-found"); return; }
        Player s = Bukkit.getPlayer(sID);
        if (s != null) {
            sendMsg(p, "accepted-teleport");
            sendMsg(s, "request-accepted");
            startTeleport(isHere ? p : s, isHere ? s : p);
        }
    }

    private void deny(Player p) {
        UUID sID = tpaReq.remove(p.getUniqueId());
        if (sID == null) sID = tpaHereReq.remove(p.getUniqueId());
        if (sID != null) {
            sendMsg(p, "cancelled-request", "%player%", Bukkit.getOfflinePlayer(sID).getName());
            Player s = Bukkit.getPlayer(sID);
            if (s != null) sendMsg(s, "cancelled-request-sender", "%player%", p.getName());
        } else {
            sendMsg(p, "no-requests-found");
        }
    }

    private void toggleAuto(Player p) {
        if (tpAuto.add(p.getUniqueId())) sendMsg(p, "tp-auto-on");
        else { tpAuto.remove(p.getUniqueId()); sendMsg(p, "tp-auto-off"); }
    }

    private void startTeleport(Player who, Player to) {
        int time = getConfig().getInt("teleport.countdown", 5);
        Location startPos = who.getLocation().clone();
        double range = getConfig().getDouble("teleport.allowed-walk-range", 0.1);

        new TimerTask() {
            int count = time;
            @Override
            public void run() {
                if (!who.isOnline() || !to.isOnline()) { this.cancel(); return; }
                if (who.getLocation().distance(startPos) > range) {
                    sendMsg(who, "teleport-cancelled-movement");
                    who.playSound(who.getLocation(), Sound.valueOf(getConfig().getString("sounds.cancel-sound")), 1, 1);
                    this.cancel(); return;
                }
                if (count > 0) {
                    sendMsg(who, "teleport-countdown", "%time%", String.valueOf(count));
                    who.playSound(who.getLocation(), Sound.valueOf(getConfig().getString("sounds.countdown-tick")), 1, 1);
                    count--;
                } else {
                    who.getScheduler().run(ArisTPA.this, (t) -> who.teleport(to.getLocation()), null);
                    sendMsg(who, "teleport-success");
                    who.playSound(who.getLocation(), Sound.valueOf(getConfig().getString("sounds.teleport-success")), 1, 1);
                    this.cancel();
                }
            }
        }.runTimer(who, 0L, 20L);
    }

    private abstract class TimerTask {
        private org.bukkit.scheduler.BukkitTask pTask;
        private io.papermc.paper.threadedregionscheduler.ScheduledTask fTask;
        public void runTimer(Player p, long d, long pr) {
            try { fTask = p.getScheduler().runAtFixedRate(ArisTPA.this, (t) -> run(), null, d, pr); }
            catch (Throwable e) { pTask = Bukkit.getScheduler().runTaskTimer(ArisTPA.this, this::run, d, pr); }
        }
        public void cancel() { if (fTask != null) fTask.cancel(); if (pTask != null) pTask.cancel(); }
        public abstract void run();
    }

    public void sendMsg(Player p, String key, String... rep) {
        String chat = getConfig().getString("messages." + key);
        String bar = getConfig().getString("messages." + key + "-actionbar");
        
        if (chat != null) {
            for (int i = 0; i < rep.length; i += 2) chat = chat.replace(rep[i], rep[i+1]);
            chat = ChatColor.translateAlternateColorCodes('&', chat);
            if (chat.contains("[CLICK TO ACCEPT]")) {
                String[] parts = chat.split("\\[CLICK TO ACCEPT\\]");
                TextComponent msg = new TextComponent(parts[0]);
                TextComponent click = new TextComponent("§b§l[CLICK TO ACCEPT]");
                click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
                msg.addExtra(click);
                if (parts.length > 1) msg.addExtra(new TextComponent(parts[1]));
                p.spigot().sendMessage(msg);
            } else {
                p.sendMessage(chat);
            }
        }
        if (bar != null) {
            for (int i = 0; i < rep.length; i += 2) bar = bar.replace(rep[i], rep[i+1]);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', bar)));
        }
    }
  }
