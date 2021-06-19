package com.example.roomexample.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roomexample.firebase.PostedMessage
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Scene(
    var city: String,
    var name: String,
    var address: String,
    var photoUri: String,
    var description: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
): Parcelable
//{
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0L
//    //data fields declared here will not be observed in the livedata
//    //these data fields need not be initiated when created
//}

//extension function to do data transformation
fun Scene.asPostedMessage(): PostedMessage {
    return PostedMessage(
        city = this.city,
        name = this.name,
        address = this.address,
        description = this.description,
        photoUri = this.photoUri
    )
}
