package com.example.phonebook.ClassFunctions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_new_group.*
import java.io.File
import java.io.FileOutputStream

open class ContactCacheManager {
    fun check_image(file_name : String, context : Context) : Boolean {
        //Checking and creating new folder directory to host Group profile pictures
        val sdcard: File = context!!.getFilesDir()
        val folder = File(sdcard.getAbsoluteFile(), "/ContactPicture/$file_name.jpg")
        if (!folder.exists()){
            return true
        }else{
            return false
        }
    }

    fun delete_image(file_name: String, context: Context){
        //Checking and creating new folder directory to host Group profile pictures
        val sdcard: File = context!!.getFilesDir()
        val folder = File(sdcard.getAbsoluteFile(), "/ContactPicture/$file_name.jpg")
        if (!folder.exists())folder.delete()
    }

    fun upload_image(file_name: String, context: Context, image : ImageView){
        //Checking and creating new folder directory to host Group profile pictures
        val sdcard: File = context!!.getFilesDir()
        val folder = File(sdcard.getAbsoluteFile(), "/ContactPicture")
        if (!folder.exists())folder.mkdir()

        val bitmap = (image.getDrawable() as BitmapDrawable).bitmap
        val file = File(folder.getAbsoluteFile(), file_name + ".jpg")
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            //stored = "success"
            Log.d("Save", "uploaded")
        } catch (e: Exception) {
            //FirebaseCrash.report(e)
            e.printStackTrace()
        }
    }

    fun setImage(file_name: String, context: Context, image : ImageView){
        //Checking and creating new folder directory to host Group profile pictures
        val sdcard: File = context!!.getFilesDir()
        val folder = File(sdcard.getAbsoluteFile(), "/ContactPicture/$file_name.jpg")

        if(folder.exists()){
            val bitmap = BitmapFactory.decodeFile(folder.absolutePath)
            image.setImageBitmap(bitmap)
        }
    }
}