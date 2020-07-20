package top.ilum.pea.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import top.ilum.pea.R

class DialogSelection : DialogFragment() {

    interface OnInputListener {
        fun sendInput(input: Int)
    }

    var onInputListener: OnInputListener? = null

    private val newsCategories = arrayOf("Your money", "Business")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.select_category)
                .setSingleChoiceItems(
                    newsCategories, -1
                ) { dialog, item ->
                    onInputListener!!.sendInput(item)
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) {
                    dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onInputListener = targetFragment as OnInputListener
    }
}
