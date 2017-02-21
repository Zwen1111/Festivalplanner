package festivalplanner.simulator;

import festivalplanner.simulator.data.Tileset;

import javax.json.JsonArray;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maarten Nieuwenhuize, Sander de Nooijer
 */
public class TilesetManager {

    private List<Tileset> tilesets;
    private List<BufferedImage> images;
    private int largestTileHeight;
    private int largestTileWidth;

    /**
     * makes the tilesetManager
     *
     * @param tilesetJson an array of tilesets;
     */
    public TilesetManager(JsonArray tilesetJson) {
        tilesets = new ArrayList<>();
        images = new ArrayList<>();
        for (int i = 0; i < tilesetJson.size(); i++) {
            tilesets.add(new Tileset(tilesetJson.getJsonObject(i)));
        }


        for (Tileset tileset : tilesets) {
            BufferedImage image = tileset.getImage();
            if (largestTileHeight < tileset.getTileHeight()) largestTileHeight = tileset.getTileHeight();
			if (largestTileWidth < tileset.getTileWidth()) largestTileWidth = tileset.getTileWidth();
            int rows = (int) Math.floor(tileset.getImageHeight() / tileset.getTileHeight());
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < tileset.getCollums(); x++) {
                        images.add(image.getSubimage(x * tileset.getTileWidth(),
                                y * tileset.getTileHeight(),
                                tileset.getTileWidth(),
                                tileset.getTileHeight()));
                }
            }
        }
    }

    /**
     * returns an image of the gid
     *
     * @param gid the number of the tile wich is specified in the layaur
     * @return the image of the gid;
     */
    public Image getImage(int gid) {

        if(gid <= 0 || gid > images.size() ) {
            if (gid != 0) System.err.println("Gid doesn't exist");
            return null;
        }else return images.get(gid - 1);
    }

	/**
	 * Returns the largest tile's height recorded while loading the TileSets.
	 * Mostly, this value is the same for all sets.
	 * @return the largest tile's height.
	 */
	public int getLargestTileHeight() {
    	return largestTileHeight;
	}

	/**
	 * Returns the largest tile's width recorded while loading the TileSets.
	 * Mostly, this value is the same for all sets.
	 * @return the largest tile's width.
	 */
	public int getLargestTileWidth() {
    	return largestTileWidth;
	}
}
