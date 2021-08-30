package org.techtown.wishmatching.Chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.provider.PicassoProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import org.techtown.wishmatching.Authentication
import org.techtown.wishmatching.MainActivity
import org.techtown.wishmatching.Mypage.DealSituation.MyItemMoreInfoActivity
import org.techtown.wishmatching.R
import org.techtown.wishmatching.RealtimeDB.ChatMessage
import org.techtown.wishmatching.RealtimeDB.User
import java.text.SimpleDateFormat

//ㅇㅇㅇ
class ChatLogActivity : AppCompatActivity() {


    var adapter = GroupAdapter<ViewHolder>()
    companion object {
        var toUser: User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        //        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if (toUser != null) {
            supportActionBar?.title = toUser?.username
        }
        //setupDummyData()

        ListenForMessages()

        send_button_chat_log.setOnClickListener {   // send버튼 눌렀을 때
            performSendMessage()
        }

        btn_my_wish.setOnClickListener {
            val fromId = FirebaseAuth.getInstance().uid.toString() // 현재 사용자
            val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
            var post_value = usersDb.child(toUser!!.uid).child("connections").child("match")
            var post_value2 = usersDb.child(fromId!!).child("connections").child("match")
            var matchPostId2 : Task<DataSnapshot> = post_value2.get()
            var matchPostId : Task<DataSnapshot> = post_value.get()

            var my_like_post:String = ""
            var partner_like_post:String = ""

            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
            firestore = FirebaseFirestore.getInstance()  //초기화
            firestore!!.collection("Matching_Post")
                .document("${fromId.toString()}"+"${toUser!!.uid.toString()}")
                .get()
                .addOnSuccessListener {
                    my_like_post= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
//                    Toast.makeText(this,"$my_like_post",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MyItemMoreInfoActivity::class.java)
                    intent.putExtra("doc_id", my_like_post)
                    startActivity(intent)
                }


        }

