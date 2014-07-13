package com.example.searchthing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "entrydb";
	
	//テーブル作成用SQL
//	private static final String CREATE_TABLE_SQL = "" +
//			"create table entry (" +
//				"_id integer primary key autoincrement, " +
//				"filename text not null, " +
//				"x text not null, " +
//				"y text not null, " +
//				"thing text not null " + 
//			")";
	private String sql;
	
	//テーブルの削除用sql
	private static final String DROP_TABLE_SQL = "drop table entry";
	
	//コンストラクタ
	public MyDBHelper(
			Context context,
			CursorFactory factory,
			int version){
		
		super(context, DB_NAME, factory, version);
	}
	
	//テーブルの作成
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("tableCreate", "テーブル作成");
		sql = "";
		sql += "create table entry (";
		sql += " rowid integer primary key autoincrement";
		sql += ",filename text not null";
		sql += ",x text";
		sql += ",y text";
		sql += ",thing text";
		sql += ")";
		//db.execSQL(DROP_TABLE_SQL);
		db.execSQL(sql);
		Log.e("tableCreate", "テーブル作成終わり");
	}

	//テーブルの再作成
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(sql);
	}
}
