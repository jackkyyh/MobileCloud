package com.urop.mobilecloud

import com.urop.common.FactorizationTask
import com.urop.common.Profile.profile
import java.math.BigInteger

val factorize: FactorizationTask.() -> Unit = {
    var sq = BigInteger.ZERO

    profile.add("sqrt") { sq = num.sqrt() }

//    profile.add("search"){
    var rem = BigInteger.ZERO
    profile.add("mod") { rem = num.mod(sq) }
    while (rem != BigInteger.ZERO) {
        profile.add("sub") { sq = sq.subtract(BigInteger.ONE) }
        profile.add("mod") { rem = num.mod(sq) }
    }
//    }
    f1 = sq
    f2 = num.divide(sq)
}


fun BigInteger.sqrt(): BigInteger {
    var num = this.toString()

    var sqrt = "0" //开方结果
    var pre = "0" //开方过程中需要计算的被减数
    var trynum: BigInteger //试商，开放过程中需要计算的减数
    var flag: BigInteger //试商，得到满足要求减数的之后一个数
    val _20 = BigInteger("20") //就是20
    var dividend: BigInteger ///开方过程中需要计算的被减数
    var A: BigInteger? //(10*A+B)^2=M

    var B: BigInteger
    var BB: BigInteger

    var len: Int = num.length //数字的长度

    if (len % 2 == 1) //长度是奇数的画，首位补上1个0凑成偶数位
    {
        num = "0$num"
        len++
    }

    for (i in 0 until len / 2)  //得到的平方根一定是len/2位
    {
        dividend = BigInteger(pre + num.substring(2 * i, 2 * i + 2))
        A = BigInteger(sqrt)
        for (j in 0..9) {
            B = BigInteger(j.toString() + "")
            BB = BigInteger((j + 1).toString() + "")
            trynum = _20.multiply(A).multiply(B).add(B.pow(2))
            flag = _20.multiply(A).multiply(BB).add(BB.pow(2))

            //满足要求的j使得试商与计算中的被减数之差为最小正数
            if (trynum.subtract(dividend).compareTo(BigInteger.ZERO) <= 0
                && flag.subtract(dividend).compareTo(BigInteger.ZERO) > 0
            ) {
                sqrt += j //结果加上得到的j
                pre = dividend.subtract(trynum).toString() //更新开方过程中需要计算的被减数
                break
            }
        }
    }
    return BigInteger(sqrt.substring(1))
}