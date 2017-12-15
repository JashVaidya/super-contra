package com.jashvaidya.kontra.Sprites.items;
/**
 * ItemDefinition class is used to contain the possible item
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.math.Vector2;

public class ItemDefinition {
    //Creating the variable to get the location and the item type
    public Vector2 position;
    public Class<?> type;

    public ItemDefinition(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
