package com.example.searchthing;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Entity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EntryDB {
	//テーブルの定数
	private static final String TABLE_NAME = "entry";
	private static final String COLUMN_ID = "rowid";
	private static final String COLUMN_DATA = "filename";
	private static final String COLUMN_DATA1 = "x";
	private static final String COLUMN_DATA2 = "y";
	private static final String COLUMN_DATA3 = "thing";
	private static final String[] COLUMNS = {COLUMN_ID, COLUMN_DATA, COLUMN_DATA1, COLUMN_DATA2, COLUMN_DATA3};
	
	//SQLiteDataBase
	private  SQLiteDatabase db;
	
	//コンストラクタ
	public EntryDB(SQLiteDatabase db){
		this.db = db;
	}
	
	//全データの取得
	public List<MyDBEntity> findAll(){
		List<MyDBEntity> entityList = new ArrayList<MyDBEntity>();
		Cursor cursor = db.query(TABLE_NAME, COLUMNS, null, null, null, null,COLUMN_ID);
		
		while (cursor.moveToNext()) {
			MyDBEntity entity = new MyDBEntity();
			entity.setRowId(cursor.getInt(0));
			entity.setFilename(cursor.getString(1));
			entity.setX(cursor.getString(2));
			entity.setY(cursor.getString(3));
			entity.setThing(cursor.getString(4));
//			Log.e("中身", cursor.getString(0));
//			Log.e("中身", cursor.getString(1));
//			Log.e("中身", cursor.getString(2));
//			Log.e("中身", cursor.getString(3));
//			Log.e("中身", cursor.getString(4));
			entityList.add(entity);
		}
		
		return entityList;
	}
	
	//特定IDのデータを取得
	public MyDBEntity findById(int rowId) {
		String selection = COLUMN_ID + "=" + rowId;
		Cursor cursor = db.query(TABLE_NAME, COLUMNS, selection, null, null, null, null);
		cursor.moveToNext();
		MyDBEntity entity = new MyDBEntity();
		entity.setRowId(cursor.getInt(0));
		entity.setFilename(cursor.getString(1));
		entity.setX(cursor.getString(2));
		entity.setY(cursor.getString(3));
		entity.setThing(cursor.getString(4));
//		Log.e("中身", cursor.getString(0));
//		Log.e("中身", cursor.getString(1));
//		Log.e("中身", cursor.getString(2));
//		Log.e("中身", cursor.getString(3));
//		Log.e("中身", cursor.getString(4));
		return entity;
	}
	
//	//特定のthingデータを取得
	public List<MyDBEntity> findByTthing(String thing) {
		List<MyDBEntity> entityListThing = new ArrayList<MyDBEntity>();
		String selection = COLUMN_DATA3 + " like ?";
		String[] selectionArgs = {"%"+thing+"%"};
		Cursor cursor = db.query(TABLE_NAME, COLUMNS, selection, selectionArgs, null, null, null);
		//cursor.moveToNext();
		while (cursor.moveToNext()) {
			MyDBEntity entity = new MyDBEntity();
			entity.setRowId(cursor.getInt(0));
			entity.setFilename(cursor.getString(1));
			entity.setX(cursor.getString(2));
			entity.setY(cursor.getString(3));
			entity.setThing(cursor.getString(4));
	//		int rowcount = cursor.getCount();
	//		Log.e("count", "count:" + rowcount);
			entityListThing.add(entity);
		}
		return entityListThing;
	}
	
	//データの登録
	public long insert(String filename, String x, String y, String things) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_DATA, filename);
		values.put(COLUMN_DATA1, x);
		values.put(COLUMN_DATA2, y);
		values.put(COLUMN_DATA3, things);
		return db.insert(TABLE_NAME, null, values);
	}
	
	//データの更新
	public int update(MyDBEntity entity){
		ContentValues values = new ContentValues();
		values.put(COLUMN_DATA, entity.getValue());
		String whereClause = COLUMN_ID + "=" + entity.getRowId();
		return db.update(TABLE_NAME, values, whereClause, null);
	}
	
	//データの削除
	public int delete(int rowId){
		String whereClause = COLUMN_ID + "=" + rowId;
		return db.delete(TABLE_NAME, whereClause, null);
	}
	
}
