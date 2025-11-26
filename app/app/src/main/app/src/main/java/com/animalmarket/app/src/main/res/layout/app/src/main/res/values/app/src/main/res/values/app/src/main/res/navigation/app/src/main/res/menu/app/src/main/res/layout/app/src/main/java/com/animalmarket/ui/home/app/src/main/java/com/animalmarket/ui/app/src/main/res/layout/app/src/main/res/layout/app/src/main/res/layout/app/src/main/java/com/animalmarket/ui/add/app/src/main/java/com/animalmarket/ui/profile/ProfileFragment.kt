package com.animalmarket.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.animalmarket.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Kullanıcı bilgileri (geçici - gerçek uygulamada database'den gelecek)
    private var isLoggedIn = false
    private var userName = "Misafir Kullanıcı"
    private var userEmail = "Giriş yapılmamış"
    private var userPhone = ""
    private var userLocation = ""

    // İstatistikler
    private var totalListings = 0
    private var activeListings = 0
    private var favoritesCount = 0

    // Profil fotoğrafı seçmek için launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Profil fotoğrafını güncelle
                binding.profileImage.setImageURI(uri)
                showMessage("Profil fotoğrafı güncellendi")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupClickListeners()
        loadUserData()
    }

    private fun setupUI() {
        // Başlangıç durumunu ayarla
        updateLoginState()
        updateStatistics()
    }

    private fun setupClickListeners() {
        // Giriş/Çıkış butonu
        binding.btnLoginLogout.setOnClickListener {
            if (isLoggedIn) {
                showLogoutConfirmation()
            } else {
                showLoginDialog()
            }
        }

        // Profil fotoğrafı değiştirme
        binding.profileImage.setOnClickListener {
            if (isLoggedIn) {
                changeProfilePhoto()
            } else {
                showMessage("Profil fotoğrafını değiştirmek için giriş yapmalısınız")
            }
        }

        // Hızlı İşlemler
        binding.layoutMyListings.setOnClickListener {
            if (isLoggedIn) {
                navigateToMyListings()
            } else {
                showLoginRequiredMessage("İlanlarınızı görmek için")
            }
        }

        binding.layoutFavorites.setOnClickListener {
            if (isLoggedIn) {
                navigateToFavorites()
            } else {
                showLoginRequiredMessage("Favorilerinizi görmek için")
            }
        }

        binding.layoutMessages.setOnClickListener {
            if (isLoggedIn) {
                navigateToMessages()
            } else {
                showLoginRequiredMessage("Mesajlarınızı görmek için")
            }
        }

        // Ayarlar
        binding.layoutNotifications.setOnClickListener {
            // Bildirim ayarı zaten switch ile kontrol ediliyor
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Bildirimler açıldı" else "Bildirimler kapatıldı"
            showMessage(message)
        }

        binding.layoutPrivacy.setOnClickListener {
            showPrivacySettings()
        }
    }

    private fun loadUserData() {
        // Gerçek uygulamada burada API'den veya local database'den kullanıcı verileri çekilecek
        // Şimdilik örnek veriler kullanıyoruz
        
        if (isLoggedIn) {
            // Giriş yapmış kullanıcı için örnek veriler
            userName = "Ahmet Yılmaz"
            userEmail = "ahmet@example.com"
            userPhone = "+90 555 123 4567"
            userLocation = "Ankara"
            
            totalListings = 5
            activeListings = 3
            favoritesCount = 7
        } else {
            // Misafir kullanıcı için varsayılan değerler
            userName = "Misafir Kullanıcı"
            userEmail = "Giriş yapılmamış"
            userPhone = ""
            userLocation = ""
            
            totalListings = 0
            activeListings = 0
            favoritesCount = 0
        }

        updateUserInfo()
        updateStatistics()
    }

    private fun updateUserInfo() {
        binding.profileName.text = userName
        binding.profileEmail.text = userEmail
    }

    private fun updateStatistics() {
        binding.totalListingsCount.text = totalListings.toString()
        binding.activeListingsCount.text = activeListings.toString()
        binding.favoritesCount.text = favoritesCount.toString()
    }

    private fun updateLoginState() {
        if (isLoggedIn) {
            binding.btnLoginLogout.text = "Çıkış Yap"
            binding.profileEmail.setTextColor(resources.getColor(android.R.color.white, null))
        } else {
            binding.btnLoginLogout.text = "Giriş Yap"
            binding.profileEmail.setTextColor(resources.getColor(android.R.color.white, null))
        }
    }

    private fun showLoginDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Giriş Yap")
            .setMessage("Hayvan Pazarı'na hoş geldiniz! Uygulamadan tam olarak faydalanmak için giriş yapmalısınız.")
            .setPositiveButton("Giriş Yap") { dialog, _ ->
                // Simüle giriş işlemi
                simulateLogin()
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Çıkış Yap")
            .setMessage("Hesabınızdan çıkış yapmak istediğinize emin misiniz?")
            .setPositiveButton("Evet, Çıkış Yap") { dialog, _ ->
                simulateLogout()
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun simulateLogin() {
        isLoggedIn = true
        loadUserData()
        updateLoginState()
        showMessage("✅ Hoş geldiniz, $userName!")
    }

    private fun simulateLogout() {
        isLoggedIn = false
        loadUserData()
        updateLoginState()
        showMessage("Çıkış yapıldı. Tekrar bekleriz!")
    }

    private fun changeProfilePhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun navigateToMyListings() {
        showMessage("İlanlarım ekranına yönlendiriliyor...")
        // Gerçek uygulamada: findNavController().navigate(R.id.navigation_my_listings)
    }

    private fun navigateToFavorites() {
        showMessage("Favorilerim ekranına yönlendiriliyor...")
        // Gerçek uygulamada: findNavController().navigate(R.id.navigation_favorites)
    }

    private fun navigateToMessages() {
        showMessage("Mesajlarım ekranına yönlendiriliyor...")
        // Gerçek uygulamada: findNavController().navigate(R.id.navigation_messages)
    }

    private fun showPrivacySettings() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Gizlilik ve Güvenlik")
            .setMessage("""
                🔒 Gizlilik Ayarları
                
                • Profil bilgileriniz sadece sizinle iletişime geçen kullanıcılar tarafından görülebilir
                • Telefon numaranız güvenli bir şekilde saklanır
                • İlanlarınız sadece kayıtlı kullanıcılar tarafından görülebilir
                
                📞 Destek: support@hayvanpazari.com
            """.trimIndent())
            .setPositiveButton("Anladım") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoginRequiredMessage(feature: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Giriş Gerekli")
            .setMessage("$feature giriş yapmalısınız.")
            .setPositiveButton("Giriş Yap") { dialog, _ ->
                showLoginDialog()
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Kullanıcı veri modeli
    data class UserProfile(
        val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val location: String,
        val profileImageUri: Uri? = null,
        val joinDate: String,
        val totalListings: Int,
        val activeListings: Int,
        val favoritesCount: Int,
        val rating: Double
    )
}
