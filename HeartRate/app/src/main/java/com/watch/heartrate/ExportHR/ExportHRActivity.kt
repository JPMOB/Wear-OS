package com.watch.heartrate.ExportHR

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.watch.heartrate.*
import com.watch.heartrate.RoomDB.HeartRateModel
import com.watch.heartrate.RoomDB.HRDatabase
import com.watch.heartrate.ViewModels.HRViewModel
import com.watch.heartrate.ViewModels.TaskViewModelFactory
import com.watch.heartrate.databinding.ActivityExportHrBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ExportHRActivity : AppCompatActivity(), View.OnClickListener {


    private val PERMISSION_REQUEST_CODE = 100

    var handler = Handler()
    lateinit var task: Runnable


    private lateinit var binding: ActivityExportHrBinding
    var taskViewModel: HRViewModel? = null
    var HRData: ArrayList<HeartRateModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportHrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val application = requireNotNull(this).application
        val dataSource = HRDatabase.getInstance(application).HRDatabaseDAO

        //create an instance of the viewModel Factory
        val viewModelFactory = TaskViewModelFactory(dataSource, application)
        taskViewModel = ViewModelProvider(this, viewModelFactory).get(HRViewModel::class.java)
        taskViewModel!!.tasks?.observe(this, Observer {
            //  binding!!.tvTasks.text = formatTasks(it)
            HRData = ArrayList(it)
            // HRData?.addAll(it)
        }
        )
    }

    fun clearDB() {
        taskViewModel?.deleteAllTask()
    }


    override fun onResume() {
        super.onResume()
        binding.btn.setOnClickListener(this)
        binding.btn.text = getString(R.string.Confirm)

    }


    fun startExporting() {
        binding.pb.visibility = View.VISIBLE
        binding.tvStatic.text = getString(R.string.exporting_data)
        binding.btn.visibility = View.GONE
        binding!!.mainLayout.setBackground(
            resources.getDrawable(
                R.drawable.bg_border_outline_activity,
                null
            )
        )

        task = object : Runnable {
            override fun run() {
                createFile()

            }
        }
        handler.postDelayed(task, 1000)

    }

    fun createFile() {

        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            var data = ""
            if (checkPermission()) {
                val sdcard = Environment.getExternalStorageDirectory()
                val dir = File(sdcard.absolutePath + "/HRData/")
                dir.mkdir()
                val file = File(dir, "sample.csv")
                var os: FileOutputStream? = null
                try {
                    os = FileOutputStream(file)
                    for (i in HRData!!) {
                      //  Toast.makeText(this@ExportHRActivity, HRData!!.size.toString(), Toast.LENGTH_SHORT).show()
                        data = "" + i + "\n"
                    }
                    os.write(data.toString().toByteArray())
                    os.close()
                    clearDB()
                    HRData!!.clear()

                    binding.pb.visibility = View.GONE
                    binding.tvStatic.text = getString(R.string.exported_successfully)
                    binding.ivCardio.visibility = View.VISIBLE
                    binding!!.mainLayout.setBackground(
                        resources.getDrawable(
                            R.drawable.bg_border_outline_black_activity,
                            null
                        )
                    )

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                requestPermission() // Code for permission
            }
        }

    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Write External Storage permission allows us to create files. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn -> {
                if (HRData.isNullOrEmpty()) {
                    Toast.makeText(this@ExportHRActivity, "No data to export", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    startExporting()
                }


            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .")
                createFile()
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .")
            }
        }
    }
}
