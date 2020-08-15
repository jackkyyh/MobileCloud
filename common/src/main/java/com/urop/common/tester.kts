
import com.urop.common.miniTest
import kotlin.system.measureTimeMillis

//fun test(serial: (it: Int) -> (Unit), deserial: (it: Int) -> (Unit)) {
//}

//
//val t = Task()
//println(measureTimeMillis { arr.toBArr() })
//println(measureTimeMillis {
//    t.data = arr.toBArr()
//    t.toBArr()
//})


//miniTest()

var sq = ""
println(measureTimeMillis { sq = miniTest("27065524748708") })
println(measureTimeMillis { miniTest("7065524748708") })