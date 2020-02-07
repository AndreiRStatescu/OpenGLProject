package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private Vector3f position = new Vector3f(0, 1, 0);
	private float pitch = 30;
	private float yaw;
	private float roll;
	
	private float distanceFromPlayer = 100;
	private float angleAroundThePlayer = 0;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
	}
	
	public Vector3f getPosition() {return position;}
	public float getPitch() {return pitch;}
	public float getYaw() {return yaw;}
	public float getRoll() {return roll;}
	
	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundThePlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
	}
	
	private float calculateHorizontalDistance() {
		return (float)(distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float)(distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = angleAroundThePlayer + player.getRotY();
		float offsetX = (float)(horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float)(horizontalDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticalDistance;
		position.z = player.getPosition().z - offsetZ;
		
		yaw = (float)(180 - theta);
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAroundThePlayer() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundThePlayer -= angleChange;
		}
	}

	public void invertPitch() {
		pitch = -pitch;
	}
}
