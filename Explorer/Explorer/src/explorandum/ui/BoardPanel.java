/* 
 * 	$Id: BoardPanel.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import explorandum.Board;
import explorandum.Cell;
import explorandum.GameConstants;
import explorandum.GameEngine;

public final class BoardPanel extends JPanel implements MouseListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Board board;

	private CellComponent[][] cellComponents;

	private boolean editable;
	private EditMode editMode;
	private boolean dragging;
	private GameEngine engine;

	public BoardPanel()
	{
		this.setPreferredSize(new Dimension(600, 600));
		this.dragging = false;
	}

	public BoardPanel(GameEngine eng, boolean editable)
	{
		setEngine(eng);
		setBoard(engine.getBoard(), editable);
		this.editable = editable;
		this.dragging = false;
	}

	public void setEngine(GameEngine eng)
	{
		engine = eng;
	}

	public void setBoard(Board b, boolean editable)
	{
		board = b;
		this.editable = editable;
		this.removeAll();
		int width = board.getWidth();
		int height = board.getHeight();
		this.setLayout(new GridLayout(height, width));// note the inversion so
														// the gridlayout get's
														// it right.
		this.setPreferredSize(new Dimension(width * 10, height * 10));

		// build the components
		cellComponents = new CellComponent[width][];
		for (int x = 0; x < width; x++)
		{
			cellComponents[x] = new CellComponent[height];
			for (int y = 0; y < height; y++)
			{
				cellComponents[x][y] = new CellComponent(board.getCell(x, y));
				if (editable)
					cellComponents[x][y].addMouseListener(this);
				board.getCell(x, y).addCellListener(cellComponents[x][y]);
			}
		}

		// now add the components to the grid in the correct order for display
		// purposes.
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				this.add(cellComponents[x][y]);

		repaint();
	}


	public static enum EditMode {
		LAND, WATER, MOUNTAIN, STARTABLE
	}

	public EditMode getEditMode()
	{
		return editMode;
	}

	public void setEditMode(EditMode mode)
	{
		editMode = mode;
	}

	public void mouseClicked(MouseEvent arg0)
	{
	}

	public void mouseEntered(MouseEvent arg0)
	{
		if (editable && dragging)
		{
			handleEditEvent(arg0);
		}
	}

	public void mouseExited(MouseEvent arg0)
	{
	}

	public void mousePressed(MouseEvent arg0)
	{
		if (editable)
		{
			handleEditEvent(arg0);
			dragging = true;
		}
	}

	public void mouseReleased(MouseEvent arg0)
	{
		dragging = false;
	}

	private void handleEditEvent(MouseEvent event)
	{
		CellComponent c = (CellComponent) event.getSource();
		Cell cell = c.getCell();
		switch (editMode)
		{
		case LAND:
			cell.setCellTerrain(GameConstants.LAND);
			break;
		case WATER:
			cell.setCellTerrain(GameConstants.WATER);
			break;
		case MOUNTAIN:
			cell.setCellTerrain(GameConstants.MOUNTAIN);
			break;
		case STARTABLE:
			if (cell.getCellTerrain() == GameConstants.LAND)
				cell.setStartable(!cell.isStartable());
			break;
		}
		repaint();
	}
}
