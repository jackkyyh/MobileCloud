import com.urop.common.*
import com.urop.common.MyKryo.arr2byte
import com.urop.common.MyKryo.bb2task
import com.urop.common.MyKryo.byte2arr
import com.urop.common.MyKryo.task2bb
import java.util.*
import kotlin.system.measureTimeMillis

val ARR_LENGTH = 100000
val rep = 300
var arr = IntArray(ARR_LENGTH)
var r = Random()
for (i in 0 until ARR_LENGTH) {
    arr[i] = r.nextInt(100)
}

fun test(runnable: (it: Int) -> (Unit)) {
    var duration = 0L
    println("Warmup: ${measureTimeMillis { runnable(0) }}")
    repeat(rep) {
        duration += measureTimeMillis { runnable(it) }
    }
    println("Total time: $duration")
}


println("Kryo:")
test {
    val bdata = arr2byte(arr)
    val bmeta = arr2byte(intArrayOf(1, 2))
    val t = Task("ABC", bdata, bmeta)
    val bb = task2bb(t)
    val t2 = bb2task(bb)
    val data = byte2arr(t2.bdata)
//    print(data[it])
//    data[it]++
}

println("Gson:")
test {
    val sdata = arr.arr2json()
    val smeta = intArrayOf(1, 2).arr2json()
    val t = Task("ABC", sdata, smeta)
    val bb = t.task2json()
    val t2 = bb.json2task()
    val data = t2.data.json2arr()
//    print(data[it])
//    data[it]++
}