package android.com.firemessage.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.com.firemessage.R
import android.com.firemessage.SignInActivity
import android.com.firemessage.util.FirestoreUtil
import android.com.firemessage.util.StorageUtil
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import com.firebase.ui.auth.AuthUI
import com.google.firebase.storage.FirebaseStorage
import android.com.firemessage.glide.GlideApp
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import java.io.ByteArrayOutputStream

class MyAccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_my_account,container,false)
        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
            }
                btn_save.setOnClickListener {
                    if (::selectedImageBytes.isInitialized)
                        StorageUtil.uploadProfilePhoto(selectedImageBytes){ imagePath ->
                            FirestoreUtil.updateCurrentUser(editText_name.text.toString(),editText_bio.text.toString(),imagePath)
                        }
                    else
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(),editText_bio.text.toString(),null)
                }
                btn_sign_out.setOnClickListener {
                    AuthUI.getInstance().signOut(this@MyAccountFragment.context!!)
                        .addOnCompleteListener {
                            startActivity(intentFor<SignInActivity>().newTask().clearTask())
                        }
                }
            }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data!= null && data.data!=null){
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes = outputStream.toByteArray()

            //TODO: Load picture
            GlideApp.with(this).load(selectedImageBytes).into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            if (this@MyAccountFragment.isVisible){
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if (!pictureJustChanged && user.profilePicturePath != null)
                       GlideApp.with(this).load(StorageUtil.pathToReference(user.profilePicturePath)).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView_profile_picture)
            }
        }
    }
}
