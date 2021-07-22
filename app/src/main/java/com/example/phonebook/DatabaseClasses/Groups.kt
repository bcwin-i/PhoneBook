package com.example.phonebook.DatabaseClasses

class Groups{

    //Creating properties to assign values
    var id = 0
    var groupname = ""
    var grouppicture = ""

    //Creating a constructor as required parameter to access GroupsHandler class
    constructor(groupname : String, grouppicture : String){
        this.groupname = groupname
        this.grouppicture = grouppicture
    }

    //Creating an empty constructor for empty instance of the the group class
    constructor(){

    }

}