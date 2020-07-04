package com.ktgames.starfishcollectorremastered;

public class StarfishGame extends BaseGame
{
    @Override
    public void create()
    {
        // setActiveScreen method of BaseGame is static, hence no 'this' reference
        setActiveScreen(new MenuScreen());
    }
}
