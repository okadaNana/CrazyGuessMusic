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
 * ������
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
	 * ��ת��ָ����Activity�����رյ�ǰ��Activity
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
				// �رնԻ���
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				
				// �¼��ص�
				if (listener != null) {
					listener.onClick();
				}
				
				// ������Ч
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		btnCancelView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// �رնԻ���
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// ������Ч
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_CANCEL);
				
				// Cancel ֻ��Ҫ�رմ��ڼ��ɣ�����Ҫ�������Ĳ���
			}
		});
		
		// Ϊdialog����View
		builder.setView(dialogView);
		// �����Ի���
		mAlertDialog = builder.create();
		// ��ʾ�Ի���
		mAlertDialog.show();
	}
	
	/**
	 * ������Ϸ����
	 * 
	 * @param context
	 * @param stageIndex ��ǰ��
	 * @param coins ��ǰ�����
	 */
	public static void saveGameData(Context context, int stageIndex, int coins) {
		FileOutputStream fos = null;
		try {
			// ���ļ�
			fos = context.openFileOutput(Const.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fos);
			
			// д���ļ�
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
	 * ��ȡ��Ϸ����
	 */
	public static int[] loadGameData(Context context) {
		FileInputStream fis  = null;
		int[] gameData = {-1, Const.TOTAL_COINS};
		
		try {
			// ���ļ�
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);
			
			// ��ȡ�ļ�
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