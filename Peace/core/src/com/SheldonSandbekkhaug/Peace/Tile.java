package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Tile {
	public static final int TILE_SIZE = 40; // TODO: Adjust tile size based of world size
	private int tileID;
	private static int lastTileID = 0;
	private Location location; // The Location holding this Tile
	PeaceEntity e; // The PeaceEntity occupying this tile
	Rectangle rect;
	private boolean marketTile; // True if this Tile is part of the Market
	static Texture[] playerBanners;
	static Texture hasActionsIcon; // Displayed if Entity has actions left
	static Texture noActionsIcon; // Displayed if Entity has no actiosn left

	public Tile()
	{
		marketTile = false;
		tileID = lastTileID + 1;
		lastTileID = tileID;
	}
	
	/* Draw the Tile (and its contents) at using x, y as its bottom-left
	 * corner.
	 * pid: the playerID of the user.
	 */
	public void draw(SpriteBatch batch, BitmapFont font, float x, float y,
			int pid)
	{
		if (e != null)
		{
			batch.draw(e.getImg(), x, y);
			
			// Draw HP. These coordinates specify top-left corner
			String hpLabel = "" + e.getCurrHP();
			float hpX = x + Tile.TILE_SIZE - font.getBounds(hpLabel).width / 2;
			float hpY = y + font.getBounds(hpLabel).height;
			font.draw(batch, hpLabel, hpX, hpY);
			
			// Draw Strength if E is a Unit
			if (e instanceof Unit)
			{
				Unit u = (Unit)e;
				
				String strLabel = "" + u.getStrength();
				TextBounds labelBounds = font.getBounds(strLabel);
				
				float strengthX = x - labelBounds.width / 2;
				float strengthY = hpY;
				
				font.draw(batch, strLabel, strengthX, strengthY);
			}
			
			// Draw Player Banner
			if (e.getOwner() != Player.NEUTRAL)
			{
				Texture banner = playerBanners[e.getOwner()];
				float bannerX = x + TILE_SIZE - banner.getWidth() / 2;
				float bannerY = y + TILE_SIZE - banner.getHeight() / 2;
				batch.draw(banner, bannerX, bannerY);
				
				// Draw action point icons
				if (e.getOwner() == pid && e instanceof Unit) // Structures are passive
				{
					float iconX = rect.x;
					float iconY = rect.y + TILE_SIZE -
							hasActionsIcon.getHeight();
					if (e.getCurrActions() > 0)
					{
						batch.draw(hasActionsIcon, iconX, iconY);
					}
					else
					{
						batch.draw(noActionsIcon,  iconX, iconY);
					}
				}
			}
		}
	}
	
	/* Set the last tile ID to 0. */
	public static void resetLastTileID()
	{
		lastTileID = 0;
	}
	
	public int getTileID() {
		return tileID;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public PeaceEntity getE() {
		return e;
	}
	
	public void setE(PeaceEntity e) {
		this.e = e;
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public boolean isMarketTile() {
		return marketTile;
	}

	public void setMarketTile(boolean marketTile) {
		this.marketTile = marketTile;
	}
}
