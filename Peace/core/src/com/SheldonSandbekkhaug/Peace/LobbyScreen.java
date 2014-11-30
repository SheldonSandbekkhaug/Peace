package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class LobbyScreen implements Screen {
	final Peace game;
	OrthographicCamera camera;
	SpriteBatch batch;
	BitmapFont font;
	TextField serverIPField;
	
	public LobbyScreen(final Peace gam) {
        game = gam;
        batch = new SpriteBatch();
        font = new BitmapFont(); // Defaults to Arial
        font.setColor(Color.BLACK);

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
        String label = "Welcome to Peace. Entere the server IP address.";
        TextBounds bounds = font.getBounds(label);
        float textX = MainGameScreen.WINDOW_WIDTH / 2 - bounds.width / 2;
        float textY = MainGameScreen.WINDOW_HEIGHT / 2;
        
        font.draw(batch, label, textX, textY);
        batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new MainGameScreen(game));
            dispose();
        }
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
		font.dispose();
	}

}
