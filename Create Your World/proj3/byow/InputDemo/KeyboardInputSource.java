package byow.InputDemo;

/**
 * Created by hug.
 */
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;
    public KeyboardInputSource() {
        Font titleFont = new Font("Herculanum-Regular", Font.BOLD, 20);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.setFont(titleFont);
        StdDraw.text(.5, .5, "Are you sexy enough to be an Avatar?");
        Font options = new Font("Serif", Font.PLAIN, 10);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(options);
        StdDraw.text(.5, .4, "[N] New Game" );
        StdDraw.text(.5, .3, "[M] New Game with Name" );
        StdDraw.text(.5,.2, "[L] Load Game" );
        StdDraw.text(.5,.1, "[Q] I suck and I quit already" );
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}
