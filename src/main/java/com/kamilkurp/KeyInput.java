package com.kamilkurp;

import org.newdawn.slick.Input;

import java.util.Map;
import java.util.TreeMap;

public class KeyInput {

    private final Map<Key, Boolean> keyPressed;

    public enum Key {
        W,S,A,D,E,SPACE,ESC,I,F5
    }

    public KeyInput() {
        keyPressed = new TreeMap<>();
    }

    public void readKeyPresses(Input input) {

        keyPressed.put(Key.W, input.isKeyPressed(Input.KEY_W));

        keyPressed.put(Key.A, input.isKeyPressed(Input.KEY_A));

        keyPressed.put(Key.S, input.isKeyPressed(Input.KEY_S));

        keyPressed.put(Key.D, input.isKeyPressed(Input.KEY_D));

        keyPressed.put(Key.E, input.isKeyPressed(Input.KEY_E));

        keyPressed.put(Key.SPACE, input.isKeyPressed(Input.KEY_SPACE));

        keyPressed.put(Key.ESC, input.isKeyPressed(Input.KEY_ESCAPE));

        keyPressed.put(Key.I, input.isKeyPressed(Input.KEY_I));

        keyPressed.put(Key.F5, input.isKeyPressed(Input.KEY_F5));

    }

    public boolean isKeyPressed(Key key) {
        return keyPressed.get(key);
    }

}
