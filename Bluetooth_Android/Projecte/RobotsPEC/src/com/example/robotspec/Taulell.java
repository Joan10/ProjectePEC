package com.example.robotspec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class Taulell extends SurfaceView implements SurfaceHolder.Callback {

	private Context _context;
	private ControlUsuari _controls;
	
	private int robot; //0 Autobot, 1 Deception

	public Taulell(Context context, int robot) {
		super(context);
		// TODO Auto-generated constructor stub
		_context = context;
		init();
	}

	private void init(){
		//initialize our screen holder
		SurfaceHolder holder = getHolder();
		holder.addCallback( this);

		//initialize our game engine

		//initialize our Thread class. A call will be made to start it later
		setFocusable(true);

		_controls = new ControlUsuari();
		setOnTouchListener(_controls);
	}


	public void doDraw(Canvas canvas){
		//update the pointer
		_controls.update(null);
        setBackgroundColor(getResources().getColor(0xFFFF));
     //   resetColor();
       
		if (robot == 1)
			canvas.drawText("Autobot", 0, 0, null);
		else
			canvas.drawText("Deception", 0, 0, null);
	}



	//these methods are overridden from the SurfaceView super class. They are automatically called 
	//when a SurfaceView is created, resumed or suspended.
	@Override 
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}
//	private boolean retry;
	
	public void surfaceDestroyed(SurfaceHolder arg0) {	}

	@Override 
	public void surfaceCreated(SurfaceHolder arg0) {}
	public void Update() {
		// TODO Auto-generated method stub

	}

}