package com.example.greetings

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
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

    var validDrop = false

    lateinit var projects: ProjectStore

    var rows: ArrayList<TableRow> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        projects = ProjectJSONStore(this)

        setContentView(R.layout.activity_main)



        ToDoRow.setOnDragListener(dragListener)
        rows.add(ToDoRow)
        DoingRow.setOnDragListener(dragListener)
        rows.add(DoingRow)
        IdleRow.setOnDragListener(dragListener)
        rows.add(IdleRow)
        DoneRow.setOnDragListener(dragListener)
        rows.add(DoneRow)

        button.setOnLongClickListener(taskDragListener)


        populateRows()
    }


    val taskDragListener = OnLongClickListener {
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

    val dragListener = OnDragListener { view, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                validDrop = false
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                button.text = "yay"
                validDrop = true
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                if(rows.contains(view)) {
                    true
                }else{
                    button.text = "bingo"
                    view.visibility = VISIBLE
                    false
                }

            }
            DragEvent.ACTION_DRAG_EXITED -> {
                view.visibility = VISIBLE
                button.text = "help"
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
                    button.text = "nope"
                    true
                } else{
                    //button.text = "yay"
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

    fun populateRows() {
        var i: Int = 0
        projects.findAll().forEach {
            i = i + 1
            when (it.state) {
                ProjectState.TODO -> {
                    //val button = Button(this)
                    val newButton = assignIcons(it)

                    newButton.text = it.title


                    ToDoRow.addView(newButton)
                }

                ProjectState.INPROGRESS -> {
                    //val button = Button(this, null, R.style.taskBlock)
                    val newButton = assignIcons(it)

                    newButton.text = it.title

                    it.state = ProjectState.DOING

                    DoingRow.addView(newButton)
                }

                ProjectState.WAITING -> {
                    //val button = Button(this, null, R.style.taskBlock)
                    val newButton = assignIcons(it)

                    newButton.text = it.title

                    IdleRow.addView(newButton)
                }

                ProjectState.DONE -> {
                    //val newButton = Button(this, null, R.style.taskBlock)
                    val newButton: Button = assignIcons(it)

                    newButton.text = it.title
                    button.text = it.title

                    var params: TableRow.LayoutParams = TableRow.LayoutParams(
                        180 * newButton.getContext().getResources().getDisplayMetrics().density.toInt(),
                        TableRow.LayoutParams.MATCH_PARENT
                    )




                    newButton.layoutParams = params



                    DoneRow.addView(newButton)

                    newButton.setOnLongClickListener(taskDragListener)
                    print("////////////////////////////////////// &it.description")
                }
            }
        }

        button.text = i.toString()
    }

    fun assignIcons(mod: ProjectModel): Button {
        when (mod.skill) {
            MemberSkill.AUDIO -> return Button(this, null, R.style.artBlock, R.style.audioBlock)

            MemberSkill.PROGRAMMING -> return Button(
                this,
                null,
                R.style.artBlock,
                R.style.progBlock
            )

            MemberSkill.DESIGN -> return Button(this, null, R.style.artBlock, R.style.designBlock)

            MemberSkill.ART -> return Button(this, null, R.style.artBlock, R.style.artBlock)
        }
    }
}
