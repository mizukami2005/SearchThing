package com.example.searchthing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import com.example.searchthing.R.drawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{

	private Button search_button,entry_button;	//探すボタン,登録ボタン
	private String filename;					//写真撮影時ファイル名
	private Uri mImageUri;						//撮影画像のURI
	static final int CONTEXT_MENU1_ID = 0;
	static final int CONTEXT_MENU2_ID = 1;
	private LinearLayout layout;
	private LinearLayout.LayoutParams layoutParams;
	private ImageView view;
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private int getX,getY;
	private int a = 130,b = 10,c,d;
	GestureDetector gestureDetector;
	private String path;
	Bitmap before_photo_bmp,after_photo_bmp,mutableBitmap;
	Matrix matrix;
	
	//private final static int WC = RelativeLayout.LayoutParams.WRAP_CONTENT;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		MyView view = new MyView(getApplication());
//		setContentView(view);
		
		setContentView(R.layout.activity_main);
		
		//setContentView(new DrawTest(this));
		//ボタン作成,リスナー追加
		search_button = (Button) findViewById(R.id.search);
		search_button.setOnClickListener(search);
		//ボタン作成,リスナー追加
		entry_button = (Button) findViewById(R.id.entry);
		entry_button.setOnClickListener(entry);
		
		gestureDetector = new GestureDetector(this,this);
		
		layout = (LinearLayout) findViewById(R.id.LinearLayout1);
		view = new ImageView(getApplicationContext());
		view.setImageResource(R.drawable.ic_launcher);
		
		layoutParams = new LinearLayout.LayoutParams(WC,WC);
		
		
		//view.setLayoutParams(new LayoutParams(WC,WC));
//		layoutParams.setMargins(getX, getY, c, d);
//		view.setLayoutParams(layoutParams);
		//layout.addView(view);
		
//		RelativeLayout relativeLayout = new RelativeLayout(this);
//		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(WC,WC);
//		params1.setMargins(150, 150, 0, 0);
		
		
		
		//registerForContextMenu(layout);
//		Resources r = getResources();
//		Bitmap defo = BitmapFactory.decodeResource(r, R.drawable.ic_launcher);
//		
//		((EntryImage) findViewById(R.id.EntryView)).setImage(defo);
	}//end onCreate 
	
//	static public class MyView extends View{
//		public MyView(Context context){
//			super(context);
//		}
//	}
//	
//	protected void onDraw(Canvas canvas){
//		Paint paint = new Paint();
//		paint.setColor(Color.argb(255, 255, 255, 255));
//		
//		paint.setAntiAlias(false);
//		canvas.drawCircle(80.5f, 80.5f, 20.0f, paint);
//	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		
		//コンテキストの設定
		menu.setHeaderTitle("この場所に登録しますか?");
		menu.add(0, CONTEXT_MENU1_ID, 0, "登録");
		menu.add(0, CONTEXT_MENU2_ID, 0, "キャンセル");
		
	}
	
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case CONTEXT_MENU1_ID:
			Log.d("aaaa","aaaaa");
			return true;
		case CONTEXT_MENU2_ID:
			Log.d("bbbb","bbbbb");

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}//end onCreateOptionsMenu
	
	//登録ボタン処理
	private View.OnClickListener entry = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//ファイル名を現在の時間をもとに付与
			filename = System.currentTimeMillis() + ".jpg";
		    ContentValues values = new ContentValues();
		    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/cmr");
		    //撮影画像を保存するパス
		    path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/cmr/" + filename;
		    Log.e("file", "file:" + file );
		    //フォルダが存在しなかったら場合フォルダ作成
		    if (!file.exists()) {
		    	Log.e("作成", "作成");
				file.mkdir();
			}
		    values.put(MediaStore.Images.Media.TITLE, filename);
		    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		    values.put("_data", path);
		    mImageUri = getContentResolver().insert(
		            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		    Log.e("保存場所", "保存場所:" + mImageUri);
		    Intent intent = new Intent();
		    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		    Log.e("onClick", mImageUri.toString());
		    startActivityForResult(intent, 2);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == 2) {
			Log.e("phtoShoto","OK");
			Log.e("data", "data:" + data);
			try{
				photoSetImage(mImageUri,path);
				InputStream in = getContentResolver().openInputStream(mImageUri);
				Bitmap img = BitmapFactory.decodeStream(in);
				in.close();
				//画像表示
				((EntryImage) findViewById(R.id.EntryView)).setFilename(filename);
				((EntryImage) findViewById(R.id.EntryView)).setImage(photoSetImage(mImageUri,path));
				//((EntryImage) findViewById(R.id.EntryView)).setPoint(x, y);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			//Intent photo_data = new Intent(MainActivity.this, EntryForm.class);
			//photo_data.putExtra("data", mImageUri);
			//photo_data.putExtra("filename", filename);
			//startActivity(photo_data);
		}
	}
	//検索ボタン処理
	private View.OnClickListener search = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.e("Search", "Search");
			//((EntryImage) findViewById(R.id.EntryView)).saveBitmap(path);
			Intent searchActivity = new Intent(MainActivity.this, SearchFormActivity.class);
			startActivity(searchActivity);
			
			
