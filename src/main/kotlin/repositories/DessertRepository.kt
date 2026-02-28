package org.delcom.repositories

import org.delcom.dao.DessertDAO
import org.delcom.entities.Dessert
import org.delcom.helpers.daoToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.DessertTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class DessertRepository : IDessertRepository {
    override suspend fun getDesserts(search: String): List<Dessert> = suspendTransaction {
        if (search.isBlank()) {
            DessertDAO.all()
                .orderBy(DessertTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"

            DessertDAO
                .find {
                    DessertTable.nama.lowerCase() like keyword
                }
                .orderBy(DessertTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getDessertById(id: String): Dessert? = suspendTransaction {
        DessertDAO
            .find { (DessertTable.id eq UUID.fromString(id)) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getDessertByName(name: String): Dessert? = suspendTransaction {
        DessertDAO
            .find { (DessertTable.nama eq name) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addDessert(dessert: Dessert): String = suspendTransaction {
        val dessertDAO = DessertDAO.new {
            nama = dessert.nama
            pathGambar = dessert.pathGambar
            deskripsi = dessert.deskripsi
            bahanUtama = dessert.bahanUtama
            kategori = dessert.kategori
            createdAt = dessert.createdAt
            updatedAt = dessert.updatedAt
        }

        dessertDAO.id.value.toString()
    }

    override suspend fun updateDessert(id: String, newDessert: Dessert): Boolean = suspendTransaction {
        val dessertDAO = DessertDAO
            .find { DessertTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dessertDAO != null) {
            dessertDAO.nama = newDessert.nama
            dessertDAO.pathGambar = newDessert.pathGambar
            dessertDAO.deskripsi = newDessert.deskripsi
            dessertDAO.bahanUtama = newDessert.bahanUtama
            dessertDAO.kategori = newDessert.kategori
            dessertDAO.updatedAt = newDessert.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeDessert(id: String): Boolean = suspendTransaction {
        val rowsDeleted = DessertTable.deleteWhere {
            DessertTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}