package com.example.searchthing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Style;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EntryActivity extends Activity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
	
	private ImageView photo_img;	//撮影画像用
	private Button entry_button;	//登録ボタン用
	private Uri getDataUri;			//撮影画像Uri
	private String getFileName;		//撮影画像名前
	private String image_orient;	//カメラの回転
	private Matrix matrix;			
	private Bitmap before_photo_bmp, after_photo_bmp, mutableBitmap,search_icon_bmp;
	private GestureDetector gesDetect;
	private FrameLayout layout;
	private FrameLayout.LayoutParams[] layoutParams;
	private FrameLayout.LayoutParams layoutParams1;
	private ImageView[] view;								//サーチアイコン用
	private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private int[] getX,getY;								//画面のX座標 Y座標
	private Canvas canvas;		
	private Paint paint;
    private String path;									//画像のパスを格納
    private AlertDialog.Builder alertDialogBuilder;			//登録確認画面用
    private AlertDialog alertDialog;
    private AlertDialog.Builder custom_alertDialogBuilder;	//物登録画面用
    private AlertDialog custom_alertDialog;
    private AlertDialog.Builder entryalertDialogBuilder;	//登録確認用
    private AlertDialog entry_alertDialog;
    boolean touchFlog = true;								//サーチアイコンを動かすか動かさないかの判定
    private int entrycnt = 0;								//登録数を格納
    private EditText id;
    private static int dbsaveX, dbsaveY;
    
    //可変関係
    private static final String TAG = "DynamicView";

    private static final int INIT_CHILD_COUNT = 3;
    private static final String KEY_INPUT_DATA = "input.data";
    private static final String KEY_FIELD_COUNT = "fld.count";
    private static final String KEY_SELECT_POS = "select.pos";
    private static final String TYPE_MAIL = "探しもの";
    private static final String[] ITEM_TYPES = { TYPE_MAIL };


    class EditItem {
    String type;
    LinearLayout layout;
    List<View> fields;

    EditItem() {
    fields = new ArrayList<View>();
    }
    }

    // すべての項目と項目追加ボタンの親ビュー
    private LinearLayout mContainerView;
    // 追加項目選択ダイアログ
    private AlertDialog mItemSelectDialog;

    // 追加項目のマップ
    private Map<String, EditItem> fItems = new HashMap<String, EditItem>();

    //カスタム
    //private EditText edit,id;
    private LinearLayout dlgContainerView,addrowView;
    private String get_edit_text = null;	//探し物を格納
    private ArrayList<String> entry_things;	//探し物を複数格納するため
    private TextView edit_text;
    LinearLayout add_rowLayout = null;
    TextView editText;
    private ArrayList<String> deleate_entry;//削除用の
    
    //ダイアログ関係
    static final int DIALOG_CHECK_ENTRY = 0;
    static final int DIALOG_ENTRY = 1;
    static final int DIALOG_CHECK_END_ENTRY = 2;
    View layout_dlg;
    private ArrayList<View> startViews;	//探し物を複数格納するため

    //DB関係
    private EntryDB Edb;
    
    private int setImagecnt = 0;
	
    //px関係
    private DisplayMetrics metrics;
    static float scaledDensity;
    static int widthPixels;
    static int heightPixels;
    private final int twW = 70;
    private final int twH = 85;
    
    private float imageX;
    private float imageY;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.entry);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		float width = disp.getWidth();
		float height = disp.getHeight();
		
		//SQLiteの準備
		MyDBHelper helper = new MyDBHelper(this, null, 1);
		SQLiteDatabase db = helper.getWritableDatabase();
		Edb = new EntryDB(db);
		
		
		//可変関係
		entry_things = new ArrayList<String>();
		deleate_entry = new ArrayList<String>();
		startViews = new ArrayList<View>();
		entryalertDialogBuilder = new AlertDialog.Builder(this);
		//一度の画像に5個まで格納可能
		getX = new int[4];
		getY = new int[4];
		view = new ImageView[4];
		layoutParams = new FrameLayout.LayoutParams[4];
		
		photo_img = (ImageView) findViewById(R.id.photo_img);
		entry_button = (Button)findViewById(R.id.entry_button);
		entry_button.setOnClickListener(entry);
		
		layout = (FrameLayout) findViewById(R.id.addFramelayout);
		//layout.addView(new EntryActivityView(this));
		gesDetect = new GestureDetector(photo_img.getContext(),this);
		
