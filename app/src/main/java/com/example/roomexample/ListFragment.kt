package com.example.roomexample

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomexample.Adapter.SceneAdapter
import com.example.roomexample.Adapter.SwipeHandler
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.databinding.ListFragmentBinding
import com.example.roomexample.utilities.hideKeyboard
import com.google.android.material.bottomnavigation.BottomNavigationView

//fragment with a recyclerview to show a list of scenes
class ListFragment : Fragment() {

    private lateinit var binding: ListFragmentBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false)

        //get the shared viewModel associated with the activity
        viewModel = ViewModelProvider(
            requireActivity(),
            MyViewModelFactory(requireActivity().application)
        ).get(MyViewModel::class.java)

        //setup RecyclerView
        val layoutManager = LinearLayoutManager(this.activity)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = SceneAdapter(requireActivity(), viewModel) //based on ListAdapter
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this.activity,
                DividerItemDecoration.VERTICAL
            )
        )

        //setup swipe handler
        val swipeHandler = ItemTouchHelper(
            SwipeHandler(
                adapter,
                0,
                (ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            )
        )
        swipeHandler.attachToRecyclerView(binding.recyclerView)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //observe any changes on the data source of the recylerview
        //sceneList is a livedata return by the database query
        viewModel.sceneList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)  //submit the up-to-date sceneList to the recyclerView
            }
        })

        //enable options menu
        setHasOptionsMenu(true)

        //show bottom nav
        this.activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.VISIBLE

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.action_menu, menu)

        // Initialize Search View
        searchView = menu.findItem(R.id.searchView)?.actionView as SearchView

        searchView.setOnSearchClickListener {
            //hide bottom nav
            activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.GONE
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //trigger when press enter with non-empty query text
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard(context, binding.root)
                viewModel.searchScenes(query!!)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                viewModel.getFromDatabaseAgain()
                searchView.onActionViewCollapsed()
                hideKeyboard(context, binding.root)
                //show bottom nav
                activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility =
                    View.VISIBLE
                return true
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return NavigationUI.
//               onNavDestinationSelected(item, requireView().findNavController())
//               || super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.addScene -> requireView().findNavController()
                .navigate(ListFragmentDirections.actionListFragmentToAddFragment(null))
        }
        return super.onOptionsItemSelected(item)
    }

}