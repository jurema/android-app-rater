package la.jurema.android.app.rater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RateAnalyzer extends SQLiteOpenHelper{

	private final static String DATABASE_NAME = "jRate";
	private final static int DATABASE_VERSION = 1;
	
	private final String TABLE_INTERVAL = "Interval";
	private final String TABLE_MANAGER = "Manager";
	
	private final String COLUNS_INTERVAL[] = {"days","counts"};
	private final String COLUNS_MANAGER[] = {"active"};
	
	public final static byte TYPE_NOT_SHOW = 0;
	public final static byte TYPE_SHOW_CANCEL = 1;
	public final static byte TYPE_SHOW_LATER = 2;
	public final static byte TYPE_SHOW_RATE = 3;
	public final static byte TYPE_SHOW_BACK = 4;
	
	public final static byte TYPE_NOT_ACTIVE = 0;
	public final static byte TYPE_ACTIVE_FIRST = 1;
	public final static byte TYPE_ACTIVE = 2;
	
	private final String QUERY_INTERVAL = "CREATE TABLE IF NOT EXISTS "  
			+ TABLE_INTERVAL + " "  
			+ "(" + COLUNS_INTERVAL[0] + " integer not null,"
			+ COLUNS_INTERVAL[1] + " integer not null ); ";
	private final String QUERY_MANAGER = "CREATE TABLE IF NOT EXISTS "  
			+ TABLE_MANAGER + " "  
			+ "(" + COLUNS_MANAGER[0] + " integer not null ); ";
	
	public RateAnalyzer(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		createParams();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogManager.print("Creating database");
		db.execSQL(QUERY_INTERVAL);
		db.execSQL(QUERY_MANAGER);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}	
	
	public void make(){
		if(isActive()){
			open();
		}else{
			LogManager.print("Not Activity");
		}
	}
	
	private void createParams(){
		LogManager.print("Creating rule");
		
		Cursor c = getWritableDatabase().query(TABLE_MANAGER, COLUNS_MANAGER, null, null, null, null, null);
		int lines = c.getCount();
		c.close();
		
		if(lines<1){
			SQLiteDatabase sqLiteDatabase = getWritableDatabase();
			
			ContentValues contentValues = new ContentValues();
			contentValues.put(COLUNS_INTERVAL[0], ManipuleDate.getCurrentTime());
			contentValues.put(COLUNS_INTERVAL[1], 0);
			
			sqLiteDatabase.insert(TABLE_INTERVAL, null, contentValues);
			
			contentValues = new ContentValues();
			contentValues.put(COLUNS_MANAGER[0], TYPE_ACTIVE_FIRST);
			
			sqLiteDatabase.insert(TABLE_MANAGER, null, contentValues);
			
			sqLiteDatabase.close();
		}
	}
	
	private void open(){
		LogManager.print("Checking Rule");
		if(isShowNow()){
			LogManager.print("Rule is true");
			clearCount();
			Rate.showDialog();
		}else{
			LogManager.print("Rule is false");
			addCount();
			resultToHistory(TYPE_NOT_SHOW);
		}
	}
	
	public void resultToHistory(int result){
		switch (result) {
		case TYPE_SHOW_RATE:
		case TYPE_SHOW_CANCEL:
			update(TABLE_MANAGER, COLUNS_MANAGER, TYPE_NOT_ACTIVE);
			break;
		case TYPE_NOT_SHOW:
			break;
		default:
			update(TABLE_MANAGER, COLUNS_MANAGER, TYPE_ACTIVE);
			break;
		}
	}
	
	private boolean isFirstShow(){
		LogManager.print("is first show: "+(getCurrentType() == TYPE_ACTIVE_FIRST));
		return getCurrentType() == TYPE_ACTIVE_FIRST;
	}
	
	private byte getCurrentType(){
		byte type = -1;
		Cursor c = getWritableDatabase().query(TABLE_MANAGER, COLUNS_MANAGER, null, null, null, null, null);
		while(c.moveToNext()){
			type = (byte) c.getInt(0);
		}
		LogManager.print(type);
		c.close();
		return type;
	}
	
	private boolean isActive(){
		LogManager.print("Checking if rule is need");
		Cursor c = getWritableDatabase().query(TABLE_MANAGER, COLUNS_MANAGER, null, null, null, null, null);
		while(c.moveToNext()){
			if(c.getInt(0)==TYPE_NOT_ACTIVE){
				LogManager.print("rated or never remind is false");
				c.close();
				return false;
			}
		}
		LogManager.print("Rated or never remind is true");
		c.close();
		return true;
	}
	
	private boolean isShowNow(){
		LogManager.print("Checking rule days and opes");
		Cursor c = getWritableDatabase().query(TABLE_INTERVAL, COLUNS_INTERVAL, null, null, null, null, null);
		long lastDay = 0;
		int opensCount = 0;
		while(c.moveToNext()){
			lastDay = c.getLong(0);
			opensCount = c.getInt(1);
		}
		c.close();
		LogManager.print("last day is "+lastDay+" opened "+opensCount+"*");
		
		boolean param = false;
		if(Rate.daysInterval != null){
			param = ManipuleDate.after(ManipuleDate.getTimeMoreDays(lastDay, (isFirstShow()?Rate.daysIntervalFirst:Rate.daysInterval)));  
		}
		
		LogManager.print(param);
		
		if(!param && Rate.opensInterval != null ) {
			param = opensCount>=(isFirstShow()?Rate.opensIntervalFirst:Rate.opensInterval);
		}
		
		LogManager.print(param);
		
		return param;
	}
	
	private void addCount(){
		LogManager.print("Adding count");
		Cursor c = getWritableDatabase().query(TABLE_INTERVAL, COLUNS_INTERVAL, null, null, null, null, null);
		int opensCount = 0;
		while(c.moveToNext()){
			opensCount = c.getInt(1);
		}
		c.close();
		opensCount++;
		LogManager.print("Day update to "+ManipuleDate.getCurrentTime()+" and opens update to "+ opensCount);
		update(TABLE_INTERVAL, COLUNS_INTERVAL, ManipuleDate.getCurrentTime(),opensCount);
	}
	
	private void clearCount(){
		LogManager.print("Clearing counts");
		LogManager.print("Days cleared to date "+ManipuleDate.getCurrentTime()+" and count "+0);
		update(TABLE_INTERVAL,COLUNS_INTERVAL, ManipuleDate.getCurrentTime(),0);
	}
	
	private void update(String table,String colums[],long ... values){
		LogManager.print("Abstract update");
		ContentValues contentValues = new ContentValues();
		for (int c = 0;c < colums.length;c++) {
			contentValues.put(colums[c], values[c]);
			LogManager.print("Params in values "+colums[c]+" and "+values[c]);
		}
		
		SQLiteDatabase sqLiteDatabase = getWritableDatabase(); 
		sqLiteDatabase.delete(table, null, null);
		sqLiteDatabase.insert(table, null, contentValues);
		sqLiteDatabase.close();
	}
}