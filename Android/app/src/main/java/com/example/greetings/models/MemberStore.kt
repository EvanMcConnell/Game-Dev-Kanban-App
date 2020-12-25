package com.example.greetings.models

interface MemberStore {
    fun findAll(): List<MemberModel>
    fun findSkill(skill: MemberSkill): ArrayList<MemberModel>
    fun findOne(id: Int): MemberModel?
    fun create(member: MemberModel)
    fun update(memeber: MemberModel)
    fun delete(member: MemberModel)
}