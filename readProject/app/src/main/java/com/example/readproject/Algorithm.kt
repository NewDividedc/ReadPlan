package com.example.readproject

import org.ansj.splitWord.analysis.NlpAnalysis
import java.io.*
import java.text.DecimalFormat
import java.util.*
import kotlin.math.ln


/**
 *
 * @author X.H.Yang
 * 计算文档的TF值 词频(TF)=某个词在文章中出现的次数/文章的总词数
 * 计算文档的IDF值 逆文档频率(IDF)=log(文档总数/包含改词的文档数)
 * 计算TF-IDF值：TF-IDF = 词频(TF)*逆文档频率(IDF)
 */
internal object Algorithm {
    var resultlist:MutableList<MutableMap.MutableEntry<String, Double?>> = ArrayList()
    @Throws(FileNotFoundException::class, UnsupportedEncodingException::class)
    @JvmStatic
    fun main(noteline:List<String>) {
        val df = DecimalFormat("######0.0000")
        //只关注这些词性的词
        val expectedNature: HashSet<String?> =
            object : HashSet<String?>() {
                init {
                    add("n")
                    add("nt")
                    add("nz")
                    add("nw")
                    add("nl")
                    add("ns")
                    add("ng")
                }
            }
        //从本地读取采集好的语料库，每篇文档占用一行
        //val sampleFile = FileInputStream("E:\\test.txt")
        //val isr = InputStreamReader(input, "UTF-8")
        var text =""
//        val buread = BufferedReader(isr)
        val Textlist: MutableList<List<MutableMap.MutableEntry<String, Double?>>> =
            ArrayList()
        try {
            for(line in noteline) {
                //正则匹配去除文本中的标点符号
                text = line.replace("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]".toRegex(), "")
                //采用nlp分词
                val result = NlpAnalysis.parse(text)
                val terms = result.terms
                //size用来统计每篇文档分词后的总词数
                var size = 0.0
                val dict: MutableMap<String, Double?> =
                    HashMap()
                for (i in terms.indices) {
                    val key = terms[i].name
                    val natureStr = terms[i].natureStr //拿到词性
                    if (expectedNature.contains(natureStr)) {
                        size++
                        if (dict.containsKey(key)) {
                            val count = dict[key]!! + 1
                            dict[key] = count
                        } else {
                            dict[key] = 1.0
                        }
                    }
               }
                val list: MutableList<MutableMap.MutableEntry<String, Double?>> =
                    ArrayList()
                for (entry in dict.entries) {
                    val tf = entry.value!! / size
                    entry.setValue(tf)
                    list.add(entry) //将map中的元素放入list中
                }
                Textlist.add(list)
               // println(list.size)
            }
            println("文章个数${Textlist.size}")
            var index = 0
            for (entry in Textlist) {
                index++
                for (each in entry) {
                    val key = each.key
                    val idf = getIDF(key, Textlist)
                    val tf_idf = each.value!! * idf
                    each.setValue(java.lang.Double.valueOf(df.format(tf_idf)))

                }
                println("词汇个数${entry.size}")
                entry.sortedBy { it.value }
                for (each in entry){
                    println("${each.key}"+"${each.value}")
                }
            }
            var flag=1
            for (entry in Textlist){
                for(each in entry){
                    for(one in resultlist){
                        if(one.key==each.key){
                            flag=0
                            break
                        }
                    }
                    if(flag==1){
                        resultlist.add(each)
                    }
                }
            }
            resultlist.sortBy { it.value }
            resultlist.reverse()
            println("最后结果的大小为"+resultlist.size)
            //saveTextTF(Textlist, "E:\\test1.txt")

        } catch (e: Exception) {
            println(e.toString())
        }
    }
    //计算IDF值
    fun getIDF(
        key: String,
        Textlist: List<List<MutableMap.MutableEntry<String, Double?>>>
    ): Double {
        try {
            var count = 0.0
            for (entry in Textlist) {
                for ((key1) in entry) {
                    if (key == key1) {
                        count++
                        break
                    }
                }
            }
            print("key是${key},value是${count}")
            return ln(Textlist.size / (count + 1))
        } catch (e: Exception) {
            println(e.toString())
            return  0.0
        }
    }

    @Throws(IOException::class)
    fun saveTextTF(
        list: List<List<MutableMap.MutableEntry<String, Double?>>>,
        path: String?
    ) {
        // TODO Auto-generated method stub
        println("保存TF-IDF值")
        val countLine = 0
        val outPutFile = File(path)
        //if file exists, then delete it
        if (outPutFile.exists()) {
            outPutFile.delete()
        }
        //if file doesnt exists, then create it
        if (!outPutFile.exists()) {
            outPutFile.createNewFile()
        }
        val outPutFileWriter = FileWriter(outPutFile)
        for (entry in list) {
            for ((key, value) in entry) {
                outPutFileWriter.write("$key:$value,")
            }
            outPutFileWriter.write("\n")
        }
        outPutFileWriter.close()
        println("属性词个数：$countLine")
    }
}