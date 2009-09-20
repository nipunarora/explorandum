package explorandum;

///* 
// * 	$Id: Tournament.java,v 1.2 2007/11/14 08:07:16 johnc Exp $
// * 
// * 	Programming and Problem Solving
// *  Copyright (c) 2007 The Trustees of Columbia University
// */
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

//
public final class Tournament
{
	private GameEngine engine;
	private ArrayList<TournamentListener> listeners;
	private Logger log;
	private ArrayList<TournamentResult> results;
	private File file;

	public Tournament(GameEngine engine, File file)
	{
		this.engine = engine;
		results = new ArrayList<TournamentResult>();
		listeners = new ArrayList<TournamentListener>();
		log = new Logger(Logger.LogLevel.INFO, this.getClass());
		this.file = file;
	}

	//	
	public void run()
	{
		results.clear();
		// File []boardList = engine.getConfig().getBoardList();
		Class<Player>[] players = engine.getConfig().getClassList();
		final int nGames = engine.getConfig().getTournamentGames();
		final File boardFile = engine.getConfig().getSelectedBoard();
		// TournamentResult[] results = new TournamentResult[players.length];
		int matchNumber = 0;

		// for (int p = 0; p < players.length; p++)
		{

			TournamentResult result = play(players, boardFile, nGames);
			if (result != null)
			{
				results.add(result);
			}
			matchNumber++;
		}

		// for(int i = 0; i < results.length; i++){
		// notifyListeners(results[i].toString());
		// System.out.println(results[i]);
		// }
		// engine.removeGameListener(this);

	}

	private TournamentResult play(final Class<Player>[] players, final File boardFile, final int nGames)
	{
		for (int i = 0; i < players.length; i++)
			engine.getConfig().addActivePlayer(players[i]);

		// engine.getConfig().setActivePlayer(playerClass);
		engine.getConfig().setSelectedBoard(boardFile);

		if (engine.setUpGame())
		{ // TODO handle boolean retval
			TournamentResult result = new TournamentResult(players, boardFile.getName(), nGames, engine.getConfig().getActivePlayerNum());
			notifyListeners("Current match information: " + engine.getConfig().getActivePlayerNum() + " Players on board \"" + boardFile.getName() + "\"");
			for (int i = 0; i < nGames; i++)
			{
				notifyListeners("Playing game number " + (i + 1) + " of " + nGames);
				log.info("Playing game number " + (i + 1) + " of " + nGames);
				engine.setUpGame();
				while (engine.step())
					continue;
				result.scores[i] = engine.getScores();

				writeToFile(result, i);
			}
			return result;
		}
		return null;
	}

	/**
	 * 
	 */
	private void writeToFile(TournamentResult r, int i)
	{
		try
		{
			FileWriter f = new FileWriter(file, true);

			f.write(r.toString(i) + "\n");
			// f.write("\n");
			f.close();
			log.info("Written to file");
		} catch (IOException e)
		{
			System.err.println("Failed printing to output file:" + file);
		}
	}

	public ArrayList<TournamentResult> getResults()
	{
		return results;
	}

	//	
	public void addTournamentListener(TournamentListener l)
	{
		listeners.add(l);
	}

	public void notifyListeners(String update)
	{
		Iterator<TournamentListener> it = listeners.iterator();
		while (it.hasNext())
		{
			it.next().tournamentUpdated(update);
		}
	}

	static public interface TournamentListener
	{
		public void tournamentUpdated(final String update);
	}

	//	
	public class TournamentResult
	{
		final Class<Player>[] players;
		final String boardName;
		Integer[][] scores;

		TournamentResult(final Class<Player>[] players, final String boardName, final int numGames, final int numPlayers)
		{
			this.players = players;
			this.boardName = boardName;
			scores = new Integer[numGames][numPlayers];
		}

		public String toString(int i)
		{
			String ret = "";
			for (int j = 0; j < players.length; j++)
			{
				try
				{
					ret += "\n" + players[j].newInstance().name();
				} catch (Exception e)
				{
					ret += "\n" + players[j].toString();
				}

				ret += "\t" + scores[i][j];
			}
			return ret;
		}

		public String toString()
		{
			String ret = boardName;
			ret += "\t Range:" + engine.getConfig().getRange();
			ret += "\t Rounds:" + engine.getConfig().getMaxRounds();

			for (int j = 0; j < players.length; j++)
			{
				int sum = 0;
				try
				{
					ret += "\n" + players[j].newInstance().name();
				} catch (Exception e)
				{
					ret += "\n" + players[j].toString();
				}

				for (int i = 0; i < scores.length; i++)
				{
					ret += "\t" + scores[i][j];
					sum += scores[i][j];
				}
				ret += "\t" + ((double) sum / (double) scores.length);
			}
			return ret;
		}
	}

	public static final void main(String args[])
	{
		if (args.length != 5)
		{
			System.err.println("Usage: Tournament <config file> <board> <range> <num rounds> <num games>");
			System.exit(-1);
		}
		GameEngine engine = new GameEngine(args[0]);
		GameConfig config = engine.getConfig();

		File boardFile = new File(args[1]);
		// File boardFile = new File(config. + File.separator+args[1]);
		if (!boardFile.exists())
		{
			System.err.println("Board file: " + boardFile + " does not exist.");
			System.exit(-1);
		}
		config.setSelectedBoard(boardFile);
		config.setRange(Integer.parseInt(args[2]));
		config.setMaxRounds(Integer.parseInt(args[3]));
		config.setTournamentGames(Integer.parseInt(args[4]));

		File file = new File("tournament_" + boardFile.getName() + "_" + args[2] + "_" + args[3] + "_" + args[4] + ".txt");
		// file.delete();
		try
		{
			file.createNewFile();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// file = new File("tournament_" + boardFile.getName() + "_" + args[2] +
		// "_" + args[3] + "_" + args[4] + ".txt");
		Tournament t = new Tournament(engine, file);
		t.run();
		ArrayList<TournamentResult> results = t.getResults();
		for (TournamentResult r : results)
			System.out.println(r);

		try
		{
			FileWriter f = new FileWriter(file, false);

			for (TournamentResult r : results)
				f.write(r.toString() + "\n");
			// f.write("\n");
			f.close();
		} catch (IOException e)
		{
			System.err.println("Failed printing to output file:" + file);
		}
	}
}
