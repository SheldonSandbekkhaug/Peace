package com.SheldonSandbekkhaug.Peace;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainGameScreen implements Screen {
	Peace game;
	OrthographicCamera camera;
	
    public MainGameScreen(final Peace gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Peace.WORLD_WIDTH, Peace.WORLD_HEIGHT);
    }
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		game.batch.begin();
		
		// Draw locations
		for (Location l : game.locations)
		{
			l.draw(game.batch);
		}
		
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		game.dispose();
	}

}
