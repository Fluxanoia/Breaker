package co.uk.fluxanoia.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;

// The GridBackground class, is a general, scaled, window-sized image
public class GridBackground extends Drawable {

    // The size of the grid boxes
    private final int GRID_SIZE = 32;
    // The offset of the two grids
    private final int FORE_OFFSET = 8;
    private final int BACK_OFFSET = 0;
    // The base opacity of the fore/background
    private final int FORE_OPACITY = 160;
    private final int BACK_OPACITY = 80;

    // The opacity of the grid
    private Tween opacity;
    // The colour components of the grid
    private Tween r, g, b;
    // The current shifts of the grids
    private double fore_x_shift;
    private double fore_y_shift;
    private double back_x_shift;
    private double back_y_shift;
    // The direction of the shifts of the grids
    private Tween fore_x_dir;
    private Tween fore_y_dir;
    private Tween back_x_dir;
    private Tween back_y_dir;

    // Constructs the grid
    public GridBackground(int r, int g, int b, double a) {
        // Construct the Drawable
        super();
        // Assigns values
        this.r = new Tween(r);
        this.g = new Tween(g);
        this.b = new Tween(b);
        // Initialises the values
        opacity = new Tween(a);
        fore_x_shift = 0;
        fore_y_shift = 0;
        back_x_shift = 0;
        back_y_shift = 0;
        fore_x_dir = new Tween(0);
        fore_y_dir = new Tween(0);
        back_x_dir = new Tween(0);
        back_y_dir = new Tween(0);
    }

    // Updates the background
    public void update() {
    	// Update the tween values
    	r.update();
    	g.update();
    	b.update();
    	fore_x_dir.update();
    	fore_y_dir.update();
    	back_x_dir.update();
    	back_y_dir.update();
        // Shift the grid position
        this.fore_x_shift += fore_x_dir.value();
        this.fore_y_shift += fore_y_dir.value();
        this.back_x_shift += back_x_dir.value();
        this.back_y_shift += back_y_dir.value();
        // Modulo the sizes so it doesn't get too large
        this.fore_x_shift %= GRID_SIZE;
        this.fore_y_shift %= GRID_SIZE;
        this.back_x_shift %= GRID_SIZE;
        this.back_y_shift %= GRID_SIZE;
        // Update the opacity
        opacity.update();
        if (opacity.value() == 0) return;
        // Push the clip bounds
        this.pushClipBounds(Display.drawBounds());
    }

    // Draws the background
    public void draw(Graphics2D g) {
        int i_x, i_y;
        // Draw the background
        g.setColor(new Color(getRed() / 10, getGreen() / 10, getBlue() / 10));
        g.fill(Display.drawBounds());
        if (opacity.value() == 0) return;
        // Draw the foreground grid
        g.setColor(new Color(getRed(), getGreen(), getBlue(), foregroundOpacity()));
        i_x = (int) (FORE_OFFSET + fore_x_shift);
        i_y = (int) (FORE_OFFSET + fore_y_shift);
        for (int i = -1; i < boxWidth() + 1; i++) {
            g.drawLine(i_x + i * GRID_SIZE, 0,
                    i_x + i * GRID_SIZE, Main.DRAW_HEIGHT);
        }
        for (int i = -1; i < boxHeight() + 1; i++) {
            g.drawLine(0, i_y + i * GRID_SIZE,
                    Main.DRAW_WIDTH, i_y + i * GRID_SIZE);
        }
        // Draw the background grid
        g.setColor(new Color(getRed(), getGreen(), getBlue(), backgroundOpacity()));
        i_x = (int) (BACK_OFFSET + back_x_shift);
        i_y = (int) (BACK_OFFSET + back_y_shift);
        for (int i = -1; i < boxWidth() + 1; i++) {
            g.drawLine(i_x + i * GRID_SIZE, 0,
                    i_x + i * GRID_SIZE, Main.DRAW_HEIGHT);
        }
        for (int i = -1; i < boxHeight() + 1; i++) {
            g.drawLine(0, i_y + i * GRID_SIZE,
                    Main.DRAW_WIDTH, i_y + i * GRID_SIZE);
        }
    }

    // Sets the colour of the grid
    public void setColour(Color c, int duration) {
    	ErrorHandler.checkNull(c, "A Background was given a null colour.");
        this.r.move(TweenType.EASE_IN, c.getRed(), duration, 0);
        this.g.move(TweenType.EASE_IN, c.getGreen(), duration, 0);
        this.b.move(TweenType.EASE_IN, c.getBlue(), duration, 0);
    }

    // Sets the directions of the grid
    public void setGridDirections(double f_x, double f_y, double b_x, double b_y) {
        fore_x_dir.move(TweenType.EASE_OUT, f_x, 15, 0);
        fore_y_dir.move(TweenType.EASE_OUT, f_y, 15, 0);
        back_x_dir.move(TweenType.EASE_OUT, b_x, 15, 0);
        back_y_dir.move(TweenType.EASE_OUT, b_y, 15, 0);
    }
    
    // Returns the colour components
    private int getRed() {
    	return (int) r.value();
    }
    private int getGreen() {
    	return (int) g.value();
    }
    private int getBlue() {
    	return (int) b.value();
    }

    // Returns the foreground opacity
    private int foregroundOpacity() {
        return (int) (opacity.value() * FORE_OPACITY);
    }

    // Returns the background opacity
    private int backgroundOpacity() {
        return (int) (opacity.value() * BACK_OPACITY);
    }

    // Returns the amount of grid boxes that can fit vertically
    private int boxHeight() {
        return Main.DRAW_HEIGHT / GRID_SIZE;
    }

    // Returns the amount of grid boxes that can fit horizontally
    private int boxWidth() {
        return Main.DRAW_WIDTH / GRID_SIZE;
    }

    // Returns the opacity tween
    public Tween getOpacity() {
        return opacity;
    }

}

