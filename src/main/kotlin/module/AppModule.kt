package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.IDessertRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.DessertRepository
import org.delcom.services.PlantService
import org.delcom.services.DessertService
import org.delcom.services.ProfileService
import org.koin.dsl.module


val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }
    single<IDessertRepository> {
        DessertRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }
    single {
        DessertService(get())
    }
    // Profile Service
    single {
        ProfileService()
    }
}