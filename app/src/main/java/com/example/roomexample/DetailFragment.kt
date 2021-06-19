package com.example.roomexample

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.roomexample.database.asPostedMessage
import com.example.roomexample.databinding.FragmentDetailBinding
import com.example.roomexample.firebase.PostedMessage
import com.example.roomexample.utilities.FirebaseService
import com.example.roomexample.utilities.showSnackMessage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

//fragment to display the detailed scene selected from the recyclerview
class DetailFragment : Fragment() {

   private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)

        //retrieve the passed argument (selected scene's id from the recyclerview)
        val args = DetailFragmentArgs.fromBundle(requireArguments())

        binding.scene = args.scene

        binding.mapButton.setOnClickListener {
            it.findNavController()
                .navigate(DetailFragmentDirections.actionDetailFragmentToMapFragment(args.scene.name, args.scene.address))
        }

        binding.weatherButton.setOnClickListener {
            it.findNavController()
                .navigate(DetailFragmentDirections.actionDetailFragmentToWeatherFragment(args.scene.city))
        }

        //handle send data to remote
        binding.shareButton.setOnClickListener {
            val user = FirebaseService.firebaseAuth.currentUser
            val sentMessage = args.scene.asPostedMessage()

            if ( user != null) {
                sentMessage.uploaderName = user.displayName
                sentMessage.uploaderId = user.uid
                uploadMessage(sentMessage) //upload the selectedScene to the server
            }
            else
                showSnackMessage(requireView(), "Switch to discovery and login")
        }

        //hide bottom nav
        this.activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.GONE

        return binding.root
    }

    private fun uploadMessage(sentMessage: PostedMessage) {
        //upload data to the firestore
        FirebaseService.collectionRef.add(sentMessage)
            .addOnSuccessListener { docRef ->
                // Build a StorageReference and then upload the image file
                val uri = Uri.parse(sentMessage.photoUri)
                val key = docRef.id
                val storageRef = Firebase.storage
                    .getReference(sentMessage.uploaderId!!)
                    .child(key)
                    .child(uri.lastPathSegment!!)  //get the file name

                putImageInStorage(storageRef, docRef, uri, key)
                //ignore possible errors on storage
                showSnackMessage(requireView(), "Complete data uploading")
            }
            .addOnFailureListener {error ->
                Log.d("Main", "Error", error)
                showSnackMessage(requireView(), "Error on firestore")
            }
    }

    private fun putImageInStorage(storageReference: StorageReference, documentRef: DocumentReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        //Asynchronously uploads from a content URI
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot -> // After the image loads, get a public downloadUrl for the image
                // and add it to the message.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        val photoUri = uri.toString()
                        // update the photoUri and doc.id to the firestore
                        documentRef.update("id", documentRef.id, "photoUri", photoUri)
                    }
            }
            .addOnFailureListener {error ->
                //write the same file will also cause error
                Log.d("Main", "Error on firebase storage", error)
            }
    }

}