package com.example.readproject

import PlanDatabase.MyPlanHelper
import android.app.Activity
import android.content.Context
import org.litepal.LitePal
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.readproject.note.FragmentNote
import com.example.readproject.note.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main2.view.*
import kotlinx.android.synthetic.main.bar_header.view.*

class MainActivity : AppCompatActivity() {
    private val myViewModel by viewModels<NoteViewModel>()
    var account:String?=null
    var CAMERA_CODE = 0
    var picture_scene_img: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {

//plan的数据库创建：

      val dbHelper = MyPlanHelper(this ,"plan.db",1)
        val db = dbHelper.writableDatabase
        val dbHelper2 = MyPlanHelper(this ,"card.db",1)
        val db2 = dbHelper.writableDatabase

        //书架数据库建立：
        val helper = DatabaseHelper(this,"ReadProject.db",1)
        val helperdb = helper.writableDatabase

        val cursor = helperdb?.query("books",null,"bid=?", arrayOf("1"),null,null,null)

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                Toast.makeText(this, "已经初始化数据库", Toast.LENGTH_SHORT).show()

            }
            else{
                initdata()
                Toast.makeText(this, "初始化成功", Toast.LENGTH_SHORT).show()
            }
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //setContentView(R.layout.bar_header)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.user)
        setSupportActionBar(toolbar)
        replaceFragment(FragmentPlan())
        initActionBarDrawer()
        initListener()

