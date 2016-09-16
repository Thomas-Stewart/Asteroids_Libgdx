package com.myfirst.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.myfirst.game.MyGdxGame;
import com.myfirst.managers.GameStateManager;

public class MenuState extends GameState{

	private SpriteBatch sb;
	private BitmapFont titleFont;
	private BitmapFont font;
	
	private final String title = "Asteroids";
	GlyphLayout titleFontLayout;
	GlyphLayout fontLayout;
	
	
	private int currentItem;
	private String[] menuItems;
	
	public MenuState(GameStateManager gsm) {
		super(gsm);
	}

	@Override
	public void init() {
		sb = new SpriteBatch();
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
			Gdx.files.internal("fonts/Hyperspace Bold.ttf")
		);
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.size = 56;
		
		titleFont = gen.generateFont(param);
		titleFont.setColor(Color.WHITE);
		
		param.size = 20;
		font = gen.generateFont(param);
		
		menuItems = new String[] {
				"Play",
				"Hi-Scores",
				"Quit"
		};
		
		//glyphlayout magic 
		titleFontLayout = new GlyphLayout(); //dont do this every frame! Store it as member
		titleFontLayout.setText(titleFont, title);
		fontLayout = new GlyphLayout();
		
	}

	@Override
	public void update(float dt) {
		handleInput();
	}

	@Override
	public void draw() {
		sb.setProjectionMatrix(MyGdxGame.cam.combined);
		sb.begin();
		float width = titleFontLayout.width;
		
		//draw centered title
		titleFont.draw(sb, title, (MyGdxGame.WIDTH - width) / 2, 300); 
		
		//draw menu
		for (int i = 0; i < menuItems.length; i++){
			fontLayout.setText(font, menuItems[i]);
			width = fontLayout.width;
			if(currentItem == i) font.setColor(Color.RED);
			else font.setColor(Color.WHITE);
			font.draw(sb, menuItems[i], (MyGdxGame.WIDTH - width) / 2, 180 - 35 * i);
		}
		sb.end();
	}

	@Override
	public void handleInput() {
		if(Gdx.input.isKeyJustPressed(Keys.UP)){
			if(currentItem > 0) currentItem--;
		}
		if(Gdx.input.isKeyJustPressed(Keys.DOWN)){
			if(currentItem < menuItems.length - 1) currentItem++;
		}
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
			select();
		}
	}

	public void select(){
		if (currentItem == 0) {
			gsm.setState(GameStateManager.PLAY);
		}
		else if (currentItem == 1){
			//gsm.setState(GameStateManager.HISCORES);
		}
		else if (currentItem == 2){
			Gdx.app.exit();
		}
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
