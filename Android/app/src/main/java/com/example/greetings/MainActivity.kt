package com.example.greetings

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //KanbanScrollView.setOnDragListener(dragListener)
        ToDoTopRow.setOnDragListener(dragListener)
        ToDoBottomRow.setOnDragListener(dragListener)
        DoingTopRow.setOnDragListener(dragListener)
        DoingBottomRow.setOnDragListener(dragListener)
        IdleTopRow.setOnDragListener(dragListener)
        IdleBottomRow.setOnDragListener(dragListener)
        DoneTopRow.setOnDragListener(dragListener)
        DoneBottomRow.setOnDragListener(dragListener)

        button.setOnLongClickListener{
            val clipText = "Test"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)

            val dragShadowBuilder = DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

            it.visibility = View.INVISIBLE
            true
        }
    }

    val dragListener = OnDragListener{view, event->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                //Toast.makeText(this, dragData, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, "Destination: $view", Toast.LENGTH_LONG).show()
                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout

                destination.addView(v)
                v.visibility = VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }
}
