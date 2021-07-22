package com.example.phonebook.DatabaseHandlers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.phonebook.DatabaseClasses.Contacts
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.Homepage

//Creating global variables for database
val C_TABLE_NAME = "Contacts"
val C_COL_ID = "id"
val C_COL_FIRSTNAME = "firstname"
val C_COL_LASTNAME = "lastname"
val C_COL_EMAIL = "email"
val C_COL_PHONE = "phone"
val C_COL_PROFILE = "profile"

class ContactsDatabaseHandler (var context: Context) : SQLiteOpenHelper(context, "PhoneBook_Database_Contacts", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        //sql databae definition
        //"CREATE TABLE "+ C_TABLE_NAME +" ("+ C_COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ C_COL_FIRSTNAME +" VARCHAR(256),"+ C_COL_LASTNAME +" VARCHAR(256),"+ C_COL_EMAIL +" VARCHAR(300),"+ C_COL_PHONE +" VARCHAR(256),"+ C_COL_PROFILE +" VARCHAR(256))";
        val createTable = "CREATE TABLE $C_TABLE_NAME " +
                "($C_COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$C_COL_FIRSTNAME VARCHAR(256)," +
                "$C_COL_LASTNAME VARCHAR(256)," +
                "$C_COL_EMAIL VARCHAR(256)," +
                "$C_COL_PHONE VARCHAR(256)," +
                "$C_COL_PROFILE VARCHAR(50))";

        //Executing query
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
    fun insertData(contacts : Contacts){
        //creating a variable to hold context and write database
        val db = this.writableDatabase

        //getting content values for the current database
        var cv = ContentValues()

        //Inserting values accordance to database names
        cv.put(C_COL_FIRSTNAME, contacts.firstname)
        cv.put(C_COL_LASTNAME, contacts.lastname)
        cv.put(C_COL_EMAIL, contacts.email)
        cv.put(C_COL_PHONE, contacts.phone)
        cv.put(C_COL_PROFILE, contacts.profile)

        //database insertion
        val result = db.insert(C_TABLE_NAME, null, cv)

        //Error and success check
        if(result == -1.toLong()) Toast.makeText(context, "Failed saving", Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "contacts saved.", Toast.LENGTH_SHORT).show()

    }

    fun readUserData(id : String) : MutableList<Contacts>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Contacts> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        var query = "Select * from $C_TABLE_NAME WHERE id=$id"

        if(Homepage.search != ""){
            query = "SELECT * FROM $C_TABLE_NAME WHERE " +
                    "firstname LIKE '%${Homepage.search}%' OR " +
                    "lastname LIKE '%${Homepage.search}%' OR " +
                    "phone LIKE '%${Homepage.search}%' OR " +
                    "email LIKE '%${Homepage.search}%' " +
                    "ORDER BY firstname ASC"
        }

        val result = db.rawQuery(query, null)
        var had : MutableList<String> = ArrayList<String>()

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var contacts = Contacts()
                contacts.id = result.getString(result.getColumnIndex(C_COL_ID)).toInt()
                contacts.firstname = result.getString(result.getColumnIndex(C_COL_FIRSTNAME))
                contacts.lastname = result.getString(result.getColumnIndex(C_COL_LASTNAME))
                contacts.email = result.getString(result.getColumnIndex(C_COL_EMAIL))
                contacts.phone = result.getString(result.getColumnIndex(C_COL_PHONE))
                contacts.profile = result.getString(result.getColumnIndex(C_COL_PROFILE))

                //adding objects to the list
                list.add(contacts)
                had.add(result.getString(result.getColumnIndex(C_COL_FIRSTNAME)).toString())
            }while (result.moveToNext())
        }

        if(Homepage.search != "")Log.d("Queried", had.toString())

        result.close()
        db.close()
        return list
    }
    fun readData() : MutableList<Contacts>{
        //creating an arraylist to hold retrieved data
        var list : MutableList<Contacts> = ArrayList()

        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        var query = "Select * from $C_TABLE_NAME ORDER BY firstname ASC"

        if(Homepage.search != ""){
            query = "SELECT * FROM $C_TABLE_NAME WHERE " +
                    "firstname LIKE '%${Homepage.search}%' OR " +
                    "lastname LIKE '%${Homepage.search}%' OR " +
                    "phone LIKE '%${Homepage.search}%' OR " +
                    "email LIKE '%${Homepage.search}%' " +
                    "ORDER BY firstname ASC"
        }

        val result = db.rawQuery(query, null)
        var had : MutableList<String> = ArrayList<String>()

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                var contacts = Contacts()
                contacts.id = result.getString(result.getColumnIndex(C_COL_ID)).toInt()
                contacts.firstname = result.getString(result.getColumnIndex(C_COL_FIRSTNAME))
                contacts.lastname = result.getString(result.getColumnIndex(C_COL_LASTNAME))
                contacts.email = result.getString(result.getColumnIndex(C_COL_EMAIL))
                contacts.phone = result.getString(result.getColumnIndex(C_COL_PHONE))
                contacts.profile = result.getString(result.getColumnIndex(C_COL_PROFILE))

                //adding objects to the list
                list.add(contacts)
                had.add(result.getString(result.getColumnIndex(C_COL_FIRSTNAME)).toString())
            }while (result.moveToNext())
        }

        if(Homepage.search != "")Log.d("Queried", had.toString())

        result.close()
        db.close()
        return list
    }

    fun deleData(id: Int){
        //creating a variable to hold context and write database
        val db = this.writableDatabase

        //arrayOf is used to delete multiple rows
        db.delete(C_TABLE_NAME, C_COL_ID + "= ?", arrayOf(id.toString()))

        //Closing database
        db.close()
    }

    fun updateData(id : Int, contacts: Contacts) {
        //creating a variable to hold context and read database
        val db = this.readableDatabase

        //querying database
        val query = "Select * from $C_TABLE_NAME WHERE $C_COL_ID= $id"
        val result = db.rawQuery(query, null)

        //Reading all data in the list and checking is database table is not empty
        if(result.moveToFirst()){
            //reading data in a row using do while loop
            do {
                //creating content value to access column names in the database
                var cv = ContentValues()

                //Getting all results from the column age of database using cv
                //Inserting values accordance to database names
                contacts.id = result.getString(result.getColumnIndex(C_COL_ID)).toInt()
                contacts.firstname = result.getString(result.getColumnIndex(C_COL_FIRSTNAME))
                contacts.lastname = result.getString(result.getColumnIndex(C_COL_LASTNAME))
                contacts.email = result.getString(result.getColumnIndex(C_COL_EMAIL))
                contacts.phone = result.getString(result.getColumnIndex(C_COL_PHONE))
                contacts.profile = result.getString(result.getColumnIndex(C_COL_PROFILE))

                //updating results of the content values
                db.update(C_TABLE_NAME, cv, C_COL_ID + "=?", arrayOf(id.toString()))
            }while (result.moveToNext())
        }

        result.close()
        db.close()
    }


}