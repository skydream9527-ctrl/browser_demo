package com.litebrowse.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.litebrowse.databinding.DialogEnginePickerBinding
import com.litebrowse.databinding.ItemEnginePickerBinding
import com.litebrowse.engine.SearchEngine
import com.litebrowse.engine.SearchEngineManager

class EnginePickerSheet(
    private val manager: SearchEngineManager,
    private val onSelect: (SearchEngine) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogEnginePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogEnginePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.engineList.layoutManager = LinearLayoutManager(requireContext())
        binding.engineList.adapter = EngineListAdapter()
    }

    private inner class EngineListAdapter : RecyclerView.Adapter<EngineListAdapter.VH>() {
        inner class VH(val binding: ItemEnginePickerBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemEnginePickerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val engine = manager.engines[position]
            holder.binding.engineLetter.text = engine.iconLetter
            holder.binding.engineLetter.setBackgroundColor(Color.valueOf(engine.iconColor.toFloat() / 0xFFFFFFFF.toFloat(), 0f, 0f, 1f).toArgb())
            // Simple approach: set background color from engine
            try {
                val color = engine.iconColor.toInt()
                holder.binding.engineLetter.background.setTint(color)
            } catch (_: Exception) {}
            holder.binding.engineName.text = engine.name
            holder.binding.engineDomain.text = engine.urlTemplate
                .substringAfter("://").substringBefore("/")
            holder.binding.checkMark.visibility =
                if (engine.id == manager.currentEngineId) View.VISIBLE else View.GONE
            holder.binding.root.setOnClickListener {
                onSelect(engine)
                dismiss()
            }
        }

        override fun getItemCount() = manager.engines.size
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
