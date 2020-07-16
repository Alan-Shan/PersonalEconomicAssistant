package top.ilum.pea.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_item.view.*
import top.ilum.pea.R

class NewsAdapter(var list: List<Results>) :
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
        holder.description.text = list[position].abstract
        Picasso.get().load(list[position].multimedia[2].url).into(holder.preview)
        holder.card.setOnClickListener {
            startActivity(it.context, Intent(Intent.ACTION_VIEW, Uri.parse(list[position].url)), null)
        }
    }

    inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        var header: TextView = root.header
        var description: TextView = root.description
        var preview: ImageView = root.preview
        var card: CardView = root.findViewById(R.id.card)
    }
}
