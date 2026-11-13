package com.louiskirsch.quickdynalist.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import java.util.concurrent.Executors
import java.util.concurrent.Future

// Local replacements for the small set of helpers previously supplied by Anko.
private val backgroundExecutor = Executors.newFixedThreadPool(4)
private val mainHandler = Handler(Looper.getMainLooper())

fun doAsync(task: () -> Unit): Future<*> = backgroundExecutor.submit {
    try { task() } catch (error: Exception) {
        Log.e("QuickDynalist", "Background operation failed", error)
    }
}

fun uiThread(task: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) task()
    else mainHandler.post { task() }
}

fun Context.toast(message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).also { it.show() }
fun Context.toast(message: CharSequence): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).also { it.show() }
fun Context.longToast(message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_LONG).also { it.show() }

fun Context.browse(url: String): Boolean = try {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    true
} catch (error: android.content.ActivityNotFoundException) { false }

fun <T : View> View.find(id: Int): T = findViewById<T>(id)
    ?: throw IllegalArgumentException("View $id was not found")

class NativeAlert(context: Context) {
    internal val builder = AlertDialog.Builder(context)
    var titleResource: Int = 0
        set(value) { field = value; builder.setTitle(value) }
    var messageResource: Int = 0
        set(value) { field = value; builder.setMessage(value) }
    fun show(): AlertDialog = builder.show()
}
fun Context.alert(configure: NativeAlert.() -> Unit): NativeAlert =
    NativeAlert(this).apply(configure)
fun NativeAlert.okButton(action: (DialogInterface) -> Unit) {
    builder.setPositiveButton(android.R.string.ok) { dialog, _ -> action(dialog) }
}
