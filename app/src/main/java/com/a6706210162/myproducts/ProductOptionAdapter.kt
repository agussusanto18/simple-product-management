package com.a6706210162.myproducts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.a6706210162.myproducts.data.Item
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProductOptionAdapter(
    private val dataList: List<Item>,
    private val context: Context,
    private val item_name_input: TextInputEditText,
    private val item_price_input: TextInputEditText,
    private val item_stock_input: TextInputEditText,
    private val item_image_input: TextInputEditText) : RecyclerView.Adapter<ProductOptionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val stockTextView: TextView = itemView.findViewById(R.id.stockTextView)
        val btnAddView: TextView = itemView.findViewById(R.id.btnAddView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_list_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.titleTextView.text = data.title
        holder.priceTextView.text = "$${data.price}"
        holder.stockTextView.text = "Stock: ${data.quantity}"

        holder.btnAddView.setOnClickListener {
            item_name_input.setText(data.title)
            item_price_input.setText(data.price)
            item_stock_input.setText(data.quantity)
            item_image_input.setText(data.image)

            // Create and display the notification
            val notificationId = position
            val channelId = "prodman123"
            val channelName = "Product Management"
            val notificationText = "You have added the ${data.title} product"

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, channelId)
                .setContentTitle(channelName)
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(notificationId, notification)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
