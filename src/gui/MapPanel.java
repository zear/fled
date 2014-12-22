package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.event.MouseInputListener;

import level.*;

public class MapPanel extends DrawPanel implements KeyListener, MouseInputListener
{
	public Level level = null;
	private TilesetPanel tileset = null;
	private TileInfoPanel tileInfoPanel = null;
	private ObjectPanel objectPanel = null;
	private EditMode editMode = EditMode.MODE_TILE_EDIT;
	private boolean canPaint = false;
	private int paintX = 0;
	private int paintY = 0;
	private int lastPaintOnLayer = 1;
	private int lastTile = 0;
	private int [][] selectedArea = null;
	private int [][] selectedAreaBackup = null;
	private int selectedAreaX;
	private int selectedAreaY;
	private int selectedAreaX2;
	private int selectedAreaY2;
	private int selectedAreaBackupX;
	private int selectedAreaBackupY;
	private ArrayList<TileBackupNode> tileBackup = null;
	private boolean drawSelection = false;

	private GameObject selectedObject = null;
	private boolean draggingObject = false;
	private boolean objectIsNew = false;

	protected boolean showGrid = true;
	protected boolean showCollision = false;
	protected boolean showObjects = true;

	public EditMode getEditMode()
	{
		return this.editMode;
	}
	public void setEditMode(EditMode newEditMode)
	{
		this.editMode = newEditMode;
	}

	public void setObjectIsNew(boolean value)
	{
		this.objectIsNew = value;
	}

	public void paintTile(int layer, int num, int x, int y, boolean repaint)
	{
		if (this.tileset == null)
		{
			System.out.printf("Tileset is empty!\n");
			return;
		}
		if (this.level == null)
		{
			System.out.printf("No level loaded!\n");
			return;
		}
		if (layer >= this.drawAreaLayers.size())
		{
			System.out.printf("No such layer: %d!\n", layer);
			return;
		}

		int tileX = num%16;
		int tileY = num/16;

		super.blit(layer, this.tileset.getImage(0), x, y, x + 16, y + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);

		if(this.showCollision && layer == 1)
		{
			int col = this.level.getCollision(num);
			Color color = null;

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				color = new Color(0, 255 ,255, 100);
			}
			if((col & Collision.COLLISION_PLATFORM) > 0)
			{
				color = new Color(0, 255 ,0, 100);
			}
			if((col & Collision.COLLISION_DAMAGE) > 0)
			{
				color = new Color(255, 0 ,0, 100);
			}
			if((col & Collision.COLLISION_DESTRUCTIBLE) > 0)
			{
				color = new Color(255, 0 ,255, 100);
			}
			if((col & Collision.COLLISION_HIDDEN) > 0)
			{
				color = new Color(0, 0 ,0, 100);
			}
			if((col & Collision.COLLISION_CLIMB) > 0)
			{
				color = new Color(255, 255 ,255, 100);
			}

			if(color != null)
			{
				super.drawCollision(layer, x, y, color);
			}
		}

