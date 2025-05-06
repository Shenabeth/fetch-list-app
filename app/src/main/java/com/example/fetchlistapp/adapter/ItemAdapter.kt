package com.example.fetchlistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchlistapp.R
import com.example.fetchlistapp.model.Item
import com.example.fetchlistapp.model.ItemGroup

/**
 * Adapter for the outer RecyclerView which displays a list of item groups.
 * Each item group includes a listId and a nested RecyclerView of individual items.
 */
class ItemGroupAdapter : RecyclerView.Adapter<ItemGroupAdapter.GroupViewHolder>() {

    // Backing list for all item groups shown in the outer RecyclerView
    private var itemGroups: List<ItemGroup> = emptyList()

    /**
     * Replaces the current list of item groups with new data and refreshes the view.
     * @param newItemGroups The new list of grouped items to display.
     */
    fun updateData(newItemGroups: List<ItemGroup>) {
        this.itemGroups = newItemGroups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        // Inflate the layout for an item group (outer RecyclerView item)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        // Bind the item group data at the given position to the ViewHolder
        val itemGroup = itemGroups[position]
        holder.bind(itemGroup)
    }

    override fun getItemCount(): Int = itemGroups.size

    /**
     * ViewHolder class representing one group of items.
     * Each group shows a listId and a nested list of items in its own RecyclerView.
     */
    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // UI references to group title and nested RecyclerView
        private val groupTitleTextView: TextView = itemView.findViewById(R.id.groupTitleTextView)
        private val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)

        // Adapter for the inner RecyclerView that shows individual items
        private val itemAdapter = ItemAdapter()

        /**
         * Binds the group-level data and sets up the nested RecyclerView.
         * @param itemGroup A single group of items with the same listId.
         */
        fun bind(itemGroup: ItemGroup) {
            // Set the group title using the listId
            groupTitleTextView.text = buildString {
                append("List ID: ")
                append(itemGroup.listId)
            }

            // Configure the nested RecyclerView for items
            itemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = itemAdapter
            }

            // Pass the group's items to the inner adapter
            itemAdapter.updateData(itemGroup.items)
        }
    }
}

/**
 * Adapter for the inner RecyclerView which displays the individual items
 * that belong to a specific group (shown in the outer RecyclerView).
 */
class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // List of items to display within a single group
    private var items: List<Item> = emptyList()

    /**
     * Updates the adapter's item list and notifies the UI to refresh.
     * @param newItems A new list of items for this group.
     */
    fun updateData(newItems: List<Item>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Inflate the layout for a single item row
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Bind item data at the given position to the ViewHolder
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder for a single item row within a group.
     * Displays the item's name and its ID.
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // UI references for the item name and ID text views
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemIdTextView: TextView = itemView.findViewById(R.id.itemIdTextView)

        /**
         * Binds a single item's data to the row.
         * @param item The item object containing name and ID info.
         */
        fun bind(item: Item) {
            // Show item name if not null, else show blank
            itemNameTextView.text = item.name ?: ""

            // Show item ID prefixed with "ID:"
            itemIdTextView.text = buildString {
                append("ID: ")
                append(item.id)
            }
        }
    }
}
