package com.example.roomexample


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.roomexample.database.Scene
import com.example.roomexample.database.SceneDatabase
import com.example.roomexample.firebase.LoginStateLiveData
import com.example.roomexample.firebase.PostedMessage
import com.example.roomexample.network.GetService
import com.example.roomexample.network.TemperatureEntity
import com.example.roomexample.network.asTemperatureEntity
import com.example.roomexample.utilities.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

//viewmodel need to know the database dao (passed argument here) for accessing data from the database
//class MyViewModel(dataSource: SceneDatabaseDao) : ViewModel(){

//use the AndroidViewModel with default parameter: application context
class MyViewModel(application: Application) : AndroidViewModel(application) {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = LoginStateLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    //get the reference to the database dao
    private val database = SceneDatabase.getInstance(application).sceneDatabaseDao

    //a list of all scenes or matched scenes from the database (LiveData)
    val sceneList = MediatorLiveData<List<Scene>>()
    //data source 1: from database
    val fromDatabase = database.loadAllScenes()
    //data source 2: from search result
    val fromSearch = MutableLiveData<List<Scene>>()

    //hold the city weather (livedata)
    val cityWeather = MutableLiveData<TemperatureEntity?>()

    //hold the selected message from the server (livedata)
    val selectedMessage = MutableLiveData<PostedMessage>()

    //list of cities in Taiwan (the names should be match with the names used in CWB's open data)
    val cityList : Array<String> = application.resources.getStringArray(R.array.city_array)

    init {
        addDataSource()
        //initDB(application)
    }

    //add livedata source to the sceneList
    private fun addDataSource() {
        sceneList.addSource(fromDatabase) { scenes ->
            sceneList.value = scenes
        }
        sceneList.addSource(fromSearch) { scenes ->
            sceneList.value = scenes
        }
    }

    fun getFromDatabaseAgain() {
        sceneList.value = fromDatabase.value
    }

    fun searchScenes(name: String) {
        viewModelScope.launch {
            fromSearch.value = database.findScenes(name)
        }
    }

//    fun getScene(sceneId: Long): Scene? {  //get the specified scene from the sceneList
//        var target: Scene? = null
//
//        sceneList.value?.let { list ->
//            //selectedScene.value = it.find { it.id == sceneId }
//            target = list.find { it.id == sceneId }
//        }
//        return target
//    }

    fun insertScene(newScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.insertScene(newScene)
        }
    }

    fun updateScene(oldScene: Scene) {  //add a new scene into the database
        viewModelScope.launch {
            database.updateScene(oldScene)
        }
    }

    fun deleteScene(oldScene: Scene) {  //delete a scene from the database
        viewModelScope.launch {
            database.deleteScene(oldScene)
        }
    }

    fun retrieveWeather(location: String) {
        //initially set null
        cityWeather.value = null //force the UI refresh and not see the last result
        viewModelScope.launch {
            try {
                val result =
                    GetService.retrofitService.getAppData(location)
                cityWeather.value = result.asTemperatureEntity()
            } catch (e: Exception) {
                Log.d("Main", "Fail to access: ${e.message}")
            }
        }
    }

    fun increaseSupport() {
        //updating one field value does not cause the value change event
        val current = selectedMessage.value

        current?.let { message ->
            message.support = message.support.plus(1)
            selectedMessage.value = message  //must assign the whole value

            //update the new value to the server
            FirebaseService.collectionRef.document(message.id!!)
                .update("support", FieldValue.increment(1))
        }


    }

    fun deleteMessage(target: PostedMessage) {
        FirebaseService.collectionRef.document(target.id!!).delete()
    }


    private fun initDB(context: Context) {  //setup the initial database
        val resourceUri = "android.resource://"+ context.packageName +"/"
        viewModelScope.launch {
            repeat(1) {
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "長春祠",
                        "花蓮縣秀林鄉長春祠",
                        resourceUri+R.drawable.photo1_0,
                        "為紀念開闢中橫公路殉職人員所建，祠旁湧泉長年流水成散瀑，公路局取名為「長春飛瀑」，成為中橫公路具特殊意義的地標。"
                    )
                )
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "燕子口",
                        "花蓮縣秀林鄉燕子口",
                        resourceUri+R.drawable.photo1_1,
                        "燕子口步道從燕子口到靳珩橋，途中可欣賞太魯閣峽谷、壺穴、湧泉、印地安酋長岩等景觀。"
                    )
                )
                database.insertScene(
                    Scene(
                        "花蓮縣",
                        "慈母橋",
                        "花蓮縣秀林鄉慈母橋",
                        resourceUri+R.drawable.photo1_2,
                        "慈母橋是一座形狀美麗的紅色大橋，位於天祥以東三公里處的中橫公路上，為立霧溪與其支流荖西溪的匯流處。"
                    )
                )
                database.insertScene(
                    Scene(
                        "新北市",
                        "天元宮",
                        "新北市淡水無極天元宮",
                        resourceUri+R.drawable.photo0_0,
                        "擁有五層圓型寶塔的壯觀寺廟，每逢櫻花季會吸引大批人潮。"
                    )
                )
                database.insertScene(
                    Scene(
                        "臺北市",
                        "Taipei101",
                        "台北市101大樓",
                        resourceUri+R.drawable.photo0_1,
                        "台北101是超高大樓，是綠建築，是購物中心，是觀景台，更是台灣的指標。"
                    )
                )
                database.insertScene(
                    Scene(
                        "屏東縣",
                        "墾丁",
                        "屏東墾丁國家公園",
                        resourceUri+R.drawable.photo2_0,
                        "墾丁國家公園是台灣在戰後時期第一個成立的國家公園，成立於1982年。"
                    )
                )
                database.insertScene(
                    Scene(
                        "屏東縣",
                        "龍磐公園",
                        "屏東龍磐公園",
                        resourceUri+R.drawable.photo2_1,
                        "未經開發的公園，於開闊的草坪中設有荒野小徑，並坐擁一望無際的海岸風光。"
                    )
                )
            }
        }
    }

}