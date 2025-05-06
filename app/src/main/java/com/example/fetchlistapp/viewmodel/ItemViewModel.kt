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
 * ViewModel that manages loading, filtering, sorting, and organizing item data.
 * It reads a local JSON file and prepares the data for display in the UI.
 */
class ItemViewModel(application: Application) : AndroidViewModel(application) {

    // create the grouped item data for the UI to see
    private val _itemGroups = MutableLiveData<List<ItemGroup>>()
    val itemGroups: LiveData<List<ItemGroup>> = _itemGroups

    // checks whether data is currently loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // show any error messages in the UI
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // automatically load items when the ViewModel is created
    init {
        loadItems()
    }

    /**
     * Loads item data from a JSON file using a coroutine (runs in the background).
     * Also handles errors and shows a loading state.
     */
    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true // start loading data
            try {
                // read and parse the JSON file in the background
                val items = withContext(Dispatchers.IO) {
                    val jsonFileString = getJsonFromAssets("hiring.json")
                    parseJson(jsonFileString)
                }
                // process the raw data into a grouped format
                processItems(items)
                _isLoading.value = false // end loading data
            } catch (e: Exception) {
                // error with loading data
                Log.e("ItemViewModel", "Error loading items", e)
                _error.value = "Failed to load items: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Reads a JSON file from the assets folder and returns its contents as a string.
     * @param fileName The name of the file in the assets folder.
     * @return The content of the file as a string.
     * @throws IOException if the file can’t be read.
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
     * @param jsonString The JSON string that contains the item data.
     * @return A list of Item objects.
     */
    private fun parseJson(jsonString: String): List<Item> {
        val gson = Gson()
        val listType = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(jsonString, listType)
    }

    /**
     * Takes the raw item list and:
     * - Removes items with no name
     * - Sorts by listId (number) and then name (alphabetically)
     * - Groups items by listId into ItemGroup objects
     * - Sends the final grouped list to LiveData so the UI can show it
     *
     * @param items A raw list of Item objects from the JSON file.
     */
    private fun processItems(items: List<Item>) {
        // remove items where the name is null or blank
        val filteredItems = items.filter { !it.name.isNullOrBlank() }

        // sort items by listId first
        // then sort by name
        val sortedItems = filteredItems.sortedWith(
            compareBy<Item> { it.listId }
                .thenBy { it.name }
        )

        // group items by listId and put them in ItemGroup objects
        val groupedItems = sortedItems.groupBy { it.listId }
            .map { (listId, items) -> ItemGroup(listId, items) }
            .sortedBy { it.listId }

        // send grouped items to the UI
        _itemGroups.value = groupedItems
    }
}
