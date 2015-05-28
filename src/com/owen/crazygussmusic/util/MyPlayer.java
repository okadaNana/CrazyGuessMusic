package com.owen.crazygussmusic.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * ���ֲ�����
 * 
 * @author owen
 *
 */
public class MyPlayer {
	
	// ����
	private static MediaPlayer mMediaPlayer;
	
	/**
	 * ���Ÿ���
	 */
	public static void playSong(Context context, String fileName) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		// ����
		mMediaPlayer.reset();
		
		// ���������ļ�
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			// ��������Դ
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), 
					fileDescriptor.getStartOffset(), 
					fileDescriptor.getLength());
			
			mMediaPlayer.prepare();
			
			// ��������
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ֹͣ����
	 */
	public static void stopTheSone(Context context) {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	// ��Ч�ļ�������
	public static final String[] SONG_NAMES = new String[] {"enter.mp3", "cancel.mp3", "coin.mp3"};
	// ��Ч�ļ�������
	public static final int INDEX_STONE_ENTER  = 0;
	public static final int INDEX_STONE_CANCEL = 1;
	public static final int INDEX_STONE_COIN   = 2;
	// ��Ч������
	private static MediaPlayer[] mTonePlayers = new MediaPlayer[SONG_NAMES.length];

	/**
	 * ������Ч
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
