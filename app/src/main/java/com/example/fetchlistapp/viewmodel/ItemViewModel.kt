package com.example.fetchlistapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fetchlistapp.model.Item
import com.example.fetchlistapp.model.ItemGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * ViewModel that handles loading, filtering, sorting, and grouping item data from a local JSON file.
 */
class ItemViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData to observe processed and grouped items in the UI
    private val _itemGroups = MutableLiveData<List<ItemGroup>>()
    val itemGroups: LiveData<List<ItemGroup>> = _itemGroups

    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to observe error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Automatically load items when the ViewModel is created
    init {
        loadItems()
    }

    /**
     * Loads item data from a JSON file and processes it asynchronously using coroutines.
     */
    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = withContext(Dispatchers.IO) {
                    val jsonFileString = getJsonFromAssets("hiring.json")
                    parseJson(jsonFileString)
                }
                processItems(items)
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("ItemViewModel", "Error loading items", e)
                _error.value = "Failed to load items: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Retrieves the contents of a JSON file stored in the assets directory.
     *
     * @param fileName Name of the file to read (e.g., "hiring.json")
     * @return The file's text content as a String
     * @throws IOException if the file can't be read
     */
    private fun getJsonFromAssets(fileName: String): String {
        val context = getApplication<Application>()
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("ItemViewModel", "Error reading JSON file", ioException)
            throw ioException
        }
    }

    /**
     * Converts a JSON string into a list of Item objects using Gson.
     *
     * @param jsonString The raw JSON data as a string
     * @return A list of Item objects parsed from the JSON
     */
    private fun parseJson(jsonString: String): List<Item> {
        val gson = Gson()
        val listType = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    /**
     * Filters, sorts, and groups the list of items as follows:
     * - Removes items with null or blank names
     * - Sorts items by listId (ascending) and then by name (alphabetically)
     * - Groups items by listId into ItemGroup objects
     * - Updates the LiveData to be used in the UI
     *
     * @param items The raw list of Item objects to be processed
     */
    private fun processItems(items: List<Item>) {
        val filteredItems = items.filter { !it.name.isNullOrBlank() }

        val sortedItems = filteredItems.sortedWith(
            compareBy<Item> { it.listId }
                .thenBy { it.name }
        )

        val groupedItems = sortedItems.groupBy { it.listId }
            .map { (listId, items) -> ItemGroup(listId, items) }
            .sortedBy { it.listId }

        _itemGroups.value = groupedItems
    }
}
