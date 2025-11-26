package com.animalmarket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.animalmarket.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Örnek hayvan verileri (geçici)
    private val sampleAnimals = listOf(
        AnimalItem("Sarıkız", "Sığır", "3 Yaş", "25.000 TL", "🐄"),
        AnimalItem("Karabaş", "Koyun", "2 Yaş", "4.500 TL", "🐑"),
        AnimalItem("Boncuk", "Keçi", "1 Yaş", "3.200 TL", "🐐")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupClickListeners()
        loadSampleData()
    }

    private fun setupUI() {
        // İstatistikleri güncelle
        binding.totalAnimalsText.text = sampleAnimals.size.toString()
        binding.activeAnimalsText.text = sampleAnimals.size.toString()

        // RecyclerView'ı ayarla
        setupRecyclerView()
        
        // Hızlı erişim grid'ini ayarla
        setupQuickAccessGrid()
    }

    private fun setupClickListeners() {
        // Hayvanları Görüntüle butonu
        binding.btnBrowseAnimals.setOnClickListener {
            showMessage("Hayvan listesi gösterilecek")
        }

        // Hayvan Sat butonu
        binding.btnSellAnimal.setOnClickListener {
            // İlan ekleme ekranına git
            navigateToAddAnimal()
        }
    }

    private fun setupRecyclerView() {
        val adapter = AnimalAdapter(sampleAnimals) { animal ->
            // Hayvan detayına git
            showMessage("${animal.name} detayı gösterilecek")
        }
        
        binding.recentAnimalsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        // Eğer hayvan yoksa boş durum görünümünü göster
        if (sampleAnimals.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recentAnimalsTitle.visibility = View.GONE
            binding.recentAnimalsRecycler.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recentAnimalsTitle.visibility = View.VISIBLE
            binding.recentAnimalsRecycler.visibility = View.VISIBLE
        }
    }

    private fun setupQuickAccessGrid() {
        // Hızlı erişim öğeleri
        val quickAccessItems = listOf(
            QuickAccessItem("Sığır", "🐄", "#4CAF50"),
            QuickAccessItem("Koyun", "🐑", "#FF9800"),
            QuickAccessItem("Keçi", "🐐", "#2196F3"),
            QuickAccessItem("Tavuk", "🐔", "#9C27B0")
        )

        // Grid için basit bir adapter (şimdilik butonlarla)
        // Daha sonra proper adapter ekleyeceğiz
    }

    private fun loadSampleData() {
        // Gerçek uygulamada burada API'den veri çekeceğiz
        println("📊 Örnek hayvan verileri yüklendi: ${sampleAnimals.size} adet")
    }

    private fun navigateToAddAnimal() {
        // Bottom navigation'da ilan ekleme sekmesine git
        val navController = androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_add)
    }

    private fun showMessage(message: String) {
        // Basit bir toast mesajı (sonra Snackbar'a çevireceğiz)
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class'ları
    data class AnimalItem(
        val name: String,
        val type: String,
        val age: String,
        val price: String,
        val emoji: String
    )

    data class QuickAccessItem(
        val title: String,
        val emoji: String,
        val color: String
    )

    // Basit RecyclerView Adapter
    class AnimalAdapter(
        private val animals: List<AnimalItem>,
        private val onItemClick: (AnimalItem) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

        class AnimalViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val name: android.widget.TextView = view.findViewById(R.id.animal_name)
            val type: android.widget.TextView = view.findViewById(R.id.animal_type)
            val price: android.widget.TextView = view.findViewById(R.id.animal_price)
            val emoji: android.widget.TextView = view.findViewById(R.id.animal_emoji)
            val card: android.widget.LinearLayout = view.findViewById(R.id.animal_card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_animal, parent, false)
            return AnimalViewHolder(view)
        }

        override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
            val animal = animals[position]
            
            holder.name.text = animal.name
            holder.type.text = "${animal.type} • ${animal.age}"
            holder.price.text = animal.price
            holder.emoji.text = animal.emoji
            
            holder.card.setOnClickListener {
                onItemClick(animal)
            }
        }

        override fun getItemCount() = animals.size
    }
}
