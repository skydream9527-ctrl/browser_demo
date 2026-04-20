package com.litebrowse.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.litebrowse.LiteBrowseApp
import com.litebrowse.MainActivity
import com.litebrowse.R
import com.litebrowse.data.entity.Bookmark
import com.litebrowse.data.entity.BookmarkFolder
import com.litebrowse.databinding.FragmentBookmarkBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private var currentFolderId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = (requireActivity().application) as LiteBrowseApp
        val dao = app.database.bookmarkDao()

        binding.bookmarkList.layoutManager = LinearLayoutManager(requireContext())

        val adapter = BookmarkAdapter(
            onBookmarkClick = { bookmark ->
                (requireActivity() as MainActivity).tabManager.addTab(bookmark.url)
                findNavController().navigate(R.id.browserFragment)
            },
            onFolderClick = { folder ->
                currentFolderId = folder.id
                loadBookmarks()
            },
            onBookmarkLongClick = { bookmark ->
                showEditDialog(bookmark)
            },
            onFolderLongClick = { folder ->
                showDeleteFolderDialog(folder)
            }
        )
        binding.bookmarkList.adapter = adapter

        binding.newFolder.setOnClickListener {
            val input = EditText(requireContext())
            input.hint = getString(R.string.folder_name)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.new_folder)
                .setView(input)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotEmpty()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.insertFolder(BookmarkFolder(name = name, parentId = currentFolderId))
                        }
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        loadBookmarks()
    }

    private fun loadBookmarks() {
        val app = (requireActivity().application) as LiteBrowseApp
        val dao = app.database.bookmarkDao()

        val foldersFlow = if (currentFolderId == null) dao.getRootFolders() else dao.getSubFolders(currentFolderId!!)
        val bookmarksFlow = if (currentFolderId == null) dao.getUncategorized() else dao.getByFolder(currentFolderId!!)

        foldersFlow.asLiveData().observe(viewLifecycleOwner) { folders ->
            bookmarksFlow.asLiveData().observe(viewLifecycleOwner) { bookmarks ->
                val adapter = binding.bookmarkList.adapter as BookmarkAdapter
                adapter.submitData(folders, bookmarks)
                binding.emptyText.visibility = if (folders.isEmpty() && bookmarks.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showEditDialog(bookmark: Bookmark) {
        val app = (requireActivity().application) as LiteBrowseApp
        val dao = app.database.bookmarkDao()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit)
            .setItems(arrayOf("编辑", "删除")) { _, which ->
                when (which) {
                    0 -> { /* TODO: show edit form */ }
                    1 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            dao.delete(bookmark)
                        }
                    }
                }
            }
            .show()
    }

    private fun showDeleteFolderDialog(folder: BookmarkFolder) {
        val app = (requireActivity().application) as LiteBrowseApp
        val dao = app.database.bookmarkDao()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除文件夹")
            .setMessage("确定删除「${folder.name}」？")
            .setPositiveButton(R.string.delete) { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    dao.deleteFolder(folder)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
