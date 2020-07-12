package me.zabzabdoda.TTT;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreBoard {

	private static Scoreboard board;

	
    public static void setScoreBoard() {
    	
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("teams", "dummy", "teams");
        
        Objective sideBarObjective = board.registerNewObjective("TTT", "dummy", ChatColor.RED + "TTT");
        sideBarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        teamSetup(obj);
        setupSideBar(sideBarObjective);
    }
    
    
    public static Team getPlayerTeam(Player player) {    	
    	return board.getPlayerTeam(player);
    }
    
    public static void setupSideBar(Objective obj) {

    	Team worldborderposition = board.registerNewTeam("gamestatus");
    	worldborderposition.addEntry(ChatColor.AQUA + "");
        Score worldborderpositionscore = obj.getScore(ChatColor.AQUA + "");
        worldborderpositionscore.setScore(1);
        
    	Team worldborderCenter = board.registerNewTeam("time");
    	worldborderCenter.addEntry(ChatColor.BLACK + "");
        Score worldborderCenterscore = obj.getScore(ChatColor.BLACK + "");
        worldborderCenterscore.setScore(2);
    }
    
    public static void scoreboardrefresh(Player player, String gameStatus, String minutes, String seconds) {
    	Scoreboard playerBoard = player.getScoreboard();
    	playerBoard.getTeam("gamestatus").setPrefix(ChatColor.GREEN + gameStatus);
    	playerBoard.getTeam("time").setPrefix(ChatColor.GRAY + "" + minutes + ":" + seconds);
    }
    
    public static void joinScoreBoard(Player player) {
    	player.setScoreboard(board);
    }
    
    public static void teamSetup(Objective obj) {
        Team InnocentTeam = board.registerNewTeam("Innocent");
        InnocentTeam.setColor(ChatColor.GREEN);
        Score InnocentScore = obj.getScore(ChatColor.GREEN + "Innocent");
        InnocentScore.setScore(0);
        InnocentTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        
        Team TraitorTeam = board.registerNewTeam("Traitor");
        TraitorTeam.setColor(ChatColor.RED);
        Score TraitorScore = obj.getScore(ChatColor.RED + "Traitor");
        TraitorScore.setScore(0);
        TraitorTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        
        Team DetectiveTeam = board.registerNewTeam("Detective");
        DetectiveTeam.setColor(ChatColor.BLUE);
        Score DetectiveScore = obj.getScore(ChatColor.BLUE + "Detective");
        DetectiveScore.setScore(0);
        DetectiveTeam.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        

    }
    
    
    public static void joinInnocent(Player player) {
    	Scoreboard board = player.getScoreboard();
    	board.getTeam("Innocent").addPlayer(player);
    }
    
    public static void joinTraitor(Player player) {
    	Scoreboard board = player.getScoreboard();
    	board.getTeam("Traitor").addPlayer(player);
    }
    
    public static void joinDetective(Player player) {
    	Scoreboard board = player.getScoreboard();
    	board.getTeam("Detective").addPlayer(player);
    }

    
    public static void leaveTeam(Player player, Team team) {
    	Scoreboard board = player.getScoreboard();
    	if(team != null) {
    		team.removePlayer(player);
    	}
    }
    
}
