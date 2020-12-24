package com.kamilkurp;

import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.Treasure;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSlickGame extends BasicGame {


    private KeyInput keyInput;

    private GameSystem gameSystem;


    public SimpleSlickGame(String gamename) {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.getGraphics().setAntiAlias(true);

        Assets.loadAssets();

        gameSystem = new GameSystem();

        keyInput = new KeyInput();


        gc.setMouseCursor(Assets.cursor, 0,0);

        gc.setMouseGrabbed(true);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {



        gameSystem.update(gc, i, keyInput);
    }

    public void render(GameContainer gc, Graphics g) {
        gameSystem.render(g);

        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();

        g.drawImage(Assets.cursor, mouseX, mouseY);
    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new SimpleSlickGame("Simple Slick Game"));
            appgc.setDisplayMode(Globals.SCREEN_WIDTH, Globals.SCREEN_HEIGHT, false);

            appgc.start();
        }
        catch (SlickException ex) {
            Logger.getLogger(SimpleSlickGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean closeRequested() {

        System.exit(0);
        return true;
    }


}