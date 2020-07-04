package com.ktgames.starfishcollectorremastered;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import static java.lang.Class.forName;

// LibGDX Actor class with extended functionality

public class BaseActor extends Actor
{
    // Fields to store the animation and related data
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;

    // --Physics Data--
    // Store velocity data
    private Vector2 velocityVec;

    // Store acceleration data, also a vector
    private Vector2 accelerationVec;
    private float acceleration;

    // smooth physics - when arrow key is released, Actor should slow down
    // Actor can only go so fast
    private float maxSpeed;
    private float deceleration;

    // replacing collision rectangles with more advanced polygons
    // default for game is rectangular polygon, as polygons can rotate
    private Polygon boundaryPolygon;

    // stores size of game world for all Actors, hence static
    private static Rectangle worldBounds;

    // constructor
    public BaseActor(float x, float y, Stage stage)
    {
        // call parent 'Actor' class constructor
        super();

        // set position of Actor
        this.setPosition(x, y);

        // automatically add instantiated Actor to stage
        stage.addActor(this);

        // --initialize animation data--
        this.animation = null;
        this.elapsedTime = 0;
        this.animationPaused = false;

        // --initialize physics data--
        /* Vector2 indicates 2d data, initialized below
         x:0, y:0 corresponds to a speed of 0 and an undefined angle of motion
         we have to use setSpeed */

        // Velocity vector
        this.velocityVec = new Vector2(0,0);

        // Acceleration vector/data
        this.accelerationVec = new Vector2(0, 0);
        this.acceleration = 0;

        this.maxSpeed = 1000;
        this.deceleration = 0;

        // for collisions, replacing the rectangle with more advanced and accurate polygon
        this.boundaryPolygon = null;
    }
    // /** indicates that the comment is for documentation

    /** Align center of actor at given position coordinates
    @param x x-coordinate to center at
    @param y y-coordinate to center at
     */
    /*
    Recall that the setPosition method actually sets the bottom left corner of an Actor object to a given location.
    In order to center an Actor at a given location, you have to shift it from this location by half its width along
    the x direction and half its height along the y direction. centerAtPosition/centerAtActor achieves this below
     */
    public void centerAtPosition(float x, float y)
    {
        this.setPosition(x - this.getWidth()/2, y - this.getHeight()/2);
    }

    /**
     * Repositions this BaseActor so its center is aligned with
     * center of other BaseActor. Useful when one BaseActor spawns another.
     * @param other: BaseActor to align this BaseActor with.
     */

    public void centerAtActor(BaseActor other)
    {
        this.centerAtPosition(other.getX() + other.getWidth()/2, other.getY() + other.getHeight()/2);
    }


    //----------------------------
    // Animation methods
    //----------------------------

    /**
    Sets the animation used when rendering this actor; also sets actor size.
    @param anim animation that will be drawn when actor is rendered.
     */
    public void setAnimation(Animation<TextureRegion> anim)
    {
        this.animation = anim;

        // the images of an animation are also called keyframes
        TextureRegion tr = this.animation.getKeyFrame(0);

        // now that the animation is set, we can set teh size (width, height) of the Actor
        // width and height of the actor are set to the width and height of the first image of the animation
        float width = tr.getRegionWidth();
        float height = tr.getRegionHeight();
        this.setSize(width, height);

        // the Origin is the point around which the actor should be rotated, typically the center of the Actor
        // shown below
        this.setOrigin(width/2, height/2);

        // now that width and height are set, set the default rectangle shaped polygon
        // collision object assuming it wasn't already defined
        if (this.boundaryPolygon == null)
        {
            this.setBoundaryRectangle();
        }
    }

    /**
    Creates animation from images stored in separate files
    @param fileNames: Array of names of files containing animation images
    @param frameDuration: How long each frame should be displayed
    @param loop: Should the animation loop?
    @return Animation<TextureRegion>: Created from method, useful for storing multiple animations
     */

