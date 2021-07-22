package com.example.phonebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.example.phonebook.Fragments.Contacts
import com.example.phonebook.Fragments.Groups
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_homepage.*
import java.util.*

public val listen : MutableLiveData<String> =  MutableLiveData<String>()


class Homepage : AppCompatActivity() {

    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    var viewAdapter: ViewPager? = null
    private var addtype: Int = 0

    companion object{
        var search : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        toolbar = findViewById(R.id.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setSupportActionBar(toolbar)

        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        setupViewPager(viewPager)

        tabLayout = findViewById(R.id.tablayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
        //setupTabIcons()

        toolbar!!.setNavigationOnClickListener{

        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                addtype = position
                //Toast.makeText(this@Welcome, addtype.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        btn_add_contact.setOnClickListener {
            val intent : Intent
            if(addtype != 1) intent = Intent(this, NewContact::class.java)
            else intent = Intent(this, NewGroup::class.java)

            startActivity(intent)
        }


        val searchItem = toolbar!!.findViewById<SearchView>(R.id.searchItem)

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            val r = Random()
            val i1: Int = r.nextInt(45 - 28) + 28
            override fun onQueryTextSubmit(query: String?): Boolean {

                if(query.toString().isNotEmpty()){
                    search = query.toString()
                    listen.value = i1.toString()
                    return true
                }else {
                    search = ""
                    listen.value = i1.toString()
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.toString().isNotEmpty()){
                    search = newText.toString()
                    listen.value = i1.toString()
                    return true
                }else {
                    search = ""
                    listen.value = i1.toString()
                    return false
                }
            }

        })

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Contacts(), "Contacts")
        adapter.addFragment(Groups(), "Groups")
        //adapter.addFragment(MemberFragment(), "Listed")
        Thread(Runnable {
            this.runOnUiThread {
                viewPager.adapter = adapter
                //viewAdapter = viewPager
            }
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }
    }
}
