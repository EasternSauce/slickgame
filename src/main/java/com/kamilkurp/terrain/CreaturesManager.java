package com.kamilkurp.terrain;

import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NonPlayerCharacter;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.items.Item;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CreaturesManager {
    private final Map<String, Creature> creatures;
    private final Area area;

    private Queue<Creature> renderPriorityQueue;


    public CreaturesManager(Area area) {
        this.area = area;
        creatures = new HashMap<>();
    }

    public void onAreaChange() {
        for (Creature creature : creatures.values()) {
//            System.out.println("on area change " + creature.getId());
            if (!(creature instanceof PlayerCharacter || creature instanceof NonPlayerCharacter)) {
                System.out.println(creature.getId() + " health: " + creature.getHealthPoints());
                if (!creature.isAlive()) {
                    System.out.println("marking for deletion");
                    creature.markForDeletion();
//                    System.out.println("marking for deletion");
                }

            }
        }

        System.out.println("removing marked objects");
        creatures.entrySet().removeIf(e ->  {

            System.out.println(e.getValue().getId() + " is to be removed: " + e.getValue().isToBeRemoved());
            return e.getValue().isToBeRemoved();
        });
    }

    public void processAreaChanges(List<Creature> creaturesToMove) throws SlickException {
        for (Creature creature : creatures.values()) {
            if (creature.getPendingArea() != null) {
                creaturesToMove.add(creature);
            }
        }

        area.updateSpawns();
    }

    public void updateRenderPriorityQueue() {
        renderPriorityQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getHealthPoints() <= 0.0f) return -1;
            if (o2.getHealthPoints() <= 0.0f) return 1;
            if (o1.getRect().getY() == o2.getRect().getY()) return 0;
            return (o1.getRect().getY() - o2.getRect().getY() > 0.0f) ? 1 : -1;
        });

        renderPriorityQueue.addAll(creatures.values());
    }

    public void renderCreatures(Graphics g, Camera camera) {
        if (renderPriorityQueue != null) {
            while (!renderPriorityQueue.isEmpty()) {
                Creature creature = renderPriorityQueue.poll();

                creature.render(g, camera);
            }

        }


        for (Creature creature : creatures.values()) {
            creature.renderAbilities(g, camera);
        }
    }

    public void saveToFile(FileWriter writer) throws IOException {
        for (Creature creature : creatures.values()) {
            if (creature.getClass() != PlayerCharacter.class && creature.getClass() != NonPlayerCharacter.class) continue;
            writer.write("creature " + creature.getId() + "\n");
            writer.write("area " + creature.getArea().getId() + "\n");
            writer.write("pos " + creature.getRect().getX() + " " + creature.getRect().getY() + "\n");
            writer.write("health " + creature.getHealthPoints() + "\n");

            Map<Integer, Item> equipmentItems = creature.getEquipmentItems();

            for (Map.Entry<Integer, Item> equipmentItem : equipmentItems.entrySet()) {
                if (equipmentItem.getValue() != null) {
                    String damage = equipmentItem.getValue().getDamage() == null ? "0" : "" + equipmentItem.getValue().getDamage().intValue();

                    String armor = equipmentItem.getValue().getArmor() == null ? "0" : "" + equipmentItem.getValue().getArmor().intValue();
                    writer.write("equipment_item " + equipmentItem.getKey() + " " + equipmentItem.getValue().getItemType().getId() + " " + damage + " " + armor + "\n");
                }
            }
        }
    }

    public Creature getCreatureById(String id) {
        return creatures.get(id);
    }

    public void addCreature(Creature creature) {
        creatures.put(creature.getId(), creature);
    }

    public void updateAttackTypes() {
        for (Creature creature : creatures.values()) {
            creature.updateAttackType();
        }
    }

    public void removeCreature(String id) {
        creatures.remove(id);

    }

    public void updateGatesLogic(AreaGate areaGate, CurrentAreaHolder currentAreaHolder) {
        for (Creature creature : creatures.values()) {
            if (creature instanceof PlayerCharacter) {
                if (!creature.isPassedGateRecently()) {
                    Rectangle gateRect = null;
                    Area destinationArea = null;
                    Area oldArea = null;
                    Rectangle destinationRect = null;
                    if (area == areaGate.getAreaFrom()) {
                        gateRect = areaGate.getFromRect();
                        oldArea = areaGate.getAreaFrom();
                        destinationArea = areaGate.getAreaTo();
                        destinationRect = areaGate.getToRect();
                    }
                    if (area == areaGate.getAreaTo()) {
                        gateRect = areaGate.getToRect();
                        oldArea = areaGate.getAreaTo();
                        destinationArea = areaGate.getAreaFrom();
                        destinationRect = areaGate.getFromRect();
                    }

                    if (creature.getRect().intersects(gateRect)) {
                        creature.setPassedGateRecently(true);

                        creature.moveToArea(destinationArea, destinationRect.getX(), destinationRect.getY());

                        currentAreaHolder.setCurrentArea(destinationArea);


                        oldArea.onLeave();
                        destinationArea.onEntry();

                    }
                }
            }
        }
    }


    public Map<String, Creature> getCreatures() {
        return creatures;
    }

    public void clearRespawnableCreatures() {
        creatures.entrySet().removeIf(creature -> !(creature.getValue() instanceof PlayerCharacter || creature.getValue() instanceof NonPlayerCharacter));

    }
}
