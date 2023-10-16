package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


public class Engine {
    private static final int COIN_MAX = 4;
    private static String engineName = " ";

    private static MakeWorld currWorld = null;
    private static boolean gameWon = false;
    private static String path = "savedFile.txt";
    private Out out;
    private In read;
    private static MakeWorld miniWorld = null;
    private static final int WIDTH = 85;
    private static final int HEIGHT = 35;
    private static InputSource in;
    private static final int HUD_HEIGHT = 10;
    private static int SEED;
    private static final int MAX_SIZE = 15;
    private static boolean colon = false;
    private static boolean playMini = false;
    private static int wins = 0;
    private TERenderer render;

    private double tileID(double x, double y) {
        double id = x * HUD_HEIGHT * HUD_HEIGHT * HUD_HEIGHT * HUD_HEIGHT * HUD_HEIGHT + y;
        return id;
    }

    private void initTERender(InputSource inn) {
        if (inn.getClass().equals(KeyboardInputSource.class)) {
            render = new TERenderer();
            render.initialize(WIDTH, HEIGHT + HUD_HEIGHT);
            StdDraw.enableDoubleBuffering();
            StdDraw.setXscale(0, WIDTH);
            StdDraw.setYscale(0, HUD_HEIGHT + HEIGHT - 1);
        }
    }


    public class MakeWorld {
        private String instructions;

        private Avatar player;
        private String element;

        private TETile[][] tiles;
        private List<Build> floors = new ArrayList<>();
        private Room[] roomArr;
        private List<Hallway> hallList = new ArrayList<>();
        private int roomCt = 0;
        private List<Build> iconList = new ArrayList<>();
        private Map<Double, String> tileMap = new HashMap<>();
        Random r;

        private int randCoord(int limit) {
            return r.nextInt(limit);
        }

        public void tileTypeDisplay() {
            double mouseX = StdDraw.mouseX();
            double mouseY = StdDraw.mouseY();
            double l = tileID(Math.floor(mouseX), Math.floor(mouseY));
            String tileType = tiles[(int) Math.floor(mouseX)][(int) Math.floor(mouseY)].description();
            int s = 15;
            Font titleFont = new Font("Herculanum-Regular", Font.BOLD, s);
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.setFont(titleFont);
            StdDraw.textLeft(1, (HEIGHT + HUD_HEIGHT - 4), tileType);
            StdDraw.textLeft(1, (HEIGHT + HUD_HEIGHT - 6), "Elements Mastered: " + wins);
            StdDraw.textLeft(1, (HEIGHT + HUD_HEIGHT - 8), instructions);
            StdDraw.setFont();

        }

        private void fileTypeName(int x, int y, String type) {
            if (type.equals("fire")) {
                tiles[x][y] = Tileset.FIRE;
            } else if (type.equals("water")) {
                tiles[x][y] = Tileset.WATA;
            } else if (type.equals("earth")) {
                tiles[x][y] = Tileset.EARTH;
            } else if (type.equals("air")) {
                tiles[x][y] = Tileset.AIR;
            } else if (type.equals("avatar")) {
                tiles[x][y] = Tileset.AVATAR;
            }
        }

