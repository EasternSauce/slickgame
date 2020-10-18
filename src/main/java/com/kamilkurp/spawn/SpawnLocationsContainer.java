package com.kamilkurp.spawn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SpawnLocationsContainer {

    private final List <SpawnLocation> spawnLocationList;


    public SpawnLocationsContainer(String enemyFileLocation) {

        spawnLocationList = new LinkedList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(enemyFileLocation));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                String spawnType = s[0];
                String enemyType = s[1];
                int posX = Integer.parseInt(s[2]);
                int posY = Integer.parseInt(s[3]);

                spawnLocationList.add(new SpawnLocation(spawnType, enemyType, posX, posY));

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<SpawnLocation> getSpawnLocationList() {
        return spawnLocationList;
    }

    public static class SpawnLocation {
        String spawnType;
        String creatureType;
        int posX;
        int posY;

        public SpawnLocation(String spawnType, String creatureType, int posX, int posY) {
            this.spawnType = spawnType;
            this.creatureType = creatureType;
            this.posX = posX;
            this.posY = posY;
        }

        public String getSpawnType() {
            return spawnType;
        }

        public String getCreatureType() {
            return creatureType;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }
    }

}
