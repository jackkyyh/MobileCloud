
import com.urop.common.SortTask
import com.urop.common.Task

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


val lam: Task.() -> Unit = {
    println("in lambda")
    id = "iid"
}

fun getQSort(): SortTask {
    return SortTask(1, 2)
}

//val t: Task = getQSort()
val t = Task()
val tt = t.apply(lam)
println(tt.id)