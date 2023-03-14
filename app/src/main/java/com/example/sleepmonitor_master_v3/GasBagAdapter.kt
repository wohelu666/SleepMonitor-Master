package com.example.sleepmonitor_master_v3

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class GasBagAdapter:BaseQuickAdapter<GasBean,BaseViewHolder>(R.layout.item_gas) {
    override fun convert(holder: BaseViewHolder, item: GasBean) {
        when(item.isPress){
            true->{
                holder.setBackgroundResource(R.id.tvGasBag,R.drawable.circle_press)

            }
            false->{
                holder.setBackgroundResource(R.id.tvGasBag,R.drawable.circle_relax)

            }
        }
        holder.setText(R.id.tvGasBag,item.name)

    }
}