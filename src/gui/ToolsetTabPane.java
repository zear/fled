package gui;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ToolsetTabPane extends JTabbedPane
{
	private MapPanel mapPanel = null;

	public void setMapPanel(MapPanel newMapPanel)
	{
		this.mapPanel = newMapPanel;

		this.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				switch(getSelectedIndex())
				{
					case 0:
						mapPanel.setEditMode(EditMode.MODE_TILE_EDIT);
					break;
					case 1:
						mapPanel.setEditMode(EditMode.MODE_OBJECT_EDIT);
					break;
					case 2:
						mapPanel.setEditMode(EditMode.MODE_LEVEL_EDIT);
					break;

					default:
					break;
				}

				mapPanel.repaint();
			}
		});
	}
}
