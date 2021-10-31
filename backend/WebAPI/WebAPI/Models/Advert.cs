﻿using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebAPI.Models
{
    public class Advert
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public uint Id { get; set; }
        public ResidenceType ResidenceType { get; set; }
        public SaleType SaleType { get; set; }
        public StructureType StructureType { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }
        public string City { get; set; }
        public string Address { get; set; }
        public decimal Size { get; set; } //M^2
        public decimal Price { get; set; } //EURO
        public uint OwnerID { get; set; }
        public uint NumBedrooms { get; set; }
        public uint NumBathrooms { get; set; }
        public bool Furnished { get; set; }
        public uint YearOfMake { get; set; }
        public DateTime DateCreated { get; set; }

        public static implicit operator Listing(Advert a)
        {
            Listing b = new Listing { Id = a.Id, Price = a.Price, Title = a.Title, City = a.City, Address = a.Address,
                                ResidenceType = a.ResidenceType, SaleType = a.SaleType, Size = a.Size, 
                                StructureType = a.StructureType };

            return b;
        }
    }

    public enum ResidenceType { Apartment, House }
    public enum StructureType { Studio, OneRoom, OneAndAHalfRoom, TwoRoom, TwoAndAHalfRoom }
    public enum SaleType { Purchase, Rent }
}
