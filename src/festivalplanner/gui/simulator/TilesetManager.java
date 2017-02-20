package festivalplanner.gui.simulator;

import festivalplanner.simulator.data.Tileset;

import javax.json.JsonArray;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maarten on 20/02/2017.
 */
public class TilesetManager {
    List<Tileset> tilesets;
    List<BufferedImage> images;

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
    public BufferedImage getImage(int gid) {

        if(gid <= 0 || gid > images.size() ) {
            System.err.println("Gid doesn't exist");
            return null;
        }else return images.get(gid - 1);
    }
}
