﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    //[Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class TestController : ControllerBase
    {
        SkuciSeDBContext ctx;

        private readonly string username;

        public TestController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;

            //username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value;
        }

        [HttpGet("get_user")]
        public ActionResult<User> GetUser([FromForm] uint id)
        {
            return ctx.Users.Find(id);
        }

        [HttpGet("get_all_users")]
        public ActionResult<DbSet<User>> GetUsers()
        {
            return ctx.Users;
        }

        //[HttpGet("get_all_adverts")]
        //public ActionResult<DbSet<Advert>> GetAdverts()
        //{
        //    return ctx.Adverts;
        //}

        [HttpPost("add_user")]
        public ActionResult<User> AddUser([FromForm] string firstName, [FromForm] string lastName)
        {
            User newUser = new User { FirstName = firstName, LastName = lastName };
            ctx.Users.Add(newUser);
            ctx.SaveChanges();
            return newUser;
        }

        [HttpGet]
        [Route("testing")]
        public ActionResult<List<object>> Tester(int adsPerPage, int pageNum, string orderBy, bool ascending, string city, decimal priceMin, decimal priceMax, string saleType, decimal sizeMin, decimal sizeMax, string structureType, int numOfBedrooms, int numOfBathrooms, bool furnished, string residenceType)
        {
            //var adverts = ctx.Adverts.Where(a => (a.Price <= priceMax && a.Price >= priceMin) &&
            //                        a.SaleType.ToString() == saleType &&
            //                        (a.Size <= sizeMax && a.Size >= sizeMin) &&
            //                        a.StructureType.ToString() == structureType &&
            //                        a.NumBedrooms == numOfBedrooms &&
            //                        a.NumBathrooms == numOfBathrooms &&
            //                        a.Furnished == Furnished &&
            //                        a.ResidenceType.ToString() == ResidenceType).OrderBy(a => 3);

            List<object> returns = new List<object>();

            if (adsPerPage == 0)
                returns.Add(new { adsPerPage = 1 });
            else
                returns.Add(new { adsPerPage = adsPerPage });

            if (pageNum == 0)
                returns.Add(new { pageNum = 1 });
            else
                returns.Add(new { pageNum = pageNum });

            if (numOfBedrooms != 0)
                returns.Add(new { numOfBedrooms = numOfBedrooms });

            if (numOfBathrooms != 0)
                returns.Add(new { numOfBathrooms = numOfBathrooms });

            if (!string.IsNullOrWhiteSpace(orderBy))
                returns.Add(new { orderBy = orderBy });

            if(ascending)
                returns.Add(new { ascending = ascending });

            if (furnished)
                returns.Add(new { furnished = furnished });

            if (!string.IsNullOrWhiteSpace(city))
                returns.Add(new { city = city });

            if (priceMin != 0)
                returns.Add(new { priceMin = priceMin });

            if (priceMax == 0)
                returns.Add(new { priceMax = decimal.MaxValue });
            else
                returns.Add(new { priceMax = priceMax });

            if (sizeMin != 0)
                returns.Add(new { sizeMin = sizeMin });

            if (sizeMax == 0)
                returns.Add(new { sizeMax = decimal.MaxValue });
            else
                returns.Add(new { sizeMax = sizeMax });

            if (!string.IsNullOrWhiteSpace(saleType))
                returns.Add(new { saleType = saleType });

            if (!string.IsNullOrWhiteSpace(structureType))
                returns.Add(new { structureType = structureType });

            if (!string.IsNullOrWhiteSpace(residenceType))
                returns.Add(new { residenceType = residenceType });

            return returns;

        }

        //[Authorize]
        [HttpPost]
        [Route("testTokenInfo")]
        public ActionResult<string> TestToken()
        {
            return username;
        }
    }
}