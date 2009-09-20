/*
 * 	$Id: ConfigurationPanel.java,v 1.3 2007/11/14 22:00:22 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import explorandum.ClassRenderer;
import explorandum.GameConfig;
import explorandum.GameConstants;
import explorandum.Player;
import explorandum.PlayerWrapper;

public final class ConfigurationPanel extends JPanel implements GameConstants, ChangeListener, ItemListener, ListSelectionListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	private GameConfig config;

	static Font config_font = new Font("Arial", Font.PLAIN, 14);

	private JLabel roundLabel;
	private JSpinner roundSpinner;

	private JLabel numAntsLabel;
	private JSpinner numAntsSpinner;

	private JList playerList;
	private JList scoreList;
	private JButton add;
	private JButton remove;

	private JLabel playerLabel;
	private JComboBox playerBox;

	private JLabel boardLabel;
	private JComboBox boardBox;

	private JLabel gamesLabel;
	private JSpinner gamesSpinner;

	private Class<Player> selectedPlayer;

	private JSlider speedSlider;
	private JCheckBox singleStartCheck;

	public ConfigurationPanel(GameConfig config)
	{
		this.config = config;

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Configuration"));
		this.setPreferredSize(new Dimension(350, 1200));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		numAntsLabel = new JLabel("Range: ");
		numAntsLabel.setFont(config_font);
		numAntsSpinner = new JSpinner(new SpinnerNumberModel(this.config.getRange(), 1, null, 1));
		numAntsSpinner.setPreferredSize(new Dimension(120, 25));
		numAntsSpinner.addChangeListener(this);

		JPanel panel = new JPanel(new FlowLayout());
		panel.add(numAntsLabel);
		panel.add(numAntsSpinner);
		layout.setConstraints(panel, c);
		this.add(panel);

		roundLabel = new JLabel("Number of Rounds:");
		roundLabel.setFont(config_font);
		roundSpinner = new JSpinner(new SpinnerNumberModel(this.config.getMaxRounds(), 1, null, 1));
		roundSpinner.setPreferredSize(new Dimension(120, 25));
		roundSpinner.addChangeListener(this);

		panel = new JPanel(new FlowLayout());
		panel.add(roundLabel);
		panel.add(roundSpinner);
		layout.setConstraints(panel, c);
		this.add(panel);
		
		panel = new JPanel(new FlowLayout());
		layout.setConstraints(panel, c);
		singleStartCheck = new JCheckBox("Start on same Cell");
		panel.add(singleStartCheck);
		singleStartCheck.setSelected(config.getSingleStart());
		singleStartCheck.addChangeListener(this);
		this.add(panel);

		panel = new JPanel(new FlowLayout());
		panel.setMinimumSize(new Dimension(100, 200));
		ClassRenderer cr = new ClassRenderer();
		playerLabel = new JLabel("Players:");
		playerList = new JList();// config.getActivePlayerList());//data);
		playerList.addListSelectionListener(this);
		playerList.setCellRenderer(cr);
		scoreList = new JList();
		JScrollPane playerScroll = new JScrollPane(playerList);
		JScrollPane scoreScroll = new JScrollPane(scoreList);
		playerList.setFixedCellWidth(200);
		scoreList.setFixedCellWidth(80);
		//playerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scoreScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//panel.add(playerLabel);
		panel.add(playerScroll);// playerList);
		panel.add(scoreScroll);
		remove = new JButton("Remove");
		remove.addActionListener(this);
		panel.add(remove);

		// make player combo box
		playerBox = new JComboBox(config.getClassList());
		playerBox.addItemListener(this);
		panel.add(playerBox);
		add = new JButton("Add");
		add.addActionListener(this);
		panel.add(add);
		layout.setConstraints(panel, c);
		this.add(panel);


		// board combo
		panel = new JPanel(new FlowLayout());
		boardLabel = new JLabel("Board:");
		boardBox = new JComboBox(config.getBoardList());
		boardBox.addItemListener(this);
		panel.add(boardLabel);
		panel.add(boardBox);
		layout.setConstraints(panel, c);
		this.add(panel);

		speedSlider = new JSlider(0, 1000);
		panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Delay (0 - 1000ms):"));
		panel.add(speedSlider);
		layout.setConstraints(panel, c);
		this.add(panel);

		JPanel tourneyPanel = new JPanel(new GridLayout(1, 1));
		tourneyPanel.setPreferredSize(new Dimension(300, 300));
		gamesLabel = new JLabel("Games per match:");
		gamesLabel.setFont(config_font);
		gamesSpinner = new JSpinner(new SpinnerNumberModel(config.getTournamentGames(), 1, null, 1));
		((SpinnerNumberModel) gamesSpinner.getModel()).setValue(config.getTournamentGames());
		gamesSpinner.setPreferredSize(new Dimension(120, 25));
		gamesSpinner.addChangeListener(this);
		panel = new JPanel(new FlowLayout());
		panel.add(gamesLabel);
		panel.add(gamesSpinner);
		tourneyPanel.add(panel);

		tourneyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tournament"));
		layout.setConstraints(tourneyPanel, c);
		this.add(tourneyPanel);
	}

	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		roundSpinner.setEnabled(enabled);
		playerBox.setEnabled(enabled);
		gamesSpinner.setEnabled(enabled);
		numAntsSpinner.setEnabled(enabled);
		boardBox.setEnabled(enabled);
		playerList.setEnabled(enabled);
		scoreList.setEnabled(enabled);
		remove.setEnabled(enabled);
		add.setEnabled(enabled);
	}

	public void stateChanged(ChangeEvent arg0)
	{
		if (arg0.getSource().equals(roundSpinner))
			config.setMaxRounds(((Integer) ((JSpinner) arg0.getSource()).getValue()).intValue());
		else if (arg0.getSource().equals(gamesSpinner))
			config.setTournamentGames(((Integer) ((JSpinner) arg0.getSource()).getValue()).intValue());
		else if (arg0.getSource().equals(numAntsSpinner)){
			config.setRange(((Integer) ((JSpinner) arg0.getSource()).getValue()).intValue());
//			config.readBoards();
//			reloadBoards();
		}
		else if (arg0.getSource().equals(singleStartCheck))
			config.setSingleStart(singleStartCheck.isSelected());
		else
			throw new RuntimeException("Unknown State Changed Event!!");
	}

	public void itemStateChanged(ItemEvent arg0)
	{
		if (arg0.getSource().equals(playerBox) && arg0.getStateChange() == ItemEvent.SELECTED)
		{
			// config.setActivePlayer((Class)arg0.getItem());
			selectedPlayer = (Class) arg0.getItem();
			//System.out.println("SELECTED:" + selectedPlayer.toString());
		}
		if (arg0.getSource().equals(boardBox) && arg0.getStateChange() == ItemEvent.SELECTED)
		{
			config.setSelectedBoard((File) arg0.getItem());
		}
	}

	public void reloadBoards()
	{
		boardBox.removeAllItems();
		File[] files = config.getBoardList();
		for (int i = 0; i < files.length; i++)
			boardBox.addItem(files[i]);
	}

	public JSlider getSpeedSlider()
	{
		return speedSlider;
	}

	public void valueChanged(ListSelectionEvent e)
	{
	}

	public void actionPerformed(ActionEvent e)
	{
		Class<Player>[] players = config.getClassList();

		if (selectedPlayer == null)
		{
			if (players.length > 0)
				selectedPlayer = players[0];
			else
				return;
		}

		if (e.getSource().equals(add))
		{
			config.addActivePlayer(selectedPlayer);
			playerList.setListData(config.getActivePlayerList());
			playerList.setSelectedValue(selectedPlayer, true);
		} else if (e.getSource().equals(remove))
		{
			Class<Player> removePlayer = (Class<Player>) playerList.getSelectedValue();
			if(removePlayer == null)
			{
				return;
			}
			
			config.removeActivePlayer(removePlayer);
			playerList.setListData(config.getActivePlayerList());
			playerList.setSelectedIndex(0);
		}
		
	}
	
	public void updateScores(Integer[] scores)
	{
		scoreList.removeAll();
		scoreList.setListData(scores);
	}
}
