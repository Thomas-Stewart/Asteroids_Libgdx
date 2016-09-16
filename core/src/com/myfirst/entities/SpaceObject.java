package com.myfirst.entities;

import com.myfirst.game.MyGdxGame;

public class SpaceObject {
	protected float x;
	protected float y;
	
	protected float dx;
	protected float dy;
	
	protected float radians;
	protected float speed;
	protected float rotationSpeed;
	
	protected int width;
	protected int height;
	
	protected float [] shapex;
	protected float [] shapey;
	
	public float getx(){ return x; }
	public float gety(){ return y; }
	
	public float[] getShapex(){ return shapex; }
	public float[] getShapey(){ return shapey; }
	
	
	public void setPosition(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public boolean intersects (SpaceObject other){
		float[] shapex = other.getShapex();
		float[] shapey = other.getShapey();
		for (int i = 0; i < shapex.length; i++){
			if (contains(shapex[i], shapey[i])){
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(float x, float y){
		boolean b = false;
		for (int i = 0, j = shapex.length - 1;
				i < shapex.length;
				j = i++){
			if ((shapey[i] > y) != (shapey[j] > y) &&
				(x < (shapex[j] - shapex[i]) *
				(y - shapey[i]) / (shapey[j] - shapey[i])
				+ shapex[i])){
				b = !b;
			}
		}
		return b;
	}
	
	protected void wrap(){
		if (x < 0) {x = MyGdxGame.WIDTH;}
		if (x > MyGdxGame.WIDTH) {x = 0;}
		if (y < 0) {y = MyGdxGame.HEIGHT;}
		if (y > MyGdxGame.HEIGHT) {y = 0;}
	}
}
