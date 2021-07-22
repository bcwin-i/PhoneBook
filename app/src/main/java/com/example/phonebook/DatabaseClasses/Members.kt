package com.example.phonebook.DatabaseClasses

class Members{
    var id = 0
    var user_id = ""
    var group_id = ""

    constructor(user_id : String, group_id : String){
        this.user_id = user_id
        this.group_id = group_id
    }

    constructor(){

    }
}