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
 * ItemViewModel is responsible for:
 * - Loading raw JSON data from the assets folder
 * - Parsing it into structured objects (Item)
 * - Filtering, sorting, and grouping the data
 * - Exposing this cleaned-up data to the UI using LiveData
 *
 * Application context needs to read files from the assets directory, so AndroidViewModel is used
 * instead of just ViewModel.
 */
class ItemViewModel(application: Application) : AndroidViewModel(application) {

    // private item group that can be changed in this class
    private val _itemGroups = MutableLiveData<List<ItemGroup>>()
    // read-only for outside classes
    val itemGroups: LiveData<List<ItemGroup>> = _itemGroups

    // show loading UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // error: loading or parsing fails
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // This init block runs when the ViewModel is first created.
    // It immediately starts the process of loading and preparing the data.
    init {
        loadItems()
    }

    /**
     * Triggers the process of loading and processing items.
     * - Runs inside a coroutine on the ViewModel scope to avoid blocking the UI.
     * - Sets isLoading to true while working, and back to false when done.
     * - Uses a try-catch block to catch and handle any errors gracefully.
     */
    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true // start loading operation
            try {
                // This block is run on the IO dispatcher to avoid doing heavy work on the main thread
                val items = withContext(Dispatchers.IO) {
                    val jsonFileString = getJsonFromAssets("hiring.json") // Step 1: Load raw JSON as string
                    parseJson(jsonFileString) // Step 2: Parse string into a List<Item>
                }
                processItems(items) // Step 3: Clean and organize the data for UI
                _isLoading.value = false // loading is complete
            } catch (e: Exception) {
                // log any errors
                Log.e("ItemViewModel", "Error loading items", e)
                _error.value = "Failed to load items: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads a JSON file from the assets directory and returns its contents as a raw string.
     * - Uses the Application context (from AndroidViewModel) to access assets.
     * - Uses a bufferedReader with `.use` for automatic resource management.
     * - Any IOExceptions are logged and re-thrown so they can be caught in loadItems().
     */
    private fun getJsonFromAssets(fileName: String): String {
        val context = getApplication<Application>() // Get application context
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("ItemViewModel", "Error reading JSON file", ioException)
            throw ioException
        }
    }

    /**
     * Parses a JSON string into a List<Item> using Gson.
     * - Uses a TypeToken to help Gson deserialize a list of a generic type.
     * - This turns raw JSON text into Kotlin objects we can work with.
     */
    private fun parseJson(jsonString: String): List<Item> {
        val gson = Gson()
        val listType = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    /**
     * Transforms the raw list of items into structured and cleaned-up data:
     *
     * 1. Filter out any items with null or blank names (invalid data).
     * 2. Sort the remaining items by listId (numerically), then by name (alphabetically).
     * 3. Group the sorted items by listId to form logical groups.
     * 4. Convert those groups into ItemGroup objects and sort those by listId.
     * 5. Push the result to LiveData so the UI can observe it.
     */
    private fun processItems(items: List<Item>) {
        val filteredItems = items.filter { !it.name.isNullOrBlank() } // Step 1

        val sortedItems = filteredItems.sortedWith(
            compareBy<Item> { it.listId }
                .thenBy { it.name }
        ) // step 2

        val groupedItems = sortedItems.groupBy { it.listId } // step 3
            .map { (listId, items) -> ItemGroup(listId, items) } // step 4
            .sortedBy { it.listId }

        _itemGroups.value = groupedItems // step 5: post value to LiveData
    }
}
