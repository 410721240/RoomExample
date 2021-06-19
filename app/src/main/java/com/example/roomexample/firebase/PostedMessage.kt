package com.example.roomexample.firebase

import android.os.Parcelable
import com.example.roomexample.database.Scene
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize  //object serialization
data class PostedMessage(
    var id: String? =null,
    var city: String? = null,
    var name: String? = null,
    var address: String? = null,
    var description: String? = null,
    var photoUri: String? = null,
    var uploaderName: String? = null,
    var uploaderId: String? = null,
    var support: Int = 0,
    @ServerTimestamp
    var timestamp: Timestamp? = null
) : Parcelable


//extension function to do data transformation
fun PostedMessage.asScene(): Scene {
    return Scene(
        city = this.city!!,
        name = this.name!!,
        address = this.address!!,
        description = this.description!!,
        photoUri = this.photoUri!!
    )
}
