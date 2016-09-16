package com.myfirst.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myfirst.entities.Asteroid;
import com.myfirst.entities.Bullet;
import com.myfirst.entities.Particle;
import com.myfirst.entities.Player;
import com.myfirst.game.MyGdxGame;
import com.myfirst.managers.GameStateManager;
import com.myfirst.managers.Jukebox;

public class PlayState extends GameState{

	private SpriteBatch sb;
	private BitmapFont font;
	private ShapeRenderer sr;
	private Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	private ArrayList<Particle> particles;
	private Player lifeIcon;
	
	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;
	
	private float maxDelay;
	private float minDelay;
	private float currentDelay;
	private float bgTimer;
	private boolean playLowPulse;
	
	public PlayState(GameStateManager gsm){
		super(gsm);
	}

	@Override
	public void init() {
		
		lifeIcon = new Player(null);
		sb = new SpriteBatch();
		
		//set font
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
				Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		font = gen.generateFont(parameter);
		
		sr = new ShapeRenderer();
		bullets = new ArrayList<Bullet>();
		player = new Player(bullets);
		asteroids = new ArrayList<Asteroid>();
		particles = new ArrayList<Particle>();
		
		level = 1;
		spawnAsteroids();
		
		//set up bg music
		maxDelay = 1;
		minDelay = 0.25f;
		currentDelay = maxDelay;
		bgTimer = maxDelay;
		playLowPulse = true;

	}
	
	private void createParticles(float x, float y){
		for (int i = 0; i < 6; i++){
			particles.add(new Particle(x,y));
		}
	}
	
	private void spawnAsteroids(){
		currentDelay = maxDelay;
		asteroids.clear();
		
		int numToSpawn = 4 + level - 1;
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		
		float dist,x,y;
		for (int i = 0; i < numToSpawn; i++){
			do{
				x = MathUtils.random(MyGdxGame.WIDTH);
				y = MathUtils.random(MyGdxGame.HEIGHT);
	
				float dx = x - player.getx();
				float dy = y - player.gety();
				dist = (float) Math.sqrt(dx * dx + dy * dy);
				
			}while(dist < 100);
			
			asteroids.add(new Asteroid(x,y,Asteroid.LARGE));
		}
	}

	@Override
	public void update(float dt) {
		//get player input
		handleInput();
		
		//next level
		if (asteroids.size() == 0){
			level++;
			spawnAsteroids();
		}
		
		//update player
		player.update(dt);
		if (player.isDead()){
			if(player.getLives() == 0){
				gsm.setState(GameStateManager.MENU);
			}
			player.reset();
			player.loseLife();
			return;
		}
		
		
		
		//update bullets
		for (int i = 0; i < bullets.size(); i++){
			bullets.get(i).update(dt);
			if (bullets.get(i).shouldRemove()){
				bullets.remove(i);
				i--;
			}
		}
		
		//update asteroids
		for (int i = 0; i < asteroids.size(); i++){
			asteroids.get(i).update(dt);
			if (asteroids.get(i).shouldRemove()){
				asteroids.remove(i);
				i--;
			}
		}
		
		//update particles
		for (int i = 0; i < particles.size(); i++){
			particles.get(i).update(dt);
			if (particles.get(i).shouldRemove()){
				particles.remove(i);
				i--;
			}
		}
		
		//check collisions
		checkCollisions();
		
		//play background music
		bgTimer += dt;
		if (!player.isHit() && bgTimer >= currentDelay){
			if (playLowPulse){
				Jukebox.play("pulselow");
			} else {
				Jukebox.play("pulsehigh");
			}
			playLowPulse = !playLowPulse;
			bgTimer = 0;
		}
		
	}
	
	private void checkCollisions(){
		//bullet-asteroid collision
		for (int i = 0; i < bullets.size(); i++){
			Bullet b = bullets.get(i);
			for (int j = 0; j < asteroids.size(); j++){
				Asteroid a = asteroids.get(j);
				if (a.contains(b.getx(), b.gety())){
					Jukebox.play("explode");
					bullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					break;
				}
			}
		}
		
		//player-asteroid collision
		if (!player.isHit()){
			for (int i = 0; i < asteroids.size(); i++){
				Asteroid a = asteroids.get(i);
				if (a.intersects(player)){
					Jukebox.play("explode");
					player.hit();
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					break;
				}
			}
		}
	}
	
	private void splitAsteroids(Asteroid a){
		createParticles(a.getx(), a.gety());
		numAsteroidsLeft--;
		currentDelay = ((maxDelay - minDelay) *
				numAsteroidsLeft / totalAsteroids)
				+ minDelay;
				
		if (a.getType() == Asteroid.LARGE){
			player.incrementScore(20);
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
		}
		if (a.getType() == Asteroid.MEDIUM){
			player.incrementScore(50);
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
		}
		if (a.getType() == Asteroid.SMALL){
			player.incrementScore(100);
		}
	}

	@Override
	public void draw() {
		//draw player
		player.draw(sr);
		
		//draw bullets
		for (int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(sr);
		}
		
		//draw asteroids
		for (int i = 0; i < asteroids.size(); i++){
			asteroids.get(i).draw(sr);
		}
		
		//draw particles
		for (int i = 0; i < particles.size(); i++){
			particles.get(i).draw(sr);
		}
		
		//draw score
		sb.setColor(1,1,1,1);
		sb.begin();
		font.draw(sb, Long.toString(player.getScore()), 40, 390);
		sb.end();
		
		//draw lives
		for (int i = 0 ; i < player.getLives(); i++){
			lifeIcon.setPosition(40 + i * 15, 360);
			lifeIcon.draw(sr);
		}
	}

	@Override
	public void handleInput() {
		player.setLeft(Gdx.input.isKeyPressed(Keys.LEFT));
		player.setRight(Gdx.input.isKeyPressed(Keys.RIGHT));
		player.setUp(Gdx.input.isKeyPressed(Keys.UP));
		if(Gdx.input.isKeyJustPressed(Keys.SPACE)){
			player.shoot();
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
