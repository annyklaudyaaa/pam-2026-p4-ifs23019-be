package org.delcom.repositories

import org.delcom.entities.Dessert

interface IDessertRepository {
    suspend fun getDesserts(search: String): List<Dessert>
    suspend fun getDessertById(id: String): Dessert?
    suspend fun getDessertByName(name: String): Dessert?
    suspend fun addDessert(dessert: Dessert): String
    suspend fun updateDessert(id: String, newDessert: Dessert): Boolean
    suspend fun removeDessert(id: String): Boolean
}