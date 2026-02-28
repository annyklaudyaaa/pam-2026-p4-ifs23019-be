package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.DessertRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IDessertRepository
import org.delcom.entities.Dessert
import java.io.File
import java.util.*

class DessertService(private val dessertRepository: IDessertRepository) {
    // Mengambil semua data dessert
    suspend fun getAllDesserts(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""

        val desserts = dessertRepository.getDesserts(search)

        val response = DataResponse(
            "success",
            "Berhasil mengambil daftar dessert",
            mapOf(Pair("desserts", desserts))
        )
        call.respond(response)
    }

    // Mengambil data dessert berdasarkan id
    suspend fun getDessertById(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dessert tidak boleh kosong!")

        val dessert = dessertRepository.getDessertById(id) ?: throw AppException(404, "Data dessert tidak tersedia!")

        val response = DataResponse(
            "success",
            "Berhasil mengambil data dessert",
            mapOf(Pair("dessert", dessert))
        )
        call.respond(response)
    }

    // Ambil data request dari form multipart
    private suspend fun getDessertRequest(call: ApplicationCall): DessertRequest {
        val dessertReq = DessertRequest()

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> dessertReq.nama = part.value.trim()
                        "deskripsi" -> dessertReq.deskripsi = part.value
                        "bahanUtama" -> dessertReq.bahanUtama = part.value
                        "kategori" -> dessertReq.kategori = part.value
                    }
                }

                is PartData.FileItem -> {
                    val ext = part.originalFileName
                        ?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" }
                        ?: ""

                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/desserts/$fileName"

                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    dessertReq.pathGambar = filePath
                }

                else -> {}
            }
            part.dispose()
        }

        return dessertReq
    }

    // Validasi request data
    private fun validateDessertRequest(dessertReq: DessertRequest){
        val validatorHelper = ValidatorHelper(dessertReq.toMap())
        validatorHelper.required("nama", "Nama dessert tidak boleh kosong")
        validatorHelper.required("deskripsi", "Deskripsi tidak boleh kosong")
        validatorHelper.required("bahanUtama", "Bahan utama tidak boleh kosong")
        validatorHelper.required("kategori", "Kategori tidak boleh kosong")
        validatorHelper.required("pathGambar", "Gambar tidak boleh kosong")
        validatorHelper.validate()

        val file = File(dessertReq.pathGambar)
        if (!file.exists()) {
            throw AppException(400, "Gambar dessert gagal diupload!")
        }
    }

    // Menambahkan data dessert
    suspend fun createDessert(call: ApplicationCall) {
        val dessertReq = getDessertRequest(call)
        validateDessertRequest(dessertReq)

        val existDessert = dessertRepository.getDessertByName(dessertReq.nama)
        if(existDessert != null){
            val tmpFile = File(dessertReq.pathGambar)
            if(tmpFile.exists()) tmpFile.delete()
            throw AppException(409, "Dessert dengan nama ini sudah terdaftar!")
        }

        val dessertId = dessertRepository.addDessert(dessertReq.toEntity())

        val response = DataResponse(
            "success",
            "Berhasil menambahkan menu dessert baru",
            mapOf(Pair("dessertId", dessertId))
        )
        call.respond(response)
    }

    // Mengubah data dessert
    suspend fun updateDessert(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dessert tidak boleh kosong!")

        val oldDessert = dessertRepository.getDessertById(id) ?: throw AppException(404, "Data dessert tidak tersedia!")

        val dessertReq = getDessertRequest(call)

        if(dessertReq.pathGambar.isEmpty()){
            dessertReq.pathGambar = oldDessert.pathGambar
        }

        validateDessertRequest(dessertReq)

        if(dessertReq.nama != oldDessert.nama){
            val existDessert = dessertRepository.getDessertByName(dessertReq.nama)
            if(existDessert != null){
                val tmpFile = File(dessertReq.pathGambar)
                if(tmpFile.exists()) tmpFile.delete()
                throw AppException(409, "Dessert dengan nama ini sudah terdaftar!")
            }
        }

        if(dessertReq.pathGambar != oldDessert.pathGambar){
            val oldFile = File(oldDessert.pathGambar)
            if(oldFile.exists()){
                oldFile.delete()
            }
        }

        val isUpdated = dessertRepository.updateDessert(id, dessertReq.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data dessert!")

        call.respond(DataResponse("success", "Berhasil mengubah data dessert", null))
    }

    // Menghapus data dessert
    suspend fun deleteDessert(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID dessert tidak boleh kosong!")

        val oldDessert = dessertRepository.getDessertById(id) ?: throw AppException(404, "Data dessert tidak tersedia!")
        val oldFile = File(oldDessert.pathGambar)

        val isDeleted = dessertRepository.removeDessert(id)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data dessert!")

        if (oldFile.exists()) oldFile.delete()

        call.respond(DataResponse("success", "Berhasil menghapus data dessert", null))
    }

    // Mengambil file gambar dessert
    suspend fun getDessertImage(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: return call.respond(HttpStatusCode.BadRequest)

        val dessert = dessertRepository.getDessertById(id)
            ?: return call.respond(HttpStatusCode.NotFound)

        val file = File(dessert.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)

        call.respondFile(file)
    }
}