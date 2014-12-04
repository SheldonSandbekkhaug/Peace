package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LobbyScreen implements Screen {
	private final Peace game;
	private OrthographicCamera camera;
	private SpriteBatch batch; // TODO: do we need this here?
	private TextField serverIPField;
	private Stage stage;
	private Skin skin;
	private Table table;
	
	public LobbyScreen(final Peace gam) {
        game = gam;
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage); // Required for menu control
        
        // Create the LibGDX menu skin (not the same thing as a Peace skin)
        TextureAtlas atlas = new TextureAtlas(
        		Gdx.files.internal("menu_skin/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("menu_skin/uiskin.json"));
        skin.addRegions(atlas);
        
        // Create the main table
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        table.setSkin(skin);
        
        // Create welcome label
        String welcomeMessage = "Welcome to Peace. Enter the server IP address.";
        Label welcomeLabel = new Label(welcomeMessage, skin);
        welcomeLabel.setX(MainGameScreen.WINDOW_WIDTH / 2 - welcomeLabel.getWidth() / 2);
    	welcomeLabel.setY(MainGameScreen.WINDOW_WIDTH / 2 - welcomeLabel.getHeight() / 2);
        table.addActor(welcomeLabel);
        
        // Create server IP address TextField
        serverIPField = new TextField("127.0.0.1", skin);
        serverIPField.setBounds(100, 200, 400, 100);
        table.add(serverIPField);
        
        // Create the start Button
        TextButton startGameButton = new TextButton("Connect to Server", skin);
        startGameButton.setX(400);
        startGameButton.setY(400);
        startGameButton.setWidth(120);
        startGameButton.setHeight(80);
        table.add(startGameButton);
        
        startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// Start Peace
		        startGame();
			}
        });
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGameScreen.WINDOW_WIDTH, MainGameScreen.WINDOW_HEIGHT);
    }

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        
        batch.end();
	}
	
	/* Connect to the server to play the game. */
	public void startGame()
	{
		String serverIP = serverIPField.getText();
        System.out.println("server IP: " + serverIP);
        
        // Use the server IP to connect to the server
        game.newGame(serverIP);
        
        // TODO: error handling
        
		game.setScreen(new MainGameScreen(game));
        dispose();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {

	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
		skin.dispose();
	}

}
