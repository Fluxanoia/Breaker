package co.uk.fluxanoia.entity;

import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;

// The AnimationTween class, represents a change in value during an animation
public class AnimationTween {

	// The tween value
	private Tween tween;
	// The tween type
	private TweenType type;
	// The duration
	private int duration;
	// The before and after values
	private double before, after;
	
	// Constructs an animation tween
	public AnimationTween(double before, double after, int duration, TweenType type) {
		// Check for null values
		ErrorHandler.checkNull(type, "An AnimationTween was given a null TweenType.");
		// Assign values
		this.before = before;
		this.after = after;
		this.duration = duration;
		this.type = type;
		// Initialise values
		this.tween = new Tween(before);
	}

	// Starts the animation tween
	public void start() {
		this.tween.move(type, after, duration, 0);
	}
	
	// Resets the animation tween
	public void reset() {
		this.tween.set(before);
	}
	
	// Updates the animation tween
	public boolean update() {
		tween.update();
		return tween.dropMoved();
	}
	
	// Returns the value of the tween
	public double value() {
		return tween.value();
	}
	
}