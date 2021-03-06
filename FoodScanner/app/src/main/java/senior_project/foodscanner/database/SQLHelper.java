package senior_project.foodscanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Evan on 9/16/2015.
 */
public class SQLHelper extends SQLiteOpenHelper {
	private static SQLHelper mDbHelper;

	private static final String DATABASE_NAME = "foodScanner.db";
	//Update database version whenever changing tables or columns
	private static final int DATABASE_VERSION = 5;
	private static final String DROP = "DROP TABLE IF EXISTS ";

	public static final String TABLE_MEALS = "table_meals";
	public static final String TABLE_FOOD_ITEMS = "food_items";


	//Needs this exact name for some classes (e.g. CursorAdapter)
	public static final String COLUMN_ID = "_id";

	public static final String COLUMN_MEAL_TYPE = "meal_type";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_FOOD_LIST = "food_list";
	public static final String COLUMN_NEW = "new";
	public static final String COLUMN_CHANGED = "finished";
	public static final String COLUMN_DELETED = "deleted";
	public static final String COLUMN_SERVER_ID = "server_id";

	private static final String TABLE_MEALS_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_MEALS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_MEAL_TYPE + " TEXT, "
			+ COLUMN_TIME + " INT, "
			+ COLUMN_FOOD_LIST + " BLOB, "
			+ COLUMN_NEW + " INT, "
			+ COLUMN_CHANGED + " INT, "
			+ COLUMN_DELETED + " INT, "
			+ COLUMN_SERVER_ID + " INT);";


	public static final String COLUMN_MEAL_ID = "meal_id";
	public static final String COLUMN_FOOD_NAME = "food_name";
	public static final String COLUMN_BRAND = "brand";
	public static final String COLUMN_SERVING_SIZE = "serving_size";
	public static final String COLUMN_SERVING_UNIT = "serving_size_unit";

	private static final String TABLE_FOOD_ITEM_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_FOOD_ITEMS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "FOREIGN KEY(" + COLUMN_MEAL_ID + ") REFERENCES " + TABLE_MEALS + "(" + COLUMN_ID + "), "
			+ COLUMN_FOOD_NAME + " TEXT, "
			+ COLUMN_BRAND + " TEXT, "
			+ COLUMN_SERVING_SIZE + " REAL);";



	private SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static void initialize(Context context) {
		if(mDbHelper == null)
			mDbHelper = new SQLHelper(context.getApplicationContext());
	}

	public static void clear(Context context) {
		mDbHelper.clearDatabase(mDbHelper.getWritableDatabase());
		mDbHelper.close();
		context.deleteDatabase(DATABASE_NAME);
		mDbHelper = null;
	}

	public static SQLHelper getInstance() {
		return mDbHelper;
	}


	/**
	 * Convenience method for quickly inserting several rows into a single table
	 *
	 * @param tableName		Name of the table where the records will be inserted
	 * @param vals			Array of ContentValues to be inserted
	 */
	public static int bulkInsert(@NonNull String tableName, @NonNull ContentValues[] vals) {
		if(tableName == null)
			throw new NullPointerException("SQLHelper:bulkInsert() - table name cannot be null");
		if(vals == null)
			throw new NullPointerException("SQLHelper:bulkInsert() - ContentValues cannot be null");

		int inserted = 0;

		SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
		mDb.beginTransaction();
		try {
			for (ContentValues val : vals) {
				mDb.insertWithOnConflict(tableName, null, val, SQLiteDatabase.CONFLICT_REPLACE);
				inserted++;
			}
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
		}

		return inserted;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_MEALS_CREATE);
//		db.execSQL(TABLE_FOOD_ITEM_CREATE);
	}

	/**
	 * Upgrades the database when <code>DATABASE_VERSION</code> is incremented.
	 *
	 * This operation currently deletes all data stored in the database!
	 *
	 * To setup the upgrade to preserve data see:
	 * http://stackoverflow.com/questions/8425861/how-do-i-upgrade-a-database-without-removing-the-data-that-the-user-input-in-the
	 *
	 * @param db 			The database to be updated
	 * @param oldVersion	The current version of <code>db</code>
	 * @param newVersion	The desired final version of <code>db</code>
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLHelper.class.getSimpleName(), "Upgrading database from version " + oldVersion
				+ " to " + newVersion);
		clearDatabase(db);
		onCreate(db);
	}

	public void clearDatabase(SQLiteDatabase db) {
		db.execSQL(DROP + TABLE_MEALS);
		db.execSQL(DROP + TABLE_FOOD_ITEMS);
	}
}