        //if()
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frag,fragment)
        transaction.commit()
        transaction.addToBackStack(null)
    }

    private fun initActionBarDrawer() {
        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //Toast.makeText(this, "extraData", Toast.LENGTH_SHORT).show()
    }

    private fun initListener() {
        /**
         * 搜索框点击事件
         */
        searchBookInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!"".equals(s.toString())){
                    search_delete.visibility=View.VISIBLE
                }
                else{
                    search_delete.visibility=View.GONE
                }
            }
        })

        search_delete.setOnClickListener {
            searchBookInput.setText("")
            search_delete.visibility=View.GONE
        }


        /**
         * 搜索框搜索事件
         */
        searchBookInput.setOnEditorActionListener(object:TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId==EditorInfo.IME_ACTION_SEARCH){
                    if(searchBookInput.text.isEmpty()){
                        Toast.makeText(applicationContext,"书名不能为空！",Toast.LENGTH_SHORT).show()
                    }
                    else if (BookDao(this@MainActivity).queryBookbyName(searchBookInput.text.toString())==null){
                        Toast.makeText(applicationContext,"搜索不到该书！",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent=Intent(this@MainActivity,BookDetailActivity::class.java)
                        intent.putExtra("bid",BookDao(this@MainActivity).queryBookbyName(searchBookInput.text.toString())!!.bid)
                        intent.putExtra("uid",5)
                        startActivity(intent)
                    }
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm!!.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
                return true
            }
        })

        /**
         * 侧边栏点击事件
         */
        nav_view.getHeaderView(0).useraccount.text = SPUtils.instance.getString("ACCOUNT")
        nav_view.setNavigationItemSelectedListener {

            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_comment -> {
                    val intent=Intent(this,MyCommentActivity::class.java)
                    intent.putExtra("uid",5)
                    startActivity(intent)
                    true
                }
                R.id.nav_scan -> {
                    Toast.makeText(getApplicationContext(), "扫一扫", Toast.LENGTH_SHORT).show()
                    this.takePhoto()
                    true
                }
                R.id.nav_set -> {
                    Toast.makeText(getApplicationContext(), "通知设置", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_safety -> {
                    Toast.makeText(getApplicationContext(), "账号安全", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_help -> {
                    Toast.makeText(getApplicationContext(), "帮助", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(getApplicationContext(), "退出登录", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
            }//关闭侧边栏
            drawer_layout.closeDrawer(GravityCompat.START)

            true

        }
        bnv.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.plan->{
                    replaceFragment(FragmentPlan())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bookshelf->{
                    replaceFragment(FragmentShelf())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bookhome->{
                    replaceFragment(FragmentHome())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.note->{
//
                    replaceFragment(FragmentNote())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

    }
    fun clickMessage(view:View){
        val intent = Intent(this, MessageActivity::class.java)
        startActivity(intent)
    }
    private fun takePhoto() {

        val state: String = Environment.getExternalStorageState() //拿到sdcard是否可用的状态码

        if (state == Environment.MEDIA_MOUNTED) {   //如果可用
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_CODE)
        } else {
            Toast.makeText(this, "sdcard不可用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_CODE && resultCode== RESULT_OK){
            var bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            //picture_scene_img?.setImageBitmap(bitmap)
            nav_view.nav_view.getHeaderView(0).headimage.setImageBitmap(bitmap)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev!!)) {//点击的是其他区域，则调用系统方法隐藏软键盘
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm!!.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }


    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null) {
            when (v.id) {
                R.id.searchBookInput -> {
                    val leftTop = intArrayOf(0, 0)
                //获取输入框当前的location位置
                    v!!.getLocationInWindow(leftTop)
                    val left = leftTop[0]
                    val top = leftTop[1]
                    val bottom = top + v!!.getHeight()
                    val right = DisplayUtil.getScreenWidth(this)
                    return if (event.x > left && event.x < right
                            && event.y > top && event.y < bottom) {
                 // 点击的是输入框区域，保留点击EditText的事件
                        false
                    } else {
                        true
                    }
                }
                else -> {
                    return false
                }
            }
        }
        return false
    }


    fun initdata(){
        //推荐
        val book1=BooksItemOne(1,"https://img1.doubanio.com/view/subject/s/public/s33849778.jpg",
            "美丽黑暗","[法] 法比安·韦尔曼",
            "中国纺织出版社",
            "一天，沉浸在幸福时光里的优雅公主奥罗拉，发现自己生活的地方开始滴落湿软、黏稠的液体。即将被不明液体淹没的她，和同一屋檐下的伙伴们纷纷逃出那个曾经的庇护所。随后，这些外表可爱、性格迥异的小人儿们，为了活下去而相爱或相杀，漫无目的地流浪在生存条件瞬息万变的森林里，然后接二连三地死去……",
            "8.9","2021-4")
        val book2=BooksItemOne(2,"https://img2.doubanio.com/view/subject/s/public/s33891922.jpg",
            "数星星的夜","尹东柱",
            "江苏凤凰文艺出版社","他生于中国，死于日本。他远离故土，失了姓氏，却以笔为戈，在28年的短暂生命中，用自己民族的语言写作，以最单纯的文字，为最残酷的时代证言。本书收录了朝鲜族诗人尹东柱的91首诗歌和4篇散文，道出了在日治的黑暗年代，他对故土、人民的热爱，对独立、自由的向往，以及对生活细致入微的观察。",
            "8.6","2021-5-5")
        val book3=BooksItemOne(3,"https://img9.doubanio.com/view/subject/s/public/s26018916.jpg",
            "神雕侠侣","金庸",
            "生活·读书·新知三联书店",
            "《神雕侠侣》是金庸作品集之一。其主人公杨过自然而然地走上了非正统的人生道路，入了“道流”。其特点是“至情至性，实现自我”，即把个人的利益、情感、个性及人格尊严置于人生首位，作为首要目标，亦作为待人处事，评价是非的首要原则。书中将杨过对郭靖的杀父之仇与疼惜之恩难以取舍的复杂心理刻画得维妙维肖；他与“姑姑”小龙女的情感纠葛和他对江湖世事的渴望又令他挣扎不已……",
            "8.9","1994-5")
        val book4=BooksItemOne(4,"https://img1.doubanio.com/view/subject/s/public/s24598159.jpg",
            "众病之王","[美] 悉达多·穆克吉",
            "中信出版社",
            "《众病之王：癌症传》是一部饱含人文主义色彩的社科文化著作。作者悉达多•穆克吉历时六年，凭借翔实的历史资料、专业文献、媒体报道、患者专访等众多信息，向读者阐述了癌症的起源与发展，人类对抗癌症、预防癌症的斗争史。作者借由医学、文化、社会、政治等视角透露出一种社会化关怀；生动、文学性的写作手法展现出鲜活的人物和历史事件，让读者为之动容。",
            "9.1","2013-2")
        val book5=BooksItemOne(5,"https://img9.doubanio.com/view/subject/s/public/s3248016.jpg",
            "基督山伯爵","大仲马",
            "上海译文出版社",
            "小说以法国波旁王朝和七月王朝两大时期为背景，描写了一个报恩复仇的故事。法老号大副唐泰斯受船长的临终嘱托，为拿破仑送了一封信，受到两个对他嫉妒的小人的陷害，被打入死牢，狱友法里亚神甫向他传授了各种知识，还在临终前把一批宝藏的秘密告诉了他。他设法越狱后找到了宝藏，成为巨富。从此他化名为基督山伯爵，经过精心策划，报答了他的恩人，惩罚了三个一心想置他于死地的仇人。",
            "9.0","1991-12-1")


        //小说
        val book6=BooksItemOne(6,"https://img3.doubanio.com/view/subject/s/public/s29053580.jpg",
            "活着","余华",
            "作家出版社",
            "《活着(新版)》讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于赌光了家业，一贫如洗。他的父亲被他活活气死，母亲则在穷困中患了重病，福贵前去求药，却在途中被国民党抓去当壮丁。经过几番波折回到家里，才知道母亲早已去世，妻子家珍含辛茹苦地养大两个儿女。此后更加悲惨的命运一次又一次降临到福贵身上，他的妻子、儿女和孙子相继死去，最后只剩福贵和一头老牛相依为命，但老人依旧活着，仿佛比往日更加洒脱与坚强。",
            "9.4","2012-8-1")
        val book7=BooksItemOne(7,"https://img1.doubanio.com/view/subject/s/public/s33834057.jpg",
            "文城","余华",
            "北京十月文艺出版社",
            "在溪镇人最初的印象里，林祥福是一个身上披戴雪花，头发和胡子遮住脸庞的男人，有着垂柳似的谦卑和田地般的沉默寡言。哪怕后来成了万亩荡和木器社的主人，他身上的谦卑和沉默依旧没有变。他的过去和一座谜一样的城联系在了一起，没人知道他为什么要找一个不存在的地方。\n" +
                    "\n" +
                    "他原本不属于这里，他的家乡在遥远的北方。为了一个承诺他将自己连根拔起，漂泊至此。往后的日子，他见识过温暖赤诚的心，也见识过冰冷无情的血。最终他徒劳无获，但许多人的牵挂和眼泪都留在了他身上。\n" +
                    "\n" +
                    "“文城在哪里？”\n" +
                    "\n" +
                    "“总会有一个地方叫文城。”",
            "7.8","2021-3")
        val book8=BooksItemOne(8,"https://img9.doubanio.com/view/subject/s/public/s33821754.jpg",
            "克拉拉与太阳","[英] 石黑一雄",
            "上海译文出版社",
            "“太阳总有办法照到我们，不管我们在哪里。”\n" +
                    "\n" +
                    "克拉拉是一个专为陪伴儿童而设计的太阳能人工智能机器人（AF），具有极高的观察、推理与共情能力。她坐在商店展示橱窗里，注视着街头路人以及前来浏览橱窗的孩子们的一举一动。她始终期待着很快就会有人挑中她，不过，当这种永久改变境遇的可能性出现时，克拉拉却被提醒不要过分相信人类的诺言。",
            "8.5","2021-3")
        val book9=BooksItemOne(9,"https://img1.doubanio.com/view/subject/s/public/s24514468.jpg",
            "白夜行","[日] 东野圭吾",
            "南海出版公司",
            "1973年，大阪的一栋废弃建筑中发现一名遭利器刺死的男子。案件扑朔迷离，悬而未决。\n" +
                    "\n" +
                    "此后20年间，案件滋生出的恶逐渐萌芽生长，绽放出恶之花。案件相关者的人生逐渐被越来越重的阴影笼罩……",
            "9.1","2013-1-1")

        //散文
        val book10=BooksItemOne(10,"https://img3.doubanio.com/view/subject/s/public/s1066570.jpg",
            "撒哈拉的故事","三毛",
            "哈尔滨出版社",
            "三毛作品中最脍炙人口的《撒哈拉的故事》，由12篇精彩动人的散文结合而成，其中《沙漠中的饭店》，是三毛适应荒凉单调的沙漠生活后，重新拾笔的第一篇文字，自此之后，三毛便写出一系列以沙漠为背景的故事，倾倒了全世界的中文读者。",
            "9.2","2003-8")
        val book11=BooksItemOne(11,"https://img2.doubanio.com/view/subject/s/public/s29721281.jpg",
            "持续做一个深情的人","林清玄",
            "北京时代华文书局",
            "情深，万象皆深。心扉的突然洞开，来自从容，来自有情。\n" +
                    "\n" +
                    "《持续做一个深情的人》是林清玄先生以情感为主题的美文随笔。书中有林先生对一朵花、一杯茶，一场清风的深情，更有对父母、恋人、友人的大爱。\n" +
                    "\n" +
                    "人生的幸福很多时候是得自于看起来无甚意义的事。例如偶然看见桑间濮上的老妇说了一段充满启示的话语，有人突然给了我们一杯清茶，在小路上突然听见冰果店里传来一段喜欢的乐曲，在书上读到了一首动人的诗歌，偶然看见一朵酢浆花的开放；例如情爱，算是人间最浓烈的感觉了，若能存心如清茶、如素帕，那么不论得失，情意也不至于完全失去，自然也不会反目成仇了，转爱成恨了。\n" +
                    "\n" +
                    "要温和的爱，这样方得久远；太快和太慢，其结果都是一样迟缓。愿我们持续做一个深情的人，一个从容有情的人。\n" +
                    "\n",
            "8.3","2018-5")
        val book12=BooksItemOne(12,"https://img2.doubanio.com/view/subject/s/public/s1015872.jpg",
            "我们仨","杨绛",
            "生活·读书·新知三联书店",
            "《我们仨》是钱钟书夫人杨绛撰写的家庭生活回忆录。1998年，钱钟书逝世，而他和杨绛唯一的女儿钱瑗已于此前（1997年）先他们而去。在人生的伴侣离去四年后，杨绛在92岁高龄的时候用心记述了他们这个特殊家庭63年的风风雨雨、点点滴滴，结成回忆录《我们仨》。",
            "8.7","2003-7")
        val book13=BooksItemOne(13,"https://img2.doubanio.com/view/subject/s/public/s2875823.jpg",
            "朝花夕拾","鲁迅",
            "人民文学出版社",
            "《小引》\n" +
                    "\n" +
                    "《狗·猫·鼠》\n" +
                    "\n" +
                    "《阿长与山海经》\n" +
                    "\n" +
                    "《二十四孝图》\n" +
                    "\n" +
                    "《五猖会》\n" +
                    "\n" +
                    "《无常》\n" +
                    "\n" +
                    "《从百草园到三味书屋》\n" +
                    "\n" +
                    "《父亲的病》\n" +
                    "\n" +
                    "《琐记》\n" +
                    "\n" +
                    "《藤野先生》\n" +
                    "\n" +
                    "《范爱农》\n" +
                    "\n" +
                    "《后记》",
            "8.9","1972-4")

        //人文
        val book14=BooksItemOne(14,"https://img2.doubanio.com/view/subject/s/public/s27814883.jpg",
            "人类简史","[以色列] 尤瓦尔·赫拉利",
            "中信出版社",
            "《人类简史：从动物到上帝》是以色列新锐历史学家的一部重磅作品。从十万年前有生命迹象开始到21世纪资本、科技交织的人类发展史。十万年前，地球上至少有六个人种，为何今天却只剩下了我们自己？我们曾经只是非洲角落一个毫不起眼的族群，对地球上生态的影响力和萤火虫、猩猩或者水母相差无几。为何我们能登上生物链的顶端，最终成为地球的主宰？\n" +
                    "\n" +
                    "从认知革命、农业革命到科学革命，我们真的了解自己吗？我们过得更加快乐吗？我们知道金钱和宗教从何而来，为何产生吗？人类创建的帝国为何一个个衰亡又兴起？为什么地球上几乎每一个社会都有男尊女卑的观念？为何一神教成为最为广泛接受的宗教？科学和资本主义如何成为现代社会最重要的信条？理清影响人类发展的重大脉络，挖掘人类文化、宗教、法律、国家、信贷等产生的根源。这是一部宏大的人类简史，更见微知著、以小写大，让人类重新审视自己。",
            "9.1","2014-11")
        val book15=BooksItemOne(15,"https://img9.doubanio.com/view/subject/s/public/s33736115.jpg",
            "远方旅人","唐洬",
            "青岛出版社",
            "《远方旅人》是唐洬在清华读博期间创作的长篇小说，通过追溯一系列发生在沿海城市“瀛海”的往日悬案和谜团，讲述了少女樊思琴深陷官商、高校暗网的坎坷经历，以及因她和义母韩梓妍而起的一场时代浪涛。亲情和背叛，残酷和温情，隔膜和希望，交汇成21世纪初的一幅“清明上河图”。",
            "8.5","2020-9")
        val book16=BooksItemOne(16,"https://img2.doubanio.com/view/subject/s/public/s3893343.jpg",
            "美的历程","李泽厚",
            "生活·读书·新知三联书店",
            "本书是中国美学的经典之作。凝聚了作者李泽厚先生多年研究，他把中国人古往今来对美的感觉玲珑剔透地展现在大家眼前，如斯感性，如斯亲切。今配以精美的插图，本书就更具体地显现出中国这段波澜壮阔的美的历程。",
            "9.2","2009-1-1")
        val book17=BooksItemOne(17,"https://img9.doubanio.com/view/subject/s/public/s1074166.jpg",
            "菊与刀","[美国] 鲁思·本尼迪克特",
            "商务印书馆",
            "“菊”本是日本皇室家徽，“刀”是武士道文化的象征。美国人类学家鲁思・本尼迪克特用《菊与刀》来揭示日本人的矛盾性格亦即日本文化的双重性(如爱美而黩武、尚礼而好斗、喜新而顽固、服从而不驯等)……",
            "8.2","1990-6")

        //经管
        val book18=BooksItemOne(18,"https://img1.doubanio.com/view/subject/s/public/s32271479.jpg",
            "棉花帝国","[美]斯文·贝克特",
            "民主与建设出版社",
            "棉花产品在我们的生活中无处不在，以至于我们往往难以注意到它的重要性。但棉花产业的历史实际上是与近代资本主义的历史紧密关联在一起，理解棉花产业发展史是理解资本主义和当代世界的关键。本书作者斯文·贝克特通过叙述棉花产业发展的历史，解释了欧洲国家和资本家如何在短时间内重塑了这个世界历史上最重要的一项产业，并进而改变了整个世界面貌的。\n" +
                    "\n" +
                    "本书是名副其实的全球史，内容涉及五大洲，将非洲的贩奴贸易和红海贸易联系在了一起，将美国南北战争和印度棉花种植联系在了一起。在贝克特波澜壮阔的巨著中，商人、商业资本家、经纪人、代理人、国家官僚、工业资本家、佃农、自耕农、奴隶都有自己的角色，贝克特清楚地表明，这些人的命运是如何与近代资本主义发展联系在一起的，又是如何塑造我们现在这个存在着巨大不平等的世界的。本书告诉我们，并不存在一个所谓的“全球化”资本主义阶段，资本主义从一开始就是全球化的。",
            "8.5","2019-3")
        val book19=BooksItemOne(19,"https://img1.doubanio.com/view/subject/s/public/s33784858.jpg",
            "不拘一格","[美] 里德·哈斯廷斯 / [美] 艾琳·迈耶",
            "中信出版社",
            "本书是里德·哈斯廷斯的作品，联合欧洲工商管理学院教授艾琳·迈耶，开创性地利用内外部交叉视角，揭示了人才重于流程、创新高于效率、自由多于管控的文化内核，让你能从网飞的实践经验中获得高价值的思维启发。",
            "8.2","2021-1-1")
        val book20=BooksItemOne(20,"https://img1.doubanio.com/view/subject/s/public/s33856247.jpg",
            "价值共生","陈春花",
            "人民邮电出版社",
            "《价值共生》以数字化生存为背景，尝试梳理数字技术带来变化的场景下，完整呈现新的组织管理体系，深入探索组织价值重构的未来。\n" +
                    "\n" +
                    "从个人角度，给身处数字化时代的职场人提供了详细的职业规划解决方案，从工作内容到工作方式上都落脚在通过企业给自我赋能的基点上。通过对数字化时代的管理的解读，让读者认知到数字化时代超级个体，所需的基础能力。也充分说明了“个体”对于组织的价值，在数字化时代的组织发展中将成为关键。\n" +
                    "\n" +
                    "从企业管理角度，针对数字化转型的困难，从工作环境、组织、目标3个方向，对企业管理者提出了：管理层需要做好四个改变、对管理层的三个要求、管理者要做好四件事。帮助企业管理者更好的把握数字化转变的4个关键节点：从管控到赋能、从科层固化到平台利他、从分工到协同共生、从实现组织目标到兼顾人的意义，促使企业完成数字化过程中的新功能、新结构、新能力、新目标的转变。\n" +
                    "\n" +
                    "本书针对数字化时代的管理真问题进行深入研究，既有最新理论的阐述，让大家对企业组织受到的冲击有新的认识，还依托持续跟踪研究华为、腾讯、海尔、新希望六和等50多家中国优秀企业，提出了切实可执行性的解决方案。",
            "8.5","2021-5")
        val book21=BooksItemOne(21,"https://img9.doubanio.com/view/subject/s/public/s6807265.jpg",
            "浪潮之巅","吴军",
            "电子工业出版社",
            "近一百多年来，总有一些公司很幸运地、有意识或无意识地站在技术革命的浪尖之上。在这十几年间，它们代表着科技的浪潮，直到下一波浪潮的来临。\n" +
                    "\n" +
                    "从一百年前算起，AT&T 公司、IBM 公司、苹果公司、英特尔公司、微软公司、思科公司、雅虎公司和Google公司都先后被幸运地推到了浪尖。虽然，它们来自不同的领域，中间有些已经衰落或正在衰落，但是它们都极度辉煌过。本书系统地介绍了这些公司成功的本质原因及科技工业一百多年的发展。\n" +
                    "\n" +
                    "在极度商业化的今天，科技的进步和商机是分不开的。因此，本书也系统地介绍了影响到科技浪潮的风险投资公司，诸如 KPCB 和红杉资本，以及百年来为科技捧场的投资银行，例如高盛公司，等等。\n" +
                    "\n" +
                    "在这些公司兴衰的背后，有着它必然的规律。本书不仅讲述科技工业的历史，更重在揭示它的规律性。",
            "9.0","2011-8")

        //生活
        val book22=BooksItemOne(22,"https://img2.doubanio.com/view/subject/s/public/s33323852.jpg",
            "被讨厌的勇气","岸见一郎 / 古贺史健",
            "机械工业出版社",
            "「被讨厌的勇气」并不是要去吸引被讨厌的负向能量，而是，如果这是我生命想绽放出最美的光彩，那么，即使有被讨厌的可能，我都要用自己的双手双脚往那里走去。」\n" +
                    "\n" +
                    "「因为拥有了被讨厌的勇气，于是有了真正幸福的可能。」\n" +
                    "\n" +
                    "你是否常常对繁琐的生活感到乏味？\n" +
                    "\n" +
                    "你是否时时为复杂的人际关系感到疲惫？\n" +
                    "\n" +
                    "你是否认为人生的意义越来越模糊难见？\n" +
                    "\n" +
                    "我们如何能够在繁杂的日常琐碎和复杂的人际关系中用自己的双手去获得真正的幸福？\n" +
                    "\n" +
                    "这一切的答案尽在这本《被讨厌的勇气》中！",
            "8.6","2015-3-1")
        val book23=BooksItemOne(23,"",
            "亲密关系","[美] 罗兰·米勒",
            "人民邮电出版社",
            "《亲密关系》汲取了社会心理学、沟通研究、家庭研究、认知心理学、发展心理学、演化心理学、社会学、传播学及家政学等学科的最新成果，研究实践和理论建构并重，学术标准与大众兴趣兼备。全书结构清晰、逻辑严密、语言生动、启发思考，既通俗易懂，读来轻松愉快，又科学权威，崇尚实证精神。本书遵循由浅入深、由一般到特殊的认知规律，论述了亲密关系的基础、活动形态、类型、矛盾和修复等内容，读完本书，你将对人际吸引、爱情、婚姻、承诺、友谊、激情、沟通、性爱、依恋、择偶、嫉妒、出轨、家暴等亲密关系的方方面面有全新的认识。\n" +
                    "\n" +
                    "亲密关系是人类经验的核心，处理得好能给人带来极大的快乐，处理得不好则会造成重大创伤，因此科学地认识亲密关系，攸关我们每个人的幸福。本书既适合研究亲密关系的专业人士，能给他们带来启发与灵感，也适合每个想爱情甜蜜、婚姻长久、人生幸福的普通读者。",
            "9.3","2015-6")
        val book24=BooksItemOne(24,"https://img2.doubanio.com/view/subject/s/public/s1670932.jpg",
            "社会心理学","[美国] 戴维·迈尔斯",
            "人民邮电出版社",
            "《社会心理学》这本书被美国700多所大学或学院的心理系所采用，是这一领域的主导教材，已经成为评价其他教材的标准。\n" +
                    "\n" +
                    "这本书将基础研究与实践应用完美地结合在一起，以富有逻辑性的组织结构引领学生了解人们是如何思索、影响他人并与他人建立联系的。是人们了解自身、了解社会、了解自己与社会之间关系的最佳的指导性书籍。",
            "9.0","2006-1")
        val book25=BooksItemOne(25,"https://img9.doubanio.com/view/subject/s/public/s33835834.jpg",
            "美食与文明","蕾切尔·劳丹",
            "民主与建设出版社",
            "从大约两万年前人类掌握谷物烹饪到现在，本书作者讲述了世界上主要饮食的兴衰历史。透过多种饮食表面上的混杂局势，作者揭示了烹饪谱系图潜在的简明规律，说明了烹饪哲学中关于健康、经济、政治、社会和神灵信仰的周期性变化如何推动新饮食的建立，其中一些被选为帝国饮食，在全球逐渐占据主导地位。\n" +
                    "\n" +
                    "作者还阐述了商人、传教士和军队如何将饮食跨越山脉、海洋、沙漠和政治边界。本书的创新叙事方式将烹饪如语言、服装或建筑，视为人类创造的事物。通过强调烹饪如何将农产品变成食物，并将全球而不是某个特定的国家作为舞台，挑战了支撑当代食品运动的农耕和浪漫主义神话。",
            "8.6","2021-3")

        val bookdao=BookDao(this)
        bookdao.insertBook(book1)
        bookdao.insertBook(book2)
        bookdao.insertBook(book3)
        bookdao.insertBook(book4)
        bookdao.insertBook(book5)
        bookdao.insertBook(book6)
        bookdao.insertBook(book7)
        bookdao.insertBook(book8)
        bookdao.insertBook(book9)
        bookdao.insertBook(book10)
        bookdao.insertBook(book11)
        bookdao.insertBook(book12)
        bookdao.insertBook(book13)
        bookdao.insertBook(book14)
        bookdao.insertBook(book15)
        bookdao.insertBook(book16)
        bookdao.insertBook(book17)
        bookdao.insertBook(book18)
        bookdao.insertBook(book19)
        bookdao.insertBook(book20)
        bookdao.insertBook(book21)
        bookdao.insertBook(book22)
        bookdao.insertBook(book23)
        bookdao.insertBook(book24)
        bookdao.insertBook(book25)

        val user1=User(1,"汉青堂","https://img1.doubanio.com/icon/u145458142-9.jpg")
        val user2=User(2,"炎樱","https://img9.doubanio.com/icon/u16235694-135.jpg")
        val user3=User(3,"D君","https://img2.doubanio.com/icon/u1484677-13.jpg")
        val user4=User(4,"Scorpio|千寻","https://img9.doubanio.com/icon/u3034854-75.jpg")
        val user5=User(5,"NewDividedc","https://img2.baidu.com/it/u=2951542358,3634590085&fm=11&fmt=auto&gp=0.jpg")

        val userdao=UserDao(this)
        userdao.insertUser(user1)
        userdao.insertUser(user2)
        userdao.insertUser(user3)
        userdao.insertUser(user4)
        userdao.insertUser(user5)

        val review1=bookReview(1,6,1,"2016-05-17","谁过中国人没有信仰？中国人的信仰就是两个字，活着。","10",751)
        val review2=bookReview(2,6,2,"2013-04-10","眼里看的是别人的故事 心里想的是自己的生活","8.3",1098)
        val review3=bookReview(3,6,3,"2015-10-19","好几次都想合上不看了，可还是整本读下来了。福贵是麻木的，也是乐观的。文字很质朴，就是太疼了。。","9.8",423)
        val review4=bookReview(4,6,4,"2014-11-18","像被悶槌重擊的胸口，無法喘息的痛。","9.7",1898)

        val reviewdao=ReviewDao(this)
        reviewdao.addReview(review1)
        reviewdao.addReview(review2)
        reviewdao.addReview(review3)
        reviewdao.addReview(review4)
    }

}