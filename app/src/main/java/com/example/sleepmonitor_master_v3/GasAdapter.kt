package com.example.sleepmonitor_master_v3

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class GasAdapter : BaseQuickAdapter<GasBean, BaseViewHolder>(R.layout.item_gas) {
    override fun convert(holder: BaseViewHolder, item: GasBean) {
        when (item.isPress) {

            true -> {
                holder.setBackgroundColor(R.id.tvGasBag, Color.GREEN)

                when (item.airIsPress) {
                    true -> {
                        holder.setBackgroundColor(R.id.tvGasBag, Color.RED)

                    }
                    false -> {
                        holder.setBackgroundColor(R.id.tvGasBag, Color.GREEN)

                    }
                }

            }
            false -> {
                holder.setBackgroundColor(R.id.tvGasBag, Color.parseColor("#e8e8e8"))

            }
        }
//        holder.setText(R.id.tvGasBag, item.name)
    }
}