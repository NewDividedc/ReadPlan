package PlanDatabase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class MyPlanHelper(val context: Context,name:String,version:Int) : SQLiteOpenHelper(context,name,null,version) {



    val sqlCreate = "create table Plan (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            "time Text , " +
            "topic text, " +
            "content text, " +
            "is_warn Int,"+
            "is_finish Int) "


    val sqlCreate2 = "create table Card (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            "time Text ) "


    override fun onCreate(db: SQLiteDatabase) {
        Log.e("PlanDataBase", "onCreate")

        db.execSQL(sqlCreate)

        db.execSQL(sqlCreate2)

        Log.d("make_database","ok")
        //Toast.makeText(context,"database ok",Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
