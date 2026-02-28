package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Dessert

@Serializable
data class DessertRequest(
    var nama: String = "",
    var deskripsi: String = "",
    var bahanUtama: String = "", // Menggantikan 'manfaat'
    var kategori: String = "",    // Menggantikan 'efekSamping'
    var pathGambar: String = "",
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "deskripsi" to deskripsi,
            "bahanUtama" to bahanUtama,
            "kategori" to kategori,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): Dessert {
        return Dessert(
            nama = nama,
            deskripsi = deskripsi,
            bahanUtama = bahanUtama,
            kategori = kategori,
            pathGambar = pathGambar,
        )
    }
}