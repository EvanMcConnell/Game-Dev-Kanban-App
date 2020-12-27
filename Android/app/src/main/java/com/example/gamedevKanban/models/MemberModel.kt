package com.example.gamedevKanban.models

data class MemberModel(var id: Int = 0,
                       var name: String = "",
                       var skills: ArrayList<MemberSkill> = ArrayList(),
                       var description: String = "")
