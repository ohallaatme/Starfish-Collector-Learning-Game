package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.Game;

public abstract class BaseGame extends Game
{
    /**
     * Stores reference to game; used when calling <code>setActiveScreen</code> method.
     */

    private static BaseGame game;

    /**
     * Called when game is initialized; stores global reference to game object
     */

    public BaseGame()
    {
        game = this;
    }

    /**
     * Used to switch screens while game is running.
     * Method is static to simplify usage.
     */

    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);
    }
}
