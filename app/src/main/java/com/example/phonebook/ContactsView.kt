package com.example.phonebook

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.phonebook.ClassFunctions.AdapterDisplayClass
import com.example.phonebook.ClassFunctions.ContactCacheManager
import com.example.phonebook.DatabaseClasses.Contacts
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseHandlers.ContactsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.GroupsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.MembersDatabaseHandler
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_contacts_view.*
import kotlinx.android.synthetic.main.adp_contacts_view.*
import kotlinx.android.synthetic.main.adp_contacts_view.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.fragment_groups.*
import java.util.*
import kotlin.collections.ArrayList

class ContactsView : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    var phone : String = ""
    var email = ""
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener{
            finish()
        }
        toolbar!!.overflowIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        if (this.intent.extras != null && this.intent.extras!!.containsKey("id")) {
            val bundle = intent.extras
            id = bundle?.get("id").toString()
            loadContactData(id)
        }else finish()

        btn_context_view_phone.setOnClickListener {
            var number = phone
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:<$number>")
            ContextCompat.startActivity(this, intent, null)
        }

        btn_contact_view_email.setOnClickListener {
            var mail = email
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:$mail"))
            try {
                startActivity(intent)
            }catch (ex : ActivityNotFoundException){
                Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.mn_contact_view, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_edit_contact -> {
                var intent = Intent(this, NewContact::class.java)
                intent.putExtra("id", id!!)
                try{
                    startActivity(intent)
                }catch (ex : Exception){}
                return true
            }
            R.id.btn_delete_contact ->{
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun loadContactData(id : String){
        var manager = ContactCacheManager()
        manager.setImage(id, this, img_contact_view_profile)

        var db = ContactsDatabaseHandler(this)
        var data = db.readUserData(id)

        if(data.size > 0){
            for(i in 0..(data.size - 1)){
                txt_contact_view_name.text = "${data.get(i).firstname} ${data.get(i).lastname}"
                btn_context_view_phone.text = data.get(i).phone
                btn_contact_view_email.text = data.get(i).email

                phone = data.get(i).phone
                email = data.get(i).email
            }

            var dbm = MembersDatabaseHandler(this)
            var datam = dbm.readUserTotalGroups(id.toInt())
            if(datam.size > 0){
                var count = ArrayList<String>()

                for(i in 0..(datam.size - 1)){
                    count.add(datam.get(i).group_id)
                }
                if(count.size > 0){
                    rcv_contest_view_groups.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
                    val adapter = GroupAdapter<GroupieViewHolder>()

                    var groupDatabase = GroupsDatabaseHandler(this)

                    for (n in 0..(count.size - 1)){
                        var groupData = groupDatabase.readData(count[n])
                        var group: Groups
                        for(i in 0..(groupData.size - 1)){
                            group = Groups(groupData.get(i).groupname, groupData.get(i).grouppicture)
                            adapter.add(AdapterDisplayClass.GroupsDisplay(group, groupData.get(i).id, this))
                            rcv_contest_view_groups.adapter =adapter
                        }
                    }
                }
            }
        }
    }
}
