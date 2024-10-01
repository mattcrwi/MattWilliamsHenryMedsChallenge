package com.example.henrymedschallenge.repository

import com.example.henrymedschallenge.network.ServerApi
import com.example.henrymedschallenge.network.ServerApiInterface

class NetworkRepository : NetworkRepositoryInterface {
    companion object {
        @Volatile
        private var instance: NetworkRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: NetworkRepository().also { instance = it }
            }

    }

    //This would be where we would inject the server API
    //It would require OKHTTP and a JSON serializer configuration
    override val serverApi = ServerApi()

}

interface NetworkRepositoryInterface {
    val serverApi: ServerApiInterface
}