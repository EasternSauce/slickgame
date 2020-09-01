package com.kamilkurp.terrain;

public class CurrentAreaManager {
    private Area currentArea;

    public Area getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(Area area) {
        currentArea = area;

        System.out.println("setting current area to be " + area.getId());
    }
}