        btn_partner_wish.setOnClickListener {
            val fromId = FirebaseAuth.getInstance().uid.toString() // 현재 사용자
            val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
            var post_value = usersDb.child(toUser!!.uid).child("connections").child("match")
            var post_value2 = usersDb.child(fromId!!).child("connections").child("match")
            var matchPostId2 : Task<DataSnapshot> = post_value2.get()
            var matchPostId : Task<DataSnapshot> = post_value.get()

            var my_like_post:String = ""
            var partner_like_post:String = ""

            var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
            firestore = FirebaseFirestore.getInstance()  //초기화
            firestore!!.collection("Matching_Post")
                .document("${toUser!!.uid.toString()}"+"${fromId.toString()}")
                .get()
                .addOnSuccessListener {
                    partner_like_post= it.data?.get("matchPostId")?.toString() ?: return@addOnSuccessListener
//                    Toast.makeText(this,"$partner_like_post",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MyItemMoreInfoActivity::class.java)
                    intent.putExtra("doc_id", partner_like_post)
                    startActivity(intent)
                }

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_chattinglog,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.action_exit -> {
                var builder = AlertDialog.Builder(this)
                    builder.setTitle("채팅방 나가기")
                    builder.setMessage("채팅방을 나가면 대화 내역이 삭제됩니다. 나가겠습니까?")
                    builder.setPositiveButton("예") { dialog, which ->

                        val fromId= FirebaseAuth.getInstance().uid
                        val toId = toUser?.uid
                        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
                        val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

                        ref.removeValue()
                        latestMessageFromRef.removeValue()

                        this.finish()
                        var intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("취소",null)
                        .create()
                builder.show()


                true
            }
            R.id.action_matchInfo -> {
                val fromId = FirebaseAuth.getInstance().uid // 현재 사용자
                val usersDb = FirebaseDatabase.getInstance().getReference().child("matching-users")
                var post_value = usersDb.child(toUser!!.uid).child("connections").child("match")
                var post_value2 = usersDb.child(fromId!!).child("connections").child("match")
                var matchPostId2 : Task<DataSnapshot> = post_value2.get()
                var matchPostId : Task<DataSnapshot> = post_value.get()

                var my_like_post:String = ""
                var partner_like_post:String = ""

                var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
                firestore!!.collection("Matching_Post")
                firestore!!.collection("Matching_Post")
                    .document("$fromId"+"${toUser!!.uid}")
                    .get()
                    .addOnSuccessListener {

                    }
//                    .addOnSuccessListener { documents->
//                        for(document in documents){
//                            mypage_location.text = document.data["area"].toString()
//                            mypage_nickname.text = document.data["nickname"].toString()
//                            val image = storage!!.getReferenceFromUrl(document.data["imageUrl"].toString())
//                            displayImageRef(image, img_myPage_profileImg)
//
//                        }
//                    }




                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun ListenForMessages() {
        val fromId= FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
//        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChattingFragment.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser,chatMessage.timestamp,chatMessage.nickname)) // 채팅 내용 리사이클 뷰에 띄우기
                        Log.d("ChatMessage", "보내는사람:${fromId}")

                    } else {

                        toUser?.let { ChatToItem(chatMessage.text, it,chatMessage.timestamp,chatMessage.nickname) }?.let { adapter.add(it) }
                        Log.d("ChatMessage", "받는 사람:${toId}")

//                        val channel_name = "match_channel"
//                        val channelId = "MATCH_ID"
//                        val channel_description = "test"
//                        val notificationBuilder = NotificationCompat.Builder(this@ChatLogActivity, channelId)
//                            .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
//                            .setContentTitle("매칭이 성사되었습니다.") // 제목
//                            .setContentText("채팅방이 생성되었습니다.") // 메시지 내용
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            .setAutoCancel(true)
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                            val importance = NotificationManager.IMPORTANCE_DEFAULT
//                            val channel = NotificationChannel(channelId, channel_name, importance).apply {
//                                description = channel_description
//                            }
//                            // Register the channel with the system
//                            val notificationManager: NotificationManager =
//                                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                            notificationManager.createNotificationChannel(channel)
//                        }
//
//                        with(NotificationManagerCompat.from(this@ChatLogActivity)) {
//                            // notificationId is a unique int for each notification that you must define
//                            notify(8154, notificationBuilder.build())
//                        }


//                        adapter.add(ChatToItem(chatMessage.text,toUser!!)) // 본문 강의 코드
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage() {  //보낸 메세지 파이어베이스 보내기
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        if (fromId == null) return
//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        var user_nickname :String = ""

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        var firestore : FirebaseFirestore? = null   // 데이터베이스를 사용할 수 있도록
        firestore = FirebaseFirestore.getInstance()  //초기화
        firestore!!.collection("user")
            .whereEqualTo("uid", Authentication.auth.currentUser!!.uid).limit(1)
            .get()
            .addOnSuccessListener { documents->
                for(document in documents){
                    user_nickname = document.data["nickname"].toString()
                }
            }


        val chatMessage =
            toUser?.let {
                ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis(),
                    it.username)
            }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatMessage", "채팅 메세지 저장:${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)

        val latestMessageFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageFromRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}


class ChatFromItem(val text:String,val user: User,val time: Long,val nickname: String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text =text  //채팅 입력->말풍선에 반영

        var time_hours = SimpleDateFormat("HH").format(time).toString()
        if(time_hours.toInt()>12 ){
            time_hours = (time_hours.toInt()-12).toString()
        }
        val time_minutes = SimpleDateFormat("mm").format(time).toString()
        val time_AP= SimpleDateFormat("aa").format(time).toString()
        val time_string = time_AP+" "+time_hours+":"+time_minutes.toString()
        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        PicassoProvider.get().load(uri).into(targetImageView)

        viewHolder.itemView.textview_from_chat_time.text = time_string.toString()
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String, val user:User,val time: Long,val nickname:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text   // 채팅 입력->말풍선에 반영

        var time_hours = SimpleDateFormat("HH").format(time).toString()
        if(time_hours.toInt()>12 ){
            time_hours = (time_hours.toInt()-12).toString()
        }
        val time_minutes = SimpleDateFormat("mm").format(time).toString()
        val time_AP= SimpleDateFormat("aa").format(time).toString()
        val time_string = time_AP+" "+time_hours+":"+time_minutes
        var uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        PicassoProvider.get().load(uri).into(targetImageView)

        viewHolder.itemView.textview_to_chat_time.text = time_string.toString()
        viewHolder.itemView.textview_to_chat_nickname.text = nickname.toString()
//            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time).toString()
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }



}