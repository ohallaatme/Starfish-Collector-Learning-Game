package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;


public abstract class BaseScreen implements Screen
{
    protected Stage mainStage;
    protected Stage uiStage;

    public BaseScreen()
    {
        this.mainStage = new Stage();
        this.uiStage = new Stage();

        initialize();
    }

    public abstract void initialize();

    public abstract void update(float dt);

    /*
    Game Loop:
        1) Process input (discretely handled by listener; continuous in update)
        2) Update game logic
        3) Render the graphics
     */

    public void render(float dt)
    {

        // act methods
        this.uiStage.act(dt);
        this.mainStage.act(dt);

        // defined by user
        this.update(dt);

        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw the graphics
        this.mainStage.draw();
        this.uiStage.draw();
    }

    // methods required by Screen interface
    public void resize(int width, int height){}

    public void pause(){}

    public void resume(){}

    public void dispose(){}

    public void show(){}

    public void hide(){}
}


















