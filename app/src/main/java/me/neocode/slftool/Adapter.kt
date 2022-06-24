package me.neocode.slftool

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class Adapter(private var mList: List<ItemModel>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    fun setItems(mList: List<ItemModel>) {
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemModel = mList[position]
        holder.itemImage.setImageResource(itemModel.image)
        holder.itemText.text = itemModel.text
        holder.itemDescription.text = itemModel.type

        holder.itemView.setOnClickListener {
            if(itemModel.image != R.drawable.ic_baseline_help_24) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://de.wikipedia.org/wiki/" + holder.itemText.text.split("(")[0])
                )
                holder.itemView.context.startActivity(browserIntent)
            }
        }

        holder.itemView.setOnLongClickListener {
            if(itemModel.image != R.drawable.ic_baseline_help_24) {
                val clipboardManager =
                    holder.itemView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData = ClipData.newPlainText("text", holder.itemText.text)
                clipboardManager.setPrimaryClip(clipData)

                Toast.makeText(
                    holder.itemView.context,
                    R.string.copied,
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemText: TextView = itemView.findViewById(R.id.itemText)
        var itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
    }

}