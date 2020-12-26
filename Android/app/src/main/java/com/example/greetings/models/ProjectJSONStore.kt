package com.example.greetings.models

import android.content.Context
import com.example.greetings.helpers.exists
import com.example.greetings.helpers.read
import com.example.greetings.helpers.write
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*

val JSON_FILE = "projects.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<ProjectModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ProjectJSONStore : ProjectStore {

    val context: Context
    var projects = mutableListOf<ProjectModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        } else serialize()
    }

    override fun findState(state: ProjectState) : ArrayList<ProjectModel> {
        var tempArray = ArrayList<ProjectModel>()
        for(x: ProjectModel in projects){
            tempArray.add(x)
        }
        var outputArray = ArrayList<ProjectModel>()
        var tempProjectModel: ProjectModel
        while(tempArray.find { p -> p.state == state } != null){
            tempProjectModel = tempArray.find { p -> p.state == state }!!
            outputArray.add(tempProjectModel)
            tempArray.remove(tempProjectModel)
        }
        return outputArray
    }

    override fun findSkill(skill: MemberSkill) : ArrayList<ProjectModel> {
        var tempArray = ArrayList<ProjectModel>()
        for(x: ProjectModel in projects){
            tempArray.add(x)
        }
        var outputArray = ArrayList<ProjectModel>()
        var tempProjectModel: ProjectModel
        while(tempArray.find { p -> p.skill == skill } != null){
            tempProjectModel = tempArray.find { p -> p.skill == skill }!!
            outputArray.add(tempProjectModel)
            tempArray.remove(tempProjectModel)
        }
        return outputArray
    }

    override fun findOne(id: Int) : ProjectModel? {
        return projects.find { p -> p.id == id }
    }

    override fun findAll(): MutableList<ProjectModel> {
        return projects
    }

    override fun create(project: ProjectModel) {
        //project.id = generateRandomId()
        projects.add(project)
        serialize()
    }


    override fun update(project: ProjectModel) {
        // todo
    }

    override fun delete(project: ProjectModel) {}

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(projects, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        projects = Gson().fromJson(jsonString, listType)
    }
}