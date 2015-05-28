package com.owen.crazygussmusic.model;

import android.widget.Button;

/**
 * ���ְ�ť
 * 
 * @author owen
 */
public class WordButton {

	// ����
	private int mIndex;
	// ��ʾ�����صı�־
	private boolean mIsVisible;
	// ��ť������
	private String mWordString;

	// Button�ؼ�
	private Button mViewButton;

	/**
	 * �޲ι��췽������ʼ���ɼ��Կɰ�ť����
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