//		alertDialogBuilder = new AlertDialog.Builder(this);
//		//アラートダイアログのタイトル設定
//		alertDialogBuilder.setTitle("登録確認");
//		//アラートダイアログのメッセージを設定
//		alertDialogBuilder.setMessage("この場所に登録しますか?");
//		
//		//アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//		alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Log.e("Yes", "Yes");
//				custom_alertDialog.show();
//			}
//		});
//		//アラートダイアログの否定ボタンがクリックされた時に呼ばれるコールバックリスナーを登録
//		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Log.e("No", "No");
//				entrycnt--;
//				layout.removeView(view[entrycnt]);
//				
//			}
//		});
//		//アラートダイアログのキャンセル可能かどうか設定
//		alertDialogBuilder.setCancelable(false);
//		alertDialog = alertDialogBuilder.create();
		
		
		
		//カスタムビューを設定
		LayoutInflater inflater =(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout_dlg = inflater.inflate(R.layout.custom_dialog, (ViewGroup)findViewById(R.id.layout_root));
		add_rowLayout = (LinearLayout)inflater.inflate(R.layout.row, null);
		edit_text = (TextView)add_rowLayout.findViewById(R.id.text_mail);
		dlgContainerView = (LinearLayout) layout_dlg.findViewById(R.id.layout_root);
		
		
		
//		custom_alertDialogBuilder = new AlertDialog.Builder(this);
//		custom_alertDialogBuilder.setTitle("登録");
//		custom_alertDialogBuilder.setView(layout_dlg);
		id = (EditText)layout_dlg.findViewById(R.id.customDlg_id);
		
		id.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//EnterKeyが押されたかどうかを判定
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					//キードーボを閉じる
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
					get_edit_text = id.getText().toString();
					entry_things.add(get_edit_text);
					inflateEditItem(TYPE_MAIL);
					// 挿入項目に最初の１行追加
					inflateEditRow(TYPE_MAIL, "", 0);
					final int checkedItem = -1;
					id.getEditableText().clear();
					CharSequence _char = "続けて追加できます";
					id.setHint(_char);
					Log.d("------------", get_edit_text);
					return true;
				}
				return false;
			}
		});
		