		if(repaint)
		{
			super.repaint();
		}
	}

	public void paintTile(int x, int y)
	{
		if (this.tileset == null)
		{
			System.out.printf("Tileset is empty!\n");
			return;
		}

		int tileNum = tileset.getSelY() * 16 + tileset.getSelX();
		selectedAreaBackup = null;
		tileBackup.add(new TileBackupNode(x/16, y/16, this.level.getLayer(super.paintOnLayer).getTile(x/16,y/16)));
		this.level.getLayer(super.paintOnLayer).setTile(x/16, y/16, tileNum);

		super.blit(super.paintOnLayer, this.tileset.getImage(0), x, y, x + 16, y + 16, this.tileset.getSelX() * 16, this.tileset.getSelY() * 16, this.tileset.getSelX() * 16 + 16, this.tileset.getSelY() * 16 + 16);

		if(this.showCollision && super.paintOnLayer == 1)
		{
			int col = this.level.getCollision(tileNum);
			Color color = null;

			if((col & Collision.COLLISION_SOLID) > 0)
			{
				color = new Color(0, 255 ,255, 100);
			}
			if((col & Collision.COLLISION_PLATFORM) > 0)
			{
				color = new Color(0, 255 ,0, 100);
			}
			if((col & Collision.COLLISION_DAMAGE) > 0)
			{
				color = new Color(255, 0 ,0, 100);
			}
			if((col & Collision.COLLISION_DESTRUCTIBLE) > 0)
			{
				color = new Color(255, 0 ,255, 100);
			}
			if((col & Collision.COLLISION_HIDDEN) > 0)
			{
				color = new Color(0, 0 ,0, 100);
			}
			if((col & Collision.COLLISION_CLIMB) > 0)
			{
				color = new Color(255, 255 ,255, 100);
			}

			if(color != null)
			{
				super.drawCollision(super.paintOnLayer, x, y, color);
			}
		}

		super.repaint();
	}

	public void restoreBackup()
	{
		if(tileBackup != null)
		{
			// Eliminate tiles visited more than once
			for(TileBackupNode node : tileBackup)
			{
				for(TileBackupNode node2 : tileBackup)
				{
					if(node.x == node2.x && node.y == node2.y)
					{
						node2.value = node.value;
					}
				}
			}

			for(TileBackupNode node : tileBackup)
			{
				this.level.getLayer(super.paintOnLayer).setTile(node.x, node.y, node.value);

				int tileX = node.value % 16;
				int tileY = node.value / 16;
				int dx = node.x * 16;
				int dy = node.y * 16;
				super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);

				if(this.showCollision && super.paintOnLayer == 1)
				{
					int col = this.level.getCollision(node.value);
					Color color = null;

					if((col & Collision.COLLISION_SOLID) > 0)
					{
						color = new Color(0, 255 ,255, 100);
					}
					if((col & Collision.COLLISION_PLATFORM) > 0)
					{
						color = new Color(0, 255 ,0, 100);
					}
					if((col & Collision.COLLISION_DAMAGE) > 0)
					{
						color = new Color(255, 0 ,0, 100);
					}
					if((col & Collision.COLLISION_DESTRUCTIBLE) > 0)
					{
						color = new Color(255, 0 ,255, 100);
					}
					if((col & Collision.COLLISION_HIDDEN) > 0)
					{
						color = new Color(0, 0 ,0, 100);
					}
					if((col & Collision.COLLISION_CLIMB) > 0)
					{
						color = new Color(255, 255 ,255, 100);
					}

					if(color != null)
					{
						super.drawCollision(super.paintOnLayer, dx, dy, color);
					}
				}
			}

			lastTile = 0;
		}
		else if(selectedAreaBackup != null)
		{
			int x = selectedAreaBackupX;
			int y = selectedAreaBackupY;

			if(x < 0)
				x = 0;
			if(y < 0)
				y = 0;

			for(int j = 0; j < selectedAreaBackup[0].length; j++)
			{
				if(y * 16 + j * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getHeight())
					continue;

				for(int i = 0; i < selectedAreaBackup.length; i++)
				{
					if(x * 16 + i * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getWidth())
						continue;

					this.level.getLayer(super.paintOnLayer).setTile(x+i, y+j, selectedAreaBackup[i][j]);

					int tileX = selectedAreaBackup[i][j] % 16;
					int tileY = selectedAreaBackup[i][j] / 16;
					int dx = x*16 + i*16;
					int dy = y*16 + j*16;

					super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);

					if(this.showCollision && super.paintOnLayer == 1)
					{
						int col = this.level.getCollision(selectedAreaBackup[i][j]);
						Color color = null;

						if((col & Collision.COLLISION_SOLID) > 0)
						{
							color = new Color(0, 255 ,255, 100);
						}
						if((col & Collision.COLLISION_PLATFORM) > 0)
						{
							color = new Color(0, 255 ,0, 100);
						}
						if((col & Collision.COLLISION_DAMAGE) > 0)
						{
							color = new Color(255, 0 ,0, 100);
						}
						if((col & Collision.COLLISION_DESTRUCTIBLE) > 0)
						{
							color = new Color(255, 0 ,255, 100);
						}
						if((col & Collision.COLLISION_HIDDEN) > 0)
						{
							color = new Color(0, 0 ,0, 100);
						}
						if((col & Collision.COLLISION_CLIMB) > 0)
						{
							color = new Color(255, 255 ,255, 100);
						}

						if(color != null)
						{
							super.drawCollision(super.paintOnLayer, dx, dy, color);
						}
					}
				}
			}
		}
		else // There is no backup, do nothing.
		{
			return;
		}

		super.repaint();
	}

	public void selectArea(int x, int y, int w, int h)
	{
		selectedArea = new int[Math.abs(w-x) + (x >= w ? 1 : 0)][Math.abs(h-y) + (y >= h ? 1 : 0)];

		for(int j = 0; j < selectedArea[0].length; j++)
		{
			for(int i = 0; i < selectedArea.length; i++)
			{
				selectedArea[i][j] = this.level.getLayer(super.paintOnLayer).getTile((x >= w ? w+i : x+i), (y >= h ? h+j : y+j));
			}
		}
	}

	public void deleteSelectedArea(int x, int y, int w, int h)
	{
		int startX = (x >= w ? w : x);
		int startY = (y >= h ? h : y);
		int width = (x >= w ? x+1 : w);
		int height = (y >= h ? y+1 : h);

		if(startX < 0)
		{
			startX = 0;
		}
		if(startY < 0)
		{
			startY = 0;
		}

		tileBackup = null;
		selectedAreaBackup = new int[Math.abs(width-startX)][Math.abs(height-startY)];
		selectedAreaBackupX = startX;
		selectedAreaBackupY = startY;

		for(int j = startY; j < height; j++)
		{
			if(j * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getHeight())
				continue;

			for(int i = startX; i < width; i++)
			{
				if(i * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getWidth())
					continue;

				selectedAreaBackup[i - startX][j - startY] = this.level.getLayer(super.paintOnLayer).getTile(i, j);
				this.level.getLayer(super.paintOnLayer).setTile(i, j, 0);

				int dx = i*16;
				int dy = j*16;

				super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, 0, 0, 16, 16);
			}
		}

		super.repaint();
	}

	public void pasteSelectedArea(int x, int y)
	{
		if(selectedArea == null)
			return;

		if(x < 0 || y < 0)
			return;

		tileBackup = null;
		selectedAreaBackup = new int[selectedArea.length][selectedArea[0].length];
		selectedAreaBackupX = x;
		selectedAreaBackupY = y;

		for(int j = 0; j < selectedArea[0].length; j++)
		{
			if(y * 16 + j * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getHeight())
				continue;

			for(int i = 0; i < selectedArea.length; i++)
			{
				if(x * 16 + i * 16 >= super.drawAreaLayers.get(super.paintOnLayer).getWidth())
					continue;

				selectedAreaBackup[i][j] = this.level.getLayer(super.paintOnLayer).getTile(x+i, y+j);
				this.level.getLayer(super.paintOnLayer).setTile(x+i, y+j, selectedArea[i][j]);

				int tileX = selectedArea[i][j] % 16;
				int tileY = selectedArea[i][j] / 16;
				int dx = x*16 + i*16;
				int dy = y*16 + j*16;

				super.blit(super.paintOnLayer, this.tileset.getImage(0), dx, dy, dx + 16, dy + 16, tileX * 16, tileY * 16, tileX * 16 + 16, tileY * 16 + 16);

				if(this.showCollision && super.paintOnLayer == 1)
				{
					int col = this.level.getCollision(selectedArea[i][j]);
					Color color = null;

					if((col & Collision.COLLISION_SOLID) > 0)
					{
						color = new Color(0, 255 ,255, 100);
					}
					if((col & Collision.COLLISION_PLATFORM) > 0)
					{
						color = new Color(0, 255 ,0, 100);
					}
					if((col & Collision.COLLISION_DAMAGE) > 0)
					{
						color = new Color(255, 0 ,0, 100);
					}
					if((col & Collision.COLLISION_DESTRUCTIBLE) > 0)
					{
						color = new Color(255, 0 ,255, 100);
					}
					if((col & Collision.COLLISION_HIDDEN) > 0)
					{
						color = new Color(0, 0 ,0, 100);
					}
					if((col & Collision.COLLISION_CLIMB) > 0)
					{
						color = new Color(255, 255 ,255, 100);
					}

					if(color != null)
					{
						super.drawCollision(super.paintOnLayer, dx, dy, color);
					}
				}
			}
		}
		super.repaint();
	}

	public void setPanels(TilesetPanel newTileset, TileInfoPanel newTileInfoPanel, ObjectPanel newObjectPanel)
	{
		this.tileset = newTileset;
		this.tileInfoPanel = newTileInfoPanel;
		this.objectPanel = newObjectPanel;
	}

	private boolean isInMapArea(int x, int y)
	{
		if(super.drawAreaLayers.size() < 1)
			return false;

		if(super.drawAreaLayers.get(0) == null)
			return false;

		if (x < 0)
			return false;
		else if (x >= super.drawAreaLayers.get(0).getWidth())
			return false;
		else if (y < 0)
			return false;
		else if (y >= super.drawAreaLayers.get(0).getHeight())
			return false;
		else
		{
			return true;
		}
	}

	public void setSelectedObject(GameObject selectedObject)
	{
		this.selectedObject = selectedObject;
		this.repaint();
	}

	// key listener
	public void keyPressed(KeyEvent e)
	{
		boolean left = false;
		boolean right = false;
		boolean up = false;
		boolean down = false;

		int modifiers = e.getModifiers();

		switch(e.getKeyCode())
		{
			case KeyEvent.VK_A:
				left = true;
			break;
			case KeyEvent.VK_D:
				right = true;
			break;
			case KeyEvent.VK_W:
				up = true;
			break;
			case KeyEvent.VK_S:
				down = true;
			break;
			case KeyEvent.VK_SHIFT:	// Select
				if (this.editMode == EditMode.MODE_TILE_EDIT)
				{
					this.editMode = EditMode.MODE_TILE_SELECTION;
				}
			break;
			case KeyEvent.VK_Z:	// Restore
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					restoreBackup();
				}
			break;
			case KeyEvent.VK_X:	// Cut
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					deleteSelectedArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					drawSelection = false;
				}
			break;
			case KeyEvent.VK_C:	// Copy
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
				}
			break;
			case KeyEvent.VK_V:	// Paste
				if (this.editMode == EditMode.MODE_TILE_EDIT && ((modifiers & InputEvent.CTRL_MASK) > 0))
				{
					Point pos = this.getMousePosition();

					if (pos != null)
					{
						int x = (int)pos.getX()/16;
						int y = (int)pos.getY()/16;

						pasteSelectedArea(x, y);
					}

					drawSelection = false;
				}
			break;
			case KeyEvent.VK_INSERT: // Unix style
				if ((this.editMode == EditMode.MODE_TILE_EDIT || this.editMode == EditMode.MODE_TILE_SELECTION))
				{
					if ((modifiers & InputEvent.CTRL_MASK) > 0)		// Copy
					{
						selectArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					}
					else if (((modifiers & InputEvent.SHIFT_MASK) > 0))	// Paste
					{
						Point pos = this.getMousePosition();

						if (pos != null)
						{
							int x = (int)pos.getX()/16;
							int y = (int)pos.getY()/16;

							pasteSelectedArea(x, y);
						}

						drawSelection = false;
					}
				}
			break;
			case KeyEvent.VK_DELETE: // Delete
				if (this.editMode == EditMode.MODE_TILE_EDIT)
				{
					deleteSelectedArea(selectedAreaX, selectedAreaY, selectedAreaX2, selectedAreaY2);
					drawSelection = false;
				}
			break;

			default:
			break;
		}

		if((left || right || up || down) && (this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null))
		{
			int x = this.tileset.getSelX();
			int y = this.tileset.getSelY();

			if(left)
				x--;
			if(right)
				x++;
			if(up)
				y--;
			if(down)
				y++;

			if(x < 0)
				x = 0;
			if(x > 15)
				x = 15;
			if(y < 0)
				y = 0;
			if(y > 15)
				y = 15;

			this.tileset.setSelX(x);
			this.tileset.setSelY(y);
			this.tileset.repaint();
			if(this.tileInfoPanel != null)
				this.tileInfoPanel.updateInfo(x, y);
		}
	}
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_SHIFT:
				if (this.editMode == EditMode.MODE_TILE_SELECTION)
				{
					this.editMode = EditMode.MODE_TILE_EDIT;
				}
			break;

			default:
			break;
		}
	}
	public void keyTyped(KeyEvent e)
	{
	}

	// mouse listener
	public void mouseExited(MouseEvent e)
	{
	}
	public void mouseEntered(MouseEvent e)
	{
		this.requestFocusInWindow();
	}
	public void mouseReleased(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
					this.canPaint = false;
				break;

				default:
				break;
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
					{
						selectedAreaX2 = e.getX()/16+1;
						selectedAreaY2 = e.getY()/16+1;

						if(selectedAreaX2 <= selectedAreaX)
							selectedAreaX2 -= 1;
						if(selectedAreaY2 <= selectedAreaY)
							selectedAreaY2 -= 1;
						this.repaint();
					}
				}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
				{
					if(this.draggingObject)
						this.draggingObject = false;
				}
				break;

				default:
				break;
			}
		}
	}
	public void mousePressed(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						this.canPaint = true;
						int curX = e.getX()/16*16;
						int curY = e.getY()/16*16;
						int curTile = this.tileset.getSelY() * 16 + this.tileset.getSelX();

						if(this.paintX != curX || this.paintY != curY || this.lastPaintOnLayer != super.paintOnLayer || this.lastTile != curTile)
						{
							if((this.canPaint) && (this.isInMapArea(e.getX(), e.getY())))
							{
								this.paintX = e.getX()/16*16;
								this.paintY = e.getY()/16*16;
								this.lastPaintOnLayer = super.paintOnLayer;
								this.lastTile = curTile;
								tileBackup = new ArrayList<TileBackupNode>();
								this.paintTile(this.paintX, this.paintY);
							}
						}
						drawSelection = false;
					}
					break;
					case InputEvent.BUTTON3_MASK:
					{
						int tile = this.level.getLayer(super.paintOnLayer).getTile(e.getX()/16, e.getY()/16);
						int x = tile % 16;
						int y = tile / 16;

						this.tileset.setSelX(x);
						this.tileset.setSelY(y);
						this.tileset.repaint();
						if(this.tileInfoPanel != null)
							this.tileInfoPanel.updateInfo(x, y);
					}
					break;

					default:
					break;
				}
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
			if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
			{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					selectedAreaX = e.getX()/16;
					selectedAreaY = e.getY()/16;

					if(selectedAreaX < 0)
						selectedAreaX = 0;
					if(selectedAreaY < 0)
						selectedAreaY = 0;

					drawSelection = true;
					this.repaint();
				}
			}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			if(this.level != null && this.objectPanel != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						int objX;
						int objY;
						int curX = e.getX();
						int curY = e.getY();

						if(!this.draggingObject)
						{
							for(GameObject curObj : this.level.getObjectList())
							{
								if(curX >= curObj.getX() && curX <= curObj.getX() + curObj.getW() && curY >= curObj.getY() && curY <= curObj.getY() + curObj.getH())
								{
									this.selectedObject = curObj;
									this.draggingObject = true;
									this.objectPanel.setSelectedObject(curObj);
									this.repaint();
									break;
								}
								else if(this.objectIsNew)
								{
									this.objectIsNew = false;
									this.draggingObject = true;

									curX = e.getX()/16*16;
									curY = e.getY()/16*16;

									if(super.drawAreaLayers.size() < 1)
										break;

									if(super.drawAreaLayers.get(0) == null)
										break;

									if(curX < (this.selectedObject.getW() - 1)/16*16)
										curX = (this.selectedObject.getW() - 1)/16*16;
									else if(curX >= super.drawAreaLayers.get(0).getWidth())
										curX = super.drawAreaLayers.get(0).getWidth() - 16;

									if(curY < (this.selectedObject.getH() - 1)/16*16)
										curY = (this.selectedObject.getH() - 1)/16*16;
									else if(curY >= super.drawAreaLayers.get(0).getHeight())
										curY = super.drawAreaLayers.get(0).getHeight() - 16;

									// center at the bottom-left corner
									if(this.selectedObject.getName().equals("diamond")) // TODO: Solve this by checking object type
									{
										this.selectedObject.setX(curX + 8 - this.selectedObject.getW()/2);
										this.selectedObject.setY(curY + 8 - this.selectedObject.getH()/2);
									}
									else
									{
										this.selectedObject.setX(curX + 16 - this.selectedObject.getW());
										this.selectedObject.setY(curY + 16 - this.selectedObject.getH());
									}
									this.repaint();
								}
							}
						}
					}
					break;
					case InputEvent.BUTTON3_MASK:
					break;

					default:
					break;
				}

				drawSelection = false;
			}
		}
	}
	public void mouseClicked(MouseEvent e)
	{
	}
	public void mouseMoved(MouseEvent e)
	{
	}
	public void mouseDragged(MouseEvent e)
	{
		if(this.editMode == EditMode.MODE_TILE_EDIT)
		{
			switch(e.getModifiers())
			{
				case InputEvent.BUTTON1_MASK:
				{
					int curX = e.getX()/16*16;
					int curY = e.getY()/16*16;
					int curTile = this.tileset.getSelY() * 16 + this.tileset.getSelX();

					if(this.paintX != curX || this.paintY != curY || this.lastPaintOnLayer != super.paintOnLayer || this.lastTile != curTile)
					{
						if((this.canPaint) && (this.isInMapArea(e.getX(), e.getY())))
						{
							this.paintX = curX;
							this.paintY = curY;
							this.lastPaintOnLayer = super.paintOnLayer;
							this.lastTile = curTile;
							this.paintTile(this.paintX, this.paintY);
						}
					}
				}
				break;

				default:
				break;
			}
		}
		else if(this.editMode == EditMode.MODE_TILE_SELECTION)
		{
				int modifiers = e.getModifiers();

				if((modifiers & InputEvent.BUTTON1_MASK) > 0)
				{
					if(this.level != null && this.level.getNumOfLayers() > 0 && this.tileset != null)
					{
						selectedAreaX2 = e.getX()/16+1;
						selectedAreaY2 = e.getY()/16+1;

						if(selectedAreaX2 < 0)
							selectedAreaX2 = 0;
						if(selectedAreaY2 < 0)
							selectedAreaY2 = 0;

						this.repaint();
					}
				}
		}
		else if(this.editMode == EditMode.MODE_OBJECT_EDIT)
		{
			if(this.level != null && this.objectPanel != null)
			{
				switch(e.getModifiers())
				{
					case InputEvent.BUTTON1_MASK:
					{
						if(this.selectedObject != null && this.draggingObject)
						{
							int curX = e.getX()/16*16;
							int curY = e.getY()/16*16;

							if(super.drawAreaLayers.size() < 1)
								break;

							if(super.drawAreaLayers.get(0) == null)
								break;

							if(curX < (this.selectedObject.getW() - 1)/16*16)
								curX = (this.selectedObject.getW() - 1)/16*16;
							else if(curX >= super.drawAreaLayers.get(0).getWidth())
								curX = super.drawAreaLayers.get(0).getWidth() - 16;

							if(curY < (this.selectedObject.getH() - 1)/16*16)
								curY = (this.selectedObject.getH() - 1)/16*16;
							else if(curY >= super.drawAreaLayers.get(0).getHeight())
								curY = super.drawAreaLayers.get(0).getHeight() - 16;

							// center at the bottom-left corner
							if(this.selectedObject.getName().equals("diamond")) // TODO: Solve this by checking object type
							{
								this.selectedObject.setX(curX + 8 - this.selectedObject.getW()/2);
								this.selectedObject.setY(curY + 8 - this.selectedObject.getH()/2);
							}
							else
							{
								this.selectedObject.setX(curX + 16 - this.selectedObject.getW());
								this.selectedObject.setY(curY + 16 - this.selectedObject.getH());
							}
							this.repaint();
						}
					}
					break;
					case InputEvent.BUTTON3_MASK:
					break;

					default:
					break;
				}
			}
		}
	}

	protected void draw(Graphics g)
	{
		// draw the level grid
		if(this.showGrid)
		{
			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(new Color(0, 0 ,0, 100));

			if(this.level != null && this.level.getNumOfLayers() > 0)
			{
				for(int i = 0; i <= this.level.getLayer(0).getWidth(); i++)
				{
					// vertical
					g2d.drawLine(i * 16, 0, i * 16, this.level.getLayer(0).getHeight() * 16);
				}
				for(int j = 0; j <= this.level.getLayer(0).getHeight(); j++)
				{
					// horizontal
					g2d.drawLine(0, j * 16, this.level.getLayer(0).getWidth() * 16, j * 16);
				}
			}
		}

		if (this.drawSelection)
		{
			int curX = 0;
			int curY = 0;
			int x1 = selectedAreaX * 16;
			int y1 = selectedAreaY * 16;
			int x2;
			int y2;

			if(this.editMode == EditMode.MODE_TILE_SELECTION)
			{
				Point pos = this.getMousePosition();

				if(pos != null)
				{
					curX = (int)pos.getX()/16*16 + 16;
					curY = (int)pos.getY()/16*16 + 16;

					x2 = curX;
					y2 = curY;
				}
				else
				{
					x2 = selectedAreaX2 * 16;
					y2 = selectedAreaY2 * 16;
				}
			}
			else
			{
				x2 = selectedAreaX2 * 16;
				y2 = selectedAreaY2 * 16;
			}

			if(x1 < 0)
				x1 = 0;
			if(y1 < 0)
				y1 = 0;
			if(x1 >= this.level.getLayer(0).getWidth() * 16)
				x1 = this.level.getLayer(0).getWidth() * 16;
			if(y1 >= this.level.getLayer(0).getHeight() * 16)
				y1 = this.level.getLayer(0).getHeight() * 16;

			if(x2 < 0)
				x2 = 0;
			if(y2 < 0)
				y2 = 0;
			if(x2 >= this.level.getLayer(0).getWidth() * 16)
				x2 = this.level.getLayer(0).getWidth() * 16;
			if(y2 >= this.level.getLayer(0).getHeight() * 16)
				y2 = this.level.getLayer(0).getHeight() * 16;

			if(x2 <= x1)
			{
				x1 += 16;
				x2 -= 16;
			}
			if(y2 <= y1)
			{
				y1 += 16;
				y2 -= 16;
			}

			Graphics2D g2d = (Graphics2D)g;
			g2d.setColor(Color.yellow);
			// top
			g2d.drawLine(x1, y1, x2, y1);
			// bottom
			g2d.drawLine(x1, y2, x2, y2);
			// left
			g2d.drawLine(x1, y1, x1, y2);
			// right
			g2d.drawLine(x2, y1, x2, y2);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		this.draw(g);

		// temp
		if(this.editMode == EditMode.MODE_OBJECT_EDIT || this.showObjects)
		{
			if(this.level != null)
			{
				for(GameObject curObj : this.level.getObjectList())
				{
					Graphics2D g2d = (Graphics2D)g;

					if(curObj.getDirection())
						g2d.drawImage(curObj.getTile(1), curObj.getX() + curObj.getOffsetRightX(), curObj.getY() + curObj.getOffsetRightY(), null);
					else
						g2d.drawImage(curObj.getTile(0), curObj.getX() + curObj.getOffsetLeftX(), curObj.getY() + curObj.getOffsetLeftY(), null);

					if(this.editMode == EditMode.MODE_OBJECT_EDIT)
					{
						if(curObj == this.selectedObject)
							g2d.setColor(Color.GREEN);
						else
							g2d.setColor(Color.RED);

						// draw rectangle
						g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX() + curObj.getW(), curObj.getY());
						g2d.drawLine(curObj.getX(), curObj.getY() + curObj.getH(), curObj.getX() + curObj.getW(), curObj.getY() + curObj.getH());
						g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX(), curObj.getY() + curObj.getH());
						g2d.drawLine(curObj.getX() + curObj.getW(), curObj.getY(), curObj.getX() + curObj.getW(), curObj.getY() + curObj.getH());
	//					// draw diagonal line
	//					if(curObj.getDirection())
	//						g2d.drawLine(curObj.getX(), curObj.getY(), curObj.getX() + curObj.getTileW(), curObj.getY() + curObj.getTileH());
	//					else
	//						g2d.drawLine(curObj.getX() + curObj.getTileW(), curObj.getY(), curObj.getX(), curObj.getY() + curObj.getTileH());
					}
				}
			}
		}
	}
}
