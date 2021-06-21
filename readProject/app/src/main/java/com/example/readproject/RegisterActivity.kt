package com.example.readproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import org.litepal.LitePal

class RegisterActivity : AppCompatActivity() ,View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //region 设置appCompatActivity的actionBar
        title = getString(R.string.register_button_text)//给actionBar添加title
        val actionBar = supportActionBar//获取AppCompatActivity的默认的actionBar
        //给actionBar添加返回按钮
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //endregion

        //region获取从LoginActivity传过来的账号密码
        val intent = intent
        val account : String? = intent.getStringExtra("ACCOUNT")
        val password : String? = intent.getStringExtra("PASSWORD")
        if(!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
            account_edit.text = Editable.Factory.getInstance().newEditable(account)
            password_edit.text = Editable.Factory.getInstance().newEditable(password)
        }
        //endregion

        register_button.setOnClickListener(this)
    }

    //region actionBar的返回按钮的点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    //region registerActivity页面点击事件
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.register_button ->{
                if(TextUtils.isEmpty(account_edit.text.toString().trim())){
                    Toast.makeText(this,getString(R.string.account_is_empty_tip), Toast.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(password_edit.text.toString().trim())){
                    Toast.makeText(this,getString(R.string.password_is_empty_tip), Toast.LENGTH_SHORT).show()
                    return
                }
                val account = account_edit.text.toString().trim()//获取账号
                val password = password_edit.text.toString().trim()//获取密码
                val findUser = LitePal.where("name = ?", account).find(User::class.java)//根据账号在数据库中查询
                if(findUser.size == 0){
                    //该账号在数据库中没有才可以注册
                    val newUser = User(account,password)
                    newUser.save()//将该用户插入数据库
                    Toast.makeText(this,getString(R.string.register_success_text_tip),Toast.LENGTH_SHORT).show()
                    //将用户的账号密码回传给LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("ACCOUNT",account)
                    intent.putExtra("PASSWORD",password)
                    setResult(200,intent)
                    this.finish()
                }else{
                    //该账号在数据库中存在让用户重新输入并清空账号输入框
                    account_edit.text.clear()
                    Toast.makeText(this,getString(R.string.account_exist_text_tip),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //endregion
}