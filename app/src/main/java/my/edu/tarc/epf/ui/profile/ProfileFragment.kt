package my.edu.tarc.epf.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import my.edu.tarc.epf.R
import my.edu.tarc.epf.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

class ProfileFragment : Fragment(), MenuProvider {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    //Implicit Intent
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri ->
        if (uri != null){
            binding.imageViewProfile.setImageURI(uri)
        }
    }

    private lateinit var sharedPre: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Add menu host
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val image = readProfilePicture()
        if (image != null){
            binding.imageViewProfile.setImageBitmap(image)
        }else{
            binding.imageViewProfile.setImageResource(R.drawable.default_pic)
        }

        binding.imageViewProfile.setOnClickListener {
            //Invoke Implicit Intent here
            getPhoto.launch("image/*")
        }

        //Setup Shared Preference
        sharedPre = requireActivity().getPreferences(Context.MODE_PRIVATE)
        //Read shared pref data
        val name = sharedPre.getString(getString(R.string.name),getString(R.string.nav_header_title))
        val email = sharedPre.getString(getString(R.string.email),getString(R.string.nav_header_subtitle))
        binding.editTextName.setText(name)
        binding.editTextEmailAddress.setText(email)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu)
        menu.findItem(R.id.action_about).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_save) {
            //TODO: Save user profile info and picture

            saveProfilePicture(binding.imageViewProfile)

            //Save profile info
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmailAddress.text.toString()
            with(sharedPre.edit()){
                putString(getString(R.string.name),name)
                putString(getString(R.string.email),email)
                apply()
            }
            Toast.makeText(context,getString(R.string.profile_save),Toast.LENGTH_SHORT).show()

            val navHeaderView = requireActivity().findViewById<View>(R.id.nav_view) as NavigationView
            val headerView = navHeaderView.getHeaderView(0)
            val textViewName: TextView = headerView.findViewById(R.id.textViewName)
            val textViewEmail: TextView = headerView.findViewById(R.id.textViewEmail)
            val imageViewPicture: ImageView = headerView.findViewById(R.id.imageViewPicture)

            imageViewPicture.setImageBitmap(readProfilePicture())
            textViewName.text = name
            textViewEmail.text = email

        } else if (menuItem.itemId == android.R.id.home) {
            //Handling the Up Button
            findNavController().navigateUp()
        }
        return true
    }

    private fun saveProfilePicture(view: View) {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)
        val image = view as ImageView

        val bd = image.drawable as BitmapDrawable
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun readProfilePicture(): Bitmap? {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        if (file.isFile) {
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                return bitmap
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return null
    }
}