package com.proyectofinal.batallanavalkt.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.User
import java.io.File

class UsersAdapter(private val listaUser: MutableList<User>,
                   private val userClick: (Int) -> Unit):
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>()  {

    var users: List<User> = emptyList()

    fun setData(list: List<User>){
        users = list
        notifyDataSetChanged()
    }

    class UsersViewHolder(item: View): RecyclerView.ViewHolder(item){
        private lateinit var mStorage: FirebaseStorage
        private lateinit var mReference: StorageReference
        private var db = FirebaseFirestore.getInstance()

        val userNick = item.findViewById(R.id.rivalnicklist) as TextView
        val usersEmail = item.findViewById(R.id.rivalemaillist) as TextView
        val userimg = item.findViewById(R.id.rivalimagelist) as ImageView
        fun bindGame(users: User){
            userNick.text=users.nick
            usersEmail.text=users.email
            var bitmap: Bitmap? = null
            mStorage = FirebaseStorage.getInstance()
            mReference = mStorage.reference

            val refnick = db.collection("users").document(users.email.lowercase()).get()

            refnick.addOnSuccessListener { document ->
                if(document != null){

                    val imgRef = mReference.child("images/${users.email.lowercase()}")
                    val localfile = File.createTempFile("tempImg","jpg")

                    imgRef.getFile(localfile).addOnSuccessListener {
                        bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        val options = RequestOptions()
                        options.centerCrop().fitCenter()
                        Glide.with(itemView.context).load(bitmap).apply(options).into(userimg)

                    }
                }else{
                    println("Este es print de error")
                }
            }.addOnFailureListener{ exeption ->
                println(exeption)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usergame, parent, false) as androidx.constraintlayout.widget.ConstraintLayout
        return UsersViewHolder(item)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val users =  listaUser[position]
        holder.bindGame(users)
        holder.itemView.setOnClickListener{userClick(position)}
    }

    override fun getItemCount(): Int {
        return listaUser.size
    }




}