package com.example.mychat.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychat.firebaseUtils.Constants.FLAG_UPDATE
import com.example.mychat.firebaseUtils.Resource
import com.example.mychat.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class MainViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersReference = Firebase.firestore.collection("users")
    private val storageRef = Firebase.storage.getReference("uploads")


    private val currentUserDocRef: DocumentReference
        get() = Firebase.firestore.document(
            "users/${
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )

    private val chatChannelsCollRef = Firebase.firestore.collection("chatChannels")

    val creatUserResult: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
    val loginUserResult: MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
    val users: MutableLiveData<MutableList<User?>> = MutableLiveData()
    val messages: MutableLiveData<MutableList<Message?>> = MutableLiveData()
    val chats : MutableLiveData<MutableList<Chat?>> = MutableLiveData()


    fun registerUser(email: String, password: String, name: String) {
        if (email.isNotEmpty() || password.isNotEmpty() || name.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = auth.createUserWithEmailAndPassword(email, password).await().user
                    creatUserResult.postValue(Resource.Success(user))

                    val fireStoreUser = User(name, user!!.uid, email)
                    usersReference.document(user.uid).set(fireStoreUser)


                } catch (e: Exception) {
                    creatUserResult.postValue(Resource.Error(message = e.message))
                }
            }
        }
    }

    fun logoutUser(){
        auth.currentUser?.let {
            auth.signOut()
        }
    }

    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() || password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {

                try {

                    val user = auth.signInWithEmailAndPassword(email, password).await().user
                    loginUserResult.postValue(Resource.Success(user))


                } catch (e: Exception) {
                    loginUserResult.postValue(Resource.Error(message = e.message))
                }

            }
        }
    }

    fun getCurrentUserId(): String {
        return auth.currentUser!!.uid
    }

    fun getAllUsers() {

        CoroutineScope(Dispatchers.IO).launch {
            val querySnapshot = usersReference.get().await()
            val list = mutableListOf<User?>()
            for (document in querySnapshot.documents) {
                list.add(document.toObject(User::class.java))
            }
            users.postValue(list)

        }

    }

    fun uploadToStorageAndSend(flag:Int,uri: Uri, otherUserId: String = ""){

        val fileRef = storageRef.child(uri.pathSegments.last())
        CoroutineScope(Dispatchers.IO).launch {
            val uploadTask = fileRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful){
                    Log.e("uploadImage", "Uploading failed")
                }
                fileRef.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result.toString()
                    if(flag == FLAG_UPDATE){
                        updateUser(picture = downloadUrl)
                    } else {
                        val message = Message(downloadUrl,getCurrentUserId(),Calendar.getInstance().time,MessageType.IMAGE)
                        sendMessage(otherUserId, message)
                    }

                } else {
                    Log.e("uploadImage", "Uploading failed")
                }

            }
        }


    }

    private suspend fun getOrCreateChatChannel(otherUserId: String): String {


        val channel = currentUserDocRef.collection("chatChannels").document(otherUserId).get().await()
        if (channel.exists()) {
            return channel["channelId"] as String
        } else {

            val currentUserId = auth.currentUser!!.uid
            val newChannel = chatChannelsCollRef.document()
            newChannel.set(ChatChannel(currentUserId, otherUserId))

            currentUserDocRef.collection("chatChannels").document(otherUserId)
                .set(mapOf("channelId" to newChannel.id))


            usersReference.document(otherUserId).collection("chatChannels").document(currentUserId)
                .set(mapOf("channelId" to newChannel.id))

            return newChannel.id
        }

    }


    fun sendMessage(otherUserId: String, message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            val channelId = getOrCreateChatChannel(otherUserId)
            chatChannelsCollRef.document(channelId).collection("messages").add(message)
        }
    }

    fun attachListener(otherUserId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("In attach listener", "In coroutine scope")
            val channelId = getOrCreateChatChannel(otherUserId)
            chatChannelsCollRef.document(channelId).collection("messages")
                .addSnapshotListener { value, error ->
                    Log.d("In attach listener", "In add snapshot Listener")

                    error?.let {
                        Log.e("ViewModel", error.message.toString())
                    }
                    value?.let {
                        for (docChanged in value.documentChanges) {
                            if (docChanged.type == DocumentChange.Type.ADDED) {
                                messages.value?.apply {
                                    Log.d("In attach listener", "changing messages livedata")

                                    add(docChanged.document.toObject(Message::class.java))
                                    messages.postValue(this)
                                } ?: run{
                                    val newMessage = docChanged.document.toObject(Message::class.java)
                                    val list: MutableList<Message?> = mutableListOf()
                                    list.add(newMessage)
                                    messages.postValue(list)
                                }
                            }
                        }

                    }
                }

        }
    }

    fun getOldMessages(otherUserId: String){
        CoroutineScope(Dispatchers.IO).launch {
            val channelId = getOrCreateChatChannel(otherUserId)
            val messagesSnapShot = chatChannelsCollRef.document(channelId).collection("messages").orderBy("time").get().await()
            val list = mutableListOf<Message?>()

            for (message in messagesSnapShot.documents){
                list.add(message.toObject(Message::class.java))
            }
            messages.postValue(list)

        }
    }

    fun updateUser(status: String = "", picture: String = "", name: String = ""){
        val updatesMap = mutableMapOf<String,String>()
        if (status != ""){
            updatesMap["status"] = status
        }
        if (picture != ""){
            updatesMap["picturePath"] = picture
        }
        if (name != ""){
            updatesMap["name"] = name
        }
        currentUserDocRef.update(updatesMap as Map<String, Any>)
    }

    fun getCurrentUser(userId: String, onComplete: (user: User) -> Unit){

        CoroutineScope(Dispatchers.IO).launch {
            val document = usersReference.document(userId).get().await()
            val user = document?.toObject(User::class.java)
            withContext(Dispatchers.Main){
                user?.let { onComplete(it) }
            }

        }
    }

    fun getAllChats(){
        CoroutineScope(Dispatchers.IO).launch {
            val chatList = mutableListOf<Chat?>()
           val channels = currentUserDocRef.collection("chatChannels").get().await()
            if (!channels.isEmpty){
                for (channel in channels.documents){
                    val channelId = channel["channelId"] as String
                    val document = chatChannelsCollRef.document(channelId).get().await()
                    val userId: String = if (document["firstUserId"] as String == getCurrentUserId()){
                        document["secondUserId"] as String
                    } else{
                        document["firstUserId"] as String
                    }
                    val userDocument = usersReference.document(userId).get().await()
                    val user = userDocument?.toObject(User::class.java)
                    val messages = chatChannelsCollRef.document(channelId).collection("messages")
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(1).get().await()
                    if (messages.isEmpty)
                        return@launch

                    val message = messages.documents[0].toObject<Message>()
                    val chat = Chat(user,message)
                    chatList.add(chat)

                }
                chats.postValue(chatList)
            }

        }
    }




}