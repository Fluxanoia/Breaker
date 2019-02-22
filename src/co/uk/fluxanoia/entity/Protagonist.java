package co.uk.fluxanoia.entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.map.Terrain;

// The Player class, the protagonist
public class Protagonist extends GravityEntity {

	// Whether the player's anim/hit boxes are shown
	private static final boolean SHOW_BOXES = false;
	
	// The ids for various animations
	private int anim_stand, anim_run, anim_stop, anim_teeter, anim_jump_start, anim_jump_air, anim_jump_end;

	// Whether to flip the protagonist
	private boolean flip;
	
	// Constructs a player
	public Protagonist(Display display, Terrain terrain, int x, int y) {
		super(EntityIndex.PROTAGONIST, display, terrain, x, y, 18, 34, 1.75, 0.4, 0.35, 7, 5, 9, 7, 0.8, 2);
		this.getAnimator().setTiles(this.getDisplay().getResourceManager()
				.getImage("res\\sprites\\protagonist.png"));
		this.getAnimator().setTileDimensions(new Dimension(40, 40));
		// Set up the animations
		anim_stand = this.getAnimator().addAnimation("res\\anims\\protagonist\\stand.anim");
		anim_run = this.getAnimator().addAnimation("res\\anims\\protagonist\\run.anim");
		anim_stop = this.getAnimator().addAnimation("res\\anims\\protagonist\\stop.anim");
		anim_jump_start = this.getAnimator().addAnimation("res\\anims\\protagonist\\jump_start.anim");
		anim_jump_air = this.getAnimator().addAnimation("res\\anims\\protagonist\\jump_air.anim");
		anim_jump_end = this.getAnimator().addAnimation("res\\anims\\protagonist\\jump_end.anim");
		anim_teeter = this.getAnimator().addAnimation("res\\anims\\protagonist\\teeter.anim");
		// Start the base animation
		this.getAnimator().setAnimation(anim_jump_end, -1);
		// Initialise values
		this.flip = false;
	}

	// Updates the player
	public void update() {
		// Preparing to update the clip
		boolean updateClip = false;
		updateClip |= this.updateSuper();
		updateClip |= this.updateCollision(this.getTerrain().getTiles());
		// Set the flip
		if (x_vel < 0 && !flip) flip = true;
		if (x_vel > 0 && flip)  flip = false;
		// Update animations
		if (this.isGrounded()) {
			if (Math.abs(x_vel) > 0) {
				if (this.isTurning()) {
					this.getAnimator().setAnimation(anim_stop, 0);
				} else {
					if (this.isFrictioned()) {
						this.getAnimator().setAnimation(anim_stop, -1);
					} else {
						this.getAnimator().setAnimation(anim_run, -1);
					}
				}
			} else {
				if (this.isTeetering()) {
					this.getAnimator().setAnimation(anim_teeter, -1);
				} else {
					this.getAnimator().setAnimation(anim_stand, -1);
				}
			}
		} else {
			if (y_vel < -1.5) {
				this.getAnimator().setAnimation(anim_jump_start, -1);
			} else if (y_vel > 1.5) {
				this.getAnimator().setAnimation(anim_jump_air, -1);
			} else {
				this.getAnimator().setAnimation(anim_jump_end, -1);
			}
		}
		// Update the clip if necessary
		if (updateClip) updateClip();
	}

	// Draws the player
	public void draw(Graphics2D g) {
		this.getAnimator().drawImage(g, this.getTerrain().getCamera(), this.getHitbox(), this.flip);
		if (!SHOW_BOXES) return;
		Rectangle a_box = this.getAnimator().getBounds(this.getHitbox());
		Rectangle h_box = this.getHitbox();
		a_box = new Rectangle((int) a_box.getX() - 5,
				(int) a_box.getY() - 5,
				(int) a_box.getWidth() + 10,
				(int) a_box.getHeight() + 10);
		a_box.translate((int) -this.getTerrain().getCamera().getX(),
				(int) -this.getTerrain().getCamera().getY());
		h_box.translate((int) -this.getTerrain().getCamera().getX(),
				(int) -this.getTerrain().getCamera().getY());
		g.setColor(Color.ORANGE);
		g.draw(a_box);
		g.setColor(Color.GREEN);
		g.draw(h_box);
	}

}
