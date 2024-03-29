package explorandum;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ClassRenderer extends DefaultListCellRenderer
{
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		try
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof Class)
			{
				Class<Player> p = (Class<Player>) value;
				Player pl = ((Player) p.newInstance());
				String strPlayer = pl.name();
				if (strPlayer != null)
				{
					setText(strPlayer);
				}
				Color c = pl.color();
				if (c != null)
				{
					setBackground(c);
				}
			}
		} catch (Exception e)
		{

		}
		return this;
	}

}
