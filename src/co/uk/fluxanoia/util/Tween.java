package co.uk.fluxanoia.util;

import co.uk.fluxanoia.main.ErrorHandler;

// The TweenValue class, allows for smooth movements
public class Tween {

	// The TweenType enum, classifying different transition types
	public enum TweenType {
		LINEAR("lin"),
		EASE_IN("ein"),
		EASE_OUT("eot"),
		ELASTIC("ela");
		
		// The string representation of the tween type
		private String s;
		// Constructs the tween type
		private TweenType(String s) {
			this.s = s;
		}
		
		// Returns the string representation of a tween type
		public String getID() {
			return s;
		}
		
		// Returns the tween type that is represented by s
		public static TweenType toTweenType(String s) {
			ErrorHandler.checkNull(s, "The TweenType enum was given a null input string.");
			// Get all the TweenTypes
			TweenType[] tts = TweenType.values();
			// Iterate through the types
			for (int i = 0; i < tts.length; i++) {
				// If the representation is s, return the tween type
				if (tts[i].getID().equals(s)) {
					return tts[i];
				}
			}
			return null;
		}
	}
	
	// The TweenType of the Tween
	private TweenType tweenType;
	// Whether the component has moved or not
	private boolean moved;
	// Whether the tween value has finished moving or not
	private boolean finished;
	// --- The following are in units of updates
	// How long the tween should wait before moving
	private int hold;
	// The current progression into the tween
	private int time;
	// The duration of the tween
	private int duration;
	// The start and end values of the tween
	private double start, end;
	
	// Constructs a TweenValue
	public Tween(double t) {
		// Initialise values
	    this.tweenType = TweenType.LINEAR;
	    this.set(t);
	}

	// Updates the tween value
	public void update() {
		// If we are holding motion, decrement and return
	    if (this.hold > 0) {
	        this.hold--;
	        if (hold == 0) this.moved = true;
	        return;
	    }
	    // When we are at the end of the tween
	    if (this.time == this.duration) {
	    	// If we haven't said we're finished
	        if (!finished) {
		    	// Say we've moved one more time and finish
	            this.moved = true;
	            this.finished = true;
	        }
	    } else {
	    	// Add to the time and say we've moved
	        this.time++;
	        this.moved = true;
	    }
	} 

	// Sets the tween to move toward a value
	public void move(TweenType tweenType, double end, int duration, int hold) {
		ErrorHandler.checkNull(tweenType, "A Tween was given a null type.");
		// Move the tween to the end of its current movement
	    this.start = this.value();
		// Update the type of tween
	    this.tweenType = tweenType;
	    // Set the time to the beginning
	    this.time = 0;
	    // Set the duration
	    this.duration = duration;
	    // Set the holding time
	    this.hold = hold;
	    // Set the destination
	    this.end = end;
	    // Say we've moved
	    moved = true;
	    // Say we're not finished
	    finished = false;
	}
	
	// Sets the tween to move toward a value - forces completion of last movement
	public void push(TweenType tweenType, double end, int duration, int hold) {
		ErrorHandler.checkNull(tweenType, "A Tween was given a null type.");
		// Update the type of tween
	    this.tweenType = tweenType;
	    // Set the time to the beginning
	    this.time = 0;
	    // Set the duration
	    this.duration = duration;
	    // Set the holding time
	    this.hold = hold;
	    // Move the tween to the end of its current movement
	    this.start = this.end;
	    // Set the destination
	    this.end = end;
	    // Say we've moved
	    moved = true;
	    // Say we're not finished
	    finished = false;
	}

	// Set the tween to a value
	public void set(double s) {
		// Reset all the values to default and set a value to remain at
	    this.time = 0;
	    this.duration = 0;
	    this.hold = 0;
	    this.start = s;
	    this.end = s;
	    this.moved = true;
	    this.finished = true;
	}

	// Returns the value of a tween value
	public double value() {
	    if (this.time == this.duration) return this.end;
	    if (this.hold > 0) return this.start;

	    switch (this.tweenType) {
	        case LINEAR: return linearTween(start, end, time, duration);
	        case EASE_IN: return easeInTween(start, end, time, duration);
	        case EASE_OUT: return easeOutTween(start, end, time, duration);
	        case ELASTIC: return elasticTween(start, end, time, duration);
	        default: return 0;
	    } 
	}
	
	// Returns whether the tween value has reached its end value
	public boolean hasArrived() {
	    if (this.time == this.duration) return true;
	    return false;
	}

	// Returns the redraw value and sets it to false
	public boolean dropMoved() {
	    if (this.moved) {
	        this.moved = false;
	        return true;
	    }
	    return false;
	}
	
	// Returns the current destination
	public double getDestination() {
		return this.end;
	}

	// --- Each following function takes parameters: start, end, time, duration

	// A linear tween
	private double linearTween(double s, double e, int t, int d) {
	    return s + ((double) t / (double) d) * (e - s);
	}

	// An ease in tween
	private double easeInTween(double s, double e, int t, int d) {
	    return s + Math.pow((double) t / (double) d, 3) * (e - s);
	}

	// An ease out tween
	private double easeOutTween(double s, double e, int t, int d) {
	    return s + (1 - Math.pow(1 - ((double) t / (double) d), 3)) * (e - s);
	}

	// A bouncy tween
	private double elasticTween(double s, double e, int t, int d) {
	    double p = 0.3;
	    double inter = (double) t / (double) d;
	    return s + (Math.pow(2,-10*inter) * Math.sin((inter-p/4)*(2*Math.PI)/p) + 1) * (e - s);
	}
	
}
