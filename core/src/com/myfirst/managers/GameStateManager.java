package com.myfirst.managers;

import com.myfirst.gamestates.GameState;
import com.myfirst.gamestates.HiScoreState;
import com.myfirst.gamestates.MenuState;
import com.myfirst.gamestates.PlayState;

public class GameStateManager {
	private GameState gameState;
	
	public static final int MENU = 0;
	public static final int PLAY = 1;
	public static final int HISCORE = 2;
	
	public GameStateManager(){
		setState(MENU);
	}
	
	public void setState(int state){
		if (gameState != null) gameState.dispose();
		if (state == MENU){
			gameState = new MenuState(this);
		}
		if (state == PLAY){
			gameState = new PlayState(this);
		}
//		if (state == HISCORE){
//			gameState = new HiScoreState(this);
//		}
	}
	
	public void update(float dt){
		gameState.update(dt);
	}
	
	public void draw(){
		gameState.draw();
	}
}

