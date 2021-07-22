package com.example.phonebook

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.ClassFunctions.AdapterDisplayClass
import com.example.phonebook.ClassFunctions.ContactCacheManager
import com.example.phonebook.ClassFunctions.GroupCacheManager
import com.example.phonebook.ClassFunctions.PhoneSpacing
import com.example.phonebook.DatabaseClasses.Contacts
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseClasses.Members
import com.example.phonebook.DatabaseHandlers.ContactsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.GroupsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.MembersDatabaseHandler
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_contacts_view.*
import kotlinx.android.synthetic.main.activity_new_contact.*
import kotlinx.android.synthetic.main.activity_new_contact.toolbar
import kotlinx.android.synthetic.main.activity_new_group.*
import kotlinx.android.synthetic.main.dlg_show_groups.*
import java.io.File

class NewContact : AppCompatActivity() {

    private lateinit var img: ImageView
    private var CODE_IMG_GALLERY: Int = 1
    private var SAMPLE_CROPPED_IMG_NAME: String = "SampleCropImg"
    var pic_update : Int = 0
    var selectedProfileUri : Uri? = null
    var number = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        AdapterDisplayClass.optionsSelectedGroups.clear()

        toolbar!!.setNavigationOnClickListener{
            finish()
        }
        init()
        if (this.intent.extras != null && this.intent.extras!!.containsKey("id")) {
            val bundle = intent.extras
            var id = bundle?.get("id").toString()
            loadContactDetails(id)
            toolbar.title = "Update Contact"
            btn_save.text = "UPDATE"
        }

        edt_phone.addTextChangedListener(object : PhoneSpacing(' ', 3) {
            override fun onAfterTextChanged(text: String) {
                edt_phone.run {
                    setText(text)
                    setSelection(text.length)
                }
            }
        })

        edt_groups.setOnClickListener{
            showGroups()
        }

        img.setOnClickListener {
            startActivityForResult(Intent().setAction(Intent.ACTION_GET_CONTENT).setType("image/*"), CODE_IMG_GALLERY)
        }

        btn_save.setOnClickListener {
            saveContact()
        }
        btn_cancel.setOnClickListener {
            finish()
        }

    }

    private fun init() {
        this.img = findViewById(R.id.user_profile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            showGroups()
        }

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

    private fun showGroups(){
        val dialog = Dialog(this)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog .setCancelable(false)
        dialog .setContentView(R.layout.dlg_show_groups)
        val cancel = dialog .findViewById(R.id.btn_cancel_groups) as Button
        val select = dialog .findViewById(R.id.btn_select) as Button
        val add = dialog.findViewById(R.id.btn_add_group) as Button
        val list = dialog.findViewById(R.id.groups_recycler) as RecyclerView
        val total = dialog.findViewById(R.id.txt_group_total) as TextView

        val adapter = GroupAdapter<GroupieViewHolder>()
        dialog.groups_recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        var context = this
        var db = GroupsDatabaseHandler(context)
        var data = db.readData()
        if(data.size > 0){
            total.text = "Groups (${data.size})"
            var group: Groups

            for(i in 0..(data.size - 1)){
                group = Groups(data.get(i).groupname, data.get(i).grouppicture)
                adapter.add(AdapterDisplayClass.GroupOptionsDisplay(group, data.get(i).id, this))
                dialog.groups_recycler.adapter =adapter
            }
        }

        cancel.setOnClickListener {
            dialog .dismiss()
        }
        select.setOnClickListener {
            displaySelectedGroups()
            dialog.dismiss()
        }
        add.setOnClickListener {
            val intent = Intent(this, NewGroup::class.java)
            startActivityForResult(intent, 0)
            dialog.dismiss()
        }
        dialog .show()
    }

    fun displaySelectedGroups(){
        var groups =  AdapterDisplayClass.optionsSelectedGroups
        var selected = ""
        txt_selected_groups.text = selected
        if(groups.isNotEmpty()){
            for((key, value) in groups){
                selected += "$value \n"
            }
            txt_selected_groups.text = selected
        }
    }

    fun saveContact(){
        val context = this

        var first_name = edt_first_name.text.toString()
        var last_name = edt_last_name.text.toString()
        var email = edt_email.text.toString()
        number = edt_phone.text.toString().replace(" ", "");
        var groups = ArrayList<String>()

        if((!first_name.isNullOrEmpty() || !last_name.isNullOrEmpty()) && !number.isNullOrEmpty()){
            val db = ContactsDatabaseHandler(context)
            var data = db.readData()

            //creating new file name for profile image
            var profile = ""
            if(selectedProfileUri != null){
                //creating new file name for profile image
                profile = (data.size + 1).toString()

                var cache = ContactCacheManager()
                cache.upload_image(profile, this, user_profile)
            }

            //creating groups object
            val contacts =  Contacts(first_name, last_name, email, number, profile)
            //Creating the database handler object
            db.insertData(contacts)
            uploadGroups(data.size + 1)
        }else{
            Toast.makeText(this, "Fill non optional spaces!", Toast.LENGTH_SHORT).show()
        }
    }

    fun uploadGroups(id : Int){
        var groups =  AdapterDisplayClass.optionsSelectedGroups
        if(groups.isNotEmpty()){
            val context = this
            val db = MembersDatabaseHandler(context)
            for((key, value) in groups){
                val member = Members(id.toString(), key.toString())
                db.insertData(member)
            }
        }
        val output = Intent()
        setResult(Activity.RESULT_OK, output)
        finish()
    }

    private fun loadContactDetails(id : String){
        var manager = ContactCacheManager()
        manager.setImage(id, this, img)

        var db = ContactsDatabaseHandler(this)
        var data = db.readUserData(id)

        if(data.size > 0){
            for(i in 0..(data.size - 1)){
                edt_first_name.setText(data.get(i).firstname)
                edt_last_name.setText(data.get(i).lastname)
                edt_email.setText(data.get(i).email)
                edt_phone.setText(data.get(i).phone)
                var groups = ArrayList<String>()
            }

            var dbm = MembersDatabaseHandler(this)
            var datam = dbm.readUserTotalGroups(id.toInt())
            if(datam.size > 0){
                var count = ArrayList<String>()

                for(i in 0..(datam.size - 1)){
                    count.add(datam.get(i).group_id)
                }
                if(count.size > 0){
                    var groupDatabase = GroupsDatabaseHandler(this)

                    for (n in 0..(count.size - 1)){
                        var groupData = groupDatabase.readData(count[n])
                        for(i in 0..(groupData.size - 1)){
                            AdapterDisplayClass().SelectedGroups(groupData.get(i).id, groupData.get(i).groupname)
                            //Toast.makeText(this, groupData.get(i).id.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    displaySelectedGroups()
                }
            }
        }
    }

}
