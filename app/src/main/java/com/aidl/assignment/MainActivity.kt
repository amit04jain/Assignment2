package com.aidl.assignment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var imuData: TextView
    private var remoteServicesInterface: RemoteServicesInterface? = null
    private var serviceConnection: ServiceConnection? = null
    private var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imuData = findViewById(R.id.imuData)

        addObserver()
        bindService()
    }

    private fun addObserver() {
        RotationServices.sensorData.observe(this, Observer {
            imuData.setText(it.contentToString())
        })
    }

    private fun getDataFromAIDL() {
        remoteServicesInterface?.orientation()?.let {
            imuData.text = it
        }
    }

    private fun bindService() {
        serviceIntent = Intent(this, RotationServices::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                remoteServicesInterface = RemoteServicesInterface.Stub.asInterface(binder)
                getDataFromAIDL()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
            }
        }
        serviceIntent?.let {
            serviceConnection?.let {
                bindService(
                    serviceIntent,
                    it,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }
}