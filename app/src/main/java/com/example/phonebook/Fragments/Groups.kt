package com.example.phonebook.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.phonebook.ClassFunctions.AdapterDisplayClass
import com.example.phonebook.DatabaseClasses.Groups
import com.example.phonebook.DatabaseHandlers.GroupsDatabaseHandler
import com.example.phonebook.DatabaseHandlers.MembersDatabaseHandler
import com.example.phonebook.R
import com.example.phonebook.listen
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dlg_show_groups.*
import kotlinx.android.synthetic.main.fragment_groups.*

class Groups : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadGroups()

        listen.observe(this, androidx.lifecycle.Observer {
            loadGroups()
        })
    }

    fun loadGroups(){
        groups_fragment_recycler.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        var db = GroupsDatabaseHandler(context!!)
        var data = db.readData()
        if(data.size > 0){
            val adapter = GroupAdapter<GroupieViewHolder>()
            var group: Groups

            for(i in 0..(data.size - 1)){
                //total.append("\n" + data.get(i).id.toString() + " "+ data.get(i).groupname + " " + data.get(i).grouppicture)
                group = Groups(data.get(i).groupname, data.get(i).grouppicture)
                adapter.add(AdapterDisplayClass.GroupsDisplay(group, data.get(i).id, context!!))
                groups_fragment_recycler.adapter =adapter
            }
        }
    }

}