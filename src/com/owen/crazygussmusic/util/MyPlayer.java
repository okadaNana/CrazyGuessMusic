package com.owen.crazygussmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐播放类
 * 
 * @author owen
 *
 */
public class MyPlayer {
	
	// 单例
	private static MediaPlayer mMediaPlayer;
	
	/**
	 * 播放歌曲
	 */
	public static void playSong(Context context, String fileName) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		// 重置
		mMediaPlayer.reset();
		
		// 加载声音文件
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			// 设置数据源
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), 
					fileDescriptor.getStartOffset(), 
					fileDescriptor.getLength());
			
			mMediaPlayer.prepare();
			
			// 声音播放
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止歌曲
	 */
	public static void stopTheSone(Context context) {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	// 音效文件名数组
	public static final String[] SONG_NAMES = new String[] {"enter.mp3", "cancel.mp3", "coin.mp3"};
	// 音效文件名索引
	public static final int INDEX_STONE_ENTER  = 0;
	public static final int INDEX_STONE_CANCEL = 1;
	public static final int INDEX_STONE_COIN   = 2;
	// 音效播放器
	private static MediaPlayer[] mTonePlayers = new MediaPlayer[SONG_NAMES.length];

	/**
	 * 播放音效
	 */
	public static void playTone(Context context, int index) {
		AssetManager assetManager = context.getAssets();
		
		if (mTonePlayers[index] == null) {
			mTonePlayers[index] = new MediaPlayer();
			AssetFileDescriptor fileDescriptor;
			try {
				fileDescriptor = assetManager.openFd(SONG_NAMES[index]);
				mTonePlayers[index].setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				
				mTonePlayers[index].prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
			
		mTonePlayers[index].start();
	}
	
}
