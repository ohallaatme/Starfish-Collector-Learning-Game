package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.compression.lzma.Base;

// Starfish class uses single image, use loadTexture method created for convenience
// for this situation in BaseActor. Will also add a value-based animation (a slow rotation
// of 30 degrees every ONE second) using the Action class, which will draw the player's
// attention to this object
public class Starfish extends BaseActor
{

    // used to ensure the Starfish can only be collected once
    private boolean collected;

    public Starfish(float x, float y, Stage stage)
    {
        super(x, y, stage);

        this.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/NPCs/starfish.png");

        // rotate 30 degrees (Value based animation) every one second
        Action spin = Actions.rotateBy(30, 1);

        // add the Action to the Actor (Starfish) object
        this.addAction(Actions.forever(spin));

        // collision data - boundary polygon within default rectangle shaped polygon
        // 8 is accurate enough for simple game while preserving performance
        this.setBoundaryPolygon(8);

        this.collected = false;
    }

    public boolean isCollected()
    {
        return collected;
    }

    // sets isCollected to true and applies an animated fading-out effect, after which the Starfish
    // is removed from the stage
    public void collect()
    {
        this.collected = true;

        // clear actions applied to actor object
        this.clearActions();

        this.addAction(Actions.fadeOut(1));
        this.addAction(Actions.after(Actions.removeActor()));

    }
}

