//		custom_alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//OKボタンの処理
//				//IDを取得
////				EditText id = (EditText)layout_dlg.findViewById(R.id.customDlg_id);
//				String strId = id.getText().toString();
//				touchFlog = false;
//				//登録削除判定(削除されたものがあれば削除する)
//				if(deleate_entry.size() != 0){
//					Log.e("削除中","削除");
//					for(int j = 0; j < deleate_entry.size(); j++){
//						for (int i = 0; i < entry_things.size(); i++) {
//							if (entry_things.get(i).equals(deleate_entry.get(j))) {
//								entry_things.remove(entry_things.indexOf(deleate_entry.get(j)));
//							}//end if
//						}//end for
//					}//end for
//				}//end if
//				
//				Log.e("XXXXXXXXXX1","XXXXXXXXX1:" + getX[0]);
//				Log.e("YYYYYYYYYY1","YYYYYYYYY1:" + getY[0]);
//				Log.e("XXXXXXXXXX2","XXXXXXXXX2:" + getX[1]);
//				Log.e("YYYYYYYYYY2","YYYYYYYYY2:" + getY[1]);
//			}//end onClick
//		});//end DialogInterface.OnClickListener
//		
//		custom_alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				//キャンセルボタンの処理
//				entrycnt--;
//				layout.removeView(view[entrycnt]);
//			}
//		});
//		custom_alertDialog = custom_alertDialogBuilder.create();
		//custom_alertDialogBuilder.create();
		view[0] = new ImageView(getApplicationContext());
		view[0].setImageResource(R.drawable.ic_launcher);
		view[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//canvas.drawBitmap(search_icon_bmp, 100, 100, null);
				canvas.drawBitmap(search_icon_bmp, (int)(getX[0]*scaledDensity), (int)(getY[0]*scaledDensity), null);
				String entry_dlg_message = "";
				for(int j = 0; j < entry_things.size(); j++){
					Log.e("Hello", "登録したもの:" + entry_things.get(j));
					 entry_dlg_message += entry_things.get(j) + "\n";
				}//end for
				//アラートダイアログのタイトル設定
				entryalertDialogBuilder.setTitle("この場所にあるもの");
				//アラートダイアログのメッセージを設定
				entryalertDialogBuilder.setMessage(entry_dlg_message);
				
				entryalertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.e("Yes", "Yes");
						List<MyDBEntity> entityList = Edb.findAll();
						
						for (MyDBEntity entity: entityList) {
							Log.e("DB", "DB:" + entity.getRowId() + ":" + entity.getFilename() + ":" + entity.getX()
									+ ":" + entity.getY() + ":" + entity.getThing());
						}
					}
				});
				
				entry_alertDialog = entryalertDialogBuilder.create();
				entry_alertDialog.show();
				//Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
			}
		});
		
		view[1] = new ImageView(getApplicationContext());
		view[1].setImageResource(R.drawable.searchimg);
		layoutParams[0] = new FrameLayout.LayoutParams(WC,WC);
		layoutParams[1] = new FrameLayout.LayoutParams(WC,WC);
		//サーチアイコンを予めBitmapに変換
		search_icon_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		//インテントデータ取得
		getData();
		//画像の向き取得,補正
		checkPhotoRotate();
		//画像リサイズ,表示
		photoSetImage();
		
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("test", "density=" + metrics.density);
		Log.d("test", "densityDpi=" + metrics.densityDpi);
		Log.d("test", "scaledDensity=" +metrics.scaledDensity);
		Log.d("test", "widthPixels=" + metrics.widthPixels);
		Log.d("tesy", "heightPixels=" + metrics.heightPixels);
		Log.d("test", "xDpi=" + metrics.xdpi);
		Log.d("test", "yDpi=" + metrics.ydpi);
		scaledDensity = metrics.scaledDensity;
		widthPixels = metrics.widthPixels;
		heightPixels = metrics.heightPixels;
	}//end onCreate 
	
	public class EntryActivityView extends View{
		Paint p = new Paint();
		public EntryActivityView(Context context){
			super(context);
			Resources res = this.getContext().getResources();
			search_icon_bmp = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);

			//search_icon_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event){
			//gesDetect.onTouchEvent(event);
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				getX[entrycnt] = (int) event.getX();
				getY[entrycnt] = (int) event.getY();
				EntryActivityView.this.invalidate();

			}
			Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
			
			//EntryActivity a = new EntryActivity(); 
//			if(entrycnt == 0){
//				layoutParams[entrycnt].setMargins(getX[entrycnt]-60, getY[entrycnt]-150, 0, 0);
//			}else if(entrycnt == 1){
//				layoutParams[entrycnt].setMargins(getX[entrycnt]-60, getY[entrycnt]-150, 0, 0);
//			}
//			Log.d("TouchEvent", "afterX:" + getX[entrycnt] + ",afterY:" + getY[entrycnt]);
//			if(touchFlog == true){
//				if(entrycnt == 0){
//					Log.e("1111111111111111111111111111", "11111111111111111111");
//					view[entrycnt].setLayoutParams(layoutParams[entrycnt]);
//				}else{
//					view[entrycnt].setLayoutParams(layoutParams[entrycnt]);
//					Log.e("2222222222222222222222222", "222222222222222222222222222");
//
//				}
//			}
//			Log.e("GetDownTime:", "DownTime" + String.valueOf(event.getDownTime()));
			return true;
		}
		
