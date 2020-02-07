package entities;

import models.TexturedModel;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

	private static final float SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	private static final float TERRAIN_HEIGHT = 0;
	
	private float speed = 0;
	private float turnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(Terrain terrain) {
		checkInput();
		super.increaseRotation(0, turnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = speed * DisplayManager.getFrameTimeSeconds();
		float dx = (float)(distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float)(distance * Math.cos(Math.toRadians(super.getRotY())));
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		float dy = upwardsSpeed * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(dx, dy, dz);
		
		
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		
		System.out.println("At ("+super.getPosition().x+", "+super.getPosition().z+"): " + terrainHeight);
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	private void jump() {
		if (isInAir == false) {
			isInAir = true;
			upwardsSpeed = JUMP_POWER;
		}
	}
	
	private void checkInput() { 
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			speed = SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			speed = -SPEED;
		} else {
			speed = 0;
		}
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			turnSpeed = TURN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			turnSpeed = -TURN_SPEED;
		} else {
			turnSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
}
