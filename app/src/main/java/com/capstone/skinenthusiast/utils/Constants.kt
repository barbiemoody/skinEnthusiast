package com.capstone.skinenthusiast.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.capstone.skinenthusiast.ui.splash.SplashActivity

object Constants {
    fun showUnauthorized(activity: Activity, onLogOut: () -> Unit) {
        val builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        builder.setTitle("Unauthorized")
        builder.setMessage("Your session is expired! Plese Sign In.")

        builder.setPositiveButton("OK") { dialog, _ ->
            val iSplash = Intent(activity, SplashActivity::class.java)
            onLogOut.invoke()
            activity.startActivity(iSplash)
            activity.finishAffinity()
            dialog.dismiss()
        }

        builder.show()
    }
}