package com.example.gamedevKanban

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.marginRight
import com.example.gamedevKanban.models.*
import com.example.greetings.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var backButtonPresses = 0
    var currentScreen: Int = 0

    var nextEmpyTaskID: Int = 0

    var selectedTaskID: Int = 0

    var selectedAddSkill: MemberSkill = MemberSkill.PROGRAMMING
    lateinit var selectedAddSkillButton: Button
    var selectedAddState: ProjectState = ProjectState.TODO
    lateinit var selectedAddStateButton: Button

    var draggedView: ArrayList<View> = ArrayList()

    var refineButtons: ArrayList<Button> = ArrayList()
    var currentRefine: Int = 0

    var validDrop = false

    lateinit var projects: ProjectStore

    var tables: ArrayList<HorizontalScrollView> = ArrayList()
    var rows: ArrayList<TableRow> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        projects = ProjectJSONStore(this)

        setContentView(R.layout.activity_main)

        deleteTaskButton.setOnClickListener(openDeletionConfirmationLayout())


        declineDeletionButton.setOnClickListener(closeDeletionConfirmationLayout())
        confirmDeleteionButton.setOnClickListener(deleteTask())

        confirmDeletionLayout.visibility = INVISIBLE

        viewAllButton.setOnClickListener(viewAllTasks())
        refineButtons.add(viewAllButton)
        viewProgButton.setOnClickListener(refineRows(MemberSkill.PROGRAMMING))
        refineButtons.add(viewProgButton)
        viewDesignButton.setOnClickListener(refineRows(MemberSkill.DESIGN))
        refineButtons.add(viewDesignButton)
        viewAudioButton.setOnClickListener(refineRows(MemberSkill.AUDIO))
        refineButtons.add(viewAudioButton)
        viewArtButton.setOnClickListener(refineRows(MemberSkill.ART))
        refineButtons.add(viewArtButton)


        ToDoRow.setOnDragListener(dragListener)
        rows.add(ToDoRow)
        DoingRow.setOnDragListener(dragListener)
        rows.add(DoingRow)
        IdleRow.setOnDragListener(dragListener)
        rows.add(IdleRow)
        DoneRow.setOnDragListener(dragListener)
        rows.add(DoneRow)

        tables.add(ToDoScrollView)
        tables.add(DoingScrollView)
        tables.add(IdleScrollView)
        tables.add(DoneScrollView)

        openAddTaskLayoutButton.setOnClickListener(
            openAddTaskMenu()
        )

        closeDetailLayoutButton.setOnClickListener(
            closeDetailLayout()
        )

        selectedAddStateButton = addToDoChoice
        addToDoChoice.setOnClickListener(
            setDetailLayoutSelectedState(
                addToDoChoice,
                ProjectState.TODO
            )
        )
        addDoingChoice.setOnClickListener(
            setDetailLayoutSelectedState(
                addDoingChoice,
                ProjectState.DOING
            )
        )
        addIdleButton.setOnClickListener(
            setDetailLayoutSelectedState(
                addIdleButton,
                ProjectState.IDLE
            )
        )
        addDoneChoice.setOnClickListener(
            setDetailLayoutSelectedState(
                addDoneChoice,
                ProjectState.DONE
            )
        )

        selectedAddSkillButton = addProgChoice
        addProgChoice.setOnClickListener(
            setDetailLayoutSelectedSkill(
                addProgChoice,
                MemberSkill.PROGRAMMING
            )
        )
        addDesignChoice.setOnClickListener(
            setDetailLayoutSelectedSkill(
                addDesignChoice,
                MemberSkill.DESIGN
            )
        )
        addAudioButton.setOnClickListener(
            setDetailLayoutSelectedSkill(
                addAudioButton,
                MemberSkill.AUDIO
            )
        )
        addArtChoice.setOnClickListener(
            setDetailLayoutSelectedSkill(
                addArtChoice,
                MemberSkill.ART
            )
        )



        populateRows()
    }

    override fun onBackPressed() {
        when (currentScreen) {
            0 -> {
                backButtonPresses++
                if (backButtonPresses == 1) {
                    Toast.makeText(this, "Press Back Button Again to Exit", Toast.LENGTH_LONG)
                        .show()
                } else if (backButtonPresses == 2) {
                    finishAffinity()
                }
            }
            1, 2 -> {
                toggleMenu(
                    detailLayout,
                    openAddTaskLayoutButton
                )
                currentScreen = 0
            }
            3 -> {
                toggleMenu(
                    confirmDeletionLayout,
                    detailLayout
                )
                currentScreen = 2
            }
        }
    }

    private fun toggleMenu(thisView: View, newView: View) {
        thisView.visibility = INVISIBLE
        newView.visibility = VISIBLE
    }

    private fun closeDetailLayout() = OnClickListener {
        toggleMenu(
            detailLayout,
            openAddTaskLayoutButton
        )
    }

    private fun closeDeletionConfirmationLayout() = OnClickListener {
        toggleMenu(
            confirmDeletionLayout,
            detailLayout
        )
    }

    private fun openDeletionConfirmationLayout() = OnClickListener {
        currentScreen = 3

        toggleMenu(
            detailLayout,
            confirmDeletionLayout
        )
    }

    private fun openAddTaskMenu() = OnClickListener {
        backButtonPresses = 0
        currentScreen = 1

        toggleMenu(openAddTaskLayoutButton, detailLayout)

        deleteTaskButton.visibility = GONE

        acceptChangesButton.text = "Add Task"
        acceptChangesButton.setOnClickListener(createNewTask())
    }

    private fun openViewTaskMenu(task: ProjectModel) = OnClickListener {
        backButtonPresses = 0
        currentScreen = 2

        selectedTaskID = task.id

        toggleMenu(openAddTaskLayoutButton, detailLayout)

        titleTextField.setText(task.title)


        when (task.skill) {
            MemberSkill.PROGRAMMING -> addProgChoice.performClick()
            MemberSkill.DESIGN -> addDesignChoice.performClick()
            MemberSkill.AUDIO -> addAudioButton.performClick()
            MemberSkill.ART -> addArtChoice.performClick()
        }
        when (task.state) {
            ProjectState.TODO -> addToDoChoice.performClick()
            ProjectState.DOING -> addDoingChoice.performClick()
            ProjectState.IDLE -> addIdleButton.performClick()
            ProjectState.DONE -> addDoneChoice.performClick()
        }

        descriptionTextField.setText(task.description)

        acceptChangesButton.visibility = VISIBLE
        deleteTaskButton.visibility = VISIBLE

        acceptChangesButton.text = "Save Changes"
        acceptChangesButton.setOnClickListener(updateTask())
    }

    private fun setDetailLayoutSelectedState(thisButton: Button, newState: ProjectState) =
        OnClickListener {
            if (thisButton == selectedAddStateButton) {
                return@OnClickListener
            }

            thisButton.background = thisButton.context.getDrawable(R.drawable.taskblock_red)

            selectedAddStateButton.background =
                selectedAddStateButton.context.getDrawable(R.drawable.taskblock_dark)

            selectedAddStateButton = thisButton

            selectedAddState = newState
        }

    private fun setDetailLayoutSelectedSkill(thisButton: Button, newSkill: MemberSkill) =
        OnClickListener {
            if (thisButton == selectedAddSkillButton) {
                return@OnClickListener
            }

            thisButton.background = thisButton.context.getDrawable(R.drawable.taskblock_red)

            selectedAddSkillButton.background =
                selectedAddSkillButton.context.getDrawable(R.drawable.taskblock_dark)

            selectedAddSkillButton = thisButton

            selectedAddSkill = newSkill

//            Toast.makeText(this, newSkill.name, Toast.LENGTH_SHORT).show()
        }

    private fun taskDragListener(task: ProjectModel) = OnLongClickListener {
        val clipText = "Test"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)

        val dragShadowBuilder = DragShadowBuilder(it)
        it.startDragAndDrop(data, dragShadowBuilder, it, 0)

        //Toast.makeText(this, task.title, Toast.LENGTH_SHORT).show()
        selectedTaskID = task.id

        draggedView.add(it)

        it.visibility = INVISIBLE



        true
    }

    private val dragListener = OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                validDrop = false
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                validDrop = true
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                if (rows.contains(view)) {
                    true
                } else {
                    view.visibility = VISIBLE
                    false
                }

            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.visibility = VISIBLE
                validDrop = false
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                //Toast.makeText(this, "Destination: $view", Toast.LENGTH_LONG).show()
                view.invalidate()

                if (rows.contains(view)) {

                    val v = event.localState as View
                    val owner = v.parent as ViewGroup

                    val destination = view as TableRow

                    draggedView.removeAt(0)
                    owner.removeView(v)


                    destination.addView(v)
                    v.visibility = VISIBLE

                    updateStateAfterDrop(view)
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                //Toast.makeText(this, "Destination: $view", Toast.LENGTH_SHORT).show()
                if (draggedView.size != 0) {
                    draggedView[0].visibility = VISIBLE
                    draggedView.removeAt(0)
                }
                view.invalidate()
                true
            }
            else -> false
        }
    }


    private fun updateStateAfterDrop(v: View) {
        var newState: ProjectState = ProjectState.TODO
        when (v) {
            rows[1] -> newState = ProjectState.DOING
            rows[2] -> newState = ProjectState.IDLE
            rows[3] -> newState = ProjectState.DONE
        }

        val taskCopy: ProjectModel = projects.findOne(selectedTaskID)!!

        projects.delete(selectedTaskID)

        projects.create(
            ProjectModel(
                taskCopy.id,
                taskCopy.title,
                taskCopy.skill,
                taskCopy.description,
                newState
            )
        )
    }


    private fun refineRows(searchSkill: MemberSkill) = OnClickListener {
        rows.forEach { it.removeAllViews() }

        when (searchSkill) {
            MemberSkill.PROGRAMMING -> {
                refineButtons[currentRefine].background =
                    refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 1
                refineButtons[currentRefine].background =
                    refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
            }
            MemberSkill.DESIGN -> {
                refineButtons[currentRefine].background =
                    refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 2
                refineButtons[currentRefine].background =
                    refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
            }
            MemberSkill.AUDIO -> {
                refineButtons[currentRefine].background =
                    refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 3
                refineButtons[currentRefine].background =
                    refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
            }
            MemberSkill.ART -> {
                refineButtons[currentRefine].background =
                    refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 4
                refineButtons[currentRefine].background =
                    refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
            }
        }

        projects.findSkill(searchSkill).forEach {
            if (it.id >= nextEmpyTaskID) nextEmpyTaskID = it.id + 1

            when (it.state) {
                ProjectState.TODO -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    ToDoRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.DOING -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    DoingRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.IDLE -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.DONE -> {
                    val newButton = Button(this)
                    //var newButton:Button = assignIcons(it)

                    newButton.text = it.title


                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    //params.topMargin = 35 * newButton.context.resources.displayMetrics.density.toInt()
                    //params.setMargins(0, 50, 0, 0)

                    //newButton.setCompoundDrawables(null, newButton.context.resources.getDrawable(this, R.drawable.ic_palette)), null, null)
                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    DoneRow.addView(newButton)

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }
            }
        }
    }

    private fun populateRows() {
        rows.forEach { it.removeAllViews() }

        projects.findAll().forEach {
            if (it.id >= nextEmpyTaskID) nextEmpyTaskID = it.id + 1

            when (it.state) {
                ProjectState.TODO -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }


                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    ToDoRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.DOING -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    DoingRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.IDLE -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }

                ProjectState.DONE -> {
                    val newButton = Button(this)
                    //var newButton:Button = assignIcons(it)

                    newButton.text = it.title


                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )

                    params.setMargins(
                        0,
                        0,
                        10 * newButton.context.resources.displayMetrics.density.toInt(),
                        0
                    )

                    //params.topMargin = 35 * newButton.context.resources.displayMetrics.density.toInt()
                    //params.setMargins(0, 50, 0, 0)

                    //newButton.setCompoundDrawables(null, newButton.context.resources.getDrawable(this, R.drawable.ic_palette)), null, null)
                    when (it.skill) {
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bulb,
                            0,
                            0
                        )
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_bracket,
                            0,
                            0
                        )
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_note,
                            0,
                            0
                        )
                        MemberSkill.ART -> newButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            R.drawable.ic_palette,
                            0,
                            0
                        )
                    }
                    DoneRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener(it))
                    newButton.setOnClickListener(openViewTaskMenu(it))
                }
            }
        }


    }

    private fun viewAllTasks() = OnClickListener {
        refineButtons[0].background =
            refineButtons[0].context.getDrawable(R.drawable.taskblock_dark)
        refineButtons[currentRefine].background =
            refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
        currentRefine = 0

        populateRows()
    }

    private fun createNewTask() = OnClickListener {

        if (titleTextField.text.isEmpty()) {
            Toast.makeText(this, "Please Provide a Title", Toast.LENGTH_LONG).show()
        } else if (descriptionTextField.text.isEmpty()) {
            Toast.makeText(this, "Please Provide a Description", Toast.LENGTH_LONG).show()
        } else {

            projects.create(
                ProjectModel(
                    nextEmpyTaskID,
                    titleTextField.text.toString(),
                    selectedAddSkill,
                    descriptionTextField.text.toString(),
                    selectedAddState
                )
            )


            toggleMenu(
                detailLayout,
                openAddTaskLayoutButton
            )

            Toast.makeText(this, "New Task Created", Toast.LENGTH_LONG).show()

//        when (currentRefine) {
//            0 -> {
//                populateRows()
//            }
//            1 -> {
//                refineRows(MemberSkill.PROGRAMMING)
//            }
//            2 -> {
//                refineRows(MemberSkill.DESIGN)
//            }
//            3 -> {
//                refineRows(MemberSkill.AUDIO)
//            }
//            4 -> {
//                refineRows(MemberSkill.ART)
//            }
//        }
        }
        resetMainScreen()
    }

    private fun deleteTask() = OnClickListener {
        projects.delete(selectedTaskID)

        toggleMenu(
            confirmDeletionLayout,
            openAddTaskLayoutButton
        )

        resetMainScreen()
    }

    private fun updateTask() = OnClickListener {
        projects.delete(selectedTaskID)

        projects.create(
            ProjectModel(
                selectedTaskID,
                titleTextField.text.toString(),
                selectedAddSkill,
                descriptionTextField.text.toString(),
                selectedAddState
            )
        )

        toggleMenu(
            detailLayout,
            openAddTaskLayoutButton
        )
        resetMainScreen()
    }

    private fun resetMainScreen() {
        refineButtons[currentRefine].background =
            refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
        currentRefine = 0
        refineButtons[currentRefine].background =
            refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock_dark)

        populateRows()
    }
}
