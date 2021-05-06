package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter  {
	SpriteBatch batch;
	Texture background;

	Texture[] man;
	int manState=0,pause = 0;
	float velocity =0,gravity = 0.2f;
	int Yman=0;
	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinZCount;
	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombZCount,score;
	Random random;
	Rectangle manRectangle;
	BitmapFont font;
	int gameState=0;
	Texture dizzy;
	Sound coinSound,bombSound;
	Music backMusic;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		coinSound = Gdx.audio.newSound(Gdx.files.internal("blup.mp3"));
		bombSound = Gdx.audio.newSound(Gdx.files.internal("cat.mp3"));
		backMusic = Gdx.audio.newMusic(Gdx.files.internal("electromania.mp3"));
		background = new Texture("bg.png");
		man = new Texture[4];
		dizzy = new Texture("dizzy-1.png");
		man[0] = new Texture("frame-1.png");

		man[1] = new Texture("frame-2.png");

		man[2] = new Texture("frame-3.png");

		man[3] = new Texture("frame-4.png");
		Yman=Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		font =new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);


	}
	public void makeCoin(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}
	public void makeBomb(){
		float height = random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        if (gameState==0){
        	//game is in loading state
			if (Gdx.input.justTouched()){
				gameState=1;
			}
		}else if (gameState==1){
        	//game is in active stagte
			//coins

			if (coinZCount<75){
				coinZCount++;
			}else{
				coinZCount=0;
				makeCoin();
			}
			coinRectangles.clear();
			for (int i =0;i<coinXs.size();i++){
				batch.draw(coin, coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-12);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			//bomb

			if (bombZCount<200){
				bombZCount++;
			}else{
				bombZCount=0;
				makeBomb();
			}
			bombRectangles.clear();
			for (int i =0;i<bombXs.size();i++){
				batch.draw(bomb, bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-16);
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}
			if (Yman<=0){
				Yman=0;
			}
			if (Gdx.input.justTouched()){
				velocity=-10;
			}
			if(pause<8){
				pause++;
			}else {
				pause=0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity+=gravity;
			Yman-=velocity;

		}else if (gameState==2){
        	//game is in off state
			if (Gdx.input.justTouched()) {
				gameState = 1;
				Yman = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinYs.clear();
				coinXs.clear();
				coinRectangles.clear();
				coinZCount = 0;

				bombYs.clear();
				bombXs.clear();
				bombRectangles.clear();
				bombZCount = 0;
			}
		}
        if (gameState==2){
        	batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, Yman);
        	backMusic.stop();

		}else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, Yman);
		}
        if (gameState==0||gameState==1){
        	backMusic.play();
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth()/2-man[manState].getWidth()/2,Yman,man[manState].getWidth(),man[manState].getHeight());

		for (int i =0;i<coinRectangles.size();i++){
			if (Intersector.overlaps(manRectangle,coinRectangles.get(i))){
				Gdx.app.log("coin", "collision");
				score++;
				coinSound.play();
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for (int i =0;i<bombRectangles.size();i++){
			if (Intersector.overlaps(manRectangle,bombRectangles.get(i))){
				Gdx.app.log("bomb", "collision");
				gameState=2;
				bombSound.play();


			}
		}
		font.draw(batch,String.valueOf(score),100,200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		coinSound.dispose();
		bombSound.dispose();

	}
}
