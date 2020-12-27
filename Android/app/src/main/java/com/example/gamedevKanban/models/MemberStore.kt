package com.example.gamedevKanban.models

interface MemberStore {
    fun findAll(): List<MemberModel>
    fun findSkill(skill: MemberSkill): ArrayList<MemberModel>
    fun findOne(id: Int): MemberModel?
    fun create(member: MemberModel)
    fun update(member: MemberModel)
    fun delete(member: MemberModel)
}