package com.ktgames.starfishcollectorremastered.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.ktgames.starfishcollectorremastered.StarfishGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		// To start a LibGDX program, this method:
		// 1) Creates an instance of the game
		// 2) Creates a new application with the game instance and window settings as the parameters
		Game myGame = new StarfishGame();
		LwjglApplication launcher = new LwjglApplication(myGame, "Starfish Collector",
				800, 600);
	}
}
