package com.litebrowse.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.data.entity.BookmarkFolder
import com.litebrowse.databinding.ItemBookmarkBinding
import com.litebrowse.databinding.ItemBookmarkFolderBinding
import com.litebrowse.util.UrlUtils

class BookmarkAdapter(
    private val onBookmarkClick: (Bookmark) -> Unit,
    private val onFolderClick: (BookmarkFolder) -> Unit,
    private val onBookmarkLongClick: (Bookmark) -> Unit,
    private val onFolderLongClick: (BookmarkFolder) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var folders: List<BookmarkFolder> = emptyList()
    private var bookmarks: List<Bookmark> = emptyList()

    fun submitData(folders: List<BookmarkFolder>, bookmarks: List<Bookmark>) {
        this.folders = folders
        this.bookmarks = bookmarks
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < folders.size) TYPE_FOLDER else TYPE_BOOKMARK
    }

    override fun getItemCount() = folders.size + bookmarks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FOLDER) {
            FolderVH(ItemBookmarkFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            BookmarkVH(ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FolderVH -> {
                val folder = folders[position]
                holder.binding.folderName.text = folder.name
                holder.binding.bookmarkCount.text = ""
                holder.binding.root.setOnClickListener { onFolderClick(folder) }
                holder.binding.root.setOnLongClickListener { onFolderLongClick(folder); true }
            }
            is BookmarkVH -> {
                val bookmark = bookmarks[position - folders.size]
                holder.binding.bookmarkTitle.text = bookmark.title
                holder.binding.bookmarkUrl.text = UrlUtils.extractDomain(bookmark.url)
                holder.binding.faviconText.text = bookmark.title.firstOrNull()?.toString() ?: "?"
                holder.binding.root.setOnClickListener { onBookmarkClick(bookmark) }
                holder.binding.root.setOnLongClickListener { onBookmarkLongClick(bookmark); true }
            }
        }
    }

    class FolderVH(val binding: ItemBookmarkFolderBinding) : RecyclerView.ViewHolder(binding.root)
    class BookmarkVH(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_FOLDER = 0
        const val TYPE_BOOKMARK = 1
    }
}
