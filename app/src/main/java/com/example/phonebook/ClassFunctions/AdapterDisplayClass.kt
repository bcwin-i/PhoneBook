package com.example.phonebook.ClassFunctions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.ContactsView
import com.example.phonebook.DatabaseClasses.Contacts
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseClasses.Members
import com.example.phonebook.DatabaseHandlers.GroupsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.MembersDatabaseHandler
import com.example.phonebook.NewContact
import com.example.phonebook.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.adp_contacts_view.view.*
import kotlinx.android.synthetic.main.adp_group_options.view.*
import kotlinx.android.synthetic.main.adp_groups_view.view.*
import kotlinx.android.synthetic.main.dlg_show_groups.*

open class AdapterDisplayClass {
    companion object{
        var optionsSelectedGroups : HashMap<Int, String> = HashMap()
    }

    class GroupOptionsDisplay(val groups : Groups, var id : Int, val context: Context) : Item<GroupieViewHolder>(){

        override fun getLayout(): Int {
            return R.layout.adp_group_options
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txt_groupname.text = groups.groupname
            var name = groups.groupname
            var check = viewHolder.itemView.chk_options_check
            existed(id, check)

            viewHolder.itemView.txt_groupname.setOnClickListener {
                if(check.isChecked) {
                    check.isChecked = false
                    optionsSelectedGroups.remove(id)
                }
                else {
                    check.isChecked = true
                    existing(id, name)
                }
            }
            check.setOnCheckedChangeListener { buttonView, isChecked ->
                if(check.isChecked) existing(id, name)
                else optionsSelectedGroups.remove(id)
            }
        }

        fun existing(id: Int, groupname : String){
            var found = false
            for(n in optionsSelectedGroups.keys){
                if(n == id){
                    found = true
                    break
                }
            }
            if (!found)optionsSelectedGroups.put(id, groupname)
        }

        fun existed(id : Int, check : CheckBox){
            optionsSelectedGroups.keys.forEach{
                if(it == id){
                    //Toast.makeText(context, "$it = $id", Toast.LENGTH_SHORT).show()
                    check.isChecked = true
                }
                //else Toast.makeText(context, "$it = $id", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun SelectedGroups(id :Int, groupname: String){
        optionsSelectedGroups.put(id, groupname)
    }

    class GroupsDisplay(val groups : Groups, var id : Int, val context: Context) : Item<GroupieViewHolder>(){

        override fun getLayout(): Int {
            return R.layout.adp_groups_view
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var dbm = MembersDatabaseHandler(context!!)
            var datam = dbm.readDataTotalMembers(id.toString())

            viewHolder.itemView.txt_group_name_view.text = groups.groupname
            viewHolder.itemView.txt_group_total.text = "${datam.size} members"

            if(groups.grouppicture != ""){
                var manager = GroupCacheManager()
                manager.setImage(id.toString(), context, viewHolder.itemView.group_profile_view)
            }
        }
    }

    class ContactsDisplay(val contacts : Contacts, var id : Int, val context: Context) : Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.adp_contacts_view
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var dbm = MembersDatabaseHandler(context!!)
            var datam = dbm.readUserTotalGroups(id)

            if(contacts.profile != ""){
                var manager = ContactCacheManager()
                manager.setImage(id.toString(), context, viewHolder.itemView.user_view_profile)
            }

            viewHolder.itemView.txt_contact_name.text = "${contacts.firstname} ${contacts.lastname}"

            var db = GroupsDatabaseHandler(context!!)
            if(datam.size > 0){
                var count = 0

                for(i in 0..(datam.size - 1)){
                    if (count > 0)viewHolder.itemView.txt_contact_group.append(", ${db.getGroupName(datam.get(i).group_id.toInt())}")
                    else viewHolder.itemView.txt_contact_group.append("${db.getGroupName(datam.get(i).group_id.toInt())}")
                    count++
                }
            }


            viewHolder.itemView.imb_call_number.setOnClickListener {
                var number = contacts.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:<$number>")
                startActivity(context, intent, null)
            }
        }

    }
}