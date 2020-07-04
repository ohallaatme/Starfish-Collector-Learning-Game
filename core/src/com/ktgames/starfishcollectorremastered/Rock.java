package com.ktgames.starfishcollectorremastered;


import com.badlogic.gdx.scenes.scene2d.Stage;

// Rock objects used as solid obstacle in the game
public class Rock extends BaseActor
{

    public Rock(float x, float y, Stage stage)
    {
        super(x, y, stage);

        // load single Rock image
        this.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/NPCs/rock.png");

        // set up boundaryPolygon for more precise collisions
        this.setBoundaryPolygon(8);
    }
}
