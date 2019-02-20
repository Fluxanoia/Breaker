package co.uk.fluxanoia.entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import co.uk.fluxanoia.main.ResourceManager;
import co.uk.fluxanoia.map.Camera;
import co.uk.fluxanoia.util.Tween.TweenType;

// The Animator class, manages entity visuals
public class Animator {

	// The tileset of the entity
	private BufferedImage tileset;
	// The size of tiles in the tileset
	private Dimension tileSize;
	// The animations for the animator
	private ArrayList<Animation> animations;
	// The current animation
	private int animIndex;
	// Whether the animation has changed since last checked
	private boolean moved;

	// Constructs an animator
	public Animator() {
		// Initialise values
		this.tileset = null;
		this.tileSize = null;
		this.moved = false;
		this.animIndex = 0;
		this.animations = new ArrayList<>();
	}

	// Updates the animator
	public boolean update() {
		if (!animations.isEmpty()) return this.animations.get(animIndex).update();
		return false;
	}

	// Draws the image at the required x and y values
	public void drawImage(Graphics2D g, Camera c, Rectangle box) {
		drawImage(g, c, box, false);
	}
	public void drawImage(Graphics2D g, Camera c, Rectangle box, boolean flip) {
		// If we can't draw, return
		if (tileset == null || tileSize == null || animations.isEmpty()) return;
		// Gets the position from the animation
		Rectangle pos = this.getBounds(box);
		pos.translate((int) -c.getX(), (int) -c.getY());
		// Set up the affine transform
		AffineTransform at = new AffineTransform();
		at.translate(pos.getCenterX(), pos.getCenterY());
		double rot = Math.toRadians(animations.get(animIndex).getRotation());
		if (flip) rot *= -1;
		at.rotate(rot);
		g.setTransform(at);
		// Draw the image
		double x, y, w;
		BufferedImage image = this.getImage();
		Point centre = animations.get(animIndex).getCentre();
		x = -pos.getWidth() / 2 - centre.getX();
		y = -pos.getHeight() / 2 - centre.getY();
		w = (pos.getWidth() == 0 || pos.getHeight() == 0) ? image.getWidth() : pos.getWidth();
		if (flip) {
			x += w;
			w *= -1;
		}
		if (pos.getWidth() == 0 || pos.getHeight() == 0) {
			g.drawImage(image, (int) x, (int) y, (int) w,
					image.getHeight(), null);
		} else {
			g.drawImage(image, (int) x, (int) y,
					(int) w, (int) pos.getHeight(), null);
		}
		g.setTransform(new AffineTransform());
	}

	// Adds an animation
	public int addAnimation(String path) {
		ArrayList<String> content = ResourceManager.readFile(path);
		// Getting textures
		if (content.size() < 1) return -1;
		String[] data, parts;
		parts = content.get(0).split(" ");
		Point[] textures = new Point[parts.length];
		for (int i = 0; i < parts.length; i++) {
			data = parts[i].split("_");
			if (data.length != 2) return -1;
			textures[i] = new Point(Integer.valueOf(data[0]),
					Integer.valueOf(data[1]));
		}
		// Getting durations
		if (content.size() < 2)
			return addAnimation(textures, null, null, null, null, null, null, null);
		parts = content.get(1).split(" ");
		if (parts.length < textures.length) return -1;
		int[] durations = new int[textures.length];
		for (int i = 0; i < textures.length; i++) {
			durations[i] = Integer.valueOf(parts[i]);
		}
		// Getting (x, y, w, h)s
		if (content.size() < 3) return addAnimation(textures, durations, null,
				null, null, null, null, null);
		parts = content.get(2).split(" ");
		if (parts.length < textures.length) return -1;
		TweenType tweenType;
		AnimationTween[] x, y, w, h;
		x = new AnimationTween[textures.length];
		y = new AnimationTween[textures.length];
		w = new AnimationTween[textures.length];
		h = new AnimationTween[textures.length];
		for (int i = 0; i < textures.length; i++) {
			data = parts[i].split("_");
			if (data.length != 9) return -1;
			tweenType = TweenType.toTweenType(data[0]);
			if (tweenType == null) return -1;
			x[i] = new AnimationTween(Double.valueOf(data[1]),
					Double.valueOf(data[2]), durations[i], tweenType);
			y[i] = new AnimationTween(Double.valueOf(data[3]),
					Double.valueOf(data[4]), durations[i], tweenType);
			w[i] = new AnimationTween(Double.valueOf(data[5]),
					Double.valueOf(data[6]), durations[i], tweenType);
			h[i] = new AnimationTween(Double.valueOf(data[7]),
					Double.valueOf(data[8]), durations[i], tweenType);
		}
		// Getting rotations
		if (content.size() < 4)
			return addAnimation(textures, durations, x, y, w, h, null, null);
		parts = content.get(3).split(" ");
		AnimationTween[] rotations = new AnimationTween[textures.length];
		Point[] centres = new Point[textures.length];
		for (int i = 0; i < textures.length; i++) {
			data = parts[i].split("_");
			if (data.length != 5) return -1;
			tweenType = TweenType.toTweenType(data[0]);
			if (tweenType == null) return -1;
			rotations[i] = new AnimationTween(Double.valueOf(data[1]),
					Double.valueOf(data[2]), durations[i], tweenType);
			centres[i] = new Point(Integer.valueOf(data[3]), Integer.valueOf(data[4]));
		}
		// Return the animation
		return addAnimation(textures, durations, x, y, w, h, rotations, centres);
	}
	public int addAnimation(Point[] textures, int[] durations,
			AnimationTween[] x, AnimationTween[] y, AnimationTween[] w,
			AnimationTween[] h, AnimationTween[] rotations, Point[] centres) {
		Animation anim = new Animation(textures, durations, x, y, w, h,
				rotations, centres);
		animations.add(anim);
		return animations.indexOf(anim);
	}

	// Returns the bounds of the animation
	public Rectangle getBounds(Rectangle defBounds) {
		int x = (int) (defBounds.getX() + animations.get(animIndex).getX());
		int y = (int) (defBounds.getY() + animations.get(animIndex).getY());
		int w = (int) animations.get(animIndex).getWidth();
		int h = (int) animations.get(animIndex).getHeight();
		if (w == 0) w = (int) defBounds.getWidth();
		if (h == 0) h = (int) defBounds.getHeight();
		return new Rectangle(x, y, w, h);
	}

	// Sets an animation
	public void forceAnimation(int id, int reqLoops) {
		this.animations.get(animIndex).reset();
		this.animIndex = id;
		this.animations.get(animIndex).start(reqLoops);
	}

	public void setAnimation(int id, int reqLoops) {
		if (animIndex == id) return;
		forceAnimation(id, reqLoops);
	}

	// Drops the moved value
	public boolean dropMoved() {
		if (moved) {
			moved = false;
			return true;
		}
		return false;
	}

	// Gets the current image
	public BufferedImage getImage() {
		if (animations.isEmpty()) return null;
		return this.getImage(animations.get(animIndex).getTextureX(),
				animations.get(animIndex).getTextureY());
	}

	// Gets a specific image
	public BufferedImage getImage(int x, int y) {
		return ResourceManager.getTile(tileset, null, x, y,
				(int) tileSize.getWidth(), (int) tileSize.getHeight());
	}

	// Sets the tileset
	public void setTiles(BufferedImage tileset) {
		this.tileset = tileset;
	}

	// Sets the tile dimensions
	public void setTileDimensions(Dimension tileSize) {
		this.tileSize = tileSize;
	}

}
