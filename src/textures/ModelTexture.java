package textures;

public class ModelTexture {

	private int textureID;
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean transparent;
	private boolean usingFakeLighting;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int textureID) {
		this.textureID = textureID;
	}
	
	public int getID() {return textureID;}
	public float getShineDamper() {return shineDamper;}
	public void setShineDamper(float shineDamper) {this.shineDamper = shineDamper;}
	public float getReflectivity() {return reflectivity;}
	public void setReflectivity(float reflectivity) {this.reflectivity = reflectivity;}
	public boolean isTransparent() { return transparent; }
	public void setTransparent(boolean transparent) { this.transparent = transparent; }
	public boolean isUsingFakeLighting() { return usingFakeLighting; }
	public void setUsingFakeLighting(boolean usingFakeLighting) { this.usingFakeLighting = usingFakeLighting; }
	public int getNumberOfRows() {return numberOfRows;}
	public void setNumberOfRows(int numberOfRows) {this.numberOfRows = numberOfRows;}
}
