package com.kamilkurp.spawn;

public class SpawnLocation {
    private String spawnType;
    private String creatureType;
    private int posX;
    private int posY;
    private int blockadePosX;
    private int blockadePosY;
    private boolean hasBlockade;


    public SpawnLocation(String spawnType, String creatureType, int posX, int posY) {
        this.spawnType = spawnType;
        this.creatureType = creatureType;
        this.posX = posX;
        this.posY = posY;
        hasBlockade = false;
    }

    public SpawnLocation(String spawnType, String creatureType, int posX, int posY, int blockadePosX, int blockadePosY) {
        this.spawnType = spawnType;
        this.creatureType = creatureType;
        this.posX = posX;
        this.posY = posY;
        this.blockadePosX = blockadePosX;
        this.blockadePosY = blockadePosY;
        hasBlockade = true;
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

    public int getBlockadePosX() {
        return blockadePosX;
    }

    public int getBlockadePosY() {
        return blockadePosY;
    }

    public boolean isHasBlockade() {
        return hasBlockade;
    }
}