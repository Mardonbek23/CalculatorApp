package uz.freelance.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.freelance.calculatorapp.adapters.HistoryAdapter
import uz.freelance.calculatorapp.databinding.ActivityHistoryBinding
import uz.freelance.calculatorapp.helpers.LocalData
import uz.freelance.calculatorapp.models.HistoryItem

class HistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding
    lateinit var adapter: HistoryAdapter
    lateinit var localData: LocalData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localData = LocalData(this)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        setButtons()
        if (localData.history() != null) {
            adapter = HistoryAdapter(
                Gson().fromJson<ArrayList<HistoryItem>?>(
                    localData.history(),
                    object : TypeToken<List<HistoryItem>>() {}.type
                ).reversed()
            )
            binding.rv.adapter = adapter
            binding.tvEmpty.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.VISIBLE
        }
    }

    private fun setButtons() {
        binding.apply {
            cvBack.setOnClickListener { finish() }
            cvDelete.setOnClickListener {
                localData.history(null)
                adapter.setMyList(arrayListOf())
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }
}