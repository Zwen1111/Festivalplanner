package festivalplanner.simulator.data;

import javax.imageio.ImageIO;
import javax.json.JsonObject;
import java.awt.image.BufferedImage;

/**
 * Created by Maarten on 20/02/2017.
 */
public class Tileset {
     private int collums;
     private int firstgid;
     private BufferedImage image;

     private int imageHeight;
     private int imageWidth;
     private int margin;
     private String name;
     private int spacing;
     private int tileCount;
     private int tileHeight;
     private int tileWidth;

    /**
     * makes an tileset
     *
     * @param tileset an array of tilesets;
     *
     */
    public Tileset(JsonObject tileset) {
         try {
              collums= tileset.getInt("columns");
              firstgid = tileset.getInt("firstgid");
              String imageName = tileset.getString("image");
              imageName = imageName.substring(imageName.length()-4,imageName.length());
             name = tileset.getString("name");
              image = ImageIO.read(getClass().getResourceAsStream("/tilesets/" + name + imageName));
              imageHeight = tileset.getInt("imageheight");
              imageWidth = tileset.getInt("imagewidth");
              margin = tileset.getInt("margin");

              spacing = tileset.getInt("spacing");
              tileCount = tileset.getInt("tilecount");
              tileHeight = tileset.getInt("tileheight");
              tileWidth = tileset.getInt("tilewidth");
         }catch (Exception e) {
              System.err.println("Tileset " + name + " could not be loaded");
              e.printStackTrace();
         }
    }
     public int getCollums() {return collums;}
     //public int getFirstgid() {return firstgid;}
     public BufferedImage getImage() {
          return image;
     }
     public int getImageHeight() {
          return imageHeight;
     }
     //public int getImageWidth() {return imageWidth;}
     //public int getTileCount() {return tileCount;}
     public int getTileHeight() {
          return tileHeight;
     }
     public int getTileWidth() {
          return tileWidth;
     }
}
