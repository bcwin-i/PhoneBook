package com.example.phonebook.DatabaseHandlers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseClasses.Members
import com.example.phonebook.Homepage

//Creating global variables for database
var DATABASE_NAME = "PhoneBook_Database"
private val G_TABLE_NAME = "Groups"
private val G_COL_ID = "id"
private val G_COL_GROUP_NAME = "groupname"
private val G_COL_GROUP_PICTURE = "grouppicture"

class GroupsDatabaseHandler(var context: Context) : SQLiteOpenHelper(context, "PhoneBook_Database", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //"CREATE TABLE " + G_TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_GROUP_NAME + " VARCHAR(256)," + COL_GROUP_PICTURE + " VARCHAR(50))";
        val createTable = "CREATE TABLE $G_TABLE_NAME " +
                "($G_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$G_COL_GROUP_NAME VARCHAR(256)," +
                "$G_COL_GROUP_PICTURE VARCHAR(50))";

        //Executing query
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(groups : Groups){
        //creating a variable to hold context and write database
        val db = this.writableDatabase

        //getting content values for the current database
        var cv = ContentValues()

        //Inserting values accordance to database names
        cv.put(G_COL_GROUP_NAME, groups.groupname)
        cv.put(G_COL_GROUP_PICTURE, groups.grouppicture)

        //database insertion
        val result = db.insert(G_TABLE_NAME, null, cv)

        //Error and success check
        if(result == -1.toLong())Toast.makeText(context, "Failed.", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Success.", Toast.LENGTH_SHORT).show()

    }

    fun readData() : MutableList<Groups>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Groups> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        var query = "Select * from "+ G_TABLE_NAME +" ORDER BY groupname ASC"
        if(Homepage.search != "")query = "Select * from "+ G_TABLE_NAME +
                " WHERE groupname LIKE '%${Homepage.search}%' ORDER BY groupname ASC"

        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var groups = Groups()
                groups.id = result.getString(result.getColumnIndex(G_COL_ID)).toInt()
                groups.groupname = result.getString(result.getColumnIndex(G_COL_GROUP_NAME))
                groups.grouppicture = result.getString(result.getColumnIndex(G_COL_GROUP_PICTURE))

                //adding objects to the list
                list.add(groups)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun readData(id : String) : MutableList<Groups>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Groups> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        var query = "Select * from "+ G_TABLE_NAME +" WHERE id=$id"

        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var groups = Groups()
                groups.id = result.getString(result.getColumnIndex(G_COL_ID)).toInt()
                groups.groupname = result.getString(result.getColumnIndex(G_COL_GROUP_NAME))
                groups.grouppicture = result.getString(result.getColumnIndex(G_COL_GROUP_PICTURE))

                //adding objects to the list
                list.add(groups)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun deleData(id: Int){
        //creating a variable to hold context and write database
        val db = this.writableDatabase

        //arrayOf is used to delete multiple rows
        db.delete(G_TABLE_NAME, G_COL_ID + "= ?", arrayOf(id.toString()))

        //Closing database
        db.close()
    }

    fun updateData(id : Int, groups: Groups) {
        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from $G_TABLE_NAME WHERE $G_COL_ID= $id"
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                //creating content value to access column names in the database
                var cv = ContentValues()

                //Getting all results from the column age of database using cv
                //Inserting values accordance to database names
                cv.put(G_COL_GROUP_NAME, groups.groupname)
                cv.put(G_COL_GROUP_PICTURE, groups.grouppicture)

                //updating results of the content values
                db.update(G_TABLE_NAME, cv, G_COL_ID + "=?", arrayOf(id.toString()))
            }while (result.moveToNext())
        }

        result.close()
        db.close()
    }

    fun getGroupName(group : Int) : String {
        //creating an arraylist to hold retrieved data
        var groupname = ""

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from $G_TABLE_NAME WHERE id=$group"
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                groupname = result.getString(result.getColumnIndex(G_COL_GROUP_NAME))
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return groupname
    }


}