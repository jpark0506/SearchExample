package com.park.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),CustomAdapter.ItemClickListener {

    val TAG : String = "TAG"

    var Userlist = arrayListOf<UserDTO>()

    val customAdapter = CustomAdapter(this,Userlist,this)


    fun addUser(name: String, email: String) {

        if(name == "" || email ==""){
            Toast.makeText(this, "빈칸을 모두 채우세요!",Toast.LENGTH_SHORT).show()
        }else{
            var db= Firebase.firestore;
            val user = hashMapOf(
                "name" to name,
                "email" to email
            )
            db.collection("user")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
            nameinput.setText("")
            emailinput.setText("")
            getData()
        }

    }
    private fun getData() {
        rv_list.removeAllViewsInLayout()
        //리로딩시 recyclerview가 안뜨는 오류 해결
        //어댑터 세팅
        val layout = LinearLayoutManager(this)
        rv_list.adapter = customAdapter
        rv_list.layoutManager = layout
        rv_list.setHasFixedSize(true)
        Userlist.clear()
        var db = Firebase.firestore;
        Toast.makeText(this, "로딩중...",Toast.LENGTH_SHORT).show()
        db.collection("user")
                .get().addOnSuccessListener {
                    documents ->
                    for (document in documents) {
                        Userlist.add( UserDTO(document.data.get("name").toString(),document.data.get("email").toString()))
                         Log.d(TAG, "${document.id} => ${document.data.get("name")}")
                    }
                }.addOnFailureListener { Exception->
                    Log.e(TAG, "get failed with", Exception);
                    Toast.makeText(this, "로딩 실",Toast.LENGTH_SHORT).show()
                }
        customAdapter.notifyDataSetChanged();
        Toast.makeText(this, "로딩 완료",Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //추가 버튼 리스너
        adddata.setOnClickListener(ButtonListener())
        //데이터 불러오기
        getData()
        search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(charSequence: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                customAdapter.filter.filter(s)
            }
        })

    }
    inner class ButtonListener: View.OnClickListener {
        override fun onClick(p0: View?) {
            //버튼 클릭시 이벤트
            addUser(nameinput.text.toString(), emailinput.text.toString())
        }
    }

    override fun onItemClicked(item: UserDTO) {
        //여기에서 Toast, Dialog등을 사용해서 User를 클릭했을 때, 원하시는 Activity를 넣으시면 됩니다
    }
}