/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package collzakk;

import java.util.ArrayList;
import processing.core.*;

import java.util.HashMap;
import java.util.List;

public class Collzakk extends PApplet {
    
    int windowSize;
    int ulamMax;
    
    HashMap<Integer, Integer> nextTree;
    HashMap<Integer, Integer> occurrences;
    
    int seed;
    int current;
    
    int max;
    float radMult;
    float angleStep;
    
    int minColor;
    int maxColor;
    
    @Override
    public void setup() {
        windowSize = 900;
        ulamMax = windowSize * windowSize;
        size(windowSize, windowSize);
        
        background(0xFF111111);
        noStroke();
        
        nextTree = new HashMap<>();
        occurrences = new HashMap<>();
        
        nextTree.put(2, 1);
        
        seed = 2;
        current = seed;
        
        max = 2147483646 / 2;
        radMult = ((float) windowSize / 2) / log(max);
        angleStep = 1.0f / log(max);
        
        minColor = color(71, 12, 0);
        maxColor = color(252, 168, 151);
    }
    
    @Override
    public void draw() {
        if (seed <= ulamMax) {
            if (current == 1) {
                seed++;
                current = seed;
                println(seed);
            } else{
                if (occurrences.containsKey(current)) {
                    occurrences.put(current, occurrences.get(current) + 1);
                } else {
                    occurrences.put(current, 0);
                }
                fill(occurrenceColor(current));
                ulamPlot(current);
                if (seed%100 == 0) println(occurrences);
                int next = nextCollatz(current);
                current = next;
            }
        }
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
    
    public int occurrenceColor(int x) {
        return lerpColor(minColor, maxColor, (float) occurrences.get(x) / 450f);
    }
    
    public int magnitudeColor(int x) {
        float frac = 100 * sqrt((float) x / (float) max);
        println(frac);
        return lerpColor(minColor, maxColor, frac);
    }
    
    public int factorColor(int x) {
        return lerpColor(minColor, maxColor, (float) numPrimeFactors(x) / 31.0f );
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
    
    public void ulamPlot(int n) {
        
        if (n >= 1 && n <= ulamMax) {
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

            rect(windowSize / 2 + x, windowSize / 2 + y, 1, 1);
        }
    }
}
