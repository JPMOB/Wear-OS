package com.app.shopya.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.watch.heartrate.R


class MainAdapter(
    private val context: Context,
    private val onClickListener: View.OnClickListener,
    private val optionList: ArrayList<String>
) :
    RecyclerView.Adapter<MainAdapter.ProductViewHolder>() {
    var selectedPosition = -1

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
     //   val rlMain: RelativeLayout = itemView.rlMain

       val textView: TextView = itemView.findViewById(R.id.menu_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item,
            parent, false
        )
        return ProductViewHolder(
            itemView
        )
    }


    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        try {
            selectedPosition = position
            holder.textView.tag = position
            holder.textView.setOnClickListener(onClickListener)
            holder.textView.text = optionList[position]

        } catch (e: Exception) {


        }
    }




    override fun getItemCount() = optionList.size

}
