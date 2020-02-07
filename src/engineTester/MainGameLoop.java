package engineTester;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.text.PlainDocument;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		// ************* TERRAIN TEXTURE STUFF *************

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, 
				rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		// ****************** GUI ***************************
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		GuiTexture gui2 = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.0f, 0.6f), new Vector2f(0.5f, 0.3f));
		//guis.add(gui2);
		//guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		// ********** Models, Textures, Entities ************
		
		List<Entity> entities = new ArrayList<>();
		List<Terrain> terrains = new ArrayList<>();
		
		ModelData data = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("lowPolyTree")));
		staticModel.getTexture().setShineDamper(1);
		staticModel.getTexture().setReflectivity(0);
		
		entities.add(new Entity(staticModel, new Vector3f(0, 0, -50), 0, 0, 0, 1));
		
		RawModel model2 = OBJLoader.loadObjModel("stall", loader);
		ModelTexture texture2 = new ModelTexture(loader.loadTexture("stallTexture"));
		TexturedModel staticModel2 = new TexturedModel(model2, texture2);

		entities.add(new Entity(staticModel2, new Vector3f(0, -20, -50), 0, 0, 0, 1));
		entities.add(new Entity(staticModel2, new Vector3f(0, 20, -50), 0, 0, 0, 1));
		entities.add(new Entity(staticModel, new Vector3f(0, -50, -80), 0, 0, 0, 1));
		
		RawModel modelGrass = OBJLoader.loadObjModel("grassModel", loader);
		ModelTexture textureGrass = new ModelTexture(loader.loadTexture("grassTexture"));
		textureGrass.setTransparent(true);
		textureGrass.setUsingFakeLighting(true);
		TexturedModel staticModelGrass = new TexturedModel(modelGrass, textureGrass);
		  
		for (int i = 0; i < 1000; i++) {
			entities.add(new Entity(staticModelGrass, new Vector3f(new Random().nextInt(800)+120, 0, -new Random().nextInt(800)-120), 0, 0, 0, 1));
		}
		
		
		RawModel modelFern = OBJLoader.loadObjModel("fern", loader);
		ModelTexture textureFern = new ModelTexture(loader.loadTexture("fernAtlas"));
		textureFern.setTransparent(true);
		textureFern.setUsingFakeLighting(true);
		textureFern.setNumberOfRows(2);
		TexturedModel staticModelFern = new TexturedModel(modelFern, textureFern);
		
		for (int i = 0; i < 1000; i++) {
			entities.add(new Entity(staticModelFern, new Vector3f(new Random().nextInt(800)+120, 0, -new Random().nextInt(800)-120), 0, 0, 0, 1, new Random().nextInt(4)));
		}
		
		Player player = new Player(staticModel, new Vector3f(50, 0, -150), 0, 0, 0, 1);
		entities.add(player);
		
		//Terrain terrain = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("white")));
		//Terrain terrain2 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("blue girl s2")));
		terrains.add(new Terrain(0, -1, loader, texturePack, blendMap, "heightmap"));
		//Terrain terrain2 = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(0, 50, 0), new Vector3f(1, 1, 1), new Vector3f(1, 0f, 0f)));
		lights.add(new Light(new Vector3f(50, 50, -550), new Vector3f(10, 10, 10), new Vector3f(1, 0.001f, 0.0002f)));
		
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader);
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));
		
		// ************* WATER RENDERER STUFF *************
		
		WaterShader waterShader = new WaterShader();
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(60, -60, 0));
		waters.add(new WaterTile(180, -60, 0));
		waters.add(new WaterTile(60, -180, 0));
		waters.add(new WaterTile(180, -180, 0));
		
		
		//guis.add(new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f)));
		//guis.add(new GuiTexture(fbos.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f)));
		
		// ************************************************
		Vector4f clipPlane = new Vector4f(0, -1, 0, 2);
		
		while (!Display.isCloseRequested()) {
			camera.move();
			picker.update();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if (terrainPoint != null) {
				entities.get(1).setPosition(terrainPoint);
			}
			
			player.move(terrains.get(0));
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight()+1f));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight()));
			
			fbos.unbindCurrentFrameBuffer();
			
			renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, 1000000));
			
			waterRenderer.render(waters, camera, lights.get(0));
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		
		
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDiplay();
	}

}
