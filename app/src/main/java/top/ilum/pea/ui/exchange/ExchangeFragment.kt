package top.ilum.pea.ui.exchange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_exchange.*
import top.ilum.pea.MainActivity
import top.ilum.pea.R
import top.ilum.pea.data.ExchangeElement
class ExchangeFragment : Fragment() {

    private val siteName = "https://www.cbr-xml-daily.ru/daily_utf8.xml"

    private var leftActive = 0
    private var rightActive = 3

    private val testExchangeList = listOf(
        ExchangeElement("Российский Рубль", 1.0, "₽"),
        ExchangeElement("Евро", 1.0, "€"),
        ExchangeElement("Американский Доллар", 1.0, "\$"),
        ExchangeElement("Японская Иена", 1.0, "¥"),
        ExchangeElement("Британский фунт", 1.0, "£"),
        ExchangeElement("Швейцарский франк", 1.0, "₣")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exchange, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leftAdapter = ExchangeAdapter(testExchangeList, leftActive)
        val rightAdapter = ExchangeAdapter(testExchangeList, rightActive)

        exchanges__recyclerLeft.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerLeft.adapter = leftAdapter

        exchanges__recyclerRight.layoutManager = LinearLayoutManager(MainActivity())
        exchanges__recyclerRight.adapter = rightAdapter

        for (i in 0 until leftAdapter.itemCount) {
            val item = leftAdapter.getItemId(i)
            leftAdapter.notifyItemChanged(i, { Toast.makeText(MainActivity(), "number = $i", Toast.LENGTH_SHORT).show() })
        }
    }
}
