package com.example.roomexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomexample.Adapter.MessageAdapter
import com.example.roomexample.Adapter.SwipeHandler
import com.example.roomexample.databinding.FragmentDiscoverBinding
import com.example.roomexample.firebase.PostedMessage
import com.example.roomexample.utilities.FirebaseService
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class DiscoverFragment : Fragment() {

    private lateinit var adapter: MessageAdapter
    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var imagePickUpResult: ActivityResultLauncher<Intent>
    private lateinit var authResult: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)

        //shared viewmodel with the activity
        viewModel = ViewModelProvider(requireActivity(),
            MyViewModelFactory(requireActivity().application)).get(MyViewModel::class.java)

        //read data from the firebase
        val query = FirebaseService.collectionRef.orderBy("city")

        val options = FirestoreRecyclerOptions.Builder<PostedMessage>()
            .setQuery(query, PostedMessage::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        //configure the recyclerview
        adapter = MessageAdapter(requireActivity(), viewModel, options)
        //binding.progressBar.visibility = ProgressBar.INVISIBLE
        val manager = LinearLayoutManager(this.activity)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this.activity, DividerItemDecoration.VERTICAL))

        //setup swipe handler
        val swipeHandler = ItemTouchHelper(SwipeHandler(adapter,0,(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)))
        swipeHandler.attachToRecyclerView(binding.recyclerView)

        //deal with the return of firebase UI authentication
        authResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK)
                Log.d("Main", "login success")
            else
                Log.d("Main", "login fail")
        }

        //enable options menu
        setHasOptionsMenu(true)
        //show bottom nav
        this.activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.VISIBLE

        return binding.root
    }


    // called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()  //monitor the login state
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer {
            if (it != MyViewModel.AuthenticationState.AUTHENTICATED)
                launchSignIn()
        })
    }

    private fun launchSignIn() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity
        val loginIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        authResult.launch(loginIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> AuthUI.getInstance().signOut(requireContext())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        adapter.stopListening()  //stop reading data from the database
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()  //star reading data from the database
    }

}