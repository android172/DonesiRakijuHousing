using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class AdvertController : Controller
    {
        private readonly SkuciSeDBContext ctx;
        private readonly string username;
        private readonly int userId;

        public AdvertController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value;
            userId = int.Parse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value);
        }

        [HttpPost]
        [Route("get_recent_adverts")]
        public ActionResult<IEnumerable<object>> GetRecentAdverts(int numOfAdverts)
        {
            var adverts = ctx.Adverts.OrderBy(ad => ad.DateCreated).Take(numOfAdverts).Select(Listing.AdListing);

            return adverts.ToList();

        }

        [AllowAnonymous]
        [HttpGet]
        [Route("get_all_cities")]
        public ActionResult<IEnumerable<String>> GetAllCities()
        {
            return SkuciSeDBContext.CityNames;
        }

        [HttpPost]      
        [Route("get_advert")]
        public ActionResult<Advert> GetAdvert(int advertId)
        {
            var exists = ctx.Adverts.Where(ad => ad.Id == advertId);        // CHANGE LATER

            if (exists.Any())
                return exists.FirstOrDefault();
            else
                return NotFound("Advert doesn't exist");
        }

        //[HttpPost]
        //[Route("search_adverts")]
        //public ActionResult<IEnumerable<Advert>> SearchAdverts()
        //{
    
        //}

        [HttpPost]
        [Route("get_my_adverts")]
        public ActionResult<IEnumerable<Advert>> GetMyAdverts()
        {
            return ctx.Adverts.Where(a => a.OwnerID == userId).ToList();
        }
    }
}