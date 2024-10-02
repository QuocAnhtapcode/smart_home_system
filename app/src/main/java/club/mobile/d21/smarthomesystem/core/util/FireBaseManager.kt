package club.mobile.d21.smarthomesystem.core.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    private var userId: String = ""
    private lateinit var database: DatabaseReference

    fun setUserId(uid: String) {
        if (uid.isNotEmpty()) {
            userId = uid
            database = FirebaseDatabase.getInstance().reference.child(uid)
        }
    }

    fun getDatabaseReference(): DatabaseReference {
        if (!this::database.isInitialized) {
            throw IllegalStateException("Database is not initialized. Please set user ID first.")
        }
        return database
    }

    fun isUserIdSet(): Boolean {
        return this::database.isInitialized
    }
}
