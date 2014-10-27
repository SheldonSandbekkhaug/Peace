package com.SheldonSandbekkhaug.Peace.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.SheldonSandbekkhaug.Peace.Peace;

// Peace
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Peace";
		config.width = Peace.WINDOW_WIDTH;
		config.height = Peace.WINDOW_HEIGHT;
		
		new LwjglApplication(new Peace(), config);
	}
}
