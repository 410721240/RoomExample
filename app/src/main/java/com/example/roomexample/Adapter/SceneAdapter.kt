package com.example.roomexample.Adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.roomexample.ListFragmentDirections
import com.example.roomexample.MyViewModel
import com.example.roomexample.database.Scene
import com.example.roomexample.databinding.ItemLayoutBinding

//adapter class for the recyclerview (use ListAdapter internally)
//passed arguments: view for display the alertdialog, viewModel for accessing the database
class SceneAdapter(val view: Context, val viewModel: MyViewModel) : ListAdapter<Scene, SceneAdapter.ViewHolder>(BallDiffCallback()), SwipeHandlerInterface {

    class BallDiffCallback : DiffUtil.ItemCallback<Scene>() {
        override fun areItemsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Scene, newItem: Scene): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLayoutBinding.inflate(layoutInflater, parent, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.itemView.setOnClickListener {  //display the item's details
            //get the selected scene
            val selected = getItem(viewHolder.bindingAdapterPosition)
            it.findNavController()
                .navigate(ListFragmentDirections.actionListFragmentToDetailFragment(selected))
        }
        viewHolder.itemView.setOnLongClickListener {  //edit the item
            //get the selected scene
            val selected = getItem(viewHolder.bindingAdapterPosition)
            it.findNavController().navigate(ListFragmentDirections.actionListFragmentToAddFragment(selected))
            true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scene = getItem(position)
        holder.binding.scene = scene
        holder.binding.executePendingBindings()
    }

    override fun onItemDelete(position: Int) {
        //the view has been removed out of the screen
        val deletedScene = getItem(position)
        AlertDialog.Builder(view).apply {
            setTitle("Delete this scene?")
            setCancelable(false)
            setPositiveButton("Yes") {dialog, which ->
                viewModel.deleteScene(deletedScene) //delete the scene from the database
            }
            setNegativeButton("No") {dialog, which ->
               notifyItemChanged(position) //restore the view on the screen
            }
            show()
        }
    }
}

