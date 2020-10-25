package com.example.k1590;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String eventsTable = "thingTable";
    public static final String thing_id = "thing_id";
    public static final String name = "name";
    public static final String instruct = "instruct";
    public static final String type = "type";
    public static final String user_name = "user_name";

	private static String DATABASE_CREATE_USER = "create table tb_user(name varchar(45) primary key,  " +
			"password  varchar(45))";
    
    public DBHelper(Context context, String name,    
            CursorFactory factory, int version) {   
        super(context, name, factory, version);
        this.getWritableDatabase();  
    }
    
    /**
     * should be invoke when you never use DBhelper
     * To release the database and etc.
     */
    public void Close() {
            this.getWritableDatabase().close();
    }
   
  
    public void onCreate(SQLiteDatabase db) {   
        db.execSQL("CREATE TABLE IF NOT EXISTS "    
                + eventsTable + " ("
                + thing_id + " VARCHAR,"
                + user_name + " VARCHAR,"
                + name + " VARCHAR,"
                + instruct + " VARCHAR,"
                + type + " VARCHAR)");
        
        db.execSQL(DATABASE_CREATE_USER);

    }   
  
    public void onUpgrade(SQLiteDatabase db,    
            int oldVersion, int newVersion) {   
    	db.execSQL("DROP TABLE IF EXISTS "+eventsTable);
        onCreate(db);   
    }

    public void updateUserVo(String name,String password){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("password",password);
        db.update("tb_user",values,"name=?", new String[]{name});
    }


    /**
     * 添加设备
     * @param name
     * @param order
     * @param type
     */
    public void addThing(String id, String user_name, String name, String order, String type) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.thing_id, id);
        values.put(DBHelper.user_name, user_name);
        values.put(DBHelper.name, name);
        values.put(DBHelper.instruct ,order);
        values.put(DBHelper.type ,type);

        this.getWritableDatabase().insert(
        		DBHelper.eventsTable, null, values);
    }


    public void insert(UserBean userVo) {
        ContentValues values = new ContentValues();
        values.put("name", userVo.getName());
        values.put("password", userVo.getPassword());
        this.getWritableDatabase().insert(
                "tb_user", null, values);
    }

//    public boolean update(UserBean userVo) {
//        // TODO Auto-generated method stub
//        boolean flag = false;
//        ContentValues values = new ContentValues();
//        values.put("password", userVo.getPassword());
//        int count = this.getWritableDatabase().update("tb_user", values, "name = ?", new String[]{ userVo.getName()});
//        flag = count > 0 ? true : false;
//        return flag;
//    }

    /**
     * 登陆
     */
    public boolean is_login(String name, String pwd) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db
                    .rawQuery("select * from tb_user"
                                    + " where name = ? and password = ?",
                            new String[] { name, pwd });
            if (cursor.moveToFirst()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from thingTable", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获取设备
     * @param type
     * @return
     */
    public ArrayList<ThingBean> getThingByType(String userName, String type){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ThingBean> List= new ArrayList<>();
        Cursor cursor =db.rawQuery("select * from thingTable where user_name = ? and type = ?",new String[] {userName, type});
        if (cursor.moveToFirst()){
            do{
                ThingBean bean = new ThingBean();
                bean.setThing_id(cursor.getString(0));
                bean.setName(cursor.getString(2));
                bean.setOrder(cursor.getString(3));
                bean.setType(cursor.getString(4));
                List.add(bean);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return List;
    }


    /**
     * 获取所有设备
     * @return
     */
    public ArrayList<ThingBean> QueryAllThing(String user_name){
    	SQLiteDatabase db = this.getReadableDatabase();
    	ArrayList<ThingBean> List= new ArrayList<>();
    	Cursor cursor =db.rawQuery("select * from thingTable where user_name = ?", new String[]{user_name});
    	if (cursor.moveToFirst()){
        	do{
        		ThingBean bean = new ThingBean();
                bean.setThing_id(cursor.getString(0));
                bean.setName(cursor.getString(2));
                bean.setOrder(cursor.getString(3));
                bean.setType(cursor.getString(4));
        		List.add(bean);
        		}
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
		return List;
    }



    /**
     * 更新指令
     * @param id
     * @param order
     */
    public void updateThing(String id, int order) {
    	ContentValues values = new ContentValues();
        values.put(DBHelper.instruct ,order);
        this.getWritableDatabase().update(DBHelper.eventsTable, values, DBHelper.thing_id +"=?", new String[]{id});
    }


    /**
     * 删除
     * @param id
     */
    public void deleteThingById(String id) {
        this.getWritableDatabase().delete(
                DBHelper.eventsTable, DBHelper.thing_id+"=?" , new String[]{id});
    }


    public void deleteUser(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete from tb_user where name=" + "'" + userName + "'";
        db.execSQL(sql);
        db.close();
    }
	
}  