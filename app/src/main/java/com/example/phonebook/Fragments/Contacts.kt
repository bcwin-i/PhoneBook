package com.example.phonebook.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phonebook.ClassFunctions.AdapterDisplayClass
import com.example.phonebook.ContactsView
import com.example.phonebook.DatabaseHandlers.ContactsDatabaseHandler
import com.example.phonebook.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_contacts.*
import com.example.phonebook.DatabaseClasses.Contacts
import com.example.phonebook.Homepage
import com.example.phonebook.listen
import kotlinx.android.synthetic.main.fragment_groups.*


class Contacts : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var context = context

        loadContacts()

        listen.observe(this, androidx.lifecycle.Observer {
            //Toast.makeText(this, choice.toString(), Toast.LENGTH_SHORT).show()
            loadContacts()
        })
    }

    fun loadContacts(){
        contacts_fragment_recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var db = ContactsDatabaseHandler(context!!)
        var data = db.readData()

        if(data.size > 0){
            val adapter = GroupAdapter<GroupieViewHolder>()
            var contacts : Contacts

            for(i in 0..(data.size - 1)){
                contacts = Contacts(data.get(i).firstname, data.get(i).lastname, data.get(i).email, data.get(i).phone, data.get(i).profile)
                adapter.add(AdapterDisplayClass.ContactsDisplay(contacts, data.get(i).id, context!!))
                contacts_fragment_recycler.adapter =adapter
            }

            adapter.setOnItemClickListener { item, view ->
                val contact_detail = item as AdapterDisplayClass.ContactsDisplay
                val intent = Intent(view.context, ContactsView::class.java)
                intent.putExtra("id", contact_detail.id)
                startActivity(intent)
            }

        }
    }
}