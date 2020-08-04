package com.kamilkurp.terrain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TerrainLayout {
    private String[][] layout;


    public TerrainLayout(String layoutFileName) {
        loadLayout(layoutFileName);
    }

    public void loadLayout(String fileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] s = line.split(" ");
            int columns = Integer.parseInt(s[0]);
            int rows = Integer.parseInt(s[1]);
            layout = new String[rows][columns];
            line = reader.readLine();
            while (line != null) {
                for (int i=0; i<layout.length; i++) {
                    String[] s1 = line.split(" ");
                    for (int j = 0; j < s1.length; j++) {
                        layout[i][j] = s1[j];
                    }
                    line = reader.readLine();

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public String getLayoutTile(int x, int y) {
        return layout[y][x];
    }

    public int getLayoutColumns() {
        return layout[0].length;
    }

    public int getLayoutRows() {
        return layout.length;
    }

    public void setLayoutTile(int x, int y, String value) {
        layout[y][x] = value;
    }
}
