package com.example.phonebook.DatabaseHandlers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseClasses.Members

//Creating global variables for database
private val M_TABLE_NAME = "Groups"
private val M_COL_ID = "id"
private val M_COL_USER_ID = "user_id"
private val M_COL_GROUP_ID = "group_id"

class MembersDatabaseHandler (var context: Context) : SQLiteOpenHelper(context, "PhoneBook_Database_Members", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $M_TABLE_NAME " +
                    "($M_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$M_COL_USER_ID VARCHAR(50)," +
                    "$M_COL_GROUP_ID VARCHAR(50))";

        //Executing query
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(members: Members){
        //creating a variable to hold context and write database
        val db = this.writableDatabase

        //getting content values for the current database
        var cv = ContentValues()

        //Inserting values accordance to database names
        cv.put(M_COL_USER_ID, members.user_id)
        cv.put(M_COL_GROUP_ID, members.group_id)

        //database insertion
        val result = db.insert(M_TABLE_NAME, null, cv)

        //Error and success check
        //if(result == -1.toLong()) Toast.makeText(context, "Members Failed.", Toast.LENGTH_SHORT).show()
        //else Toast.makeText(context, "Members Success.", Toast.LENGTH_SHORT).show()

    }

    fun readDataTotalMembers(group_id : String) : MutableList<Members>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Members> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from "+ M_TABLE_NAME + " WHERE group_id="+ group_id
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var members = Members()
                members.id = result.getString(result.getColumnIndex(M_COL_ID)).toInt()
                members.user_id = result.getString(result.getColumnIndex(M_COL_USER_ID))
                members.group_id = result.getString(result.getColumnIndex(M_COL_GROUP_ID))

                //adding objects to the list
                list.add(members)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun readUserTotalGroups(user_id : Int)  : MutableList<Members>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Members> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from $M_TABLE_NAME WHERE user_id = $user_id"
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var members = Members()
                members.id = result.getString(result.getColumnIndex(M_COL_ID)).toInt()
                members.user_id = result.getString(result.getColumnIndex(M_COL_USER_ID))
                members.group_id = result.getString(result.getColumnIndex(M_COL_GROUP_ID))

                //adding objects to the list
                list.add(members)
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
        db.delete(M_TABLE_NAME, M_COL_ID + "= ?", arrayOf(id.toString()))

        //Closing database
        db.close()
    }

    fun updateData(id : Int, members: Members) {
        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from $M_TABLE_NAME WHERE $M_COL_ID= $id"
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                //creating content value to access column names in the database
                var cv = ContentValues()

                //Getting all results from the column age of database using cv
                //Inserting values accordance to database names
                cv.put(M_COL_USER_ID, members.user_id)
                cv.put(M_COL_GROUP_ID, members.group_id)

                //updating results of the content values
                db.update(M_TABLE_NAME, cv, M_COL_ID + "=?", arrayOf(id.toString()))
            }while (result.moveToNext())
        }

        result.close()
        db.close()
    }


}