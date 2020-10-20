package com.example.k1590;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String eventsTable = "eventsTable";
    public static final String title = "title";
    public static final String name = "name";
    public static final String description = "description";
    public static final String detail = "detail";
    public static final String total = "total";
    public static final String join = "joinNum";
    public static final String contact = "contact";
    public static final String tou = "tou";

    public static final String commentTable = "commentTable";
    public static final String comment = "comment";

    public static final String notify = "notifyTable";
    public static final String status = "status";

	private static String DATABASE_CREATE_USER = "create table tb_user(name varchar(45) primary key,  " +
			"password  varchar(45), tou varchar(100), role varchar(45), school varchar(100), college varchar(100), speciality varchar(45), grade varchar(45), skill varchar(100), contact varchar(45))";
    
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
                + name + " VARCHAR,"
                + title + " VARCHAR,"
                + description + " VARCHAR,"
                + detail + " VARCHAR,"
                + total + " int,"
                + join + " int,"
                + tou + " VARCHAR,"
                + contact + " VARCHAR)");
        
        db.execSQL(DATABASE_CREATE_USER);

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + commentTable + " ("
                + name + " VARCHAR,"
                + title + " VARCHAR,"
                + comment + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + notify + " ("
                + name + " VARCHAR,"
                + title + " VARCHAR)");
    }   
  
    public void onUpgrade(SQLiteDatabase db,    
            int oldVersion, int newVersion) {   
    	db.execSQL("DROP TABLE IF EXISTS "+eventsTable);
        onCreate(db);   
    }

    public void updateUserVo(String name,String tou){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("tou",tou);
        db.update("tb_user",values,"name=?", new String[]{name});
    }

    /**
     * 更新个人信息
     * @param bean
     */
    public void updateInfo(UserBean bean){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("tou", bean.getTou());
        values.put("role", bean.getRole());
        values.put("school", bean.getSchool());
        values.put("college", bean.getCollege());
        values.put("speciality", bean.getSpeciality());
        values.put("grade", bean.getGrade());
        values.put("skill", bean.getSkill());
        values.put("contact", bean.getContact());
        values.put("password",bean.getPassword());
        db.update("tb_user",values,"name=?", new String[]{bean.getName()});
    }

//    public void updateEvent(String name,String tou){
//        SQLiteDatabase db = this.getReadableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("tou",tou);
//        db.update(DBHelper.eventsTable,values,"name=?", new String[]{name});
//    }

    
    public void addEvent(String title, String name, String tou, String description, String detail, int total, String contact) {
        ContentValues values = new ContentValues(); 
        values.put(DBHelper.title ,title);
        values.put(DBHelper.name, name);
        values.put(DBHelper.description ,description);
        values.put(DBHelper.detail ,detail);
        values.put(DBHelper.total, total);
        values.put(DBHelper.join, 0);
        values.put(DBHelper.contact ,contact);
        values.put(DBHelper.tou, tou);
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
     * 获取所有提问
     * @param title
     * @return
     */
//    public ArrayList<Comment> getCommentByTitle(String title){
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Comment> List= new ArrayList<>();
//        Cursor cursor =db.rawQuery("select * from commentTable where title = ?",new String[] {title});
//        if (cursor.moveToFirst()){
//            do{
//                Comment bean = new Comment();
//                bean.setName(cursor.getString(0));
//                bean.setTitle(cursor.getString(1));
//                bean.setComment(cursor.getString(2));
//                List.add(bean);
//            }
//            while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return List;
//    }

    /**
     * 添加申请
     * @param ny
     */
//    public void insertNotify(Notify ny){
//        ContentValues values = new ContentValues();
//        values.put("name", ny.getName());
//        values.put("title", ny.getTitle());
//        this.getWritableDatabase().insert(
//                "notifyTable", null, values);
//    }

    /**
     * 获取所有申请
     * @param name
     * @return
     */
//    public ArrayList<Notify> getNotifyByName(String name){
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Notify> List= new ArrayList<>();
//        Cursor cursor =db.rawQuery("select * from notifyTable where name = ?",new String[] {name});
//        if (cursor.moveToFirst()){
//            do{
//                Notify bean = new Notify();
//                bean.setName(cursor.getString(0));
//                bean.setTitle(cursor.getString(1));
//                List.add(bean);
//            }
//            while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return List;
//    }


    /**
     * 获取组队信息
     * @param title
     * @return
     */
//    public ArrayList<Event> getEventByTitle(String title){
//        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Event> List= new ArrayList<>();
//        Cursor cursor =db.rawQuery("select * from eventsTable where title = ?",new String[] {title});
//        if (cursor.moveToFirst()){
//            do{
//                Event bean = new Event();
//                bean.setName(cursor.getString(0));
//                bean.setTitle(cursor.getString(1));
//                bean.setDescription(cursor.getString(2));
//                bean.setDetail(cursor.getString(3));
//                bean.setTotal(cursor.getInt(4));
//                bean.setJoin(cursor.getInt(5));
//                bean.setTou(cursor.getString(6));
//                bean.setContact(cursor.getString(7));
//                List.add(bean);
//            }
//            while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//        return List;
//    }


    /**
     * 添加提问
     * @param
     */
//    public void insertComment(Comment ct){
//        ContentValues values = new ContentValues();
//        values.put("name", ct.getName());
//        values.put("title", ct.getTitle());
//        values.put("comment", ct.getComment());
//        this.getWritableDatabase().insert(
//                "commentTable", null, values);
//    }

    public ArrayList<UserBean> getUserInfoByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<UserBean> List= new ArrayList<>();
        Cursor cursor =db.rawQuery("select * from tb_user where name = ?",new String[] {name});
        if (cursor.moveToFirst()){
            do{
                UserBean bean = new UserBean();
                bean.setTou(cursor.getString(2));
                bean.setRole(cursor.getString(3));
                bean.setContact(cursor.getString(9));
                List.add(bean);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return List;
    }

//    public ArrayList<Event> QueryAllQuote(){
//    	SQLiteDatabase db = this.getReadableDatabase();
//    	ArrayList<Event> List= new ArrayList<>();
//    	Cursor cursor =db.rawQuery("select * from eventsTable",null);
//    	if (cursor.moveToFirst()){
//        	do{
//        		Event event = new Event();
//                event.setName(cursor.getString(0));
//                event.setTitle(cursor.getString(1));
//                event.setDescription(cursor.getString(2));
//                event.setDetail(cursor.getString(3));
//                event.setTotal(cursor.getInt(4));
//                event.setJoin(cursor.getInt(5));
//                event.setTou(cursor.getString(6));
//                event.setContact(cursor.getString(7));
//        		List.add(event);
//        		}
//            while(cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//		return List;
//    }



    /**
     * 更新参加人数
     * @param title
     * @param num
     */
    public void updateEvent(String title, int num) {
    	ContentValues values = new ContentValues();
        values.put(DBHelper.join ,num);
        this.getWritableDatabase().update(DBHelper.eventsTable, values, DBHelper.title+"=?", new String[]{title});
    }


    /**
     * 删除
     * @param title
     */
    public void deleteEventByTitle(String title) {
        this.getWritableDatabase().delete(
                DBHelper.eventsTable, "title='" + title + "'", null);
    }

    public void deleteAllEvent() {
        this.getWritableDatabase().delete(
                DBHelper.eventsTable, null, null);
    }

    public void deleteUser(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "delete from tb_user where name=" + "'" + userName + "'";
        db.execSQL(sql);
        db.close();
    }
	
}  