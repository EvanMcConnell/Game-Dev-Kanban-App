package com.example.gamedevKanban.models

interface ProjectStore {
    fun findAll(): List<ProjectModel>
    fun findState(state: ProjectState): ArrayList<ProjectModel>
    fun findSkill(skill: MemberSkill): ArrayList<ProjectModel>
    fun findOne(id: Int): ProjectModel?
    fun create(project: ProjectModel)
    fun update(project: ProjectModel)
    fun delete(id: Int)
}