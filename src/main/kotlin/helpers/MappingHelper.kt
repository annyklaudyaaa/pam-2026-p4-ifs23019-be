package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.delcom.dao.DessertDAO // Tambahkan import ini
import org.delcom.dao.PlantDAO
import org.delcom.entities.Dessert // Tambahkan import ini
import org.delcom.entities.Plant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)


fun daoToModel(dao: PlantDAO) = Plant(
    dao.id.value.toString(),
    dao.nama,
    dao.pathGambar,
    dao.deskripsi,
    dao.manfaat,
    dao.efekSamping,
    dao.createdAt,
    dao.updatedAt
)

fun daoToModel(dao: DessertDAO) = Dessert(
    id = dao.id.value.toString(),
    nama = dao.nama,
    pathGambar = dao.pathGambar,
    deskripsi = dao.deskripsi,
    bahanUtama = dao.bahanUtama, // Menggunakan field dessert
    kategori = dao.kategori,     // Menggunakan field dessert
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)