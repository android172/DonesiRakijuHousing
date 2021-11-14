using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Models;

namespace WebAPI.Helpers
{
    public static class SkuciSeDBSeed
    {
        public static void Seed(ModelBuilder modelBuilder, int seed)
        {
            Random rgen = new Random(seed);

            modelBuilder.Entity<User>().HasData(
                new User { Id = 1, Username = "test", Password = "test", Email = "test@mail.com", FirstName = "John", LastName = "Doe", DateCreated = DateTime.Now, Confirmed = true });

            modelBuilder.Entity<User>().HasData(
                new User { Id = 2, Username = "test2", Password = "test2", Email = "test2@mail.com", FirstName = "Jack", LastName = "Daniels", DateCreated = DateTime.Now, Confirmed = false });

            uint numApartments = 35;
            uint numHouses = 20;

            var furnishedOptions = new[] { true, false };
            var saleTypeOptions = new[] { SaleType.Purchase, SaleType.Rent };
            var structureTypeOptionsHouse = new[] { StructureType.OneRoom, StructureType.OneAndAHalfRoom, StructureType.TwoRoom, StructureType.TwoAndAHalfRoom };
            var structureTypeOptionsApartment = new[] { StructureType.Studio, StructureType.OneRoom, StructureType.OneAndAHalfRoom, StructureType.TwoRoom, StructureType.TwoAndAHalfRoom };

            Dictionary<StructureType, decimal> sizeByStructureType = new Dictionary<StructureType, decimal>
            {
                [StructureType.Studio] = 26.00M,
                [StructureType.OneRoom] = 30.00M,
                [StructureType.OneAndAHalfRoom] = 40.00M,
                [StructureType.TwoRoom] = 48.00M,
                [StructureType.TwoAndAHalfRoom] = 30.00M,
            };

            Dictionary<SaleType, decimal> priceBySaleType = new Dictionary<SaleType, decimal>
            {
                [SaleType.Purchase] = 1234.00M,
                [SaleType.Rent] = 9.00M,
            };

            var streetNames = new[]
            {
                "BANJIČKA",
                "BEOGRADSKOG BATALJONA",
                "MILIVOJA POPOVIĆA-MIĆE",
                "LAZARA JANKOVIĆA",
                "SEDMOG JULA",
                "BRÐANSKA",
                "STARA LIPOVICA",
                "PARTIZANSKA",
                "IZVORSKA",
                "DOBRIVOJA BANOVIĆA",
                "STRAHINJE PETROVIĆA",
                "DR BORISLAVA VUJADINOVIĆA",
                "STEFANA KNEŽEVIĆA",
                "PROTE JEFTIMIJA IVANOVIĆA",
                "STEVANA SIMIĆA"
            };

            for (uint i = 1; i < numApartments + 1; i++)
            {
                var structureType = PickOne(rgen, structureTypeOptionsApartment);
                var saleType = PickOne(rgen, saleTypeOptions);
                var size = sizeByStructureType[structureType] + rgen.Next() % 6;

                var price = size * priceBySaleType[saleType];

                modelBuilder.Entity<Advert>().HasData(
                    new Advert
                    {
                        Id = i,
                        ResidenceType = ResidenceType.Apartment,
                        SaleType = saleType,
                        StructureType = structureType,
                        Title = "Stan na prodaju!",
                        Description = @"Opis oglasa. Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,",
                        City = $"{PickOne(rgen, SkuciSeDBContext.CityNames.ToArray())}",
                        Address = $"{PickOne(rgen, streetNames)} {(rgen.Next() % 30)}",
                        Size = size,
                        Price = price,
                        OwnerId = (uint)rgen.Next() % 2 + 1,
                        NumBedrooms = (uint)(rgen.Next() % 3) + 1,
                        NumBathrooms = (uint)(rgen.Next() % 2) + 1,
                        Furnished = PickOne(rgen, furnishedOptions),
                        YearOfMake = (uint)(1970 + (rgen.Next() % 50)),
                        DateCreated = DateTime.Now
                    });
            }

            for (uint i = 1; i < numHouses + 1; i++)
            {
                var st = PickOne(rgen, structureTypeOptionsHouse);
                var size = sizeByStructureType[st] + rgen.Next() % 6;

                modelBuilder.Entity<Advert>().HasData(
                    new Advert
                    {
                        Id = i + numApartments,
                        ResidenceType = ResidenceType.House,
                        SaleType = PickOne(rgen, saleTypeOptions),
                        StructureType = st,
                        Title = "Kuća na prodaju!",
                        Description = @"Opis oglasa. Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,",
                        City = $"{PickOne(rgen, SkuciSeDBContext.CityNames.ToArray())}",
                        Address = $"{PickOne(rgen, streetNames)} {(rgen.Next() % 30)}",
                        Size = size,
                        Price = size * 1234,
                        OwnerId = (uint)rgen.Next() % 2 + 1,
                        NumBedrooms = (uint)(rgen.Next() % 4) + 1,
                        NumBathrooms = (uint)(rgen.Next() % 2) + 1,
                        Furnished = PickOne(rgen, furnishedOptions),
                        YearOfMake = (uint)(1950 + (rgen.Next() % 70)),
                        DateCreated = DateTime.Now
                    });
            }

            modelBuilder.Entity<Message>().HasData(
                    new Message
                    {
                        Id = 1,
                        SenderId = 1,
                        ReceiverId = 2,
                        Content = "Hello world!",
                        SendDate = DateTime.Now,
                        Seen = false
                    });

            for (uint i = 2; i <= 11; i++)
            {
                modelBuilder.Entity<Message>().HasData(
                    new Message
                    {
                        Id = i,
                        SenderId = 1,
                        ReceiverId = 2,
                        Content = $"Hello world! ({i})",
                        SendDate = DateTime.UnixEpoch + new TimeSpan(days: 365*45 + rgen.Next() % 2000, 0, 0, 0),
                        Seen = PickOne(rgen, furnishedOptions)
                    });
                modelBuilder.Entity<Message>().HasData(
                    new Message
                    {
                        Id = i + 10,
                        SenderId = 2,
                        ReceiverId = 1,
                        Content = $"Hello! ({i + 10})",
                        SendDate = DateTime.UnixEpoch + new TimeSpan(days: 365 * 45 + rgen.Next() % 2000, 0, 0, 0),
                        Seen = PickOne(rgen, furnishedOptions)
                    });
            }

            for (uint i = 2; i <= 5; i++)
            {
                modelBuilder.Entity<FavouriteAdvert>().HasData(
                    new FavouriteAdvert
                    {
                        UserId = 1,
                        AdvertId = (uint)rgen.Next() % 51 + 1
                    });

                modelBuilder.Entity<FavouriteAdvert>().HasData(
                    new FavouriteAdvert
                    {
                        UserId = 2,
                        AdvertId = (uint)rgen.Next() % 51 + 1
                    });
            }

            modelBuilder.Entity<Meeting>().HasData(
                new Meeting
                {
                    Id = 1,
                    AdvertId = 1,
                    VisitorId = 2,
                    Time = DateTime.Now,
                    AgreedUpon = true,
                    Concluded = true
                });

            modelBuilder.Entity<Review>().HasData(
                new Review
                {
                    MeetingId = 1,
                    Rating = 4,
                    Text = "Mesto je OK!"
                });
        }

        private static T PickOne<T>(Random r, T[] options)
        {
            return options[r.Next() % options.Length];
        }
    }
}
