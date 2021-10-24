﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using WebAPI.Models;

namespace WebAPI.Migrations
{
    [DbContext(typeof(SkuciSeDBContext))]
    [Migration("20211023195301_Initial")]
    partial class Initial
    {
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "2.1.4-rtm-31024");

            modelBuilder.Entity("WebAPI.Models.User", b =>
                {
                    b.Property<uint>("Id")
                        .ValueGeneratedOnAdd();

                    b.Property<DateTime>("DateCreated");

                    b.Property<string>("Email");

                    b.Property<string>("FirstName");

                    b.Property<string>("LastName");

                    b.Property<string>("Username");

                    b.HasKey("Id");

                    b.HasIndex("Email")
                        .IsUnique();

                    b.HasIndex("Username")
                        .IsUnique();

                    b.ToTable("Users");

                    b.HasData(
                        new { Id = 1u, DateCreated = new DateTime(2021, 10, 23, 21, 53, 0, 928, DateTimeKind.Local), Email = "test@mail.com", FirstName = "John", LastName = "Doe", Username = "test" },
                        new { Id = 2u, DateCreated = new DateTime(2021, 10, 23, 21, 53, 0, 930, DateTimeKind.Local), Email = "test2@mail.com", FirstName = "Jack", LastName = "Daniels", Username = "test2" }
                    );
                });
#pragma warning restore 612, 618
        }
    }
}
