package com.owen.crazygussmusic.model;

public class Song {

	// ��������
	private String mSongName;
	// �����ļ���
	private String mSongFileName;
	// �������Ƶĳ���
	private int mNameLength;

	public char[] getNameCharacters() {
		return mSongName.toCharArray();
	}
	
	public String getSongName() {
		return mSongName;
	}

	public void setSongName(String songName) {
		mSongName = songName;
		
		mNameLength = mSongName.length();
	}

	public String getSongFileName() {
		return mSongFileName;
	}

	public void setSongFileName(String songFileName) {
		mSongFileName = songFileName;
	}

	public int getNameLength() {
		return mNameLength;
	}

}
