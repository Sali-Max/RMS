package com.android.oms

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


// Add List
import android.content.DialogInterface
import android.content.SharedPreferences
import android.widget.*
import androidx.appcompat.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView



class addKey : AppCompatActivity() {


    private lateinit var listView: ListView
    private lateinit var addBtn: Button
    private lateinit var nameInput: EditText
    private lateinit var itemInput: EditText
    private lateinit var adapter: ArrayAdapter<String>
    private val itemList = mutableListOf<String>() // List to display name: item pairs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_key)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        listView = findViewById(R.id.listView)
        addBtn = findViewById(R.id.addBtn)
        nameInput = findViewById(R.id.nameInput)
        itemInput = findViewById(R.id.itemInput)

        // Set up adapter for the ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter

        // Load items from SharedPreferences
        loadItemsFromSharedPreferences()

        // Add button click listener to add the item
        addBtn.setOnClickListener {
            val nameText = nameInput.text.toString().trim()
            val itemText = itemInput.text.toString().trim()
            if(readString("$nameText","other-key","") !=  "")   // Check for avilable name Key
            {
                Toast.makeText(this, "Name Is aviable :(", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if (nameInput.text.toString() != "" && nameInput.text.toString() != "key") {

                    if (nameText.isNotEmpty() && itemText.isNotEmpty()) {
                        addItem(nameText, itemText)
                        nameInput.text?.clear() // Clear name input after adding
                        itemInput.text?.clear() // Clear item input after adding
                    }
                }
                else
                {
                    Toast.makeText(this, "key name Unavilable", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set up item click listener for the ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = itemList[position]
            val name = selectedItem.split(":")[0].trim() // Extract name (key) part
            showDeleteDialog(name, position)
        }
    }

    // Add name:item pair to list and SharedPreferences
    private fun addItem(name: String, item: String) {
        val listItem = "$name: $item"
        if (!itemList.contains(listItem)) {
            itemList.add(listItem)
            adapter.notifyDataSetChanged()
            saveItemToSharedPreferences(name, item) // Save key-value pair
        }
    }

    // Load items from SharedPreferences
    private fun loadItemsFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("other-key", Context.MODE_PRIVATE)
        val allItems = sharedPreferences.all
        for ((key, value) in allItems) {
            val listItem = "$key : $value"
            itemList.add(listItem)
        }
        adapter.notifyDataSetChanged()
    }


    fun readString(key: String, group: String, defaultValue: String = ""): String? {

        val sharedPref = getSharedPreferences("$group", MODE_PRIVATE)
        return sharedPref.getString(
            key,
            defaultValue
        )  // Return the stored value, or the default value if key is not found
    }

    // Save name:item pair to SharedPreferences
    private fun saveItemToSharedPreferences(name: String, item: String) {
        val sharedPreferences = getSharedPreferences("other-key", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(name, item) // Save name as key, item as value
        editor.apply() // Apply changes
    }

    // Show a dialog to confirm deletion
    private fun showDeleteDialog(name: String, position: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you want to delete this item?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                deleteItem(name, position)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Delete Item")
        alert.show()
    }

    // Delete name:item pair from list and SharedPreferences
    private fun deleteItem(name: String, position: Int) {
        // Remove from list and notify adapter
        itemList.removeAt(position)
        adapter.notifyDataSetChanged()

        // Remove from SharedPreferences
        val sharedPreferences = getSharedPreferences("other-key", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(name) // Remove the key (name) from SharedPreferences
        editor.apply() // Apply changes
    }
}




