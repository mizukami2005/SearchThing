package com.example.searchthing;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class EntryForm extends Activity implements OnClickListener{
	private Button searchEntryBtn;
	private EditText editText;
	private String text;
	private String getFileName;
	private String x,y;
	
	//DB関係
    private EntryDB Edb;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.entry_form);
		//画像ファイル名,座標の取得
		getData();
		
		editText = (EditText) findViewById(R.id.edittext);
		//エディットのテキスト設定
		searchEntryBtn = (Button) findViewById(R.id.searchentry);
		searchEntryBtn.setOnClickListener(this);		
		
		//SQLiteの準備
		MyDBHelper helper = new MyDBHelper(this, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		Edb = new EntryDB(db);
	}
	
	public void onClick(View v){
		if(v == searchEntryBtn){
			text = editText.getText().toString();
			Log.d("text", text);
			Edb.insert(getFileName, x, y, text);
			List<MyDBEntity> entityList = Edb.findAll();
			for(MyDBEntity entity: entityList){
				Log.e("DB", "DB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
						+ ":" + entity.getY() + ":" + entity.getThing());
			}
			finish();
//			Intent mainBack = new Intent(this, MainActivity.class);
//			startActivity(mainBack);
			//Intent intent_searchActivity = new Intent(MainActivity.this, SearchActivity.class);
//			startActivity(intent_searchActivity);
		}
	}//end onClick
	
	//データ取得
	private void getData(){
		Intent get_data = getIntent();
		getFileName = get_data.getStringExtra("filename");
		x = get_data.getStringExtra("x");
		y = get_data.getStringExtra("y");
		Log.e("filename", getFileName);
		Log.e("x", x);
		Log.e("y", y);
	}

}
