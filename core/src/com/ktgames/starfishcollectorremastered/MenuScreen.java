package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class MenuScreen extends BaseScreen
{



    public void initialize()
    {
        BaseActor ocean = new BaseActor(0, 0, this.mainStage);
        ocean.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/water.jpg");
        ocean.setSize(800, 600);

        BaseActor title = new BaseActor(0, 0, this.mainStage);
        title.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Messages/starfish-collector.png");
        title.centerAtPosition(400, 300);
        //TODO 6.16 review moveBy method of BaseActor class
        title.moveBy(0, 100);

        BaseActor start = new BaseActor(0, 0, this.mainStage);
        start.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Messages/message-start.png");
        start.centerAtPosition(400, 300);
        start.moveBy(0, -100);

   }

    public void update(float dt)
    {
        // if the user hits the 'S' key start the game
        if (Gdx.input.isKeyPressed(Input.Keys.S))
        {
            StarfishGame.setActiveScreen(new LevelScreen());
        }

    }
}
