package net.pl3x.shutdownnotice;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardController {
	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective objective;
	private Score score;

	public ScoreboardController() {
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("ShutdownNotice", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Shutdown");
	}

	public ScoreboardManager getScoreboardManager() {
		return manager;
	}

	public Scoreboard getScoreboard() {
		return board;
	}

	public Objective getObjective() {
		return objective;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}
}
