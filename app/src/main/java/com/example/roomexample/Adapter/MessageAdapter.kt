package com.example.roomexample.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roomexample.DiscoverFragmentDirections
import com.example.roomexample.MyViewModel
import com.example.roomexample.databinding.MessageItemBinding
import com.example.roomexample.firebase.PostedMessage
import com.example.roomexample.utilities.FirebaseService
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

//recyvlerview adapter (inherit from FirestoreRecyclerAdapter)
//options: include data source queried from the database
//each data item is modeled as PostedMessage object
class MessageAdapter(
    val view: Context,
    val viewModel: MyViewModel,
    private val options: FirestoreRecyclerOptions<PostedMessage>
) : FirestoreRecyclerAdapter<PostedMessage, MessageAdapter.ViewHolder>(options),
    SwipeHandlerInterface {

    inner class ViewHolder(val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostedMessage) {
            binding.nameText.text = item.name
            binding.cityText.text = item.city
            binding.uploader.text = item.uploaderName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MessageItemBinding.inflate(layoutInflater, parent, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.itemView.setOnClickListener {  //display the item's details
            val item = getItem(viewHolder.bindingAdapterPosition)
            viewModel.selectedMessage.value = item
            it.findNavController()
                .navigate(DiscoverFragmentDirections.actionDiscoverFragmentToShowFragment())
        }

        return viewHolder
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: PostedMessage
    ) {
        holder.bind(model)  //model = options.snapshots[position]
    }

    override fun onItemDelete(position: Int) {
        //the view has been removed out of the screen
        val deletedScene = getItem(position)
        val userId = FirebaseService.firebaseAuth.currentUser!!.uid
        //only the owner can delete data
        if (deletedScene.uploaderId != userId) {
            Toast.makeText(view, "Access deny: not the owner", Toast.LENGTH_SHORT).show()
            notifyItemChanged(position) //restore the view on the screen
        } else
            AlertDialog.Builder(view).apply {
                setTitle("Delete this scene?")
                setCancelable(false)
                setPositiveButton("Yes") { dialog, which ->
                        viewModel.deleteMessage(deletedScene) //delete the scene from the server
                }
                setNegativeButton("No") { dialog, which ->
                    notifyItemChanged(position) //restore the view on the screen
                }
                show()
            }
    }

}