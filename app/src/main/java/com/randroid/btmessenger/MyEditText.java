package com.randroid.btmessenger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class MyEditText extends EditText {

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_ENTER) {
			clearFocus();
		}
		return super.onKeyPreIme(keyCode, event);
	}

	 @Override
	 public boolean onKeyUp(int keyCode, KeyEvent event) {
	 // TODO Auto-generated method stub
         if(keyCode == KeyEvent.KEYCODE_ENTER)
         {
         this.clearFocus();

         }
         return super.onKeyUp(keyCode, event);
	 }

}