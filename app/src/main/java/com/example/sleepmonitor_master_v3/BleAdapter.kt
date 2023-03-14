package com.example.sleepmonitor_master_v3

import android.bluetooth.le.ScanResult
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


class BleAdapter: BaseQuickAdapter<ScanResult, BaseViewHolder>(R.layout.item_ble) {


    override fun convert(holder: BaseViewHolder, item: ScanResult) {
        holder.setText(R.id.tvMac,item.device.address)
        holder.setText(R.id.tvName,item.device.name)

    }
}