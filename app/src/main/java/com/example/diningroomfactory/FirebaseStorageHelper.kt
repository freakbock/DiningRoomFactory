package com.example.diningroomfactory

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class FirebaseStorageHelper {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val imagesRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("images")

    /**
     * Загрузка изображения в Firebase Storage и возврат ссылки на него.
     */
    fun uploadImage(imagePath: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val file = Uri.fromFile(File(imagePath))
        val imageStorageRef = storageRef.child("images/${file.lastPathSegment}")

        imageStorageRef.putFile(file).addOnSuccessListener {
            imageStorageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                saveImageLinkToDatabase(downloadUrl, onSuccess, onFailure)
            }
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    /**
     * Сохранение ссылки на изображение в Realtime Database.
     */
    private fun saveImageLinkToDatabase(downloadUrl: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val key = imagesRef.push().key
        if (key != null) {
            imagesRef.child(key).setValue(downloadUrl).addOnSuccessListener {
                onSuccess(downloadUrl)
            }.addOnFailureListener {
                onFailure(it)
            }
        } else {
            onFailure(Exception("Failed to generate key for image link"))
        }
    }

    /**
     * Скачивание изображения по ссылке из Firebase Storage.
     */
    fun downloadImage(context: Context, imageUrl: String, localFile: File, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val imageStorageRef = storage.getReferenceFromUrl(imageUrl)

        imageStorageRef.getFile(localFile).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

}
