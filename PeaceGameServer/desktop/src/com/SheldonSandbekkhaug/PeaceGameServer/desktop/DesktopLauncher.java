package com.SheldonSandbekkhaug.PeaceGameServer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.SheldonSandbekkhaug.PeaceGameServer.PeaceGameServer;

/* Peace Game Server */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new PeaceGameServer(), config);
	}
}
