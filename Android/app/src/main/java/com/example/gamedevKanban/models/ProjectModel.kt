package com.example.gamedevKanban.models


data class ProjectModel(var id: Int = 0,
                        var title: String = "",
                        var skill: MemberSkill,
                        //var members: ArrayList<MemberModel> = ArrayList(),
                        var description: String = "",
                        var state: ProjectState = ProjectState.TODO
)
