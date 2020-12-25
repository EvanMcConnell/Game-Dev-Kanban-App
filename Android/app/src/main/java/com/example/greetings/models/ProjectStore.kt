package com.example.greetings.models

interface ProjectStore {
    fun findAll(): List<ProjectModel>
    fun findState(state: ProjectState): ArrayList<ProjectModel>
    fun findOne(id: Int): ProjectModel?
    fun create(project: ProjectModel)
    fun update(project: ProjectModel)
    fun delete(project: ProjectModel)
}