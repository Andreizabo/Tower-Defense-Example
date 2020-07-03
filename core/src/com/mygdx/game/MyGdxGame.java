package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.org.apache.xpath.internal.operations.And;

import java.awt.geom.Line2D;

public class MyGdxGame extends ApplicationAdapter {

	int[] Xes = new int[99];
	int[] Ys = new int[99];
	Enemy[] ens;
	Turret[] trts;
	int ensNum = 0;
	int ensBoo = 0;
	int trtsNum = 0;
	int curves = 0;
	int timer = 0;
	int remaining = 40;
	ShapeRenderer sr;
	BitmapFont bf;
	SpriteBatch sb;

	boolean won = false;
	boolean lost = false;

	@Override
	public void create () {
		sr = new ShapeRenderer();
		bf = new BitmapFont();
		sb = new SpriteBatch();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		drawRandomRoad();
		ens = new Enemy[64];
		trts = new Turret[64];
	}

	@Override
	public void render () {
		if(!won && !lost) {
			if (ensNum < 40 && timer >= 30) {
				ens[ensNum] = new Enemy();
				ens[ensNum].x = Xes[0];
				ens[ensNum].y = Ys[0];
				ens[ensNum].newWaypoint(Xes[1], Ys[1]);
				ensNum++;
				timer = 0;
			}
			if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.justTouched()) && ensNum - ensBoo > 0) {
				if (trtsNum < 5) {
					Gdx.app.log("here", Gdx.input.getX() + " " + Gdx.input.getY());
					trts[trtsNum] = new Turret();
					trts[trtsNum].x = Gdx.input.getX();
					trts[trtsNum].y = ABS(Gdx.input.getY() - Gdx.graphics.getHeight());
					trtsNum++;
				}
			}
			Gdx.gl.glClearColor(1, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			sr.begin(ShapeRenderer.ShapeType.Filled);
			sr.setColor(Color.BLUE);
			for (int i = 1; i < curves; ++i) {
				sr.rectLine(Xes[i - 1], Ys[i - 1], Xes[i], Ys[i], 5);
			}
			sr.setColor(Color.BLACK);
			for (int i = ensBoo; i < ensNum; ++i) {
				if (!ens[i].deleted) {
					if (ens[i].where + 1 == curves || ens[i].x >= Gdx.graphics.getWidth()) {
						ensBoo++;
						ens[i].deleted = true;
						remaining = 666;
						break;
					}
					if (ens[i].needWaypoint) {
						ens[i].newWaypoint(Xes[ens[i].where + 1], Ys[ens[i].where + 1]);
					}
					ens[i].move();
					sr.circle(ens[i].x, ens[i].y, 20);
				}
			}
			if (remaining == 666) {
				lost = true;
			}
			for (int i = 0; i < trtsNum; ++i) {
				sr.setColor(Color.FOREST);
				sr.circle(trts[i].x, trts[i].y, 25);
				trts[i].cooldown--;
				if (!trts[i].hasTarget && trts[i].cooldown <= 0) {
					double shortestDist = 1000;
					int k = 0;
					for (int j = ensBoo; j < ensNum; ++j) {
						if (!ens[j].deleted) {
							double sqrt = java.lang.Math.sqrt((trts[i].x - ens[j].x) * (trts[i].x - ens[j].x) + (trts[i].y - ens[j].y) * (trts[i].y - ens[j].y));
							if (sqrt < shortestDist) {
								shortestDist = sqrt;
								k = j;
							}
						}
					}
					Gdx.app.log("!", k + "");
					trts[i].newTarget(ens[k].x, ens[k].y);
					trts[i].cooldown = 120;
				}
				trts[i].shot();
				sr.setColor(Color.CYAN);
				sr.circle(trts[i].projX, trts[i].projY, 5);
				for (int j = ensBoo; j < ensNum; ++j) {
					if (!ens[j].deleted) {
						double sqrt = java.lang.Math.sqrt((trts[i].projX - ens[j].x) * (trts[i].projX - ens[j].x) + (trts[i].projY - ens[j].y) * (trts[i].projY - ens[j].y));
						if (sqrt <= 20) {
							ens[j].deleted = true;
							trts[i].hasTarget = false;
							trts[i].projY = -1;
							trts[i].projX = -1;
							remaining--;
						}
					}
				}
			}
			sr.end();
			timer++;
			sb.begin();
			bf.setColor(Color.LIME);
			bf.draw(sb, "Enemies remaining: " + remaining, Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() - 50);
			if(trtsNum != 5)
				bf.draw(sb, "You can place " + (5 - trtsNum) + " more turrets", Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() - 35);
			else
				bf.draw(sb, "You can't place any more turrets", Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() - 35);
			if (remaining == 0) {
				won = true;
			}
			sb.end();
		}
		else {
			bf.getData().setScale(3);
			if(won) {
				sb.begin();
				bf.setColor(Color.LIME);
				bf.draw(sb, "You've won!", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
				sb.end();
			}
			else {
				sb.begin();
				bf.setColor(Color.LIME);
				bf.draw(sb, "You've lost!", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() / 2 + 50);
				sb.end();
			}
			if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.justTouched()) Gdx.app.exit();
		}
	}

	public void drawRandomRoad() {
		//sr.begin(ShapeRenderer.ShapeType.Line);
		//sr.setColor(Color.BLUE);
		int crtX = 1;
		int crtY = customRand(crtX, Gdx.graphics.getHeight(), 20, crtX, Gdx.graphics.getHeight());
		Xes[curves] = crtX;
		Ys[curves++] = crtY;
		while(crtX < Gdx.graphics.getWidth()) {
			int newX = customRand(crtX, Gdx.graphics.getWidth(), 20, crtX, 200);
			newX = crtX + MIN(newX - crtX, Gdx.graphics.getWidth() - crtX);
			//sr.rectLine(crtX, crtY, newX, crtY, 5);
			crtX = newX;
			Xes[curves] = crtX;
			if(crtX >= Gdx.graphics.getWidth()) {
				Ys[curves++] = crtY;
				break;
			}
			int normalized = (int)((double)((double)((double)100 / (double)Gdx.graphics.getHeight()) * (double)crtY));
			int chance = MathUtils.random(1, 100);
			if(normalized < chance) { //go up
				int newY = customRand(crtY, Gdx.graphics.getHeight(), 20, crtY, 100);
				if(newY >= Gdx.graphics.getHeight()) newY = Gdx.graphics.getHeight() - 1;
				//sr.rectLine(crtX, crtY, crtX, newY, 5);
				crtY = newY;
			}
			else { //go down
				int newY = customRand(0, crtY, 20, crtY, 100);
				if(newY <= 0) newY = 1;
				//sr.rectLine(crtX, crtY, crtX, newY, 5);
				crtY = newY;
			}
			Ys[curves++] = crtY;
		}
		//sr.end();
	}

	int customRand(int beg, int end, int min, int compMin, int max) {
		min = MIN(min, ABS(beg - end));
		int num = MathUtils.random(beg, end);
		int crt = ABS(compMin - num);
		while(crt < min || crt > max) {
			num = MathUtils.random(beg, end);
			crt = ABS(compMin - num);
		}
		return num;
	}



	int MIN(int x, int y) {
		if(x < y) return x;
		return y;
	}

	int ABS(int x) {
		if(x < 0) return -1 * x;
		return x;
	}

	@Override
	public void dispose () {
		sr.dispose();
		bf.dispose();
		sb.dispose();
	}
}
