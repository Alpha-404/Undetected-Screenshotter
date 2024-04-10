package com.indiancoder.screenshot

import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.drawable.Icon
import android.net.Uri
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.Thread.sleep
import java.time.Instant
import java.util.Date


class QSTileService: TileService() {

    data class StateModel(val enabled: Boolean, val label: String, val icon: Icon)

    // Called when the user adds your tile.
    override fun onTileAdded() {
        super.onTileAdded()
    }
    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening();
        // val state = getStateFromService()
        qsTile.label = "Hidden Screenshot"
        qsTile.contentDescription = "Take undetected screenshot"
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.icon = Icon.createWithResource(this, R.drawable.baseline_screenshot_24)
        qsTile.updateTile()
    }

    // Called when your app can no longer update your tile.
    override fun onStopListening() {
        super.onStopListening()
    }

    // Called when the user taps on your tile in an active or inactive state.
    override fun onClick() {
        super.onClick()
        val dialog = Dialog(applicationContext)
        showDialog(dialog)
        dialog.dismiss()
        //Toast.makeText(this, "Taking Screenshot...", Toast.LENGTH_SHORT).show()
        Log.d("t", "sleep 3 secs")
        sleep(3000)
        Log.d("t", "take ss")
        Log.d("t", (Date.from(Instant.now()).toString() + ".png"))
        //Runtime.getRuntime().exec("/system/bin/screencap -p /sdcard/Download/$(date +%s).png")
        val timestamp = System.currentTimeMillis() / 1000
        try {
            val command = "su -c /system/bin/screencap -p /sdcard/Download/$timestamp.png"
            Runtime.getRuntime().exec(command)

            /*
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage("/sdcard/Download/$timestamp.png")
                .setTitle("Screenshot Success!")
                .setPositiveButton("Share") { dialog, which ->
                    val shareIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        // Example: content://com.google.android.apps.photos.contentprovider/...
                        putExtra(Intent.EXTRA_STREAM, "file:///sdcard/Download/$timestamp.png")
                        type = "image/png"
                    }
                    startActivity(Intent.createChooser(shareIntent, null))
                }
                .setNegativeButton("Open") { dialog, which ->
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(Uri.parse("file://" + "/sdcard/Download/$timestamp.png"), "image/png")
                    startActivity(intent)
                }
            val dialog: AlertDialog = builder.create()
            dialog.show()
             */
        } catch(e: Exception) {
            e.printStackTrace()
            File("ERROR_$timestamp.txt").printWriter().use { out ->
                out.println(e.printStackTrace())
            }
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, Uri.parse("/sdcard/Download/$timestamp.png"))
            putExtra(Intent.EXTRA_TITLE, "Share screenshot")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, // context parameter, provide the context instance here
            0, // requestCode, you can use 0 or any other unique code
            Intent.createChooser(shareIntent, "Share Image"), // intent
            PendingIntent.FLAG_IMMUTABLE // flags
        )

        startActivityAndCollapse(pendingIntent)
    }

    // Called when the user removes your tile.
    override fun onTileRemoved() {
        super.onTileRemoved()
    }
}