    public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames, float frameDuration, boolean loop)
    {
        // get the length of the Array of strings with file images for animation for 'for loop' below
        int fileCount = fileNames.length;

        // new array of type TextureRegion
        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        // step 1) Animation class needs array of images to display
        for (int n = 0; n < fileCount; n++)
        {
            String fileName = fileNames[n];
            Texture texture = new Texture(Gdx.files.internal(fileName));

            // 6.6.2020 - used to smooth Textures for magnification and minification
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }
        // frameDuration indicates step 2) The amount of time that each image should be displayed
        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        // if the animation should loop per the parameter
        // step 3) how the frames should be played
        if (loop)
        {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        } else
        {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        // believe this is checking the animation specific to the instance of the object
        // TODO - double check above point
        if (this.animation == null) {
            this.setAnimation(anim);
        }

        return anim;
    }


    /**
     * Creates an animation from a spritesheet: a rectangular grid of images tored in a single file.
     * @param fileName: name of file containing the spritesheet
     * @param rows: number of rows of images in spritesheet
     * @param cols: number of columns of images in spritesheet
     * @param frameDuration: how long each frame should be displayed
     * @param loop: should the animation loop
     * @return animation: created (useful for storing multiple animations)
     */
    // Need to know the size of each sub image, calculated below in the following code based on the size of the
    // original image and the number of rows and columns present in the spritesheet
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop)
    {
        //TODO check if useMipMaps is the right parameter for the true value, course code doesn't specify
        Texture texture = new Texture(Gdx.files.internal(fileName), true);

        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        int frameWidth = texture.getWidth()/cols;
        int frameHeight = texture.getHeight()/rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        //TODO: understand this double for loop better
        for (int r=0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                textureArray.add(temp[r][c]);
            }
        }

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        if (loop)
        {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        } else
        {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (animation == null)
        {
            setAnimation(anim);
        }

        return anim;
    }

    /**
        Convenience method for creating a 1-frame animation from a single texture
        @param fileName: names of image file
        @return animation created (useful for storing multiple animations)
     */

    // If we have a single frame image that does not require animation, use this method
    public Animation<TextureRegion> loadTexture(String fileName)
    {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    /**
     * Set the pause state of the animation
     * @param pause: true to pause animation, false to resume animation
     */

    public void setAnimationPaused(boolean pause)
    {
        this.animationPaused = pause;
    }

    /**
     * Checks if animation is complete: if animation play mode is normal (not looping)
     * and elapsed time is greater than time corresponding to last frame.
     * @return
     */

    public boolean isAnimationFinished()
    {
         return this.animation.isAnimationFinished(elapsedTime);
    }

    /**
     * Sets the opacity of this actor.
     * @param opacity: value from 0 (transparent) to 1 (opaque). a property of getColor() represents the
     *               alpha value that corresponds to opacity
     */

    public void setOpacity(float opacity)
    {
        this.getColor().a = opacity;
    }

    //-------------------------
    // physics/motion methods
    //-------------------------

    /**
     * Set acceleration of this object.
     * @param acc: Acceleration in (pixels/second) per second
    */

    public void setAcceleration(float acc)
    {
        this.acceleration = acc;
    }

    /**
     * Set deceleration of this object.
     * Deceleration is only applied when object is not accelerating.
     * @param dec: Deceleration in (pixels/second) per second.
     */

    public void setDeceleration(float dec)
    {
        this.deceleration = dec;
    }

    /**
     * Set maximum speed of this object.
     * @param maxSpeed: Maximum speed of this object in (pixels/second).
     */

    public void setMaxSpeed(float maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Set the speed of movement (in pixels/second) in current direction.
     * If the current speed is zero (direction is undefined), direction will be set to 0 degrees.
     * @param speed: Speed of movement (pixels/second)
     */

    // initialization of velocityVec with 0, 0 is a speed of 0 and an undefined angle
    // setting the speed will cause the angle of motion to be 0 degrees along the
    // direction of a positive x axis
    public void setSpeed(float speed)
    {
        // if velocityVec length is zero, we assume the motion angle is zero degrees
        if (this.velocityVec.len() == 0)
        {
            this.velocityVec.set(speed, 0);
        } else
        {
            this.velocityVec.setLength(speed);
        }
    }

    /**
     * Calculates the speed of movement (in pixels/second).
     * @return speed: Speed of movement (pixels/second)
     */

    public float getSpeed()
    {
        return this.velocityVec.len();
    }

    /**
     * Determines if this object is moving (if speed is greater than zero)
     * @return false when speed is zero, true otherwise
     */

    // Many game instances require us to know if an Actor is moving
    public boolean isMoving()
    {
        return (this.getSpeed() > 0);
    }

    /**
     * Sets the angle of motion (in degrees).
     * If current speed is zero, this will have no effect.
     * @param angle of motion (degrees)
     */
    // Setting the angle (velocity) has no effect on speed because if the speed of an
    // object from initialization is 0, it doesn't make sense to speak of it moving
    // in any direction
    public void setMotionAngle(float angle)
    {
        this.velocityVec.setAngle(angle);
    }

    /**
     * Get the angle of motion (in degrees), calculated from the velocity vector.
     * <br>
     * To align actor image angle with motion angle, use <code>setRotation(getMotionAngle())</code> in the
     * <code>act()</code> method of an <code>Actor</code>
     * @return angle of motion (degrees)
     */
    // below method very convenient for making an object face the direction it is moving,
    public float getMotionAngle()
    {
        return this.velocityVec.angle();
    }

    /**
     * Update accelerate vector by angle and value stored in acceleration field.
     * Acceleration is applied by <code>applyPhysics</code> method.
     * @param angle: Angle (degrees) in which to accelerate.
     * @see #acceleration
     * @see #applyPhysics
     */

    public void accelerateAtAngle(float angle)
    {
        this.accelerationVec.add(
                new Vector2(acceleration, 0).setAngle(angle));
    }

    /**
     * Update accelerate vector by current rotation angle and value stored in acceleration field.
     * Acceleration is applied by <code>applyPhysics</code> method.
     * @see #acceleration
     * @see #applyPhysics
     */

    // Accelerates an object in the direction it is currently facing
    public void accelerateForward()
    {
        this.accelerateAtAngle(this.getRotation());
    }

    /**
     * Adjust velocity vector based on acceleration vector,
     * then adjust position based on velocity vector. <br>
     * If not accelerating, deceleration value is applied. <br>
     * Speed is limited by maxSpeed value. <br>
     * Acceleration vector reset to (0,0) at end of method. <br>
     * @param dt: Time elapsed since previous frame (delta time); Typically obtained from <code>act</code> method.
     * @see #acceleration
     * @see #deceleration
     * @see #maxSpeed
     */
    // accelerate/decelerate code lines determined by mathmatical formulas for velocity
    // and acceleration
    public void applyPhysics(float dt)
    {
        // apply acceleration
        this.velocityVec.add( this.accelerationVec.x * dt, this.accelerationVec.y * dt);

        float speed = this.getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (this.accelerationVec.len() == 0)
        {
            speed -= this.deceleration * dt;
        }

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, this.maxSpeed);

        // update velocity
        this.setSpeed(speed);

        // apply velocity
        this.moveBy(this.velocityVec.x * dt, this.velocityVec.y * dt);

        // reset acceleration at the end of method
        this.accelerationVec.set(0, 0);

    }

    //----------------------------
    // Collision polygon methods
    //----------------------------

    /**
     * Set rectangular-shaped collision polygon.
     * This method is automatically called when animation is set,
     * provided that the current boundary polygon is null.
     * rectangle-shaped polygon is default collision object for this game
     * @see #setAnimation
     */
    public void setBoundaryRectangle()
    {
        float w = this.getWidth();
        float h = this.getHeight();

        float[] vertices = {0,0, w,0, w,h, 0,h};
        this.boundaryPolygon = new Polygon(vertices);
    }

    /**
     * Replace default (rectangle) collision polygon with an n-sided polygon. <br>
     * Vertices of polygon lie on the ellipse contained within boundary rectangle.
     * Note: one vertex will be located at point (0, width);
     * a 4-sided polygon will appear in the orientation of a diamond
     * @param numSides: number of sides of the collision polygon
     */
    /*
    see book for trig behind below calculation, creates ellipse for more precise
    collision beyond the standard rectangle-shaped polygon. do NOT call this method
    until after the size of the Actor object has been set via setSize or setAnimation
    since this method requires values for the width and height of the actor to be set
    in order to work correctly
     */
    public void setBoundaryPolygon(int numSides)
    {
        // x
        float w = this.getWidth();

        // y
        float h = this.getHeight();

        float[] vertices = new float[2*numSides];

        /*
        loop generates a set of n equally spaced values for t in the interval [0, 6.28],
        then calculates the corresponding x and y coordinates and stores then in an array
        that will be used to initialize the polygon. The larger the value of n,
        the smoother the shape will be, however there is a tradeoff - large values
        of n can drastically slow down the program as collision detection for
        general polygons is computationally intensive. for simple games, n=8 should
        be sufficiently accurate.

        example: if n=4, the polygon will be diamond shaped, if n=8, octagon shaped, etc.
         */
        for (int i=0; i < numSides; i++)
        {
            // sine and cosine (below) parameterize a circle or ellipse, which means
            // x=cos(t), y=sin(t), with the variable t taking on values ranging from
            // 0 to x*pi or 6.28
            float angle = i * 6.28f/numSides;

            // x-coordinate
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;

            // y-coordinate
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2;
        }

        this.boundaryPolygon = new Polygon(vertices);
    }

    /**
     * Returns bounding polygon for this BaseActor, adjusted by Actor's current position
     * and rotation.
      * @return bounding polygon for this BaseActor
     */
    public Polygon getBoundaryPolygon()
    {
        this.boundaryPolygon.setPosition(this.getX(), this.getY());
        this.boundaryPolygon.setOrigin(this.getOriginX(), this.getOriginY());
        this.boundaryPolygon.setRotation(this.getRotation());
        this.boundaryPolygon.setScale(this.getScaleX(), this.getScaleY());
        return this.boundaryPolygon;
    }

    /**
     * Determine if this BaseActor object overlaps a different BaseActor (according to collision polygons)
     * @param other BaseActor to check for overlap
     * @return true if collision polygons of this and other BaseActor overlap
     */

    // while Rectangle class has overlaps method built in, Polygon class does not,
    // so we use Intersector class's overlap methods in our defined method for Actor
    // class
    public boolean overlaps(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        /*
        -- Initial test to improve performance --
        Checking if two polygons overlap requires a lot of computation. To improve
        performance, prelim check below checks to see if the rectangles surrounding
        the collision polygons intersect (which is far simpler). If they don't it would be
        impossible for the polygons to intersect.
        */
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
        {
            return false;
        }

        return Intersector.overlapConvexPolygons(poly1, poly2);

    }

    /**
     * Implement a "solid"-like behavior:
     * when there is overlap, move this BaseActor away from other BaseActor
     * along minimum translation vector until there is no overlap.
     * @param other: BaseActor to check for overlap
     * @return direction vector by which the actor was translated, null if no overlap
     */


    public Vector2 preventOverlap(BaseActor other)
    {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance, checks if other polygon's outer rectangle is in range
        // before checking the inner collision polygon because collision computations are expensive
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
        {
            return null;
        }

        // calculates the minimum vector to move Actor back by if it hits a solid object, parameter for
        // overlapConvexPolygons
        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
        {
            return null;
        }

        // character moved back after hitting solid object based on the minimum distance and direction required
        // mtv.normal.x/y handles direction, mtv.depth handles distance
        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);

        // return the direction in which the Actor was moved when an overlap exists. Not used in Starfish
        // collector, but helpful for other games
        return mtv.normal;
    }

    /**
     * Set world dimensions for use by methods boundToWorld() and scrollTo()
     * @param width: width of world
     * @param height: height of world
     */
    // Set world bounds based on numerical values
    public static void setWorldBounds(float width, float height)
    {
        worldBounds = new Rectangle(0,0 , width, height);
    }

    /**
     * Set world dimensions for use by methods boundToWorld() and scrollTo().
     * @param ba: BaseActor: whose size determines the world bounds (typically a background image)
     */

    // set world bound based on an Actor, typically the Actor is a background image
    public static void setWorldBounds(BaseActor ba)
    {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }

    /**
     * If an edge of an object moves past the world bounds,
     * adjust its position to keep it completely on screen
     */

    // perform four comparison checks below (left, right, top and bottom) to see if the actor has passed beyond
    // the corresponding edges of the screen and if so set the actor back on the screen
    public void boundToWorld()
    {
        // Note that worldBounds field is static
        // left edge
        if (this.getX() < 0)
        {
            this.setX(0);
        }
        // right edge
        if (this.getX() + this.getWidth() > worldBounds.width)
        {
            this.setX(worldBounds.width - this.getWidth());
        }
        // bottom edge
        if (this.getY() < 0)
        {
            this.setY(0);
        }
        // top edge
        if (this.getY() + this.getHeight() > worldBounds.height)
        {
            this.setY(worldBounds.height - this.getHeight());
        }
    }

    /**
     * Center camera on this object, while keeping camera's range of view
     * (determined by screen size) completely within world bounds.
     */

    public void alignCamera()
    {
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();

        // center camera on actor
        cam.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

        // bind camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth/2,
                worldBounds.width - cam.viewportWidth/2);

        cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight/2,
                worldBounds.height - cam.viewportHeight/2);

        cam.update();
    }

    //------------------------
    // Instance list methods
    //------------------------

    /**
     *  Retrieves a list of all instances of the object from the given stage with the given class name
     *  or whose class extends the class with the given name.
     *  If no instances exist, returns an empty list.
     *  Useful when coding interactions between different types of game objects in update method.
     * @param stage: Stage containing BaseActor instances
     * @param className: name of a class that extends the BaseActor class INCLUDING THE PACKAGE
     * @return list of instances of the object in stage which extend with the given class name
     */

    // MAKE SURE TO INCLUDE CLASS PACKAGE
    public static ArrayList<BaseActor> getList(Stage stage, String className)
    {
        ArrayList<BaseActor> list = new ArrayList<BaseActor>();

        Class theClass = null;
        try
        {  theClass = forName(className);  }
        catch (Exception error)
        {  error.printStackTrace();  }

        for (Actor a : stage.getActors())
        {
            if ( theClass.isInstance( a ) )
                list.add( (BaseActor)a );
        }

        return list;
    }

    /**
     * Returns number of instances of a given class (that extends BaseActor).
     * @param className: name of a class that extends the BaseActor class
     * @return number of instances of the class
     */

    public static int count(Stage stage, String className)
    {
        // helpful in game dev to see how many objects remain at a certain time
        // leveraging this class. Note no 'this' reference as getList is a static
        // method
        return getList(stage, className).size();
    }

    //-------------------------------
    // Actor methods: act and draw
    //-------------------------------

    /**
     * Process all Actions and related code for this object;
     * automatically called by act method in Stage class.
     * @param dt: elapsed time (second) since last frame (supplied by Stage act method)
     */

    public void act(float dt)
    {
        super.act(dt);

        // if the animation is not paused, updated elapsedTime to determine which image animation should be on
        if(!this.animationPaused)
        {
            /* elapsedTime is used to keep track of how long the animation has been playing
             and therefore which image should be displayed. Updated automatically as shown below.
             elapsedTime is incremented by the amount of time that has passed since the previous iteration
             of the game loop (indicated by dt), provided that the game is not currently paused (from if check above)
            */
            this.elapsedTime += dt;
        }
    }

    /**
     * Draws current frame of animation; automatically called by draw method in Stage class. <br>
     * If color hsa been set, image will be tinted by that color. <br>
     * If no animation has been set or object is invisible, nothing will be drawn.
     * @param batch: (supplied by Stage draw method)
     * @param parentAlpha: (supplied by Stage draw method)
     * @see #setColor
     * @see #setVisible(boolean)
     */
    // override the Actor (Parent class) draw method to determine the correct image of the animation
    // to be drawn (using getKeyFrame method and elapsedTime variable)
    public void draw(Batch batch, float parentAlpha)
    {
        super.draw(batch, parentAlpha);

        // apply color tint effect, default is white, has no effect on the appearance of the image
        Color c = this.getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && this.isVisible())
        {
            // Determine which image of the animation to draw based on the getKeyFrame method and elapsedTime variable
            // taking into account the various properties stored in the Actor class (including position, size, scale
            // rotation and origin)
            batch.draw(this.animation.getKeyFrame(this.elapsedTime),
                    this.getX(), this.getY(), this.getOriginX(), this.getOriginY(),
                    this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(),
                    this.getRotation());
        }
    }
}






































