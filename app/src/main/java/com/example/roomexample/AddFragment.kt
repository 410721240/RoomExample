package com.example.roomexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.roomexample.database.Scene
import com.example.roomexample.databinding.FragmentAddBinding
import com.example.roomexample.utilities.hideKeyboard
import com.example.roomexample.utilities.showSnackMessage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

//fragment to add a new scene or edit an existing scene
class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var viewModel: MyViewModel
    private lateinit var imagePickUpResult: ActivityResultLauncher<Intent>
    private var editMode = false
    private var newScene = Scene("", "", "", "", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)

        //shared viewmodel with the activity
        viewModel = ViewModelProvider(
            requireActivity(),
            MyViewModelFactory(requireActivity().application)
        ).get(MyViewModel::class.java)

        //retrieve the passed argument (selected scene's id or 0 (add data))
        val args = AddFragmentArgs.fromBundle(requireArguments())
        editMode = (args.scene != null)
        if (editMode) //load the existing values
            newScene = args.scene!!.copy()

        //do data binding in the layout
        binding.scene = newScene

        //get input focus
        binding.nameEdit.requestFocus()

        //configure the spinner to select one city
        val adapter =
            ArrayAdapter(this.requireContext(), R.layout.my_spinner_layout, viewModel.cityList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cityPicker.adapter = adapter
        //set default selection
        if (newScene.city.isNotEmpty())
            binding.cityPicker.setSelection(viewModel.cityList.indexOf(newScene.city))
        //item selection handler
        binding.cityPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                newScene.city = parent?.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        with (binding.cityPicker) {
//            textSize = 50F
//            minValue = 0
//            maxValue = viewModel.cityList.size - 1
//            displayedValues = viewModel.cityList
//            if (newScene.city.isNotEmpty())
//                value = viewModel.cityList.indexOf(newScene.city)
//            wrapSelectorWheel = true
//            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//            setOnValueChangedListener { picker, oldVal, newVal ->
//                newScene.city = viewModel.cityList[newVal]
//            }
//        }

        //enable the photo pickup
        // get the photo file path returned from the intent
        imagePickUpResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val data = result.data!!
                    data.data!!.let {
                        newScene.photoUri = it.toString()
                    }
                }
            }

        binding.selButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            imagePickUpResult.launch(intent)
        }

        //hide bottom nav
        this.activity?.findViewById<BottomNavigationView>(R.id.navView)?.visibility = View.GONE

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun saveData() {
        newScene.name = binding.nameEdit.text.toString()
        newScene.address = binding.addressEdit.text.toString()
        newScene.description = binding.descriptEdit.text.toString()

        hideKeyboard(context, binding.root)
        if (checkNotEmpty()) {
            //save data into the database
            if (editMode)
                viewModel.updateScene(newScene)
            else
                viewModel.insertScene(newScene)
            //simulate the press of the back button
            activity?.onBackPressed()
        } else
            showSnackMessage(requireView(), "Data fields must be not empty")
    }

    private fun checkNotEmpty(): Boolean {
        return !(newScene.name.isEmpty() || newScene.address.isEmpty() || newScene.description.isEmpty() ||
                newScene.photoUri.isEmpty())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.file_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        // Hide the keyboard.
        hideKeyboard(context, binding.root)
    }
}