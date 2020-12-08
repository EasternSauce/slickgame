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

    private Music townMusic;

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

        loadGame();
        
        townMusic = Assets.townMusic;

//        townMusic.loop(1.0f, 0.5f);

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

        saveGame();

        System.exit(0);
        return true;
    }

    private void saveGame() {
        try {
            FileWriter writer = new FileWriter("saves/savegame.sav");

            for (Area area : gameSystem.getAreas().values()) {
                area.getCreaturesManager().saveToFile(writer);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("saves/inventory.sav");

            for (Map.Entry<Integer, Item> inventoryItem : gameSystem.getInventoryWindow().getInventoryItems().entrySet()) {
                if (inventoryItem.getValue() != null) {
                    int slotId = inventoryItem.getKey();
                    String damage = inventoryItem.getValue().getDamage() == null ? "0" : "" + inventoryItem.getValue().getDamage().intValue();
                    String armor = inventoryItem.getValue().getArmor() == null ? "0" : "" + inventoryItem.getValue().getArmor().intValue();
                    String quantity = inventoryItem.getValue().getQuantity() == null ? "0" : "" + inventoryItem.getValue().getQuantity();

                    writer.write("inventory_item " + slotId + " " + inventoryItem.getValue().getItemType().getId() + " " + damage + " " + armor + " " + quantity + "\n");
                }
            }

            writer.write("gold " + gameSystem.getInventoryWindow().getGold() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("saves/respawn_points.sav");

            writer.write("respawnPoint " + gameSystem.getPlayerCharacter().getRespawnArea().getId() + " "
                    + gameSystem.getPlayerCharacter().getRespawnArea().getRespawnList().indexOf(gameSystem.getPlayerCharacter().getCurrentRespawnPoint()));

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGame() {
        Creature creature = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("saves/savegame.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");
                if(s[0].equals("creature")) {
                    Creature foundCreature = null;
                    for (Area area : gameSystem.getAreas().values()) {
                        foundCreature = area.getCreaturesManager().getCreatureById(s[1]);
                        if (foundCreature != null) break;
                    }

                    creature = foundCreature;

                }
                if(s[0].equals("pos")) {
                    if (creature != null) {
                        if (creature.getArea() == null) {
                            throw new RuntimeException("position cannot be set before creature is spawned in area");
                        }
                        creature.getRect().setX(Float.parseFloat(s[1]));
                        creature.getRect().setY(Float.parseFloat(s[2]));
                    }

                }
                if(s[0].equals("area")) {
                    if (creature != null) {
                        creature.setArea(gameSystem.getAreas().get(s[1]));

                        gameSystem.getAreas().get(s[1]).moveInCreature(creature, 0f, 0f);

                        if (creature instanceof PlayerCharacter) {
                            gameSystem.getCurrentAreaHolder().setCurrentArea(gameSystem.getAreas().get(s[1]));
                        }
                    }

                }
                if(s[0].equals("health")) {
                    if (creature != null) {
                        creature.setHealthPoints(Float.parseFloat(s[1]));
                    }

                }
                if(s[0].equals("equipment_item")) {
                    if (creature != null) {
                        Map<Integer, Item> equipmentItems = creature.getEquipmentItems();
                        equipmentItems.put(Integer.parseInt(s[1]), new Item(ItemType.getItemType(s[2]), null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))), (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4]))), 1));
                    }

                }

                if (creature instanceof PlayerCharacter) {
                  if (creature.getHealthPoints() <= 0f) {
                    creature.onDeath();
                  }
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/inventory.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if(s[0].equals("inventory_item")) {
                    if (creature != null) {
                        Map<Integer, Item> inventoryItems = gameSystem.getInventoryWindow().getInventoryItems();
                        inventoryItems.put(
                                Integer.parseInt(s[1]),
                                new Item(ItemType.getItemType(s[2]),
                                        null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))),
                                        (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4]))),
                                        (s[5].equals("0") ? null : (Integer.parseInt(s[5])))
                                )
                        );
                    }

                }
                if(s[0].equals("gold")) {
                    gameSystem.getInventoryWindow().setGold(Integer.parseInt(s[1]));
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/respawn_points.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if(s[0].equals("respawnPoint")) {
                    PlayerRespawnPoint respawnPoint = gameSystem.getAreas().get(s[1]).getRespawnList().get(Integer.parseInt(s[2]));
                    gameSystem.getPlayerCharacter().setCurrentRespawnPoint(respawnPoint);
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/treasure_collected.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if (s[0].equals("treasure")) {
                    Area area = gameSystem.getAreas().get(s[1]);
                    area.getRemainingTreasureList().remove(area.getTreasureList().get(Integer.parseInt(s[2])));
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(gameSystem.getCurrentArea() == null) {
            gameSystem.getCurrentAreaHolder().setCurrentArea(gameSystem.getAreas().get("area1"));
        }

        gameSystem.getCurrentArea().getCreaturesManager().updateAttackTypes();

    }

}