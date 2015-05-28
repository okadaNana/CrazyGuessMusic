package com.owen.crazygussmusic.model;

public class Song {

	// 歌曲名称
	private String mSongName;
	// 歌曲文件名
	private String mSongFileName;
	// 歌曲名称的长度
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
