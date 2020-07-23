package top.ilum.pea.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_item.view.*
import top.ilum.pea.R
import top.ilum.pea.data.Results

class NewsAdapter(private var list: List<Results>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.news_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.header.text = list[position].title

        if (list[position].abstract.isNotEmpty()) {
            holder.description.text = list[position].abstract
        } else {
            holder.description.visibility = View.GONE
        }

        if (!list[position].multimedia.isNullOrEmpty()) {
            Picasso.get().load(list[position].multimedia[2].url)
                .into(holder.preview) // Load cropped picture
        } else { holder.preview.visibility = View.GONE }

        holder.card.setOnClickListener {

            val url: String = list[position].url

            val builder = CustomTabsIntent.Builder().setToolbarColor(getColor(it.context, R.color.colorPrimary))
            builder.setStartAnimations(it.context, R.anim.slide_in_right, R.anim.slide_out_left)
            builder.setExitAnimations(it.context, R.anim.slide_in_left, R.anim.slide_out_right)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(it.context, Uri.parse(url))
        }
    }

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        var header: TextView = root.header
        var description: TextView = root.description
        var preview: ImageView = root.preview
        var card: CardView = root.findViewById(R.id.card)
    }
}
