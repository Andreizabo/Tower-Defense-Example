package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class Turret {
    float x, y;
    float projX, projY, speedX, speedY, targetX, targetY;
    boolean hasTarget = false;
    int cooldown = 0;

    void shot() {
        if(projX <= 0 || projX >= Gdx.graphics.getWidth() || projY <= 0 || projY >= Gdx.graphics.getHeight()) {
            hasTarget = false;
        }
        if(hasTarget) {
            projX += ( speedX / 0.005);
            projY += ( speedY / 0.005);
        }
    }

    void newTarget(float newX, float newY) {
        targetX = newX;
        targetY = newY;
        speedY = 1 / ABS(targetX - x);
        speedX = 1 / ABS(targetY - y);
        if(targetY - y < 0) {
            speedY *= -1;
        }
        if(targetX - x < 0) {
            speedX *= -1;
        }
        hasTarget = true;
        projX = x;
        projY = y;
    }

    float ABS(float x) {
        if(x < 0) return -1 * x;
        return x;
    }
}
