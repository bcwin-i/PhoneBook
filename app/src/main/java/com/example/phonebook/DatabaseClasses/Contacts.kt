package com.example.phonebook.DatabaseClasses

class Contacts {
    var id = 0
    var firstname = ""
    var lastname = ""
    var email = ""
    var phone = ""
    var profile = ""

    constructor(firstname : String, lastname : String, email : String, phone : String, profile : String){
        this.firstname = firstname
        this.lastname = lastname
        this.email = email
        this.phone = phone
        this.profile = profile
    }

    constructor(){

    }
}