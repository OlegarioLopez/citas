package com.ole.citastatto

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ole.citastatto.data.Month
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MontViewModel: ViewModel() {
    private val _name = mutableStateOf("SIN CARGA")
    var name: State<String> = _name
    private val monthCollectionRef = Firebase.firestore.collection("Months")



     fun retrieveMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            val querySnapshot = monthCollectionRef.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val month = document.toObject<Month>()

                sb.append(month?.name)
            }
            withContext(Dispatchers.Main){_name.value =sb.toString()}

        }


    }
}