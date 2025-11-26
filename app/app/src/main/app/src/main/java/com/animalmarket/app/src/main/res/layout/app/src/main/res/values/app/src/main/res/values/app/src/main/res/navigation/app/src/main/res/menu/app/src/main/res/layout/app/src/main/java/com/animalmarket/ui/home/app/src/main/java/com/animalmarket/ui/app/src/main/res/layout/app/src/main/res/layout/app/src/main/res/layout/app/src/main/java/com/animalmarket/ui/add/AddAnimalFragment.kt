package com.animalmarket.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.animalmarket.databinding.FragmentAddAnimalBinding
import com.google.android.material.snackbar.Snackbar

class AddAnimalFragment : Fragment() {

    private var _binding: FragmentAddAnimalBinding? = null
    private val binding get() = _binding!!

    // Seçilen fotoğrafların URI listesi
    private val selectedPhotos = mutableListOf<Uri>()

    // Hayvan türleri listesi
    private val animalTypes = arrayOf(
        "Sığır", "Koyun", "Keçi", "Tavuk", "At", "Ördek", "Kaz", "Hindi", "Diğer"
    )

    // Cinsiyet seçenekleri
    private val genderOptions = arrayOf("Erkek", "Dişi")

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val MAX_PHOTOS = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAnimalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Hayvan türleri dropdown'ını ayarla
        setupAnimalTypeDropdown()

        // Cinsiyet dropdown'ını ayarla
        setupGenderDropdown()
    }

    private fun setupAnimalTypeDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            animalTypes
        )
        binding.actAnimalType.setAdapter(adapter)
    }

    private fun setupGenderDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            genderOptions
        )
        binding.actAnimalGender.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        // Fotoğraf ekle butonu
        binding.btnAddPhoto.setOnClickListener {
            openImagePicker()
        }

        // İlanı kaydet butonu
        binding.btnSaveAnimal.setOnClickListener {
            saveAnimalListing()
        }
    }

    private fun openImagePicker() {
        if (selectedPhotos.size >= MAX_PHOTOS) {
            showMessage("Maksimum $MAX_PHOTOS fotoğraf ekleyebilirsiniz")
            return
        }

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                if (selectedPhotos.size < MAX_PHOTOS) {
                    selectedPhotos.add(uri)
                    updatePhotoPreview()
                    showMessage("Fotoğraf eklendi (${selectedPhotos.size}/$MAX_PHOTOS)")
                } else {
                    showMessage("Maksimum fotoğraf sayısına ulaştınız")
                }
            }
        }
    }

    private fun updatePhotoPreview() {
        if (selectedPhotos.isNotEmpty()) {
            binding.photoPreviewContainer.visibility = View.VISIBLE
            binding.photoPreviewLayout.removeAllViews()

            selectedPhotos.forEachIndexed { index, uri ->
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(100, 100).apply {
                        marginEnd = 8
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageURI(uri)
                    setOnClickListener { removePhoto(index) }
                    background = resources.getDrawable(android.R.drawable.picture_frame, null)
                }

                binding.photoPreviewLayout.addView(imageView)
            }
        } else {
            binding.photoPreviewContainer.visibility = View.GONE
        }
    }

    private fun removePhoto(index: Int) {
        if (index in 0 until selectedPhotos.size) {
            selectedPhotos.removeAt(index)
            updatePhotoPreview()
            showMessage("Fotoğraf kaldırıldı")
        }
    }

    private fun saveAnimalListing() {
        // Form validasyonu
        if (!validateForm()) {
            return
        }

        // Hayvan bilgilerini al
        val animalName = binding.etAnimalName.text.toString().trim()
        val animalType = binding.actAnimalType.text.toString().trim()
        val animalAge = binding.etAnimalAge.text.toString().trim()
        val animalGender = binding.actAnimalGender.text.toString().trim()
        val animalPrice = binding.etAnimalPrice.text.toString().trim()
        val animalDescription = binding.etAnimalDescription.text.toString().trim()
        val animalLocation = binding.etAnimalLocation.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        // Hayvan nesnesi oluştur (gerçek uygulamada database'e kaydedilecek)
        val animal = AnimalListing(
            name = animalName,
            type = animalType,
            age = animalAge,
            gender = animalGender,
            price = animalPrice,
            description = animalDescription,
            location = animalLocation,
            phone = phone,
            photos = selectedPhotos.toList(),
            timestamp = System.currentTimeMillis()
        )

        // Başarı mesajı göster
        showSuccessMessage(animal)

        // Formu temizle
        clearForm()
    }

    private fun validateForm(): Boolean {
        // Hayvan adı kontrolü
        if (binding.etAnimalName.text.isNullOrEmpty()) {
            showMessage("Lütfen hayvan adını giriniz")
            binding.etAnimalName.requestFocus()
            return false
        }

        // Hayvan türü kontrolü
        if (binding.actAnimalType.text.isNullOrEmpty()) {
            showMessage("Lütfen hayvan türünü seçiniz")
            binding.actAnimalType.requestFocus()
            return false
        }

        // Yaş kontrolü
        if (binding.etAnimalAge.text.isNullOrEmpty()) {
            showMessage("Lütfen hayvanın yaşını giriniz")
            binding.etAnimalAge.requestFocus()
            return false
        }

        // Fiyat kontrolü
        if (binding.etAnimalPrice.text.isNullOrEmpty()) {
            showMessage("Lütfen fiyat giriniz")
            binding.etAnimalPrice.requestFocus()
            return false
        }

        // Konum kontrolü
        if (binding.etAnimalLocation.text.isNullOrEmpty()) {
            showMessage("Lütfen konum bilgisini giriniz")
            binding.etAnimalLocation.requestFocus()
            return false
        }

        // Telefon kontrolü
        if (binding.etPhone.text.isNullOrEmpty()) {
            showMessage("Lütfen telefon numarasını giriniz")
            binding.etPhone.requestFocus()
            return false
        }

        // Fotoğraf kontrolü (opsiyonel)
        if (selectedPhotos.isEmpty()) {
            showMessage("Uyarı: Fotoğraf eklemediniz")
            // Fotoğraf olmadan da devam edebilir, sadece uyarı ver
        }

        return true
    }

    private fun showSuccessMessage(animal: AnimalListing) {
        val message = """
            🎉 İlan başarıyla oluşturuldu!
            
            Hayvan: ${animal.name}
            Tür: ${animal.type}
            Fiyat: ${animal.price} TL
            Konum: ${animal.location}
            
            İlanınız yayınlandı!
        """.trimIndent()

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Tamam") { }
            .show()

        // Ana sayfaya yönlendir (gerçek uygulamada navigation kullanılır)
        println("✅ Yeni ilan eklendi: $animal")
    }

    private fun clearForm() {
        // Form alanlarını temizle
        binding.etAnimalName.text?.clear()
        binding.actAnimalType.text?.clear()
        binding.etAnimalAge.text?.clear()
        binding.actAnimalGender.text?.clear()
        binding.etAnimalPrice.text?.clear()
        binding.etAnimalDescription.text?.clear()
        binding.etAnimalLocation.text?.clear()
        binding.etPhone.text?.clear()

        // Fotoğrafları temizle
        selectedPhotos.clear()
        binding.photoPreviewContainer.visibility = View.GONE

        showMessage("Form temizlendi, yeni ilan girebilirsiniz")
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class for animal listing
    data class AnimalListing(
        val name: String,
        val type: String,
        val age: String,
        val gender: String,
        val price: String,
        val description: String,
        val location: String,
        val phone: String,
        val photos: List<Uri>,
        val timestamp: Long
    )
}
