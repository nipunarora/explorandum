/* 
 * 	$Id: CellComponent.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import explorandum.Cell;
import explorandum.Explorer;
import explorandum.GameConstants;
import explorandum.Logger;
import explorandum.Cell.CellListener;

public final class CellComponent extends JPanel implements CellListener, GameConstants
{
	private Cell myCell;
	private static final long serialVersionUID = 1L;

	// private Color color;

	public CellComponent(Cell cell)
	{
		myCell = cell;
		// this.color = color;
		this.setMinimumSize(new Dimension(10, 10));
		this.setPreferredSize(new Dimension(10, 10));
		updateToolTip();
	}

	public Cell getCell()
	{
		return myCell;
	}

	public void paintComponent(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();

		g.setColor(Color.white); // clear by painting white.
		g.fillRect(0, 0, w - 1, h - 1);
		
		Graphics2D g2d = (Graphics2D)g;

		// if (myCell.getNumExplorers() > 0)
		// {
		// // if(myCell.getNumExplorers() == 1)
		// // g.setColor(myCell.getExplorers().get(0).getId())
		// g.setColor(color);
		// g.fillRect(0, 0, w - 1, h - 1);
		// }

		// Draw terrain
		Color fillColor = Color.WHITE;
		switch (myCell.getCellTerrain())
		{
		case LAND:
			fillColor = Color.WHITE;
			break;
		case WATER:
			fillColor = Color.BLUE;
			break;
		case MOUNTAIN:
			fillColor = Color.GRAY;
			break;
		}

		g.setColor(fillColor);
		g.fillRect(0, 0, w - 1, h - 1);

		double height = (h) / 5;

		// Draw observation
		if (myCell.getObserved())
		{
			fillColor = myCell.getObservedBy().getColor();
			g.setColor(fillColor);
			// g.fillRect(2, (int)(2*height)+1, w - 5, h - (int)(2*height) - 4);
			if(w < 13 || h < 13)
				g.fillRect(2, 2, w-5, w-5);
			else
				g.fillOval(3, 3, w - 7, h - 7);
		}
		
		//cells stepped on
		if (myCell.getObservedDistance() == 0)
		{
			fillColor = myCell.getObservedBy().getColor();
			g.setColor(fillColor);
			g.fillRect(1, 1, w-1, h-1);
		}
		
		//Draw explorer ID boxes
		if (myCell.hasExplorers())
		{
			int n = myCell.getNumExplorers();
			ArrayList<Explorer> explorers = myCell.getExplorers();

			double width = (w) / n;

			if (n <= 4)
				width = w / 4;

			for (int i = 0; i < n; i++)
			{
				try
				{
					Explorer e = explorers.get(i);
					if (e != null)
					{
						g.setColor(e.getColor());
						g.fillRect((int) ((i % n) * width), (int) (3 * height), (int) (width), (int) (2 * height));
						
						g.setColor(Color.BLACK);
						g.drawString("" + e.getId(), (int) (((i % n)) * width), (int) (5 * height-2));
					}
				} catch (Exception e)
				{
					System.out.println("Cell paint problem");
				}

				g.setColor(Color.BLACK);
				g.drawRect((int) ((i % n) * width), (int) (3 * height), (int) (width), (int) (2 * height));
			}

			// draw cell outline.
			g.setColor(Color.black);
			g.drawRect(0, 0, w - 1, h - 1);
		} else
		{
			//Draw starting cell
			if (myCell.isStartable())
			{
				// draw circle
				g.setColor(Color.GREEN);
				g.drawRect(1, 1, w - 2, h - 2);
			} else
			{
				// draw cell outline.
				g.setColor(Color.black);
				g.drawRect(0, 0, w - 1, h - 1);
			}
		}

	}

	public void CellUpdated(Cell source)
	{
		updateToolTip();
		repaint();
	}

	private void updateToolTip()
	{
		String tip = "Location: (" + myCell.getLocation().x + ", " + myCell.getLocation().y + ")";
		if (myCell.getNumExplorers() > 0)
		{
			for (int i = 0; i < myCell.getExplorers().size(); i++)
			{
				Explorer e = myCell.getExplorers().get(i);
				tip += " | E" + e.getId();
			}
		}

		tip += " | " + TERRAIN_NAMES[myCell.getCellTerrain()];
		if (myCell.isStartable())
			tip += " | Startable";

		this.setToolTipText(tip);

	}

	// public static final void main(String []args){
	// JFrame f = new JFrame();
	// f.getContentPane().setLayout(new BorderLayout());
	// f.setPreferredSize(new Dimension(200, 100));
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//		
	//		
	// JPanel panel = new JPanel(new FlowLayout());
	// CellPrivate c1 = new CellPrivate(0,0,1d);
	// c1.setCellTerrain(WATER);
	// CellPrivate c2 = new CellPrivate(1,0,1d);
	// c2.setStartable(true);
	// c2.setCellTerrain(LAND);
	// panel.add(new CellComponent(c1, Color.YELLOW));
	// panel.add(new CellComponent(c2, Color.BLUE));
	// f.getContentPane().add(panel, BorderLayout.CENTER);
	//		
	// f.pack();
	// f.setVisible(true);
	//		
	// }
}
