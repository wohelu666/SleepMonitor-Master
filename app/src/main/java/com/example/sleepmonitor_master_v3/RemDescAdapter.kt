package com.example.sleepmonitor_master_v3

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class RemDescAdapter:BaseQuickAdapter<RemDescBean,BaseViewHolder>(R.layout.item_rem_desc) {
    override fun convert(holder: BaseViewHolder, item: RemDescBean) {
        holder.setText(R.id.tvTitle,item.title)
        holder.setText(R.id.tvContent,item.content)

    }
}