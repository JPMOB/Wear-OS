package com.watch.heartrate.StartHR

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.wear.activity.ConfirmationActivity
import com.jjoe64.graphview.GridLabelRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.watch.heartrate.*
import com.watch.heartrate.RoomDB.HeartRateModel
import com.watch.heartrate.RoomDB.HRDatabase
import com.watch.heartrate.ViewModels.HRViewModel
import com.watch.heartrate.ViewModels.TaskViewModelFactory

import com.watch.heartrate.databinding.ActivityStartHrBinding
import java.text.SimpleDateFormat
import java.util.*


class StartHRActivity : AppCompatActivity(), View.OnClickListener {

    var handler = Handler()
    lateinit var task: Runnable
    var mHandler = Handler()
    lateinit var mTimer1: Runnable
    lateinit var mTimer2: Runnable
    var mSeries1 = LineGraphSeries<DataPoint>()
    var mSeries2 = LineGraphSeries<DataPoint>()
    var graph2LastXValue = 5.0
    var taskViewModel: HRViewModel? = null
    lateinit var mCalendar: Calendar

    private lateinit var binding: ActivityStartHrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartHrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // YourManager.getInstance(this).
        /////////////////////////////////////
        val application = requireNotNull(this).application
        val dataSource = HRDatabase.getInstance(application).HRDatabaseDAO

        //create an instance of the viewModel Factory
        val viewModelFactory = TaskViewModelFactory(dataSource, application)
        taskViewModel = ViewModelProvider(this, viewModelFactory).get(HRViewModel::class.java)
        //////////////////////////////////////////

    }


    fun addToDB(avgHR: String, time: String) {
        val heartRateModel = HeartRateModel(avgHR = avgHR, time = time)
        taskViewModel?.addTask(heartRateModel)
        Toast.makeText(this,"saved",Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        binding.btn.setOnClickListener(this)
        binding.btn.text = getString(R.string.start)
        fader()
    }

    private fun fader() {
        val animator = ObjectAnimator.ofFloat(binding.ivArrow, View.ALPHA, 0f)
        animator.repeatCount = 100
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(binding.ivArrow)
        animator.start()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }


    fun startGraph() {

        mSeries1 = LineGraphSeries(generateData())
        binding.ivGraph.addSeries(mSeries1)
        mSeries2 = LineGraphSeries()
        // binding.ivGraph.addSeries(mSeries2)
        binding.ivGraph.viewport.isXAxisBoundsManual = false
        binding.ivGraph.viewport.setMinX(0.0)
        binding.ivGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        binding.ivGraph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        binding.ivGraph.getGridLabelRenderer().gridStyle = GridLabelRenderer.GridStyle.NONE
        binding.ivGraph.viewport.setMaxX(80.0)
        mTimer1 = object : Runnable {
            override fun run() {
                mSeries1.resetData(generateData())
                mHandler.postDelayed(this, 800)
            }
        }
        mHandler.postDelayed(mTimer1, 800)

        mTimer2 = object : Runnable {
            override fun run() {
                graph2LastXValue += 1.0
                //  binding.tvHR.text = graph2LastXValue.toString()
                mSeries2.appendData(DataPoint(graph2LastXValue, getRandom()), true, 40)
                mHandler.postDelayed(this, 600)
            }
        }
        mHandler.postDelayed(mTimer2, 600)


    }

    fun getDateFromMilliseconds(): String {
        val dateFormat = "MMM dd yyyy"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()
        return formatter.format(calendar.time)
    }



    fun stopGraphAndSaveData() {
        binding.pb.visibility = View.VISIBLE
        binding.tvStatic.visibility=View.VISIBLE
        binding.tvStatic.text = getString(R.string.saving_data)
        binding.ivGraph.visibility=View.GONE
        binding.ivArrow.visibility=View.GONE
        binding.btn.visibility = View.GONE
        binding.llHR.visibility = View.GONE
        task = object : Runnable {
            override fun run() {
                //  handler.postDelayed(this, 800)
                binding.pb.visibility = View.GONE
                addToDB(binding.tvHR.text.toString(), getDateFromMilliseconds())
                val intent = Intent(this@StartHRActivity, ConfirmationActivity::class.java).apply {
                    putExtra(
                        ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION
                    )
                }
                mHandler.removeCallbacks(mTimer1);
                mHandler.removeCallbacks(mTimer2);
                startActivity(intent)
                finish()


            }
        }
        handler.postDelayed(task, 1000)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"lcjs",Toast.LENGTH_SHORT).show()
        stopGraphAndSaveData()

    }




    fun generateData(): Array<DataPoint?>? {
        val count = 30
        val values = arrayOfNulls<DataPoint>(count)
        for (i in 0 until count) {
            val x = i.toDouble()
            val f: Double = mRand.nextDouble() * 0.15 + 0.3
            val y: Double = Math.sin(i * f + 2) + mRand.nextDouble() * 0.3
            val v = DataPoint(x, y)
            values[i] = v
        }
        return values
    }

    var mLastRandom = 2.0
    var mRand: Random = Random()
    fun getRandom(): Double {
        binding.tvHR.text = java.lang.String.format(
            "%.0f", mRand.nextFloat() * 99 - 10
        )
        return mRand.nextDouble() * 99 - 10.let { mLastRandom += it; mLastRandom }
    }

    private fun scaler() {
        val ScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f)
        val ScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(binding.ivHeart, ScaleX, ScaleY)
        animator.repeatCount = 1000
        animator.repeatMode = ObjectAnimator.REVERSE
        //  animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    override fun onDestroy() {
        super.onDestroy()

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn -> {
                if (binding.btn.text == getString(R.string.stop)) {
                    binding.btn.text = getString(R.string.start)
                    stopGraphAndSaveData()


                } else {
                    binding.btn.text = getString(R.string.stop)
                    binding.tvStatic.visibility = View.GONE
                    binding.ivArrow.visibility = View.GONE
                    binding.ivGraph.visibility = View.VISIBLE
                    startGraph()
                    scaler()
                }
            }

        }
    }

}