//			Intent intent_searchActivity = new Intent(MainActivity.this, SearchActivity.class);
//			startActivity(intent_searchActivity);
		}
	};
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event){
//		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
//		getX = (int) event.getX();
//		getY = (int) event.getY();
//		layoutParams.setMargins(getX, getY, c, d);
//		view.setLayoutParams(layoutParams);
//		Log.d("GetDownTime:", "DownTime" + String.valueOf(event.getDownTime()));
//		Log.e("GetDownTime","GetDownTime:" + TimeUnit.MILLISECONDS.toSeconds(event.getDownTime()));
//		return true;
//	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		gestureDetector.onTouchEvent(ev);
		Log.d("TouchEvent", "X:" + ev.getX() + ",Y:" + ev.getY());
		Log.d("ImageLocation", "X:" + view.getLeft() + ",Y:" + view.getTop());

		getX = (int) ev.getX();
		getY = (int) ev.getY();
		layoutParams.setMargins(getX-60, getY-150, c, d);
		view.setLayoutParams(layoutParams);
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("aaaaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaa");
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("bbb", "bbb");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("ccc", "ccc");
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("ddd", "ddd");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.e("eee", "eee");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("fff", "fff");
		Toast.makeText(this, "LongTap", Toast.LENGTH_LONG).show();
		Log.e("getX", "getX:" + getX);
		Log.e("getY", "getY:" + getY);
		layout.addView(view);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.e("ggg", "ggg");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("hhh", "hhh");
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("iii", "iii");
		return false;
	}
	
	//Uriをリサイズしてビットマップに変換
		public Bitmap getBitmapFromUri(Uri imageUri, int sampleSize) throws IOException{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			Bitmap resizeBitmap = null;
			ContentResolver conReslv = getContentResolver();
			InputStream iStream = conReslv.openInputStream(imageUri);
			resizeBitmap = BitmapFactory.decodeStream(iStream, null, opts);
			iStream.close();
			return resizeBitmap;
		}
		
		public Bitmap photoSetImage(Uri getDataUri, String path){
			try{
				before_photo_bmp = getBitmapFromUri(getDataUri, 4);
			}catch(IOException e){
				e.printStackTrace();
			}
				after_photo_bmp = Bitmap.createBitmap(before_photo_bmp,0,0,before_photo_bmp.getWidth(), before_photo_bmp.getHeight(), matrix, true);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
				options.inSampleSize = 2;
				mutableBitmap = BitmapFactory.decodeFile(path, options);
				return mutableBitmap;
		}
		
		public void showDialog(){
			Toast.makeText(this, "ダイアログ表示", Toast.LENGTH_LONG).show();
		}
}//end MainActivity
