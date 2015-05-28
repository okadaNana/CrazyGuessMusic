package com.owen.crazygussmusic.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.owen.crazygussmusic.R;
import com.owen.crazygussmusic.data.Const;
import com.owen.crazygussmusic.model.IAlertDialogButtonListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 工具类
 * 
 * @author owen
 */
public class Util {

	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View layout = inflater.inflate(layoutId, null);
		
		return layout;
	}
	
	/**
	 * 跳转到指定的Activity，并关闭当前的Activity
	 */
	public static void startActivity(Context context, Class destination) {
		Intent intent = new Intent();
		intent.setClass(context, destination);
		context.startActivity(intent);
		
		((Activity) context).finish();
	}

	private static AlertDialog mAlertDialog;
	
	public static void showDialog(final Context context, String dialogMessage, final IAlertDialogButtonListener listener) {
		View dialogView = null;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);
		
		ImageButton btnOkView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
		ImageButton btnCancelView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
		TextView textMessageView = (TextView) dialogView.findViewById(R.id.text_view_dialog);
		
		textMessageView.setText(dialogMessage);
		
		btnOkView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				
				// 事件回调
				if (listener != null) {
					listener.onClick();
				}
				
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		btnCancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_CANCEL);
				
				// Cancel 只需要关闭窗口即可，不需要做其他的操作
			}
		});
		
		// 为dialog设置View
		builder.setView(dialogView);
		// 创建对话框
		mAlertDialog = builder.create();
		// 显示对话框
		mAlertDialog.show();
	}
	
	/**
	 * 保存游戏数据
	 * 
	 * @param context
	 * @param stageIndex 当前关
	 * @param coins 当前金币数
	 */
	public static void saveGameData(Context context, int stageIndex, int coins) {
		FileOutputStream fos = null;
		try {
			// 打开文件
			fos = context.openFileOutput(Const.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			
			// 写入文件
			dos.writeInt(stageIndex);
			dos.writeInt(coins);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 读取游戏数据
	 */
	public static int[] loadGameData(Context context) {
		FileInputStream fis  = null;
		int[] gameData = {-1, Const.TOTAL_COINS};
		
		try {
			// 打开文件
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);
			
			// 读取文件
			gameData[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
			gameData[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return gameData;
	}
	
}