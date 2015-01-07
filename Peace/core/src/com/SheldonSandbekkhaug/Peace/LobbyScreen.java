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
	private Table mainTable;
	private Table playerListTable;
	
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
        
        // Create menu for the user
        createUITables();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, MainGameScreen.WINDOW_WIDTH, MainGameScreen.WINDOW_HEIGHT);
    }
	
	/* Creates the menu tables (buttons, labels, etc.)
	 * mainTable holds the server IP field, welcome label, and connection
	 * button. The main table is removed from view when the user successfully
	 * connects to a server.
	 * 
	 * The playerListTable holds labels for player names and the start
	 * button. The player list table is invisible until the user successfully 
	 * connects to a server.
	 */
	private void createUITables()
	{
		// Create the main Table
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        mainTable.setSkin(skin);
        
        // Create server IP address TextField
        serverIPField = new TextField("127.0.0.1", skin);
        serverIPField.setBounds(100, 200, 400, 100);
        mainTable.add(serverIPField);
        
        // Create welcome label
        String welcomeMessage = "Welcome to Peace. Enter the server IP address.";
        Label welcomeLabel = new Label(welcomeMessage, skin);
        welcomeLabel.setX(MainGameScreen.WINDOW_WIDTH / 2 - welcomeLabel.getWidth() / 2);
    	welcomeLabel.setY(MainGameScreen.WINDOW_WIDTH / 2 - welcomeLabel.getHeight() / 2);
        mainTable.addActor(welcomeLabel);
        
        // Create the "Connect to Server" button
        final TextButton connectButton = new TextButton("Connect to Server", skin);
        connectButton.setX(400);
        connectButton.setY(400);
        connectButton.setWidth(120);
        connectButton.setHeight(80);
        mainTable.add(connectButton);
		
		// Create the player list Table
        playerListTable = new Table();
        playerListTable.setFillParent(true);
        stage.addActor(playerListTable);
        playerListTable.setSkin(skin);
        playerListTable.setVisible(false);
        
        // Create the "Start Game" button
        final TextButton startGameButton = new TextButton("Start Game", skin);
        startGameButton.setX(400);
        startGameButton.setY(400);
        startGameButton.setWidth(120);
        startGameButton.setHeight(80);
        playerListTable.add(startGameButton);
        startGameButton.setVisible(false);
        
        // Create player name Labels for the player list Table
        String defaultName = "<empty>";
        for (int i = 0; i < CommonData.MAX_USERS; i ++)
        {
        	playerListTable.row();
        	Label l = new Label(defaultName, skin);
        	playerListTable.add(l);
        }
        
        connectButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO: Show list of other players in lobby
				
				// Show the start button and hide other buttons and fields
				startGameButton.setVisible(true);
				playerListTable.setVisible(true);
				serverIPField.setVisible(false);
				connectButton.setVisible(false);
				
				// Set up the game in anticipation of starting
				setUpNewGame();
			}
        });
        
        startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
		        startGame(); // Start Peace
			}
        });
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        
        // Required for switching to Main Game Screen
        if (game.isConnected())
        {
        	game.processEvents();
		}
        
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        
        batch.end();
	}
	
	public void setUpNewGame()
	{
		game.setUpNewGame();
		
		String serverIP = serverIPField.getText();
        
        // Use the server IP to connect to the server
        game.connectToServer(serverIP);
        
        // TODO: error handling
	}
	
	/* Connect to the server to play the game. */
	public void startGame()
	{
        game.requestStartGame();
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
