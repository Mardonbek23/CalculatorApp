package uz.freelance.calculatorapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.freelance.calculatorapp.databinding.ItemHistoryBinding
import uz.freelance.calculatorapp.models.HistoryItem


class HistoryAdapter(var list: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
        fun onBind(historyItem: HistoryItem) {
            binding.apply {
                tvProcess.text = historyItem.process
                tvResult.text = historyItem.result
            }
        }
    }

    fun setMyList(list: List<HistoryItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: HistoryAdapter.MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}