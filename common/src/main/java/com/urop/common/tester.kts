import com.urop.common.Task
import com.urop.common.toBAarr
import com.urop.common.toBArr
import java.util.*
import kotlin.system.measureTimeMillis

val ARR_LENGTH = 1000000
val rep = 10
var arr = IntArray(ARR_LENGTH)
var r = Random()
for (i in 0 until ARR_LENGTH) {
    arr[i] = r.nextInt(100)
}

//fun test(runnable: (it: Int) -> (Unit)) {
//    var duration = 0L
////    println("Warmup: ${measureTimeMillis { runnable(0) }}")
//    repeat(rep) {
//        duration += measureTimeMillis { runnable(it) }
////        println("${duration}")
//    }
//    println("Total time: $duration")
//    println()
//}


val t = Task()
println(measureTimeMillis { arr.toBAarr() })
println(measureTimeMillis {
    t.data = arr.toBAarr()
    t.toBArr()
})

//println(bdata1.size)
//println(bdata2.size)

//println("Kryo:")
//test {
//    val bdata = arr.toBAarr()
//    val bmeta = intArrayOf(1, 2).toBAarr()
//    val t = Task("ABC", bdata, bmeta)
//    val bb = t.toBArr()
//    println(bb.size)
//    val obj = t.toBB().toTask()
//    val arr = obj.bdata.toIArr()
//    println("data: " + arr[0])
//    print(data[it])
//    data[it]++
//}

//println("Gson:")
//test {
//    val sdata = arr.toJson()
//    val smeta = intArrayOf(1, 2).toJson()
//    val t = Task("ABC", sdata, smeta)
//    val bb = t.toJson()
//    println(40+2*bb.length)
////    print(data[it])
////    data[it]++
//}