package com.example.skucise

import java.util.*

data class Advert(
    val id: UInt,
    val residenceType: ResidenceType = ResidenceType.Apartman,
    val saleType: SaleType,
    val structureType: StructureType = StructureType.Studio,
    val title: String,
    val description: String = "",
    val city: String,
    val address: String,
    val size: Double,
    val price: Double,
    val ownerId: UInt = 0u,
    val numberOfBedrooms: UInt = 0u,
    val numberOfBathrooms: UInt = 0u,
    val yearOfMake: UInt = 0u,
    val furnished: Boolean = false,
    val dateCreated: Date = Date()
)

enum class ResidenceType { Apartman, Kuca }
enum class StructureType { Studio, Jednosobna, JednaIpoSoba, Dvosobna, DveIpoSobe }
enum class SaleType { Prodaja, Iznajmljivanje }