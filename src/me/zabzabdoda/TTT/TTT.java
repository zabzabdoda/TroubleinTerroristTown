package me.zabzabdoda.TTT;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class TTT extends JavaPlugin implements Listener {
	public static Server server;
	private World currentWorld;
	private Inventory deadInv;
	private String gameStatus = "Preparing";
	private int timeSeconds;
	private ArrayList<Player> traitorChatRoom;

	static {
		server = Bukkit.getServer();
	}

	public TTT() {
		
	}

	public void onEnable() {
		TTT.server.getPluginManager().registerEvents((Listener) this, (Plugin) this);
		traitorChatRoom = new ArrayList<Player>();
		currentWorld = Bukkit.getWorlds().get(0);
		ScoreBoard.setScoreBoard();
		scoreBoardRefresh();
		timerStart();
	}

	public void onDisable() {
		TTT.server.broadcastMessage("TTT plugin is ending");
	}
	
	public void timerStart() {
        new BukkitRunnable() {
            @Override
            public void run() {
            	timeSeconds++;
            }
        }.runTaskTimer(this, 0, 20);
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		ScoreBoard.joinScoreBoard(e.getPlayer());
		e.getPlayer().setPlayerListName(ChatColor.MAGIC + "");
		ItemStack filler = new ItemStack(Material.BARRIER,64);
		ItemMeta meta = filler.getItemMeta();
		meta.setDisplayName(ChatColor.MAGIC + "");
		filler.setItemMeta(meta);
		for(int i = 14; i < 36; i++) {
			e.getPlayer().getInventory().setItem(i,filler);
		}
		for(int i = 5; i < 9; i++) {
			e.getPlayer().getInventory().setItem(i,filler);
		}
	}
	
	public void scoreBoardRefresh() {
        new BukkitRunnable() {
            @Override
            public void run() {
            	for(Player player : Bukkit.getServer().getOnlinePlayers()) {
            		int minutes = timeSeconds/60;
            		int seconds = timeSeconds%60;
            		String sMinutes = minutes + "";
            		String sSeconds = seconds + "";
            		if(minutes < 10) {
            			sMinutes = "0" + minutes;
            		}
            		if(seconds < 10) {
            			sSeconds = "0" + seconds;
            		}
            		ScoreBoard.scoreboardrefresh(player, gameStatus, sMinutes, sSeconds);
            		
            	}
            }
        }.runTaskTimer(this, 80, 20);
	}
	
	@EventHandler
	public void interactEntity(PlayerInteractAtEntityEvent e) {
		//
		if(e.getRightClicked() instanceof ArmorStand) {
			e.setCancelled(true);
			ArmorStand as = (ArmorStand) e.getRightClicked();
			if(as.getHelmet().getType().equals(Material.PLAYER_HEAD)) {
				System.out.println(as.getName());
				//Open bomb menu
				Inventory inventory = DeadInventorySetup(e.getPlayer(),as.getName());
				e.getPlayer().openInventory(inventory);
			}
		}
	}
	
	public Inventory DeadInventorySetup(Player player, String name) {
		deadInv = Bukkit.createInventory(player, 9);
		String playerNameString = name.substring(0,name.indexOf(','));
		String gunName = name.substring(name.indexOf(',')+1,name.lastIndexOf(','));
		ItemStack playerName = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta playerNameMeta = playerName.getItemMeta();
		playerNameMeta.setDisplayName(playerNameString);
		playerName.setItemMeta(playerNameMeta);
		deadInv.setItem(3, playerName);
		
		ItemStack KilledBy = new ItemStack(Material.DIAMOND_PICKAXE, 1);
		ItemMeta KilledByMeta = KilledBy.getItemMeta();
		KilledByMeta.setDisplayName(ChatColor.BLUE + gunName);
		KilledBy.setItemMeta(KilledByMeta);
		deadInv.setItem(4, KilledBy);
		
		ItemStack TOD = new ItemStack(Material.CLOCK, 1);
		ItemMeta TODMeta = TOD.getItemMeta();
		TODMeta.setDisplayName(ChatColor.GREEN + "Killed" + name.substring(name.lastIndexOf(',')+1) + " ago");
		TOD.setItemMeta(TODMeta);
		deadInv.setItem(5, TOD);
		return deadInv;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player && cmd.getName().equalsIgnoreCase("ttt")) {
			Player player = (Player) sender;
			if (args.length == 0) {
				sender.sendMessage("Use - /ttt help - for help about the plugin.");
				return true;
			}else if (args.length == 1) {
				if(args[0].equalsIgnoreCase("start")) {
					resetGame();
				}

			}else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("join")) {
					if(args[1].equalsIgnoreCase("Innocent")) {
						ScoreBoard.joinInnocent(player);
						return true;
					}else if(args[1].equalsIgnoreCase("Traitor")) {
						ScoreBoard.joinTraitor(player);
						return true;
					}else if(args[1].equalsIgnoreCase("Detective")) {
						ScoreBoard.joinDetective(player);
						return true;
					}
				}
			}else if(args.length == 3) {
				
			}
			sender.sendMessage(ChatColor.RED + "Incorrrect usage, type '/ttt help' for help about the plugin");
			return false;
		}else if(sender instanceof Player && cmd.getName().equalsIgnoreCase("chat")) {
			if(args.length == 1) {
				Player player = (Player) sender;
				if(player.getScoreboard().getPlayerTeam(player) != null) {
					if(args[0].equalsIgnoreCase("traitor") && player.getScoreboard().getPlayerTeam(player).getName().equalsIgnoreCase("Traitor")) {
						traitorChatRoom.add((Player) sender);
						sender.sendMessage("You are now in " + ChatColor.RED + "[Traitor]" + ChatColor.WHITE + " chat.");
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("global")) {
					traitorChatRoom.remove((Player) sender);
					sender.sendMessage("You are now in " + ChatColor.BLUE + "[Global]" + ChatColor.WHITE + " chat.");
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void resetGame() {
		
	}
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		if(traitorChatRoom.contains(e.getPlayer())) {
			e.setCancelled(true);
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				if(p.getScoreboard().getPlayerTeam(p) != null && e.getPlayer().getScoreboard().getPlayerTeam(p).getName().equalsIgnoreCase("Traitor")) {
					p.sendMessage(ChatColor.RED + "[Traitor Chat]" + ChatColor.WHITE + " <" + e.getPlayer().getDisplayName() + "> " + e.getMessage());
				}
			}
		}
	}
	
	@EventHandler
	public void arrowLand(ProjectileHitEvent e) {
		if(e.getHitEntity() instanceof Player) {
			Player player = (Player) e.getHitEntity();
			//player.damage(M16.getDamage());
			
		}
		e.getEntity().remove();
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e) {
		Player player = (Player)e.getEntity();
		Location loc = new Location(player.getWorld(),player.getLocation().getX(),player.getLocation().getY()-1.5,player.getLocation().getZ());
    	
		ArmorStand as = player.getWorld().spawn(loc, ArmorStand.class);
    	ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    	SkullMeta sm = (SkullMeta) skull.getItemMeta();
    	sm.setOwner(player.getName());
    	skull.setItemMeta(sm);
		as.setHelmet(skull);
		as.setAI(false);
		as.setInvulnerable(true);
		as.setGravity(false);
		as.setVisible(false);
		String weaponName;
		if(e.getDeathMessage().contains("[")) {
			weaponName = e.getDeathMessage().substring(e.getDeathMessage().indexOf('[')+1,e.getDeathMessage().lastIndexOf(']'));
		}else {
			weaponName = "Unknown";
		}
		Team team = player.getScoreboard().getPlayerTeam(player);
		String playername = player.getName();
		if(team.getName().equals("Innocent")) {
			playername = ChatColor.GREEN + player.getName();
		}else if(team.getName().equals("Detective")) {
			playername = ChatColor.BLUE + player.getName();
		}else if(team.getName().equals("Traitor")) {
			playername = ChatColor.RED + player.getName();
		}
		as.setCustomName(playername + "," + weaponName + ", 00:00");
		as.setCustomNameVisible(true);
        new BukkitRunnable() {
        	int Timer;
        	@Override
            public void run() {
        		Timer++;
        		int minutes = Timer/60;
        		int seconds = Timer%60;
        		String sMinutes = minutes + "";
        		String sSeconds = seconds + "";
        		if(minutes < 10) {
        			sMinutes = "0" + minutes;
        		}
        		if(seconds < 10) {
        			sSeconds = "0" + seconds;
        		}
            	as.setCustomName(as.getCustomName().substring(0,as.getCustomName().lastIndexOf(',')+1) + " " + sMinutes + ":" + sSeconds);
        	}
        }.runTaskTimer(this, 0, 20);
	}
	
	
	@SuppressWarnings("deprecation")
	public Team getTeam(Player player) {
		Team team = null;
		for (Team t : player.getScoreboard().getTeams()) {
			if (t.getPlayers().contains(player)) {
				team = t;
			}
		}

		return team;
	}
	
	
	@EventHandler
	public void onInventory(InventoryInteractEvent event) {
			event.setResult(Result.DENY);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) (event.getWhoClicked());
		int slot = event.getSlot();
		if (event.getInventory().equals(deadInv)) {
			player.closeInventory();
			event.setCancelled(true);
		}

	}
	
	
	 private boolean getLookingAt(Player player, Player player1)
	  {
	    Location eye = player.getEyeLocation();
	    Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
	    double dot = toEntity.normalize().dot(eye.getDirection());
	   
	    return dot > 0.99D;
	  }
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		System.out.println("Drop");
		e.setCancelled(true);
	}
	
	private static int randomNum(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public void helpMenu(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Help Menu for LootGenerator plugin");
		sender.sendMessage(ChatColor.YELLOW + "------------------------------");
		sender.sendMessage(ChatColor.YELLOW + "Commands:");
		sender.sendMessage(ChatColor.YELLOW + "/ttt help " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Shows you this menu.");
		sender.sendMessage(ChatColor.YELLOW + "/ttt start " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Starts a game of TTT.");
		sender.sendMessage(ChatColor.YELLOW + "/ttt roundreset " + ChatColor.GRAY + "-" + ChatColor.WHITE + " Resets the round.");
		
	}
}
