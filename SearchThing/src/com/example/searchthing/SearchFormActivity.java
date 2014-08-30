package com.example.searchthing;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SearchFormActivity extends Activity implements OnClickListener{
	private EditText editText;
	private Button searchFormBtn;
	private String text;
	private EntryDB Edb;
	private String searchfilename;
	private String path;
	private String x,y;
	private String findThing;
	
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_form);
		
		editText = (EditText) findViewById(R.id.searchform);
		
		searchFormBtn = (Button) findViewById(R.id.searchformbtn);
		searchFormBtn.setOnClickListener(this);
		
		//SQLiteの準備
		MyDBHelper helper = new MyDBHelper(this, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		Edb = new EntryDB(db);
		
		List<MyDBEntity> entityList = Edb.findAll();
		//DBすべて表示
		for(MyDBEntity entity: entityList){
			Log.e("DB", "DB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
					+ ":" + entity.getY() + ":" + entity.getThing());
		}//end for
		
		
		
	}//end onCreate

	@Override
	public void onClick(View v) {
		if(v == searchFormBtn){
			text = editText.getText().toString();
			Log.e("formtext", text);
			List<MyDBEntity> entity1 = Edb.findByTthing(text);
			
			for(MyDBEntity entity: entity1){
				Log.e("SelectDB", "SelectDB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
						+ ":" + entity.getY() + ":" + entity.getThing());
				x = entity.getX();
				y = entity.getY();
				findThing = entity.getThing();
				//画像のファイル名を格納
				searchfilename = entity.getFilename();
				Log.e("Complete", "Complete:" + x + ":" + y + ":" + searchfilename);
				Intent result = new Intent(SearchFormActivity.this, SearchResultActivity.class);
				result.putExtra("filename", searchfilename);
				result.putExtra("x", x);
				result.putExtra("y", y);
				result.putExtra("thing", findThing);
				startActivity(result);
				
			}//end for
		}//end if
	}//end onClick
	
	
}
