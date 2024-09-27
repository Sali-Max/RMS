package com.android.oms

import SmsAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.Manifest
import android.content.pm.PackageManager
import android.provider.Telephony
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.database.Cursor
import android.net.Uri
import android.widget.ArrayAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.provider.ContactsContract

data class SmsItem(
    val sender: String,
    val messageBody: String,
    val dateTime: String,
    val isSent: Boolean, // true اگر پیام ارسال شده باشد
    val receiverPhoneNumber: String // شماره تلفن دریافت‌کننده
)
//data class SmsItem(val sender: String, val messageBody: String, val dateTime: String, val isSent: Boolean)

class Messages : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 100
    private val CONTACTS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // درخواست مجوز برای خواندن اس‌ام‌اس
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS), SMS_PERMISSION_CODE)
        } else {
            displaySmsMessages()
        }
    }
    private fun displaySmsMessages() {
        val smsList = mutableListOf<SmsItem>()
        val smsUri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE)

        val cursor = contentResolver.query(smsUri, projection, null, null, Telephony.Sms.DATE + " DESC")

        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val typeIndex = it.getColumnIndex(Telephony.Sms.TYPE)

            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val dateInMillis = it.getLong(dateIndex)
                val messageType = it.getInt(typeIndex)


                val receiverPhone = cursor.getString(cursor.getColumnIndexOrThrow("address"))


                // قالب‌بندی تاریخ
                val date = Date(dateInMillis)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val formattedDate = dateFormat.format(date)

                // بررسی نام مخاطب
                val contactName = getContactName(address) ?: address

                // تشخیص نوع پیام (ارسال یا دریافت)
                val isSent = messageType == Telephony.Sms.MESSAGE_TYPE_SENT

                // اضافه کردن پیام به لیست
                smsList.add(SmsItem(sender = contactName, messageBody = body, dateTime = formattedDate, isSent = isSent, receiverPhoneNumber = receiverPhone))
            }
        }

        // مرتب‌سازی بر اساس تاریخ (از جدید به قدیم)
        smsList.sortByDescending { it.dateTime }

        // تنظیم آداپتور به لیست ویو
        val listView: ListView = findViewById(R.id.listView)
        val adapter = SmsAdapter(this, smsList)
        listView.adapter = adapter
    }

    // تابع برای یافتن نام مخاطب از طریق شماره تلفن
    private fun getContactName(phoneNumber: String): String? {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?"
        val selectionArgs = arrayOf(phoneNumber)

        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            }
        }
        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displaySmsMessages()
            }
        }
    }


    data class SmsMessage(
        val date: String,
        val body: String,
        val type: Int
    ) {
        companion object {
            const val TYPE_SENT = 2
            const val TYPE_RECEIVED = 1
        }
    }


}