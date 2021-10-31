using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Helpers;

namespace WebAPI.Models
{
    public class SkuciSeDBContext : DbContext
    {
        private const int seed = 0;
        public SkuciSeDBContext(DbContextOptions<SkuciSeDBContext> options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
        public DbSet<User> Users_UnconfirmedEmail { get; set; }
        public DbSet<Advert> Adverts { get; set; }
        public DbSet<AdvertImage> AdvertImages { get; set; }

        public static List<string> CityNames = new List<string>() { "Beograd", "Novi Sad", "Niš", "Kragujevac",
                                                                "Priština", "Subotica", "Zrenjanin", "Pančevo",
                                                                "Čačak", "Kruševac", "Kraljevo", "Novi Pazar",
                                                                "Smederevo", "Leskovac", "Užice", "Vranje", "Valjevo",
                                                                "Šabac", "Sombor", "Požarevac", "Pirot", "Zaječar",
                                                                "Kikinda", "Sremska Mitrovica", "Jagodina", "Vršac",
                                                                "Bor", "Prokuplje", "Loznica"};

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>().HasIndex(u => u.Email).IsUnique();
            modelBuilder.Entity<User>().HasIndex(u => u.Username).IsUnique();

            modelBuilder.Entity<AdvertImage>().HasKey(ai => ai.AdvertID);
            modelBuilder.Entity<AdvertImage>().HasKey(ai => ai.ImageUrl);

            SkuciSeDBSeed.Seed(modelBuilder, seed);
        }
    }
}
