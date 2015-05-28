package com.owen.crazygussmusic.ui;

import com.owen.crazygussmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class AllPassView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_view);
		
		FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}
	
}
