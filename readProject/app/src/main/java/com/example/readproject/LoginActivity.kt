package com.example.readproject

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.readproject.note.FragmentNote
import kotlinx.android.synthetic.main.activity_login.*
import org.litepal.LitePal

class LoginActivity : AppCompatActivity(), View.OnClickListener{
    //region 初始化dialog
    /**
     * 使用lazy初始化AlertDialog,Kotlin lazy底层就是java的Double Check单例
     */
    private val dialog  by lazy {
        AlertDialog.Builder(this).setTitle(getString(R.string.dialog_tip_text))
            .setCancelable(false)
            .setMessage(getString(R.string.login_dialog_message_text))
            .setPositiveButton(getString(R.string.confirm_text)
            ) { _: DialogInterface, _: Int ->
                startToRegisterActivity()

            }
            .setNeutralButton(getString(R.string.cancle_text),DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .create()
    }
    //endregion

    //region 跳转到注册页面
    private fun startToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.putExtra("ACCOUNT",account_edit.text.toString().trim())
        intent.putExtra("PASSWORD",password_edit.text.toString().trim())
        startActivityForResult(intent,100)
    }
    //endregion

    //region onCreate初始化一些基本控件和sp操作
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = getString(R.string.login_button_text)
        login_btn.setOnClickListener(this)
        register_btn.setOnClickListener(this)

        //从sp中取出账号，密码，以及checkBox的状态并显示到控件上
        val account = SPUtils.instance.getString("ACCOUNT")
        val password = SPUtils.instance.getString("PASSWORD")
        val checkbox = SPUtils.instance.getBoolean("CHECKBOX",false)
        account_edit.text = Editable.Factory.getInstance().newEditable(account)
        password_edit.text = Editable.Factory.getInstance().newEditable(password)
        check_box.isChecked = checkbox
    }
    //endregion

    //region 页面的点击事件
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.login_btn ->{
                if(TextUtils.isEmpty(account_edit.text.toString().trim())){
                    Toast.makeText(this,getString(R.string.account_is_empty_tip),Toast.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(password_edit.text.toString().trim())){
                    Toast.makeText(this,getString(R.string.password_is_empty_tip),Toast.LENGTH_SHORT).show()
                    return
                }
                val account = account_edit.text.toString().trim()//获取账号
                val password = password_edit.text.toString().trim()//获取密码
                val findUser = LitePal.where("userAccount = ?", account).find(UserBean::class.java)//根据账号在数据库中查询
                if(findUser.size == 0){
                    //若返回的list长度为0则该账号还未注册，弹出对话框提示用户注册
                    dialog.show()
                }else{
                    //若list不为空则该用户已注册
                    if(password != findUser[0].passWord){
                        //判断密码和账号是否匹配
                        Toast.makeText(this,getString(R.string.password_input_error_tip),Toast.LENGTH_SHORT).show()
                    }else{
                        //判断是否勾选了记住密码
                        if(check_box.isChecked){
                            SPUtils.instance.put("ACCOUNT",account_edit.text.toString().trim())
                            SPUtils.instance.put("PASSWORD",password_edit.text.toString().trim())
                            SPUtils.instance.put("CHECKBOX",true)
                        }else{
                            SPUtils.instance.put("ACCOUNT","")
                            SPUtils.instance.put("PASSWORD","")
                            SPUtils.instance.put("CHECKBOX",false)
                        }
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

            }
            R.id.register_btn ->{
                if(!TextUtils.isEmpty(account_edit.text.toString().trim()) && !TextUtils.isEmpty(password_edit.text.toString().trim())){
                    startToRegisterActivity()
                }else{
                    startActivityForResult(Intent(this, RegisterActivity::class.java),100)
                }
            }
        }
    }
    //endregion

    //region 页面回传
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == 200){
                //将registerActivity回传的账号密码显示到页面中
                val account = data?.getStringExtra("ACCOUNT")
                val password = data?.getStringExtra("PASSWORD")
                account_edit.text = Editable.Factory.getInstance().newEditable(account)
                password_edit.text = Editable.Factory.getInstance().newEditable(password)
            }
        }
    }
    //endregion
}

