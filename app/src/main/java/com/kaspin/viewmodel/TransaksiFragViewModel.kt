package com.kaspin.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.kaspin.data.model.BarangDataClass
import com.kaspin.data.model.HeaderTransaksiDataClass
import com.kaspin.helper.SQLiteHelper
import com.kaspin.util.CommonUtil
import com.kaspin.util.Constants
import com.kaspin.util.SharedPreferenceUtil

class TransaksiFragViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var sqLiteHelper: SQLiteHelper
    private lateinit var sharedPref: SharedPreferenceUtil

    val resultBarangList = MutableLiveData<List<BarangDataClass>>()

    fun init(context: Context){
        sqLiteHelper = SQLiteHelper(context)
        sharedPref = SharedPreferenceUtil(context)
    }

    fun loadBarangList(){
        val dataList = sqLiteHelper.getAllBarangWithStock()
        var newList = ArrayList<BarangDataClass>()
        if (dataList.size > 0){
            for (i in dataList){
                var data = BarangDataClass()
                data.id_barang = i.id_barang ?: 0
                data.kode_barang = i.kode_barang
                data.nama_barang = i.nama_barang
                data.stock = i.stock
                newList.add(data)
            }
        }
        resultBarangList.postValue(newList)
    }

    fun createParentTransaksi(){
        val lastRecord = sqLiteHelper.getLastRecordTransaksiHeader()
        if (lastRecord.id_transaksi == 0){
            //create init header
            var systemCurrent = System.currentTimeMillis()
            var data = HeaderTransaksiDataClass()
            data.id_transaksi = null
            data.id_detail_transaksi = "${systemCurrent}"
            data.nama_transaksi = "Order 1"
            data.status = 1
            sqLiteHelper.insertHeaderTransaksi(data)
            sharedPref.put(Constants.PREF_ID_DETAIL_TRANSAKSI, "${systemCurrent}")
        }else{
            val isActive = lastRecord.status
            if (isActive == 1){
                //continue
                    sharedPref.put(Constants.PREF_ID_DETAIL_TRANSAKSI, lastRecord.id_detail_transaksi)
            }else{
                //create new header
                var systemCurrent = System.currentTimeMillis()
                var data = HeaderTransaksiDataClass()
                data.id_transaksi = null
                data.id_detail_transaksi = "${systemCurrent}"
                data.nama_transaksi = "Order ${lastRecord.id_transaksi ?: 0 + 1}"
                data.status = 1
                sqLiteHelper.insertHeaderTransaksi(data)
                sharedPref.put(Constants.PREF_ID_DETAIL_TRANSAKSI, "${systemCurrent}")
            }
        }
    }

    fun addToCart(){

    }
}