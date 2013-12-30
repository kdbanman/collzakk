/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collzakk;

import java.util.ArrayList;
import processing.core.*;

import java.util.HashMap;
import java.util.List;
import static processing.core.PApplet.println;

public class Collzakk extends PApplet {
    
    int windowSize = 900;
    int cellSize = 1;
    int ulamMax = windowSize * windowSize / cellSize / cellSize;
    
    HashMap<Integer, Integer> nextTree;
    HashMap<Integer, ArrayList<Coord>> sequences;
    
    int seed;
    int current;
    
    int max;
    float radMult;
    float angleStep;
    
    int minColor;
    int maxColor;
    
    int frame = 1;
    int prevFrame = 1;
    int frameNumber = 1;
    
    int pixelSeed[][];
    
    @Override
    public void setup() {
        
        size(windowSize, windowSize);
        frameRate(200);
        background(0xFF050505);
        noStroke();
        
        seed = 2;
        current = seed;
        
        //radial plot constants
        max = 2147483646 / 2;
        radMult = ((float) windowSize / 2) / log(max);
        angleStep = 1.0f / log(max);
        
        minColor = color(71, 12, 0);
        maxColor = color(252, 168, 151);
        
        // zero indices are ignored for convenience
        pixelSeed = new int[windowSize + 1][windowSize + 1];
        sequences = new HashMap<>(ulamMax);
        
        nextTree = new HashMap<>();
        nextTree.put(2, 1);
        sequences.put(2, new ArrayList<Coord>());
        sequences.get(2).add(ulamPlot(2));
        sequences.get(2).add(ulamPlot(1));
        while (seed <= ulamMax) {
            if (nextTree.containsKey(current)) {
                seed++;
                current = seed;
                sequences.put(seed, new ArrayList<Coord>());
            } else{
                Coord currCoord = ulamPlot(current);
                if (currCoord != null) {
                    sequences.get(seed).add(currCoord);
                    pixelSeed[currCoord.x][currCoord.y] = seed;
                }
                int next = nextCollatz(current);
                nextTree.put(current, next);
                current = next;
            }
            
            if (seed % 10000 == 0) println((100 * seed / ulamMax) + "%");
        }
        // reset for draw.  global state ftw.  YYEEEEEEAAAAAAHHHHHHHHHHHHHHHH
        seed = 2;
    }
    
    @Override
    public void draw() {
        
        // square: seed >= frame
        if (seed >= frame && seed <= ulamMax) {
            background(0xFF050505);
            for (int i = 2; i <= seed; i++) {
                for (Coord c : sequences.get(i)) {
                    fill(magnitudeColor(pixelSeed[c.x][c.y], seed));
                    rect(c.x - cellSize / 2, c.y - cellSize / 2, cellSize, cellSize);
                }
                if (i % 10000 == 0) println((100*i/seed) + "%");
            }
            
            for (int i = max(446, seed - (9 + frame - prevFrame)); i <= seed; i++) {
                for (Coord c : sequences.get(i)) {
                    //fill((((magnitudeColor(pixelSeed[c.x][c.y], seed) & 0x44FFFFFF) - (0x04000000 * ((seed - i))))));
                    fill(lerpTransparency(magnitudeColor(pixelSeed[c.x][c.y], seed), 0x44, 1f - (9f + (float)frame - (float)prevFrame) / (float)seed));
                    rect(c.x - 2, c.y - 2, 5, 5);
                }
            }
            saveFrame("screens/frame" + frameNumberString() + ".png");
            frameNumber++;
            prevFrame = frame;
            frame += max(1, frameNumber / 50);
        } else if (seed > ulamMax) {
            println("final frame " + (frameNumber - 1));
            exit();
        }
        seed++;
        /* collision testing
        
        int colol = 0;
        if (current % 3 == 0) colol = 0xFF0000FF;
        else if (current % 2 == 0) colol = 0xFFFF0000;
        else colol = 0xFF00FF00;
        fill(colol);
        ulamPlot(current);
        current += 1;
        * */
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{collzakk.Collzakk.class.getName()});
    }
    
    public int nextCollatz(int n) {
        if (n % 2 == 0) {
            return n / 2;
        } else {
            return n * 3 + 1;
        }
    }
    
    public void incrementSeed() {
        while (nextTree.containsKey(seed)) {
            seed++;
        }
    }
    
    /**
     * @param col hex color
     * @param max 0-255
     * @param frac 0.0-1.0
     * @return hex color
     */
    public int lerpTransparency(int col, int max, float frac) {
        return col & 0x00FFFFFF | ((int) ((float) max * frac) * 0x1000000);
    }
    
    public int magnitudeColor(int x, int max) {
        float frac = ((float) x / (float) max);
        return lerpColor(minColor, maxColor, frac);
    }
    
    public int factorColor(int x, int max) {
        return lerpColor(minColor, maxColor, (float) numPrimeFactors(x) / (log(max) / log(2)) );
    }
    
    public int numPrimeFactors(int x) {
        List<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= x; i++) {
          while (x % i == 0) {
            factors.add(i);
            x /= i;
          }
        }
        return factors.size();
    }
    
    public void radPlot(int x) {
        float r = radMult * log(x);
        float theta = x * angleStep;
        rect(windowSize/2 + (int) (r * cos(theta)), windowSize/2 + (int) (r * sin(theta)), 2, 2);
    }
    
    //scaled ulam coordinates
    public Coord ulamPlot(int n) {
        
        if (n > 1 && n <= ulamMax) {
            int root = ceil(sqrt(n));
            int shell = root + (root % 2 == 0 ? 1 : 0);
            int dist = shell * shell - n;
            int corner = dist / (shell - 1);

            int half = shell / 2;
            dist = dist % (shell - 1);
            int x, y;
            if (corner == 0) {
                x = -half;
                y = half - dist;
            } else if (corner == 1) {
                x = -half + dist;
                y = -half;
            } else if (corner == 2) {
                x = half;
                y = -half + dist;
            } else {
                x = half - dist;
                y = half;
            }

            return new Coord(windowSize / 2 + x * cellSize, windowSize / 2 + y * cellSize);
        } else if (n == 1) {
            return new Coord(windowSize / 2, windowSize / 2);
        } else {
            return null;
        }
    }
    
    public String frameNumberString() {
        String ret = "";
        for (int i = 0; i < 6 - Integer.toString(frameNumber).length(); i++) {
            ret += "0";
        }
        ret += Integer.toString(frameNumber);
        return ret;
    }
    
    public class Coord {
        public final int x, y;
        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
