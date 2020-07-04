package com.ktgames.starfishcollectorremastered;

// Effect should disappear when the animation is finished, so act method should check
// if animation is finished playing and if so, call the remove method to remove it from
// its stage (i.e. the game)

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Whirlpool extends BaseActor
{

    public Whirlpool(float x, float y, Stage stage)
    {
        super(x, y, stage);

        // Whirlpool creates animation from Sprite sheet
        this.loadAnimationFromSheet("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/NPCs/Whirlpool/whirlpool.png",
                2, 5, 0.1f, false);
    }

    // overriding the act method to remove the image if the animation is finished
    public void act(float dt)
    {
        super.act(dt);

        if (this.isAnimationFinished())
        {
            this.remove();
        }
    }
}
