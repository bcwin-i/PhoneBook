package com.example.phonebook

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.phonebook.ClassFunctions.GroupCacheManager
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseHandlers.GroupsDatabaseHandler
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_new_contact.*
import kotlinx.android.synthetic.main.activity_new_contact.toolbar
import kotlinx.android.synthetic.main.activity_new_group.*
import java.io.File
import java.io.FileOutputStream

class NewGroup : AppCompatActivity() {
    private lateinit var img: ImageView
    private var CODE_IMG_GALLERY: Int = 1
    private var SAMPLE_CROPPED_IMG_NAME: String = "SampleCropImg"
    var pic_update : Int = 0
    var selectedProfileUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)

        toolbar!!.setNavigationOnClickListener{
            finish()
        }

        init()
        img.setOnClickListener {
            pic_select()
        }

        btn_save_group.setOnClickListener {
            saveGroup()
        }
    }

    private fun init() {
        this.img = findViewById(R.id.group_profile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CODE_IMG_GALLERY && resultCode == Activity.RESULT_OK ){
            //Proceed and check what the selected image is
            var imageUri : Uri? = data!!.getData()
            if(imageUri != null){
                //img.setImageResource(R.drawable.profile_background)
                startCrop(imageUri)
                //Toast.makeText(this, "yh", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK){
            var imageResourceCrop : Uri? = data?.let { UCrop.getOutput(it) }
            if(imageResourceCrop != null){
                img.setImageURI(imageResourceCrop)
                pic_update = 1
                selectedProfileUri = imageResourceCrop
                //Toast.makeText(this, "yh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun pic_select(){
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog .setCancelable(false)
        dialog .setContentView(R.layout.profile_option)
        val update = dialog .findViewById(R.id.btn_update_photo) as Button
        val delete = dialog .findViewById(R.id.btn_delete_photo) as Button
        update.setOnClickListener {
            startActivityForResult(Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMG_GALLERY)
            dialog .dismiss()
        }
        delete.setOnClickListener {
            img.setImageResource(R.drawable.ic_image_black_24dp)
            selectedProfileUri = null
            dialog .dismiss()
        }
        dialog .show()
    }

    private fun startCrop(uri: Uri){
        var destinationFileName = SAMPLE_CROPPED_IMG_NAME
        destinationFileName += ".jpg"
        var uCrop : UCrop = UCrop.of(uri,Uri.fromFile(File(cacheDir, destinationFileName)))

        uCrop.withAspectRatio(1F, 1F)
        //uCrop.withAspectRatio(3F, 4F)
        //uCrop.withAspectRatio(2F, 3F)
        //uCrop.withAspectRatio(16F, 9F)
        //uCrop.useSourceImageAspectRatio()

        //uCrop.withMaxResultSize(450,450)

        uCrop.withOptions(getCropOptions())
        //uCrop.withOptions(UCrop.Options())
        uCrop.start(this)
    }

    private fun getCropOptions() : UCrop.Options{
        var options: UCrop.Options = UCrop.Options()

        //options.setCompressionQuality(100)

        //CompressType
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG)

        //UI
        options.setHideBottomControls(false)
        //options.setFreeStyleCropEnabled(true)

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary))
        options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark))

        options.setToolbarTitle("")

        return options
    }

    fun saveGroup(){
        val context = this

        if(edt_group_name.text.toString() != ""){
            var profile = ""

            //Checking if theres an uploaded group picture
            if(selectedProfileUri != null){
                var db = GroupsDatabaseHandler(context)
                var data = db.readData()

                //creating new file name for profile image
                profile = (data.size + 1).toString()

                var cache = GroupCacheManager()
                cache.upload_image(profile, this, group_profile)
            }

            //creating groups object
            val groups =  Groups(edt_group_name.text.toString(), profile)
            //Creating the database handler object
            val db = GroupsDatabaseHandler(context)
            db.insertData(groups)

            val output = Intent()
            setResult(Activity.RESULT_OK, output)
            finish()
            //db.updateData(1, groups)
        }else Toast.makeText(this, "Group name can't be empty.", Toast.LENGTH_SHORT).show()
        finish()
    }
}
