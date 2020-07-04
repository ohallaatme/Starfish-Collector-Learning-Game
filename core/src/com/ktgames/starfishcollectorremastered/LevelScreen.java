package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class LevelScreen extends BaseScreen
{
    private Turtle turtle;
    private boolean win;

    @Override
    public void initialize()
    {
        // recall constructor for BaseActor takes x, y coordinates and stage
        BaseActor ocean = new BaseActor(0, 0, this.mainStage);

        //-- set image and size of ocean post initialization
        // using 1 image loadTexture shortcut
        ocean.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/water-border.jpg");
        ocean.setSize(1200, 900);

        // establish the size of the game world - setWorldBounds is static method of BaseActor
        BaseActor.setWorldBounds(ocean);

        // adding multiple starfish, note that constructor of BaseActor will add them to our stage so the
        // initialization is not lost by not having a variable
        new Starfish(400, 400, this.mainStage);
        new Starfish(500, 100, this.mainStage);
        new Starfish(100, 450, this.mainStage);
        new Starfish(200, 250, this.mainStage);

        // multiple rocks
        new Rock(200, 150, this.mainStage);
        new Rock(100, 300, this.mainStage);
        new Rock(300, 350, this.mainStage);
        new Rock(450, 200, this.mainStage);

        this.turtle = new Turtle(20, 20, this.mainStage);

        this.win = false;

    }

    public void update(float dt)
    {

        // for rockActor in the instances of the rock class in the stage..
        for (BaseActor rockActor : BaseActor.getList(this.mainStage, "com.ktgames.starfishcollectorremastered.Rock"))
        {

            this.turtle.preventOverlap(rockActor);
        }


        for (BaseActor starfishActor : BaseActor.getList(this.mainStage, "com.ktgames.starfishcollectorremastered.Starfish"))
        {
            Starfish starfish = (Starfish)starfishActor;

            if (this.turtle.overlaps(starfish) && !starfish.isCollected())
            {
                starfish.collect();

                Whirlpool whirl = new Whirlpool(0, 0, this.mainStage);
                whirl.centerAtActor(starfish);
                whirl.setOpacity(0.25f);
            }
        }

        if (BaseActor.count(this.mainStage, "com.ktgames.starfishcollectorremastered.Starfish") == 0 && !win)
        {
            this.win = true;

            // win message is a UI element
            BaseActor youWinMessage = new BaseActor(0, 0, this.uiStage);

            youWinMessage.loadTexture("/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Messages/you-win.png");
            youWinMessage.centerAtPosition(400, 300);

            // starts transparent, loads in after second delay to correspond with starfish disappearing
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }


    }
}
