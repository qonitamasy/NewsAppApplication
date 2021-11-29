package com.qonita.newsappapplication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.qonita.newsappapplication.adapter.NewsAdapter
import com.qonita.newsappapplication.adapter.onItemClickCallback
import com.qonita.newsappapplication.databinding.ActivityMainBinding
import com.qonita.newsappapplication.model.ArticlesItem
import com.qonita.newsappapplication.model.ResponseNews
import com.qonita.newsappapplication.service.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
val date = getCurreDateTime()
    var refUsers : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null
    lateinit var mainBinding: ActivityMainBinding



    private fun getCurreDateTime(): Date{
        return Calendar.getInstance().time

    }
    fun Date.toString(format: String, locale : Locale = Locale.getDefault()):String{
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)
        supportActionBar?.hide()
        mainBinding.apply {
            ibProfile.setOnClickListener(this@MainActivity)
            tvDateMain.text = date.toString("dd/MM/yyyy")
        }
       // mainBinding.ibProfile,setOnClickListener(this)
       // mainBinding.tvDateMain.text = date.toString("dd/MM/)
        getNews()
    }

    private fun getNews() {
        val country = "id"
        val apiKey = "9d97a9bc85b54dc8804f9ef68e5d3c5c\n"

        val loading = ProgressDialog.show(this, "Request Data", "Loading..")
        RetrofitConfig.getInstance().getNewsHeadLines(country,apiKey).enqueue(
            object : Callback<ResponseNews>{
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>
                ) {
                    Log.d ("Response", "Success"+ response.body()?.articles)
                    loading.dismiss()
                    if (response.isSuccessful){
                        val status = response.body()?.status
                        if (status.equals("ok")){
                            Toast.makeText(this@MainActivity,
                                "Data Success", Toast.LENGTH_SHORT).show()
                            val newsData = response.body()?.articles
                            val newsAdapter = NewsAdapter(this@MainActivity, newsData)
                            newsAdapter.setOnItemClickCallback(object : onItemClickCallback{
                                override fun onItemClicked(news: ArticlesItem) {
                                    val intent= Intent(this@MainActivity, DetailActivity::class.java)
                                    intent.putExtra(DetailActivity.EXTRA_NEWS, news)
                                }
                            })
                            mainBinding.rvMain.apply {
                                adapter = newsAdapter
                                layoutManager = LinearLayoutManager(this@MainActivity)
                                val dataHighLight = response.body()
                                Glide.with(this@MainActivity)
                                    .load(dataHighLight?.articles?.component4()?.urlToImage)
                                    .centerCrop().into(mainBinding.ivHighLight)
                                mainBinding.apply {
                                    tvTitleHighLight.text = dataHighLight?.articles?.component4()?.title
                                    tvDateHighLight.text = dataHighLight?.articles?.component4()?.publishedAt
                                    tvAuthorNameHighLight.text = dataHighLight?.articles?.component4()?.author
                                }
                            }

                        }else{
                            Toast.makeText(this@MainActivity,
                                "Data Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    Log.d("Response", "Failed:" + t.localizedMessage)
                    loading.dismiss()

                }
            }
        )
    }


    companion object{
        fun getLaunchService(from :Context)= Intent(from,
            MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")

    }
}