package org.delcom.dao

import org.delcom.tables.DessertTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class DessertDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, DessertDAO>(DessertTable)

    var nama by DessertTable.nama
    var pathGambar by DessertTable.pathGambar
    var deskripsi by DessertTable.deskripsi
    var bahanUtama by DessertTable.bahanUtama
    var kategori by DessertTable.kategori // Misal: Cake, Pastry, Cold Dessert
    var createdAt by DessertTable.createdAt
    var updatedAt by DessertTable.updatedAt
}