        public MakeWorld(int seed, String elem) {
            if (playMini) {
                this.instructions = "Get all the elements!";
            } else {
                this.instructions = "Traverse through each mini world and master the element.";
            }
            this.element = elem;
            SEED = seed;
            this.tiles = new TETile[WIDTH][HEIGHT + HUD_HEIGHT];
            // HUD DISPLAY
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    this.tiles[j][i] = Tileset.NOTHING;
                    double l = tileID(1.0 * j, 1.0 * i);
                    tileMap.put(l, "the abyss");
                }
            }
            for (int i = 0; i < HUD_HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    this.tiles[j][i + HEIGHT] = Tileset.NOTHING;
                    double l = tileID(1.0 * j, (i + HEIGHT) * 1.0);
                    tileMap.put(l, "HUD Header");
                }
            }
            for (int i = 0; i < WIDTH; i++) {
                this.tiles[i][HEIGHT] = Tileset.MOUNTAIN;
                double l = tileID(1.0 * i, 1.0 * HEIGHT);
                tileMap.put(l, "mountain range #views");
            }

            r = new Random(SEED);
            // creating random rooms
            int maxRooms = r.nextInt(HEIGHT) + 2;
            roomArr = new Room[maxRooms];
            for (int i = 0; i < maxRooms; i++) {
                int w = randCoord(MAX_SIZE) + 1;
                int h = randCoord(MAX_SIZE) + 1;
                randRoom(w, h);
            }

            //creating random hallways
            Room startingRoom = roomArr[0];
            for (int i = 0; i < roomCt; i++) {
                makeHall(startingRoom, roomArr[i]);
            }
            makeWalls();
            player = addAvatar(engineName);
            if (!playMini) {
                Build a = addRandIcon("fire");
                Build b = addRandIcon("water");
                Build c = addRandIcon("earth");
                Build d = addRandIcon("air");
                iconList.add(a);
                iconList.add(b);
                iconList.add(c);
                iconList.add(d);
            } else {
                Build a;
                for (int i = 0; i < COIN_MAX; i++) {
                    a = addRandIcon(this.element);
                    iconList.add(a);
                }
            }
            if (in.getClass().equals(KeyboardInputSource.class)) {
                this.drawFrame();
            }
        }

        public void drawFrame() {
            if (in.getClass() == KeyboardInputSource.class) {
                render.renderFrame(tiles);
                Font f = new Font("Monaco", Font.PLAIN, 20);
                StdDraw.setFont(f);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(1, (HEIGHT + HUD_HEIGHT - 2), player.name);
                tileTypeDisplay();
                StdDraw.show();
            }
        }

        public void playWorld() {
            char c;
            while (in.possibleNextInput()) {
                if (wins == COIN_MAX) {
                    gameWon = true;
                    break;
                }
                this.drawFrame();
                if ((in.getClass().equals(StringInputDevice.class) && in.possibleNextInput())
                        || (in.getClass().equals(KeyboardInputSource.class) && StdDraw.hasNextKeyTyped())) {
                    c = in.getNextKey();
                    out.print(c);
                    if (colon && c == 'Q') {
                        System.exit(0);
                    } else if (c == ':') {
                        colon = true;
                    } else {
                        colon = false;
                    }
                    this.moveAvatar(player, c);
                    if (playMini && !this.equals(miniWorld)) {
                        StdDraw.clear(Color.BLACK);
                        miniWorld.playWorld();
                    } else if (!playMini && this.equals(miniWorld)) {
                        break;
                    }
                }
            }
        }

        public Avatar addAvatar(String name) {
            int f = randCoord(floors.size() - 1);
            Build b = floors.get(f);
            Avatar a = new Avatar(b.x, b.y, 1, 1, name, 0);
            double l = tileID(1.0 * b.x, 1.0 * b.y);
            tiles[a.x][a.y] = Tileset.AVATAR;
            tileMap.put(l, "avatar");
            return a;
        }

        public Build addRandIcon(String type) {
            int f = randCoord(floors.size() - 1);
            Build b = floors.get(f);
            int x = randCoord(b.w) + b.x;
            int y = randCoord(b.h) + b.y;
            double l = tileID(x * 1.0, y * 1.0);
            tileMap.put(l, type);
            Build h = new Build(x, y, 1, 1);
            h.type = type;
            fileTypeName(x, y, type);
            if (in.getClass().equals(KeyboardInputSource.class)) {
                StdDraw.show();
            }
            return h;
        }


        private void moveAvatar(Avatar a, char key) {
            int xTemp = a.x;
            int yTemp = a.y;
            if (key == 'W') {
                if (!checkWall(a.x, a.y + a.h)) {
                    a.y++;
                }
            }
            if (key == 'S') {
                if (!checkWall(a.x, a.y - a.h)) {
                    a.y--;
                }
            }
            if (key == 'A') {
                if (!checkWall(a.x - a.w, a.y)) {
                    a.x--;
                }
            }
            if (key == 'D') {
                if (!checkWall(a.x + a.w, a.y)) {
                    a.x++;
                }
            }
            if (!(a.x == xTemp && a.y == yTemp)) {
                tiles[xTemp][yTemp] = Tileset.FLOOR;
                double l = tileID(1.0 * xTemp, 1.0 * yTemp);
                tileMap.put(l, "floor");
                double j = tileID(1.0 * a.x, 1.0 * a.y);
                tiles[a.x][a.y] = Tileset.AVATAR;
                if (onIcon(j)) {
                    if (playMini) {
                        for (Build i : iconList) {
                            if (i.x == a.x && i.y == a.y) {
                                iconList.remove(i);
                                break;
                            }
                        }
                        if (iconList.isEmpty()) {
                            wins++;
                            playMini = false;
                        }
                    } else {
                        playMini = true;
                        miniWorld = new MakeWorld(r.nextInt(), tileMap.get(j));
                    }
                }
                tileMap.put(j, "avatar");
            }
            drawFrame();
        }

        private boolean onIcon(double id) {
            if (tileMap.containsKey(id)) {
                return (!tileMap.get(id).equals("wall") && !tileMap.get(id).equals("floor")
                        && !tileMap.get(id).equals("avatar"));
            }
            return false;
        }

        private boolean checkWall(int x, int y) {
            return (tiles[x][y].equals(Tileset.WALL));
        }

        private void makeWalls() {

            for (Build f : floors) {
                if (tiles[f.x][f.y + 1] == Tileset.NOTHING) {
                    tiles[f.x][f.y + 1] = Tileset.WALL;
                    double l = tileID(1.0 * f.x, 1.0 * (f.y + 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x + 1][f.y + 1] == Tileset.NOTHING) {
                    tiles[f.x + 1][f.y + 1] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x + 1), 1.0 * (f.y + 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x + 1][f.y] == Tileset.NOTHING) {
                    tiles[f.x + 1][f.y] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x + 1), 1.0 * f.y);
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x + 1][f.y - 1] == Tileset.NOTHING) {
                    tiles[f.x + 1][f.y - 1] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x + 1), 1.0 * (f.y - 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x][f.y - 1] == Tileset.NOTHING) {
                    tiles[f.x][f.y - 1] = Tileset.WALL;
                    double l = tileID(1.0 * f.x, 1.0 * (f.y - 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x - 1][f.y - 1] == Tileset.NOTHING) {
                    tiles[f.x - 1][f.y - 1] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x - 1), 1.0 * (f.y - 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x - 1][f.y + 1] == Tileset.NOTHING) {
                    tiles[f.x - 1][f.y + 1] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x - 1), 1.0 * (f.y + 1));
                    tileMap.put(l, "wall");
                }
                if (tiles[f.x - 1][f.y] == Tileset.NOTHING) {
                    tiles[f.x - 1][f.y] = Tileset.WALL;
                    double l = tileID(1.0 * (f.x - 1), 1.0 * f.y);
                    tileMap.put(l, "wall");
                }
            }
        }

        private void makeHall(Room room1, Room room2) {
            if (!room1.isConnected(room2)) {
                int x1 = randCoord(room1.w) + room1.x;
                int y1 = randCoord(room1.h) + room1.y;
                int x2 = randCoord(room2.w) + room2.x;
                int y2 = randCoord(room2.h) + room2.y;
                int hallwayHL = Math.abs(x1 - x2) + 1;
                int hallwayVL = Math.abs(y1 - y2) + 1;
                Hallway vert;
                Hallway hor;
                if (x1 < x2) {
                    builder(x1, y1, hallwayHL, 1);
                    hor = new Hallway(x1, y1, hallwayHL, 1);

                    if (y1 < y2) {
                        builder(x2, y1, 1, hallwayVL);
                        vert = new Hallway(x2, y1, 1, hallwayVL);
                    } else {
                        builder(x2, y2, 1, hallwayVL);
                        vert = new Hallway(x2, y2, 1, hallwayVL);
                    }
                } else {
                    builder(x2, y1, hallwayHL, 1);
                    hor = new Hallway(x2, y1, hallwayHL, 1);
                    if (y1 < y2) {
                        builder(x2, y1, 1, hallwayVL);
                        vert = new Hallway(x2, y1, 1, hallwayVL);
                    } else {
                        builder(x2, y2, 1, hallwayVL);
                        vert = new Hallway(x2, y2, 1, hallwayVL);
                    }
                }
                if (vert != null) {
                    hallList.add(vert);
                }
                if (hor != null) {
                    hallList.add(hor);
                }
                room1.connectedRooms.add(room2);
                for (Room j : room1.connectedRooms) {
                    if (!room2.connectedRooms.contains(j)) {
                        room2.connectedRooms.add(j);
                    }
                }
            }

        }

        private TETile[][] getTiles() {
            return this.tiles;
        }

        private boolean boundaryCheck(int x, int y) {
            return (x < WIDTH - 1 && y < HEIGHT - 1);
        }

        private void displayBuild(Build z) {
            for (int j = z.y; j < z.y + z.h; j++) {
                for (int i = z.x; i < z.x + z.w; i++) {
                    tiles[i][j] = Tileset.FLOOR;
                    double l = tileID(1.0 * i, 1.0 * j);
                    tileMap.put(l, "floor");
                    Build f = new Build(i, j, 1, 1);
                    floors.add(f);
                }
            }
        }

        private Build builder(int x, int y, int w, int h) {
            Build curr = null;
            if (boundaryCheck(x, y)) {
                if (y + h >= HEIGHT - 2) {
                    h = HEIGHT - 1 - y;
                }
                if (x + w >= WIDTH - 2) {
                    w = WIDTH - 1 - x;
                }
                curr = new Build(x, y, w, h);
                displayBuild(curr);
            }
            return curr;
        }

        private void randRoom(int w, int h) {
            int x = randCoord(WIDTH - 2) + 1;
            int y = randCoord(HEIGHT - 2) + 1;
            if (boundaryCheck(x, y)) {
                if (y + h >= HEIGHT - 2) {
                    h = HEIGHT - 1 - y;
                }
                if (x + w >= WIDTH - 2) {
                    w = WIDTH - 1 - x;
                }
            }
            Build b = builder(x, y, w, h);
            Room curr = new Room(x, y, b.w, b.h);
            curr.connectedRooms.add(curr);
            roomArr[roomCt] = curr;
            roomCt++;
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void qAndAnsFrame(String q, String ans) {
        if (!(in.getClass() == StringInputDevice.class)) {
            StdDraw.clear();
            Font qFont = new Font("Serif", Font.BOLD, 15);
            StdDraw.setFont(qFont);
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.text(.5, .5, q);
            Font ansFont = new Font("Serif", Font.PLAIN, 15);
            StdDraw.setFont(ansFont);
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.text(.5, .2, ans);
            StdDraw.show();
        }
    }

    public void drawSeedRequest(String s) {
        if (in.getClass() == KeyboardInputSource.class) {
            StdDraw.clear();
            Font seedQuestion = new Font("Serif", Font.BOLD, 20);
            StdDraw.setFont(seedQuestion);
            StdDraw.text(.5, .5, "What seed? Click S after entering.");
            Font seedInputFont = new Font("Serif", Font.PLAIN, 15);
            StdDraw.setFont(seedInputFont);
            StdDraw.text(.5, .2, s);
            StdDraw.show();
        }

    }

    public void requestSeed(InputSource i) {
        drawSeedRequest(" ");
        String ans = "";
        int sd = 0;
        char c = i.getNextKey();
        out.print(c);
        while (i.possibleNextInput() && c != 'S') {
            sd = sd * 10 + Integer.parseInt(String.valueOf(c));
            ans = ans + c;
            drawSeedRequest(ans);
            c = i.getNextKey();
            out.print(c);
        }
        SEED = sd;
    }

    public String requestName() {
        String name = "";
        String q = "lemme get to know you (; whats ur name shawty? Click * to enter.";
        qAndAnsFrame(q, name);
        while (in.possibleNextInput()) {
            char c = in.getNextKey();
            out.print(c);
            if (c != '*') {
                name = name + c;
                qAndAnsFrame(q, name);
            } else {
                break;
            }
        }
        return name;
    }


    public void interactWithKeyboard() {
        KeyboardInputSource keyInput = new KeyboardInputSource();
        in = keyInput;
        getInput(in);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public List getInput(InputSource i) {
        List<MakeWorld> worlds = new ArrayList<>();
        File f = new File(path);
        while (i.possibleNextInput()) {
            char c = i.getNextKey();
            if (c == 'M') {
                out = new Out(path);
                out.print(c);
                String name = requestName();
                engineName = name;
                requestSeed(i);
                initTERender(i);
                currWorld = new MakeWorld(SEED, null);
            }
            if (c == 'N') {

                out = new Out(path);
                out.print(c);
                requestSeed(i);
                initTERender(i);
                currWorld = new MakeWorld(SEED, null);
            }
            if (i.getClass() == KeyboardInputSource.class) {
                if (c == 'L') {
                    read = new In(path);
                    if (read.isEmpty()) {
                        System.exit(0);
                    }
                    String nxt = read.readLine();
                    if (!nxt.contains(":Q")) {
                        System.exit(0);
                    } else {
                        String onlySavedString = "";
                        String[] splitUnsaved = nxt.split(":Q");
                        if (!(nxt.charAt(nxt.length() - 2) == ':' && nxt.charAt(nxt.length() - 1) == 'Q')) {
                            for (int k = 0; k < splitUnsaved.length - 1; k++) {
                                onlySavedString += splitUnsaved[k];
                            }
                        } else {
                            for (int k = 0; k < splitUnsaved.length; k++) {
                                onlySavedString += splitUnsaved[k];
                            }
                        }
                        InputSource savedKey = new StringInputDevice(onlySavedString);
                        in = savedKey;
                        worlds = getInput(savedKey);
                        currWorld = worlds.get(0);
                        miniWorld = worlds.get(1);
                        out = new Out(path);
                        out.print(onlySavedString + ":Q");
                        in = new KeyboardInputSource();
                        initTERender(in);
                    }
                }
                if (c == 'Q') {
                    System.exit(0);
                }
            }
            if (colon && c == 'Q') {
                out.print(c);
                sysExit(i);
            } else {
                colon = (c == ':');
            }
            if (playMini) {
                miniWorld.playWorld();
            } else if (currWorld == null) {
                sysExit(in);
                break;
            }
            currWorld.playWorld();
            if (gameWon) {
                wonGameDisplay();
            }
        }
        worlds.add(currWorld);
        worlds.add(miniWorld);
        return worlds;
    }

    public void sysExit(InputSource ins) {
        if (ins.getClass().equals(KeyboardInputSource.class)) {
            System.exit(0);
        }
    }


    public void wonGameDisplay() {
        StdDraw.clear(Color.WHITE);
        Font blueFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(blueFont);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(WIDTH / 2, (HUD_HEIGHT + HEIGHT) / 2, "YOU ARE NOW AN AIRBENDER. BEND THAT SHIT FR");
        StdDraw.show();
    }

    public TETile[][] interactWithInputString(String input) {
        StringInputDevice s = new StringInputDevice(input);
        in = s;
        getInput(in);
        return this.currWorld.getTiles();
    }

    public static void main(String[] args) {
        Engine e = new Engine();
        TETile[][] test;
        test = e.interactWithInputString("N123SJ*wwwww");
    }
}