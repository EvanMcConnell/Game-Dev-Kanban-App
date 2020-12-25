package com.example.greetings.models

import android.content.Context
import android.widget.Toast
import com.example.greetings.helpers.exists
import com.example.greetings.helpers.read
import com.example.greetings.helpers.write
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.util.*

val MEMBERS_FILE = "members.json"
val memberGsonBuilder = GsonBuilder().setPrettyPrinting().create()
val memberListType = object : TypeToken<ArrayList<MemberModel>>() {}.type


var lastMemberId: Int = 0

internal fun getMemberId(): Int {
    return lastMemberId++
}

class MemberMemStore : MemberStore {

    val context: Context
    private var members = mutableListOf<MemberModel>()

    constructor(context: Context) {
        this.context = context
        if(exists(context, MEMBERS_FILE)){
            deserialize()
        }
    }

    override fun findAll(): List<MemberModel> {
        return members
    }

    override fun findOne(id: Int) : MemberModel? {
        return members.find { m -> m.id == id }
    }

    override fun findSkill(skill: MemberSkill) : ArrayList<MemberModel> {
        var tempArray = ArrayList<MemberModel>()
        for(x: MemberModel in members){
            tempArray.add(x)
        }
        var outputArray = ArrayList<MemberModel>()
        var tempMemberModel = MemberModel()
        while(tempArray.find { m -> m.skills.contains(skill) } != null){
            tempMemberModel = tempArray.find { m -> m.skills.contains(skill) }!!
            outputArray.add(tempMemberModel)
            tempArray.remove(tempMemberModel)
        }
        return outputArray
    }

    override fun create(member: MemberModel) {
        member.id = getMemberId()
        members.add(member)
        //printAll()
        println(member)
        println("Member size: "+members.size)
        serialize()
    }

    override fun update(member: MemberModel) {
        var foundMember = findOne(member.id!!)
        if (foundMember != null) {
            foundMember.name = member.name
            foundMember.description = member.description
            serialize()
        }
    }

    override fun delete(member: MemberModel){
        members.remove(member)
        serialize()
    }

    internal fun printAll() {
        if(members.isEmpty()){ println("//no members found//") }
        else {
            members.forEach {
                println("$it")
            }
        }
    }

    private fun serialize() {
        val jsonString = memberGsonBuilder.toJson(members, memberListType)
//        write(MEMBERS_FILE, jsonString)
    }

    private fun deserialize() {
        //val jsonString = read(MEMBERS_FILE)
//        members = Gson().fromJson(jsonString, memberListType)
//        lastMemberId = members.size
    }
}