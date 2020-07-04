package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Turtle extends BaseActor
{
    public Turtle(float x, float y, Stage stage)
    {
        super(x, y, stage);

        // -- initialize animation --
        // Turtle class features animation using images from multiple files
        String[] filenames = {"/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-1.png",
                "/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-2.png",
                "/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-3.png",
                "/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-4.png",
                "/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-5.png",
                "/Users/katherineohalloran/Documents/GameDev/libGDX-Learning/Starfish-Collector-Remastered/core/assets/Turtle/turtle-6.png"};

        // call load animation from file method
        this.loadAnimationFromFiles(filenames, 0.1f, true);

        // collision data - boundary polygon within default rectangle shaped polygon
        // 8 is accurate enough for simple game while preserving performance
        this.setBoundaryPolygon(8);


        // -- initialize physics data --

        /*
        Speed increases by 400 pixels/second each second, but since max speed is 100
        the Turtle will reach its max speed in .25 seconds (100/400) when starting from
        rest and move at that pace.
         */
        this.setAcceleration(400);

        // max speed is 100 pixels/second
        this.setMaxSpeed(100);

        this.setDeceleration(400);
    }

    public void act(float dt)
    {
        super.act(dt);

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            // accelerate at angle that corresponds to key pressed keeping in mind that
            // turtle starts off facing right. a 180 degree flip will reverse it to face
            // left
            this.accelerateAtAngle(180);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            this.accelerateAtAngle(0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
        {
            this.accelerateAtAngle(90);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            this.accelerateAtAngle(270);
        }

        // To actually update hte position of the turtle, the applyPhysics method
        // must be called
        this.applyPhysics(dt);

        // Pause the animation when the Turtle is not moving,
        this.setAnimationPaused(!this.isMoving());

        // rotate the turtle image to align with angle of motion
        if (this.getSpeed() > 0)
        {
            this.setRotation(this.getMotionAngle());
        }

        // make sure Turtle stays within the boundaries of the game world
        this.boundToWorld();

        // align the camera to focus on the Turtle as the game world is larger than the camera focus area
        // mainStage camera (GameBeta stage) is being adjusted to align with the turtle
        this.alignCamera();
    }
}











