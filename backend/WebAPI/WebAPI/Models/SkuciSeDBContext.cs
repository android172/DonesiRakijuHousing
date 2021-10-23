using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebAPI.Models
{
    public class SkuciSeDBContext : DbContext
    {
        public SkuciSeDBContext(DbContextOptions<SkuciSeDBContext> options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>().HasIndex(u => u.Email).IsUnique();
            modelBuilder.Entity<User>().HasIndex(u => u.Username).IsUnique();

            modelBuilder.Entity<User>().HasData(
                new User { Id = 1, Username = "test", Email = "test@mail.com", FirstName = "John", LastName = "Doe", DateCreated = DateTime.Now});

            modelBuilder.Entity<User>().HasData(
                new User { Id = 2, Username = "test2", Email = "test2@mail.com", FirstName = "Jack", LastName = "Daniels", DateCreated = DateTime.Now });
        }
    }
}
