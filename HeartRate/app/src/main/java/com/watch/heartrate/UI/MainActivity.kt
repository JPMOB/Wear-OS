package com.watch.heartrate.UI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.app.shopya.adapter.MainAdapter
import com.watch.heartrate.ExportHR.ExportHRActivity
import com.watch.heartrate.R
import com.watch.heartrate.StartHR.StartHRActivity
import com.watch.heartrate.databinding.ActivityMainBinding

class MainActivity : Activity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    var onClickListener: View.OnClickListener? = null

    private lateinit var actionList: List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickListener = this
        setMenuItems()
    }

    private fun setMenuItems() {
        actionList = ArrayList<String>()

        (actionList as ArrayList<String>).add(getString(R.string.action_measure_hr))
        (actionList as ArrayList<String>).add(getString(R.string.action_export_hr))

        adapter = MainAdapter(this, onClickListener!!, actionList as ArrayList<String>)
        //  setAmbientEnabled()
        binding.wearableRecyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            isCircularScrollingGestureEnabled = true
            layoutManager = WearableLinearLayoutManager(this@MainActivity)
        }
        binding.wearableRecyclerView.adapter = adapter

        val MAX_ICON_PROGRESS = 0.65f

        class CustomScrollingLayoutCallback : WearableLinearLayoutManager.LayoutCallback() {

            private var progressToCenter: Float = 0f

            override fun onLayoutFinished(child: View, parent: RecyclerView) {
                child.apply {
                    val centerOffset = height.toFloat() / 2.0f / parent.height.toFloat()
                    val yRelativeToCenterOffset = y / parent.height + centerOffset
                    progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset)
                    progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS)
                    scaleX = 1 - progressToCenter
                    scaleY = 1 - progressToCenter
                }
            }
        }

        binding.wearableRecyclerView.layoutManager =
            WearableLinearLayoutManager(this, CustomScrollingLayoutCallback())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu_item -> {
                val position = v.tag as Int
                when (actionList[position]) {
                    getString(R.string.action_measure_hr) -> {
                        val intent = Intent(this, StartHRActivity::class.java)
                        startActivity(intent)
                    }
                    getString(R.string.action_export_hr) -> {
                        val intent = Intent(this, ExportHRActivity::class.java)
                        startActivity(intent)
                    }

                }

            }

        }
    }

}