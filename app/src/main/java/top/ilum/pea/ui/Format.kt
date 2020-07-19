package top.ilum.pea.ui

class Format {
    companion object {
        fun formatNumber(num: Double): String {     //method for formatting numbers
            var str = ""                            //e.g: 1000000 -> 1 000 000
            var i = 0
            val n = num.toInt().toString().reversed()
            for (char in n) {
                str += char
                i++
                if (i % 3 == 0) {
                    str += " "
                }
            }
            return str.reversed()
        }
    }
}