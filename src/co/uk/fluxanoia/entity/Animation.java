package co.uk.fluxanoia.entity;

import java.awt.Point;

// The Animation class, represents a single animation for an entity
public class Animation {
	
	// The position of the indexed texture
	private Point[] textures;
	// The time for the animation to move on
	private int[] durations;
	// The positions/sizes of the textures relative to the centre
	private AnimationTween[] x;
	private AnimationTween[] y;
	private AnimationTween[] w;
	private AnimationTween[] h;
	// The rotations of the textures
	private AnimationTween[] rotations;
	// The centre points of rotations
	private Point[] centres;
	// The amount of loops since the animation started
	private int loops;
	// The amount of loops to end the animation
	private int reqLoops;
	// Whether the animation is over or not
	private boolean finished;
	// The amount of updates since the last change
	private int ticks;
	// The current animation index
	private int index;

	// Constructs an animation
	public Animation(Point[] textures, int[] durations, AnimationTween[] x, AnimationTween[] y,
			AnimationTween[] w, AnimationTween[] h,
			AnimationTween[] rotations, Point[] centres) {
		// Assign values
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.textures = textures;
		this.durations = durations;
		this.rotations = rotations;
		this.centres = centres;
		// Initialise values
		this.loops = 0;
		this.ticks = 0;
		this.index = 0;
		this.reqLoops = -1;
		this.finished = true;
	}

	// Updates the animation
	public boolean update() {
		boolean moved = false;
		if (finished) return false;
		// Update the tweens
		moved |= this.updateTweens();
		if (ticks == getDuration()) {
			// If at the end of the duration, move on
			ticks = 0;
			this.resetTweens();
			index++;
			if (index < textures.length) this.startTweens();
			moved = true;
			if (index == textures.length) {
				// If at the end of the array, move to the front
				index = 0;
				if (loops != reqLoops) this.startTweens();
				loops++;
				if (loops == reqLoops) {
					// If at the end of the loops, stop
					loops = 0;
					finished = true;
					return false;
				}

			}
		}
		ticks++;
		return moved;
	}

	// Starts the animation
	public void start(int reqLoops) {
		this.finished = false;
		this.index = 0;
		this.startTweens();
		this.reqLoops = reqLoops;
	}
	
	// Resets the animation
	public void reset() {
		this.resetTweens();
		this.finished = false;
		this.reqLoops = -1;
		this.index = 0;
		this.ticks = 0;
	}

	// Updates all the tweens safely
	private boolean updateTweens() {
		boolean moved = false;
		if (getTweenX() != null) moved |= getTweenX().update();
		if (getTweenY() != null) moved |= getTweenY().update();
		if (getTweenWidth() != null) moved |= getTweenWidth().update();
		if (getTweenHeight() != null) moved |= getTweenHeight().update();
		if (getTweenRotation() != null) moved |= getTweenRotation().update();
		return moved;
	}
	
	// Starts all the tweens safely
	private boolean startTweens() {
		boolean moved = false;
		if (getTweenX() != null) getTweenX().start();
		if (getTweenY() != null) getTweenY().start();
		if (getTweenWidth() != null) getTweenWidth().start();
		if (getTweenHeight() != null) getTweenHeight().start();
		if (getTweenRotation() != null) getTweenRotation().start();
		return moved;
	}
	
	// Starts all the tweens safely
	private boolean resetTweens() {
		boolean moved = false;
		if (getTweenX() != null) getTweenX().reset();
		if (getTweenY() != null) getTweenY().reset();
		if (getTweenWidth() != null) getTweenWidth().reset();
		if (getTweenHeight() != null) getTweenHeight().reset();
		if (getTweenRotation() != null) getTweenRotation().reset();
		return moved;
	}
	
	// Returns the current duration
	public int getDuration() {
		if (durations == null) return -1;
		return durations[index];
	}

	// Returns the current position
	public double getX() {
		if (x == null) return 0;
		return x[index].value();
	}
	public double getY() {
		if (y == null) return 0;
		return y[index].value();
	}
	public double getWidth() {
		if (w == null) return 0;
		return w[index].value();
	}
	public double getHeight() {
		if (h == null) return 0;
		return h[index].value();
	}
	
	// Returns the position tweens
	public AnimationTween getTweenX() {
		if (x == null) return null;
		return x[index];
	}
	public AnimationTween getTweenY() {
		if (y == null) return null;
		return y[index];
	}
	public AnimationTween getTweenWidth() {
		if (w == null) return null;
		return w[index];
	}
	public AnimationTween getTweenHeight() {
		if (h == null) return null;
		return h[index];
	}
	public AnimationTween getTweenRotation() {
		if (rotations == null) return null;
		return rotations[index];
	}

	// Returns the current rotation
	public double getRotation() {
		if (rotations == null) return 0;
		return rotations[index].value();
	}

	// Returns the current centre
	public Point getCentre() {
		if (centres == null) return new Point(0, 0);
		return centres[index];
	}
	
	// Returns the texture indices
	public int getTextureX() {
		return (int) textures[index].getX();
	}
	public int getTextureY() {
		return (int) textures[index].getY();
	}

	// Returns whether the animation is over or not
	public boolean isFinished() {
		return finished;
	}

}
