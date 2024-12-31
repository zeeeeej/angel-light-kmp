package com.yunext.angel.light.repo.sp

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import com.yunext.angel.light.di.json
import com.yunext.angel.light.domain.Empty
import com.yunext.angel.light.domain.poly.User
import kotlinx.coroutines.flow.Flow
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.use

internal object UserSerializer : OkioSerializer<User> {
    override val defaultValue: User = User.Empty
    override suspend fun readFrom(source: BufferedSource): User {
        return json.decodeFromString<User>(source.readUtf8())
    }

    override suspend fun writeTo(t: User, sink: BufferedSink) {
        sink.use {
            it.writeUtf8(json.encodeToString(User.serializer(), t))
        }
    }
}

class UserStore(private val produceFilePath: () -> String ) {

    private val db = DataStoreFactory.create(
        storage = OkioStorage<User>(
            fileSystem = FileSystem.SYSTEM,
            serializer = UserSerializer,
            producePath = {
                produceFilePath().toPath()
            },
        ),
    )

    val user: Flow<User> = db.data

    suspend fun saveUser(user: User) {
        db.updateData { preUser ->
            println("UserStore saveUser user:$user preUser:$preUser")
            user
        }
    }


//    private val dataStore by lazy {
//        createDataStore {
//            NAME
//        }
//    }
//
//    private fun createDataStore(producePath: () -> String): DataStore<Preferences> =
//        PreferenceDataStoreFactory.createWithPath(
//            produceFile = { producePath().toPath() }
//        )
//
    companion object {
        private const val NAME = "dice.preferences_pb"
    }
}

suspend fun UserStore.clear() {
    saveUser(User.Empty)
}



