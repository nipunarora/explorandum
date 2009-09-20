/* 
 * 	$Id: GameEngine.java,v 1.6 2007/11/28 16:30:47 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import explorandum.Board;
import explorandum.GameConfig;
import explorandum.GameEngine;
import explorandum.GameListener;
import explorandum.PlayerWrapper;
import explorandum.Board.BoardSanityException;
import explorandum.GameListener.GameUpdateType;
import explorandum.Tournament.TournamentResult;
import explorandum.ui.GUI;

public final class GameEngine implements GameConstants
{
	private GameConfig config;
	private Board board;
	// private PlayerWrapper player;
	private int round;
	private ArrayList<GameListener> gameListeners;
	private ArrayList<PlayerWrapper> explorers;
	private Random random;
	private int nExplorers;
	private int roundLimit;
	private Logger log;

	public GameEngine(String configFile)
	{
		config = new GameConfig(configFile);
		gameListeners = new ArrayList<GameListener>();
		board = new Board(10, 10, config.getRange());
		random = new Random(config.getSeed());
		log = new Logger(config.getSimulatorLogLevel(), this.getClass());
	}

	public void addGameListener(GameListener l)
	{
		gameListeners.add(l);
	}

	public int getCurrentRound()
	{
		return round;
	}

	public GameConfig getConfig()
	{
		return config;
	}

	public boolean setUpGame()
	{
		try
		{
			board.setRange(config.getRange());
			board.load(config.getSelectedBoard());
		} catch (IOException e)
		{
			log.error("Exception: " + e);
			return false;
		} catch (BoardSanityException e)
		{
			log.error("Exception: " + e);
			return false;
		}

		// config.setNumExplorers(a)
		// Default to startable locations supported by board.
		nExplorers = board.countStartable();

		if (nExplorers > config.getActivePlayerNum())
			nExplorers = config.getActivePlayerNum();

		config.setNumExplorers(nExplorers);

		// Round limit
		roundLimit = config.getMaxRounds();

		// Get list of startable cells
		Cell[] startable = board.getStartCells(board.countStartable());
		ArrayList<Cell> startableCells = new ArrayList<Cell>();

		// shuffle startable cells
		for (int i = 0; i < startable.length; i++)
		{
			int r = random.nextInt(startableCells.size() + 1);
			startableCells.add(r, startable[i]);
		}

		explorers = new ArrayList<PlayerWrapper>(nExplorers);

		if (config.getSingleStart())
		{
			//include all explorers, default was to limit explorers based on number of startable cells.
			nExplorers = config.getActivePlayerNum();
			config.setNumExplorers(nExplorers);
			
			int r = random.nextInt(startableCells.size());
			
			for (int i = 0; i < nExplorers; i++)
			{
				PlayerWrapper p = new PlayerWrapper(config, i, config.getMaxRounds(), nExplorers, config.getRange(), startableCells.get(r));
				explorers.add(i, p);
				// startableCells.get(i).addExplorer(p.getExplorer());
				board.moveExplorer(p.getExplorer(), startableCells.get(r));
			}
		} else
		{
			// Place explorers on starting positions
			for (int i = 0; i < nExplorers; i++)
			{
				PlayerWrapper p = new PlayerWrapper(config, i, config.getMaxRounds(), nExplorers, config.getRange(), startableCells.get(i));
				explorers.add(i, p);
				// startableCells.get(i).addExplorer(p.getExplorer());
				board.moveExplorer(p.getExplorer(), startableCells.get(i));
			}
		}

		round = 0;
		notifyListeners(GameUpdateType.STARTING);
		return true;
	}

	public ArrayList<PlayerWrapper> getPlayers()
	{
		return explorers;
	}

	public Board getBoard()
	{
		return board;
	}

	public boolean step()
	{
		ArrayList<PlayerWrapper> movesToResolve = new ArrayList<PlayerWrapper>();

		// Move players if they are supposed to move in this round
		for (Iterator iterator = explorers.iterator(); iterator.hasNext();)
		{
			PlayerWrapper p = (PlayerWrapper) iterator.next();
			MoveWrapper lastMove = p.getLastMove();

			if (lastMove.canMoveNow(round))
			{
				// Enqueue player for movement
				movesToResolve.add(p);

				// Set move completed
				lastMove.setMoved();
			}
		}

		processMove(movesToResolve);

		// Generate next moves if players have moved by now
		for (Iterator iterator = explorers.iterator(); iterator.hasNext();)
		{
			PlayerWrapper p = (PlayerWrapper) iterator.next();
			MoveWrapper lastMove = p.getLastMove();

			if (lastMove.hasMoved() && !p.getStrikeout())
			{
				// Generate surround cells based on visibility//

				// central cell
				Cell cell = board.getExplorer(p.getExplorer());
				Point loc = cell.getLocation();
				Boolean StepStatus = cell.isSteppedOn();
				// list of cells within visibility range
				ArrayList<Cell> cells = getVisibleCells(cell, loc);

				// list of nearby cells not visible but can be moved into
				// ArrayList<Cell> moreCells = new ArrayList<Cell>();
				for (int i = -1; i <= 1; i++)
				{
					for (int j = -1; j <= 1; j++)
					{
						int x = cell.getLocation().x + i;
						int y = cell.getLocation().y + j;
						if (board.inBounds(x, y))
						{
							Cell extraCell = board.getCell(x, y);
							if (!cells.contains(extraCell))
							{
								cells.add(extraCell);
							}
						}
					}
				}

				// Information to be passed to the player
				
				Integer[] terrain = new Integer[cells.size()];
				Boolean[] hasExplorer = new Boolean[cells.size()];
				Integer[][] otherExplorers = new Integer[cells.size()][explorers.size()];
				Point[] offsets = new Point[cells.size()];
				Point playerStart = p.getStart().getLocation();
				
				// Fill arrays
				for (int i = 0; i < cells.size(); i++)
				{
					// cell to put in array
					Cell c = cells.get(i);

					// terrain of cell
					terrain[i] = c.getCellTerrain();

					
					
					// explorers in cell
					hasExplorer[i] = c.hasExplorers();

					ArrayList<Explorer> exps = c.getExplorers();
					for (int j = 0; j < exps.size(); j++)
					{
						Explorer ex = exps.get(j);
						otherExplorers[i][j] = ex.getId();
					}

					// offset of the cell, from the player's starting cell
					offsets[i] = new Point(c.getLocation().x - playerStart.x, c.getLocation().y - playerStart.y);
				}

				Point currentLocation = new Point(cell.getLocation());
				currentLocation.x -= playerStart.x;
				currentLocation.y -= playerStart.y;

				// Query player for next move
				// Next move saved automatically in player wrapper
				p.nextMove(currentLocation, offsets, hasExplorer, otherExplorers, terrain, round,StepStatus);

			}
		}

		round++;
		if (round >= roundLimit)
		{
			notifyListeners(GameUpdateType.GAMEOVER);
			return false;
		}

		notifyListeners(GameUpdateType.MOVEPROCESSED);
		return true;
	}

	/**
	 * Generate a list of all visible cells in range
	 * 
	 * @param cell
	 * @param loc
	 * @return
	 */
	private ArrayList<Cell> getVisibleCells(Cell cell, Point loc)
	{
		ArrayList<Cell> cells = new ArrayList<Cell>();
		int range = config.getRange();

		// loop through all cells in RANGE
		for (int i = -range; i <= range; i++)
		{
			for (int j = -range; j <= range; j++)
			{
				if (board.inBounds(loc.x + i, loc.y + j))
				{
					// get the cell to check
					Cell c = board.getCell(loc.x + i, loc.y + j);

					// check if cell within range
					if (cell.getCenter().distance(c.getCenter()) <= range)
					{
						// Add to list checking visibility
						addCell(cells, c, cell);
					}
				}
			}
		}
		return cells;
	}

	/**
	 * Check cell visibility and add to cell list provided
	 * 
	 * @param loc
	 * @param cells
	 * @param x
	 */
	private void addCell(ArrayList<Cell> cells, Cell to, Cell from)
	{
		if (isCellVisible(from, to))
			cells.add(to);
	}

	class CellConflict
	{
		private ArrayList<Cell> cells;
		private ArrayList<Explorer> explorers;

		public CellConflict()
		{
			cells = new ArrayList<Cell>();
			explorers = new ArrayList<Explorer>();
		}

		public void add(Cell cell, Explorer explorer)
		{
			cells.add(cell);
			explorers.add(explorer);
		}

		public boolean contains(Cell c)
		{
			for (Cell t : cells)
			{
				if (t.getLocation().x == c.getLocation().x && t.getLocation().y == c.getLocation().y)
				{
					return true;
				}
			}

			return false;
		}

		public Explorer getExplorer(Cell c)
		{
			for (int i = 0; i < cells.size(); i++)
			{
				Cell t = cells.get(i);
				if (t.getLocation().x == c.getLocation().x && t.getLocation().y == c.getLocation().y)
				{
					return explorers.get(i);
				}
			}

			return null;
		}

		public Boolean cellContainsExplorer(Cell c, Explorer e)
		{
			for (int i = 0; i < cells.size(); i++)
			{
				Cell t = cells.get(i);
				if (t.getLocation().x == c.getLocation().x && t.getLocation().y == c.getLocation().y && explorers.get(i).getId() == e.getId())
				{
					return true;
				}
			}
			return false;
		}

		public ArrayList<Explorer> getExplorers(Cell c)
		{
			ArrayList<Explorer> exps = new ArrayList<Explorer>();

			for (int i = 0; i < cells.size(); i++)
			{
				Cell t = cells.get(i);
				if (t.getLocation().x == c.getLocation().x && t.getLocation().y == c.getLocation().y)
				{
					exps.add(explorers.get(i));
				}
			}

			return exps;
		}

		public void clear()
		{
			explorers.clear();
			cells.clear();
		}

		public int size()
		{
			return cells.size();
		}
	}

	/**
	 * 
	 * @param ant
	 * @return true if the ant lives, false if the ant dies.
	 */
	private boolean processMove(ArrayList<PlayerWrapper> player)
	{
		ArrayList<Cell> allCells = new ArrayList<Cell>();
		CellConflict conflictCells = new CellConflict();

		boolean ret = true;

		for (PlayerWrapper p : player)
		{
			try
			{
				Explorer e = p.getExplorer();
				Cell cell = board.getExplorer(e);
				Point loc = cell.getLocation();
				MoveWrapper move = p.getLastMove();

				if (move == null)
				{
					move = new MoveWrapper(STAYPUT, 0);
				}

				// Process this move only if explorer is actually moving
				if (move.get_action() != STAYPUT || round == 0)
				{
					int new_x = loc.x + _dx[move.get_action()];
					int new_y = loc.y + _dy[move.get_action()];

					log.debug("Moved to (" + new_x + "," + new_y + ")");

					// check bounds
					if (!board.inBounds(new_x, new_y))
						throw new Exception("Explorer caught attempting to leave playing area!");

					// check blockage
					if (board.getCell(new_x, new_y).isBlocked())
						throw new Exception("Explorer caught attempting to enter a blocked cell!");

					if (new_x != cell.getLocation().x || new_y != cell.getLocation().y)
					{
						// // add explorer to new cell
						// if (board.getCell(new_x, new_y).addExplorer(e))
						// // book keeping, remove this ant from its current
						// // location
						// cell.removeExplorer(e);
						board.moveExplorer(e, board.getCell(new_x, new_y));
					}

					// central cell
					Cell newCell = board.getExplorer(p.getExplorer());

					// list of cells within visibility range
					ArrayList<Cell> vcells = getVisibleCells(newCell, newCell.getLocation());
					for (Cell c : vcells)
					{
						// add explorer to the cell for consideration later
						conflictCells.add(c, e);

						// // if allCells contains c
						if (allCells.contains(c))
						{
							// // and conflictCells does not contain c
							// if (!conflictCells.contains(c))
							// // then add c to conflict cells
							// conflictCells.add(c, e);
						} else
						{
							// if allCells doesnt contain c,
							// then add it to all cells
							allCells.add(c);

							// Commit observation by explorer
							// c.Observe(e, newCell);
						}
					}
				}
			}

			catch (Exception e)
			{
				log.error(e.getMessage());
				ret = false;
			}
		}

		// If any conflicted cells
		if (conflictCells.size() > 0)
		{
			// process all cells
			for (int i = 0; i < allCells.size(); i++)
			{
				// Consider a cell
				Cell c = allCells.get(i);

				// find all explorers on that cell
				ArrayList<Explorer> exps = conflictCells.getExplorers(c);

				if (exps.size() > 1)
				{
					// resolve conflict

					double nearest = Double.MAX_VALUE;
					ArrayList<Explorer> resolveExplorers = new ArrayList<Explorer>();
					// find & count nearest explorers
					for (int j = 0; j < exps.size(); j++)
					{
						// find explorer distance to cell being considered
						Cell myExpCell = board.getExplorer(exps.get(j));
						Double dist = myExpCell.distanceToCell(c);

						if (dist < nearest)
						{
							// if this explorer is nearer, update nearest
							// distance, reset countExps
							nearest = dist;
							resolveExplorers.clear();
							resolveExplorers.add(exps.get(j));
						} else if (dist == nearest)
						{
							// if this explorer is same distance as the nearest
							// explorer, add him to our list
							resolveExplorers.add(exps.get(j));
						} else
						{
							// this explorer is too far
						}
					}

					if (resolveExplorers.size() == 0)
					{
						System.out.println("BUG: No explorers on resolvant cell");
					} else if (resolveExplorers.size() == 1)
					{
						// observe the cell
						c.Observe(resolveExplorers.get(0), board.getExplorer(resolveExplorers.get(0)));
					} else
					{
						// pick a random explorer
						int eNo = random.nextInt(resolveExplorers.size());
						log.debug("Random pick: " + eNo);

						// observe the cell
						c.Observe(resolveExplorers.get(eNo), board.getExplorer(resolveExplorers.get(eNo)));
					}
				} else if (exps.size() == 0)
				{
					// problem, should never happen
					System.out.println("BUG: No explorers on conflict cell");
				} else
				{
					// Only one explorer on this cell
					// observe the cell
					c.Observe(exps.get(0), board.getExplorer(exps.get(0)));
				}

			}
		}

		return ret;

	}

	private void notifyListeners(GameUpdateType type)
	{
		Iterator<GameListener> it = gameListeners.iterator();
		while (it.hasNext())
		{
			it.next().gameUpdated(type);
		}
	}

	public static final void main(String[] args)
	{
		// getCellsBetween(new Cell(new Point(20,10), 1.0), new Cell(new
		// Point(11,10), 1.0));

		if (args.length < 2 || args.length>6)
		{
			printUsage();
			System.exit(-1);
		}
		GameEngine engine = new GameEngine(args[0]);
		if (args[1].equalsIgnoreCase("text"))
		{
			// TextInterface ti = new TextInterface();
			// ti.register(engine);
			// ti.playGame();
			throw new RuntimeException("Text interface not implemented. Sorry.");
		} else if (args[1].equalsIgnoreCase("gui"))
		{
			new GUI(engine);
		}
		else if (args[1].equalsIgnoreCase("tournament"))
		{
			runTournament(args, engine);
		}
		else
		{
			printUsage();
			System.exit(-1);
		}
	}

	/**
	 * @param args
	 * @param engine
	 */
	private static void runTournament(String[] args, GameEngine engine)
	{
		GameConfig config = engine.getConfig();

		File boardFile = new File(args[2]);
		// File boardFile = new File(config. + File.separator+args[1]);
		if (!boardFile.exists())
		{
			System.err.println("Board file: " + boardFile + " does not exist.");
			System.exit(-1);
		}
		config.setSelectedBoard(boardFile);
		config.setRange(Integer.parseInt(args[3]));
		config.setMaxRounds(Integer.parseInt(args[4]));
		config.setTournamentGames(Integer.parseInt(args[5]));

		File file = new File("tournament_" + boardFile.getName() + "_" + args[3] + "_" + args[4] + "_" + args[5] + ".txt");
		file.delete();
		file = new File("tournament_" + boardFile.getName() + "_" + args[3] + "_" + args[4] + "_" + args[5] + ".txt");
		
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

	private final static void printUsage()
	{
		System.err.println("Usage: GameEngine <config file> gui");
		System.err.println("Usage: GameEngine <config file> tournament <board> <range> <num rounds> <num games>");
	}

	public void removeGameListener(GameListener l)
	{
		gameListeners.remove(l);
	}

	public static ArrayList<Point> getCellsBetween(Cell c1, Cell c2)
	{
		ArrayList<Point> cells = new ArrayList<Point>();

		Point2D.Double p1 = c1.getCenter();// explorer location
		Point2D.Double p2 = c2.getCenter();// cell to be checked

		// slope
		double m = (p1.x - p2.x) / (p1.y - p2.y);

		int start = 0;
		int end = 0;
		if (p1.y < p2.y)
		{
			start = (int) Math.ceil(p1.y);
			end = (int) Math.floor(p2.y);
		} else
		{
			start = (int) Math.ceil(p2.y);
			end = (int) Math.floor(p1.y);
		}

		if (p1.y == p2.y)
		{
			int j = c1.getLocation().y;
			start = Math.min(c1.getLocation().x, c2.getLocation().x) + 1;
			end = Math.max(c1.getLocation().x, c2.getLocation().x);

			for (int i = start; i < end; i++)
				cells.add(new Point(i, j));
			return cells;
		}

		// Loop between possible Y values
		for (int i = start; i <= end; i++)
		{
			// Calculate X value of intersection with cell
			double x = m * (i - p1.y) + p1.x;

			// if Diagonal intersection
			if (Math.floor(x) == x)
			{
				// Add Cell above left of point of intersection
				cells.add(new Point((int) Math.floor(x - 1), i));

				// Add Cell below left of cell of intersection
				cells.add(new Point((int) Math.floor(x - 1), i - 1));
			}

			// Add cell below point of intersection
			cells.add(new Point((int) Math.floor(x), i - 1));

			// Add cell above point of intersection
			cells.add(new Point((int) Math.floor(x), i));
		}

		// now process x-axis intersections
		if (p1.x < p2.x)
		{
			start = (int) Math.ceil(p1.x);
			end = (int) Math.floor(p2.x);
		} else
		{
			start = (int) Math.ceil(p2.x);
			end = (int) Math.floor(p1.x);
		}

		// Loop between possible X values
		for (int i = start; i <= end; i++)
		{
			// Calculate Y value of intersection with cell
			double y = (i - p1.x) / m + p1.y;

			// // Diagonal intersection already accounted for in the previous
			// loop
			// if (Math.floor(y) == y)
			// {
			// // Add Cell above left of point of intersection
			// cells.add(new Point(i, (int) Math.floor(y - 1)));
			//
			// // Add Cell below left of cell of intersection
			// cells.add(new Point(i - 1, (int) Math.floor(y - 1)));
			// }

			// Add cell left to point of intersection
			cells.add(new Point(i - 1, (int) Math.floor(y)));

			// Add cell right to point of intersection
			cells.add(new Point(i, (int) Math.floor(y)));
		}

		return cells;

	}

	public Boolean isCellVisible(Cell from, Cell to)
	{
		ArrayList<Point> cells = getCellsBetween(from, to);

		for (Point point : cells)
		{
			if (!to.getLocation().equals(point))
			{
				// if cell is mountain, cant see through it
				if (board.getCell(point.x, point.y).getCellTerrain() == MOUNTAIN)
					return false;
			}
		}

		return true;
	}

	public Integer[] getScores()
	{
		Integer[] scores = new Integer[config.getNumExplorers()];
		for (int i = 0; i < scores.length; i++)
			scores[i] = board.getScore(explorers.get(i).getExplorer());

		return scores;
	}
}