//		@Override
//		protected void onDraw(Canvas c){
//			super.onDraw(c);
//			canvas = c;
//			setImagecnt++;
//			if(setImagecnt <=2){
//				try{
//					before_photo_bmp = getBitmapFromUri(getDataUri, 4);
//					Log.e("before_photo_bmp", "before_photo_bmp" + before_photo_bmp);
//				} catch(IOException e){
//					e.printStackTrace();
//				}
//				
//			}
//			
//			after_photo_bmp = Bitmap.createBitmap(before_photo_bmp, 0, 0, before_photo_bmp.getWidth(), before_photo_bmp.getHeight(), matrix, true);
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inMutable = true;
//			options.inSampleSize = 2;
//			mutableBitmap = BitmapFactory.decodeFile(path, options);
//			canvas = new Canvas(mutableBitmap);
//			paint = new Paint();
//			photo_img.setImageBitmap(mutableBitmap);
//			
//			Log.d("onDraw", "onDraw");
//			//Paint paint = new Paint();
//			//c = canvas;
			//canvas.drawBitmap(search_icon_bmp, getX[entrycnt], getY[entrycnt], p);
			
		//}
		
	}
	
	

	//登録ボタン処理
	private View.OnClickListener entry = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.e("Entry","Entry");
			Log.e("TouchEvent", "X:" + getX[0] + ",Y:" + getY[0]);
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inMutable = true;
//			options.inSampleSize = 2;
//			mutableBitmap = BitmapFactory.decodeFile(path, options);
//			canvas = new Canvas(mutableBitmap);
			for(int i = 0; i < entrycnt; i++){
				//ここのx,yが保存時の座標
				Log.e("Canvas座標", "座標" +  getX[i] +  ";" + getY[i]);
				//Log.e("entry座標","座標" +  getX[i] +  ";" + getY[i]);
				//dp = (int)(getX[i]f / density + 0.5f);
				//search_icon_bmp.setDensity(DisplayMetrics.DENSITY_HIGH);
				canvas.drawBitmap(search_icon_bmp, getX[i]*scaledDensity, getY[i]*scaledDensity, null);
				Log.e("tesy","test1" + getX[i]*scaledDensity);
				Log.e("tesy","test2" + getY[i]*scaledDensity);
			}
			//画像保存
			photoSave();  
			//タップしたところにドロイド君表示
			//layout.addView(view);
		}
		
	};//end entry 
	
	//MainActivityからデータ受け取り
	private void getData(){
		Intent get_data = getIntent();
		Bundle photo_bundle = get_data.getExtras();
		getDataUri = (Uri) photo_bundle.get("data");
		getFileName = get_data.getStringExtra("filename");
	    path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/cmr/" + getFileName;
		Log.e("getfileName", path);
	}//end getData
	
	//画像の向きチェック,補正
	private void checkPhotoRotate(){
		if(getFileName != null){
			String filePath = Environment.getExternalStorageDirectory().getPath() + getFileName;
			try{
				ExifInterface ei = new ExifInterface(getPath(this, getDataUri));
				image_orient = getExifString(ei, ExifInterface.TAG_ORIENTATION);
				matrix = new Matrix();
				int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
				switch (orientation) {
					case ExifInterface.ORIENTATION_UNDEFINED:
						break;
					case ExifInterface.ORIENTATION_NORMAL:
						break;
					case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
						matrix.postScale(-1f, 1f);
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						matrix.postRotate(180f);
						break;
					case ExifInterface.ORIENTATION_FLIP_VERTICAL:
						matrix.postScale(1f, -1f);
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						Log.e("ho------","ho---------------");
						matrix.postRotate(90f);
						break;
					case ExifInterface.ORIENTATION_TRANSVERSE:
						matrix.postRotate(-90f);
						matrix.postScale(1f, -1f);
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						matrix.postRotate(-90f);
						break;
				}
			} catch(IOException e1){
				e1.printStackTrace();
			}
		}
	}//end checkPhotoRotate
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		gesDetect.onTouchEvent(event);
		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
		getX[entrycnt] = (int) event.getX();
		getY[entrycnt] = (int) event.getY();
		imageX = event.getX();
		imageY = event.getY();
		if(entrycnt == 0){
			layoutParams[entrycnt].setMargins(getX[entrycnt]-60, getY[entrycnt]-150, 0, 0);
		}else if(entrycnt == 1){
			layoutParams[entrycnt].setMargins(getX[entrycnt]-60, getY[entrycnt]-150, 0, 0);
		}
		Log.d("TouchEvent", "afterX:" + getX[entrycnt] + ",afterY:" + getY[entrycnt]);
		if(touchFlog == true){
			if(entrycnt == 0){
				Log.e("1111111111111111111111111111", "11111111111111111111");
				view[entrycnt].setLayoutParams(layoutParams[entrycnt]);
			}else{
				view[entrycnt].setLayoutParams(layoutParams[entrycnt]);
				Log.e("2222222222222222222222222", "222222222222222222222222222");

			}
		}
		Log.e("GetDownTime:", "DownTime" + String.valueOf(event.getDownTime()));
		return true;
	}
	
	private String getExifString(ExifInterface ei, String tag) {
		//ファイルが存在しない場合にgetAttributeを呼び出したらNULlが返る
		//ファイルにEXIFが存在しない場合も同様にNULLが返る
		return tag + ": " + ei.getAttribute(tag);
	}//end getExifString
	
	//画像リサイズ,表示
	public void photoSetImage() {
		try{
			before_photo_bmp = getBitmapFromUri(getDataUri, 4);
			Log.e("before_photo_bmp", "before_photo_bmp" + before_photo_bmp);
		} catch(IOException e){
			e.printStackTrace();
		}
		after_photo_bmp = Bitmap.createBitmap(before_photo_bmp, 0, 0, before_photo_bmp.getWidth(), before_photo_bmp.getHeight(), matrix, true);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		options.inSampleSize = 2;
		mutableBitmap = BitmapFactory.decodeFile(path, options);
		canvas = new Canvas(mutableBitmap);
		
//		canvas.drawColor(Color.argb(0, 0, 0, 0));
//		
		paint = new Paint();
//		paint.setColor(Color.BLUE);
//		paint.setStyle(Style.FILL);
//		paint.setAntiAlias(true);
//		canvas.drawCircle(0, 0, 30, paint);
//		photo_img.setImageBitmap(mutableBitmap);
		
		photo_img.setImageBitmap(mutableBitmap);
		int top = canvas.getHeight();
		int rigth = canvas.getWidth();
		int left = canvas.getDensity();
		//int bottom = photo_img.getBottom();
		Log.e("ImagePropaty", "top:" + top + "right:" + rigth + "left:" + left);
		double xxx = photo_img.getHeight();
		double yyy = photo_img.getWidth();

	}//end photoSetImage
	//画像を保存
	private void photoSave(){
		try{
			FileOutputStream out = new FileOutputStream(path);
			mutableBitmap.compress(CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			Toast.makeText(this, "保存されました", Toast.LENGTH_LONG).show();
			Intent main_intent = new Intent(EntryActivity.this, MainActivity.class);
			startActivity(main_intent);
		} catch(Exception e){
			Toast.makeText(this, "保存失敗しました", Toast.LENGTH_LONG).show();
		}
	}
	
	public static String getPath(Context context, Uri uri){
		ContentResolver contentResolver = context.getContentResolver();
        String[] columns = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
	}
	
	//Uriをリサイズしてビットマップに変換
		public Bitmap getBitmapFromUri(Uri imageUri, int sampleSize) throws IOException {
		    Log.e("ImageUri","ImageUri" + imageUri);
		    BitmapFactory.Options opts = new BitmapFactory.Options();
		    opts.inSampleSize = sampleSize;
		    Bitmap resizeBitmap = null;
		    ContentResolver conReslv = getContentResolver();
		    InputStream iStream = conReslv.openInputStream(imageUri);
		    resizeBitmap = BitmapFactory.decodeStream(iStream, null, opts);
		    iStream.close();
		    return resizeBitmap;
		}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("11111", "111111");
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("222", "222");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("3333", "3333");
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("4444", "4444");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.e("5555", "5555");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//touchFlog = true;
		//layout.removeView(view);
		Toast.makeText(this, "LongTap", Toast.LENGTH_LONG).show();
		if(entrycnt == 0){
			layout.addView(view[entrycnt]);
			canvas.drawBitmap(search_icon_bmp, imageX - search_icon_bmp.getWidth() /2, imageY - search_icon_bmp.getHeight() /2, null);
			//canvas.drawBitmap(search_icon_bmp, 10, 0, null);
			//canvas.drawBitmap(search_icon_bmp, 700, 116, null);
			//canvas.drawBitmap(search_icon_bmp, getX[0], getY[0], null);
			//canvas.drawBitmap(search_icon_bmp, getX[0], getY[0], null);
			Log.e("-------1----------", "-------1---------");
		}else{
			layout.addView(view[entrycnt]);
			Log.e("-------2----------", "-------2---------");

		}
		entrycnt++;

		//alertDialog.show();
		showDialog(DIALOG_CHECK_ENTRY);
//		canvas = new Canvas(mutableBitmap);
//		canvas.drawColor(Color.argb(0, 0, 0, 0));
//		
//		paint = new Paint();
//		paint.setColor(Color.BLUE);
//		paint.setStyle(Style.FILL);
//		paint.setAntiAlias(true);
//		
//		canvas.drawCircle(getX+50, getY+50, 30, paint);
//		photo_img.setImageBitmap(mutableBitmap);
		Log.e("6666", "6666");
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		Log.e("7777", "7777");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("8888", "8888");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("99999", "99999");
		return false;
	}
	
	/**
	* 「新規項目追加」ボタンの onClickハンドラ.
	*/
	public void onAddNewItemClicked(View v) {
		inflateEditItem(TYPE_MAIL);
		// 挿入項目に最初の１行追加
		inflateEditRow(TYPE_MAIL, "", 0);
	final int checkedItem = -1;
	mItemSelectDialog = new AlertDialog.Builder(EntryActivity.this)
	.setTitle("追加する項目を選択してください")
	.setSingleChoiceItems(ITEM_TYPES, checkedItem,
	new DialogInterface.OnClickListener() {

	@Override
	public void onClick(DialogInterface dialog,
	int which) {
	String type = ITEM_TYPES[which];
	mItemSelectDialog.dismiss();
	// 　選択されたタイプの項目を親コンテナに挿入
	inflateEditItem(type);
	// 挿入項目に最初の１行追加
	inflateEditRow(type, "", 0);
	}
	}).create();
	//mItemSelectDialog.show();
	}
	
	/**
	* 「新規追加」ボタンの onClick ハンドラ.
	*/
	public void onAddNewClicked(View v) {
	// 親ビューのテキストビューから項目タイプを取得
	View rowContainer = (View) v.getParent();
	TextView textv = (TextView) rowContainer.findViewById(R.id.textv_item);
	String itemType = textv.getText().toString();

	// 新規項目を取得して
	inflateEditRow(itemType, "", 0);
	}
	
	// 各行の "X" ボタンの onClick ハンドラ
	public void onDeleteClicked(View v) {
	// ボタンの親 : rowView を取得
	View rowView = (View) v.getParent();
	Log.d("v", "vとは何か:" + v);
	// その親 : 項目レイアウトを取得
	LinearLayout rowContainer = (LinearLayout) rowView.getParent();
	String type = ((TextView) rowContainer.findViewById(R.id.textv_item))
	.getText().toString();
	
	String deleate = ((TextView) rowContainer.findViewById(R.id.text_mail)).getText().toString();
	//Toast.makeText(this, deleate, Toast.LENGTH_LONG).show();
	deleate_entry.add(deleate); 
	//Log.e("削除したもの", ((TextView) rowContainer.findViewById(R.id.text_mail)).getText().toString());
	
	if (rowContainer.getChildCount() == INIT_CHILD_COUNT) {
	// 行が１つの場合トップコンテナから項目を削除する
		Log.d("削除1", "削除1");
		dlgContainerView.removeView(rowContainer);
	fItems.remove(type);
	} else {
	// 項目から行を削除する
	rowContainer.removeView(rowView);
	fItems.get(type).fields.remove(rowView);
	}
	}
	
	// 項目を取得するためのヘルパー
	private void inflateEditItem(String type) {
	EditItem editItem = new EditItem();

	// レイアウトXMLから項目ビューを取得
	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	final View itemView = inflater.inflate(R.layout.row_container, null);
	editItem.layout = (LinearLayout) itemView;
	editItem.type = type;
	fItems.put(type, editItem);

	// 追加項目のラベルに、項目タイプを設定
	final TextView textv = (TextView) itemView
	.findViewById(R.id.textv_item);
	textv.setText(type);

	edit_text = (TextView)add_rowLayout.findViewById(R.id.text_mail);
	edit_text.setText("aaaaaaa");

	// すべての行の最後で「新規項目追加」ボタンの前に入れる
	dlgContainerView.addView(itemView, dlgContainerView.getChildCount() - 1);
	}

	// 行を取得するためのヘルパー
	private void inflateEditRow(String itemType, String data, int select) {

	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	final View rowView = inflateRowView(itemType);
	editText = findEditText(itemType, rowView);
	startViews.add(rowView);
	//get_edit_text = editText.getText().toString();

	//if(get_edit_text.equals("")){
//		editText.setText("aaaaaaaaaaaaa");
	//}else{
//		editText.setText(get_edit_text);
	//}
	//Log.e("--------------",get_edit_text);
	if(get_edit_text == null){
		Log.e("-------","何もない");
	}
	editText.setText(get_edit_text);
	

	//get_edit_text = sb.toString();
	//Log.v("onCreate", sb.toString());

	//final Spinner spinner = findSpinner(itemType, rowView);
	LinearLayout itemLayout = fItems.get(itemType).layout;
	fItems.get(itemType).fields.add(rowView);

	if (data != null && !data.equals("")) {
	//editText.setText(data);
	}
	//if (select > 0) {
	//spinner.setSelection(select);
	//}
	//edit_text.setText(get_edit_text);

	// すべての行の最後で「新規追加」ボタンの前に入れる
	itemLayout.addView(rowView, itemLayout.getChildCount() - 1);
	}

	private View inflateRowView(String itemType) {
	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View rowView = null;
	//if (itemType.equals(TYPE_PHONE)) {
	//rowView = inflater.inflate(R.layout.phone_row, null);
	//} else 
	if (itemType.equals(TYPE_MAIL)) {
	rowView = inflater.inflate(R.layout.row, null);
	} else {
	//rowView = inflater.inflate(R.layout.address_row, null);
	}
	return rowView;
	}

	private TextView findEditText(String itemType, View rowView) {
	TextView editText = null;
	//if (itemType.equals(TYPE_PHONE)) {
	//editText = (EditText) rowView.findViewById(R.id.edit_phone);
	//} else 
	if (itemType.equals(TYPE_MAIL)) {
	editText = (TextView) rowView.findViewById(R.id.text_mail);
	} else {
	//editText = (TextView) rowView.findViewById(R.id.edit_address);
	}
	return editText;
	}

	//private Spinner findSpinner(String itemType, View rowView) {
	//Spinner spinner = null;
	//if (itemType.equals(TYPE_PHONE)) {
	//spinner = (Spinner) rowView.findViewById(R.id.spinner_phone);
	//} else if (itemType.equals(TYPE_MAIL)) {
	//spinner = (Spinner) rowView.findViewById(R.id.spinner_mail);
	//} else {
	//spinner = (Spinner) rowView.findViewById(R.id.spinner_address);
	//}
	//return spinner;
	//}
	
	//ダイアログのidを取得する
	private int getDialogId(){
		int id = 0;
		return 0;
	}
	
	@Override
	protected Dialog onCreateDialog(int id){
//		AlertDialog.Builder dialog;
		Dialog dialog = null;
		switch (id) {
		case DIALOG_CHECK_ENTRY:
			dialog = new AlertDialog.Builder(this)
			.setTitle("登録確認")
			.setMessage("この場所に登録しますか?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.e("Yes", "Yes");
					//custom_alertDialog.show();
					showDialog(DIALOG_ENTRY);
				}//onClick
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.e("No", "No");
					entrycnt--;
					layout.removeView(view[entrycnt]);
				}
			})
			.create();
			break;
		case DIALOG_ENTRY:
			dialog = new AlertDialog.Builder(this)
			.setTitle("登録")
			.setView(layout_dlg)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//OKボタンの処理
					touchFlog = false;
					//登録削除判定(削除されたものがあれば削除する)
					if(deleate_entry.size() != 0){
						Log.e("削除中","削除");
						for(int j = 0; j < deleate_entry.size(); j++){
							for (int i = 0; i < entry_things.size(); i++) {
								if (entry_things.get(i).equals(deleate_entry.get(j))) {
									entry_things.remove(entry_things.indexOf(deleate_entry.get(j)));
								}//end if
							}//end for
						}//end for
					}//end if
					//DBにデータの追加
					String db_entry_thing = ""; 
					if(entry_things.size() >= 2){
						Log.e("--------two---------", "------------two---------");
						for (int i = 0; i < entry_things.size(); i++) {
							//String db_entry_thing = null; 
							db_entry_thing += entry_things.get(i) +",";
						}//end for
						db_entry_thing = db_entry_thing.substring(0,db_entry_thing.length() - 1);
					}else if(entry_things.size() == 1){
						db_entry_thing = entry_things.get(0);
					}
					
					Edb.insert(getFileName, Integer.toString(getX[entrycnt]) , Integer.toString(getY[entrycnt]), db_entry_thing);
					//removeDialog(DIALOG_ENTRY);
				}//end onClick
			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					entrycnt--;
					layout.removeView(view[entrycnt]);
				}
			})
			.create();
			dialog.show();
			break;
		default:
			dialog = null;
			break;
		}//end swirch
		return dialog;
	}//end onCreateDialog
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog){
		Log.e("onPrepareDialog","onPrepareDialog");
		//ダイアログの表示前に必要な処理
		if(id == DIALOG_ENTRY){
			Log.e("----------a--------", "aaaaaaaaaaaaaa");
			//カスタムダイアログ初期化
			if(startViews.size() != 0){
				for (int i = 0; i < startViews.size(); i++) {
					LinearLayout rowContainer = (LinearLayout) startViews.get(i).getParent();
					dlgContainerView.removeView(rowContainer);
				}//end for
			}//end if
		}//end if
		super.onPrepareDialog(id, dialog);
	}//end onPrepareDialog
}
