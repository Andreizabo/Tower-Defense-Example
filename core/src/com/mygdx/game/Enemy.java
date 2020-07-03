package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Enemy {
    float x, y;
    float waypointX, waypointY;
    public boolean needWaypoint = true;
    float speedX, speedY;
    public int where = 0;
    boolean deleted = false;

    void move() {
        if(ABS((int)(x - waypointX)) < 5 && ABS((int)(y - waypointY)) < 5) {
            x = waypointX;
            y = waypointY;
            needWaypoint = true;
            where++;
        }
        else {
            //Gdx.app.log("??", x + " " + y + " " + speedX + " " + speedY);
            x += (speedX / 0.01);
            y += (speedY / 0.01);
        }
    }

    void newWaypoint(int newX, int newY) {
        waypointX = newX;
        waypointY = newY;
        speedX = 1 / (float)ABS((int)(y - waypointY));
        speedY = 1 / (float)ABS((int)(x - waypointX));
        if(waypointY - y < 0) {
            speedY *= -1;
        }
        needWaypoint = false;
    }

    int ABS(int x) {
        if(x < 0) return -1 * x;
        return x;
    }

}
