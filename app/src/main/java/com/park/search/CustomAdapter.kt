package com.park.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User

class CustomAdapter(private val context: Context, private val Userlist : ArrayList<UserDTO>,private val listener: ItemClickListener) :
    RecyclerView.Adapter<CustomAdapter.ItemViewHolder>(),Filterable {

    //검색을 위한 임시 list
    private var UserSearchlist = arrayListOf<UserDTO>()


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val username = itemView.findViewById<TextView>(R.id.rv_name)
        private val useremail = itemView.findViewById<TextView>(R.id.rv_email)
        fun bind(userlist: UserDTO , context: Context){
            username.text = userlist.name;
            useremail.text = userlist.email;

        }
    }
    //초기화 구문
    init {
        this.UserSearchlist= Userlist
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomAdapter.ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item,parent,false);
        return ItemViewHolder(view);
    }

    override fun onBindViewHolder(holder: CustomAdapter.ItemViewHolder, position: Int) {
        holder.bind(UserSearchlist[position],context)
    }

    override fun getItemCount(): Int {
        return UserSearchlist.size;
    }
    //필터링하는 함수
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    UserSearchlist = Userlist
                } else {
                    val filteredList = ArrayList<UserDTO>()
                    //원하는 데이터를 검색하는 부분
                    for (user in Userlist) {
                        if (user.name.toLowerCase().contains(charString.toLowerCase()) ||
                            user.email.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(user)
                        }
                    }
                    UserSearchlist = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = UserSearchlist
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults : FilterResults) {
                UserSearchlist = filterResults.values as ArrayList<UserDTO>
                notifyDataSetChanged()
            }

        }
    }
    interface ItemClickListener {
        fun onItemClicked(item : UserDTO)
    }
}