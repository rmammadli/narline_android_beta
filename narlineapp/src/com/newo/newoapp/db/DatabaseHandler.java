package com.newo.newoapp.db;

import java.util.ArrayList;
import java.util.List;

import com.newo.newoapp.entities.GCMInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "newoapp_db";

	// Ads table name
	private static final String TABLE_GCM_ARCHIVE = "gsm_archive";

	// Ads Table Columns names
	private static final String GCM_ARCHIVE_ID = "id";
	private static final String GCM_ARCHIVE_COMPANY_ID = "company_id";
	private static final String GCM_ARCHIVE_MESSAGE_TITLE = "message_title";
	private static final String GCM_ARCHIVE_MESSAGE_BODY = "message_body";
	private static final String GCM_ARCHIVE_MESSAGE_DATE = "message_date";
	private static final String GCM_ARCHIVE_MESSAGE_WAS_SEEN = "message_was_seen";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

		String CREATE_GCM_ARCHIVE_TABLE = "CREATE TABLE " + TABLE_GCM_ARCHIVE
				+ "(" + GCM_ARCHIVE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+ GCM_ARCHIVE_COMPANY_ID + " INTEGER,"
				+ GCM_ARCHIVE_MESSAGE_TITLE + " TEXT,"
				+ GCM_ARCHIVE_MESSAGE_BODY + " TEXT,"
				+ GCM_ARCHIVE_MESSAGE_DATE + " INTEGER, "
				+ GCM_ARCHIVE_MESSAGE_WAS_SEEN + " INTEGER " + ")";
		arg0.execSQL(CREATE_GCM_ARCHIVE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

		// Drop older table if existed
		arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_GCM_ARCHIVE);

		// Create tables again
		onCreate(arg0);

	}

	// Getting All GCM
	public List<GCMInfo> getAllGCMInfo() {
		List<GCMInfo> gcmInfoList = new ArrayList<GCMInfo>();
		// Select All Query
		String selectQuery = "SELECT  id,company_id,message_title,message_body,message_date FROM "
				+ TABLE_GCM_ARCHIVE + " order by id desc";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				GCMInfo gcmInfo = new GCMInfo();
				gcmInfo.setId(Integer.parseInt(cursor.getString(0)));
				gcmInfo.setCompanyId(cursor.getInt(1));
				gcmInfo.setTitle(cursor.getString(2));
				gcmInfo.setMessage(cursor.getString(3));
				gcmInfo.setMessageDate(cursor.getLong(4));

				// Adding ads to list
				gcmInfoList.add(gcmInfo);
			} while (cursor.moveToNext());
		}
		// /////////////
		// cursor.close();
		// db.close();
		// return contact list
		return gcmInfoList;
	}

	// Adding new ads
	public void addGCMInfo(GCMInfo gcmInfo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(GCM_ARCHIVE_COMPANY_ID, gcmInfo.getCompanyId());
		values.put(GCM_ARCHIVE_MESSAGE_TITLE, gcmInfo.getTitle());
		values.put(GCM_ARCHIVE_MESSAGE_BODY, gcmInfo.getMessage());
		values.put(GCM_ARCHIVE_MESSAGE_DATE, gcmInfo.getMessageDate());
		values.put(GCM_ARCHIVE_MESSAGE_WAS_SEEN, gcmInfo.getWasMessageSeen());

		// Inserting Row
		db.insert(TABLE_GCM_ARCHIVE, null, values);
		db.close(); // Closing database connection

	}

}
