package com.owen.crazygussmusic.model;

import android.widget.Button;

/**
 * 文字按钮
 * 
 * @author owen
 */
public class WordButton {

	// 索引
	private int mIndex;
	// 显示或隐藏的标志
	private boolean mIsVisible;
	// 按钮的文字
	private String mWordString;

	// Button控件
	private Button mViewButton;

	/**
	 * 无参构造方法，初始化可见性可按钮文字
	 */
	public WordButton() {
		mIsVisible = true;
		mWordString = "";
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public boolean isIsVisible() {
		return mIsVisible;
	}

	public void setIsVisible(boolean mIsVisible) {
		this.mIsVisible = mIsVisible;
	}

	public String getWordString() {
		return mWordString;
	}

	public void setWordString(String mWordString) {
		this.mWordString = mWordString;
	}

	public Button getViewButton() {
		return mViewButton;
	}

	public void setViewButton(Button mViewButton) {
		this.mViewButton = mViewButton;
	}

}
