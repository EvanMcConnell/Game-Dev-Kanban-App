package com.example.greetings

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.Toast
import com.example.greetings.models.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var draggedView:ArrayList<View> = ArrayList()

    var refineButtons:ArrayList<Button> = ArrayList()
    var currentRefine:Int = 0

    var validDrop = false

    lateinit var projects: ProjectStore

    var rows: ArrayList<TableRow> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        projects = ProjectJSONStore(this)

        setContentView(R.layout.activity_main)


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


        populateRows()
    }


    private val taskDragListener = OnLongClickListener {
        val clipText = "Test"
        val item = ClipData.Item(clipText)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(clipText, mimeTypes, item)

        val dragShadowBuilder = DragShadowBuilder(it)
        it.startDragAndDrop(data, dragShadowBuilder, it, 0)

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
                if(rows.contains(view)) {
                    true
                }else{
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
                //val item = event.clipData.getItemAt(0)
                //val dragData = item.text
                //Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Destination: $view", Toast.LENGTH_LONG).show()
                view.invalidate()




                if(rows.contains(view)) {

                    val v = event.localState as View
                    val owner = v.parent as ViewGroup

                    val destination = view as LinearLayout

                    draggedView.removeAt(0)
                    owner.removeView(v)


                    destination.addView(v)
                    v.visibility = VISIBLE
                    true
                } else{
                    true
                }
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                if(draggedView.size != 0){
                    draggedView[0].visibility = VISIBLE
                    draggedView.removeAt(0)
                }
                view.invalidate()
                true
            }
            else -> false
        }
    }


    private fun refineRows(searchSkill: MemberSkill) = OnClickListener{
        rows.forEach{ it.removeAllViews() }

        when(searchSkill){
            MemberSkill.PROGRAMMING -> {
                refineButtons[1].background = refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
                refineButtons[currentRefine].background = refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 1
            }
            MemberSkill.DESIGN -> {
                refineButtons[2].background = refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
                refineButtons[currentRefine].background = refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 2
            }
            MemberSkill.AUDIO -> {
                refineButtons[3].background = refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
                refineButtons[currentRefine].background = refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 3
            }
            MemberSkill.ART -> {
                refineButtons[4].background = refineButtons[1].context.getDrawable(R.drawable.taskblock_dark)
                refineButtons[currentRefine].background = refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
                currentRefine = 4
            }
        }

        projects.findSkill(searchSkill).forEach {
            when (it.state) {
                ProjectState.TODO -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }

                ProjectState.DOING -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    DoingRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }

                ProjectState.IDLE -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
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
                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    DoneRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }
            }
        }
    }

    private fun populateRows() {
        var i = 0
        projects.findAll().forEach {
            i++
            when (it.state) {
                ProjectState.TODO -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }


                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }

                ProjectState.DOING -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    DoingRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }

                ProjectState.IDLE -> {
                    val newButton = Button(this)
                    //val newButton = assignIcons(it)

                    newButton.text = it.title

                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    newButton.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.context.resources.displayMetrics.density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )


                    IdleRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
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
                    when(it.skill){
                        MemberSkill.DESIGN -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bulb, 0, 0)
                        MemberSkill.PROGRAMMING -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bracket, 0, 0)
                        MemberSkill.AUDIO -> newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0)
                        MemberSkill.ART ->newButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_palette, 0, 0)
                    }
                    DoneRow.addView(newButton)

                    newButton.layoutParams = params
                    newButton.background = newButton.context.getDrawable(R.drawable.taskblock)

                    newButton.setOnLongClickListener(taskDragListener)
                }
            }
        }

    }

    private fun viewAllTasks() = OnClickListener {
        refineButtons[0].background = refineButtons[0].context.getDrawable(R.drawable.taskblock_dark)
        refineButtons[currentRefine].background = refineButtons[currentRefine].context.getDrawable(R.drawable.taskblock)
        currentRefine = 0
        rows.forEach{ it.removeAllViews() }
        populateRows()
    }
}
