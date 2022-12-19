package com.home.myweather.contentProvider

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.home.myweather.R
import com.home.myweather.REQUEST_CODE
import com.home.myweather.databinding.FragmentContentProviderContactsBinding
import com.home.myweather.view.BaseFragment

class ContentProviderContactsFragment :
    BaseFragment<FragmentContentProviderContactsBinding>(FragmentContentProviderContactsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getContacts()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> showDialog()
                else -> {
                    myRequestPermission()
                }
            }
        }
    }

    private fun myRequestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            when {
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> getContacts()
                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> showDialog()
                else -> Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.read_contacts)
            .setMessage(R.string.access_contacts)
            .setPositiveButton(R.string.ok) { _, _ -> myRequestPermission() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    @SuppressLint("Range")
    fun getContacts() {
        context?.let { it ->
            val contentResolver = it.contentResolver
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
            cursor?.let { cursor ->
                for (i in 0 until cursor.count) {
                    cursor.moveToPosition(i)
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    addView(name)
                }
            }
            cursor?.close()
        }
    }

    private fun addView(name: String) {
        binding.containerForContacts.addView(TextView(requireContext()).apply {
            text = name
            textSize = 30f
        })
        binding.containerForContacts.setOnClickListener {

        }
    }

    companion object {
        fun newInstance() = ContentProviderContactsFragment()
    }
}