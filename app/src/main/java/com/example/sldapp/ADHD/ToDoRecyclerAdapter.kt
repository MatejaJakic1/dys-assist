package com.example.sldapp.ADHD

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sldapp.R


class ToDoRecyclerAdapter(private var items: List<ToDoList>, private val choiceListener: TimerToListListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ToDoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adhd_recycler, parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ToDoViewHolder -> {
                holder.bind(items[position], choiceListener)
            }
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }


    class ToDoViewHolder constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        private val taskTitle: TextView =
            itemView.findViewById(R.id.text_adhd_recycler_task)
        private val rating: RatingBar =
            itemView.findViewById(R.id.rating_adhd_recycler)

        fun bind(todos: ToDoList, choiceListener: TimerToListListener) {
            taskTitle.text = todos.toDoName
            rating.rating = todos.pomodoroCount.toFloat()
            taskTitle.setOnClickListener {
                choiceListener.onChoose(todos.toDoName, todos.pomodoroCount)
            }
        }
    }
}