package byow.TileEngine;

import java.awt.*;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile WALL = new TETile('❁', new Color(207, 167, 119), new Color(135, 103, 69),
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(141, 175, 206), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "the abyss");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲',  new Color(226, 112, 32), Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    private static final String mainPic = "Wall2.png";
    private static final String firePic = "Fire.png";
    private static final String avatarPic = "AvatarChar.png";
    private static final String waterPic = "Water.png";
    private static final String earthPic = "Earth.png";
    private static final String airPic = "Air.png";
    public static final TETile EARTH = new TETile('e', Color.white, Color.black, "earth", earthPic);

    public static final TETile WATA = new TETile('w', Color.white, Color.black, "h20", waterPic);

    public static final TETile FIRE = new TETile('f', Color.white, Color.black, "fire", firePic);

    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you", avatarPic);
    public static final TETile AIR = new TETile('a', Color.white, Color.black, "air", airPic);

    public static final TETile MAIN = new TETile(' ', Color.darkGray, Color.DARK_GRAY, "Wall2", mainPic);

}