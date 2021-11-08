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
using System.Text.Json;
using System.Text.Json.Serialization;
using WebAPI.Helpers;
using Microsoft.Net.Http.Headers;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class AdvertController : Controller
    {
        private readonly SkuciSeDBContext ctx;
        private readonly string username;
        private readonly uint userId;

        public AdvertController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name).Value;
            userId = uint.Parse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value);
        }

        [HttpPost]
        [Route("get_recent_adverts")]
        public ActionResult<IEnumerable<object>> GetRecentAdverts(int numOfAdverts)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

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
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            var exists = ctx.Adverts.Where(ad => ad.Id == advertId);        // CHANGE LATER

            if (exists.Any())
                return exists.FirstOrDefault();
            else
                return NotFound("Advert doesn't exist.");
        }

        public class Filter
        {
            public string Name { get; set; }
            public dynamic Param { get; set; }
        }

        [HttpPost]
        [Route("search_adverts")]
        public ActionResult<IEnumerable<Advert>> SearchAdverts(string filterArray, int adsPerPage, int pageNum, string orderBy, bool ascending)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Dictionary<string, Func<Advert, dynamic, bool>> filterDict = new Dictionary<string, Func<Advert, dynamic, bool>>
            {
                ["NumBedrooms"] = ((ad, param) => ad.NumBedrooms == int.Parse(param.ToString())),
                ["Price"] = ((ad, param) => ad.Price >= decimal.Parse(param.GetProperty("From").ToString()) && ad.Price <= decimal.Parse(param.GetProperty("To").ToString())),
                ["City"] = ((ad, param) => ad.City == param.ToString()),
                ["SaleType"] = ((ad, param) => ad.SaleType == Enum.Parse(typeof(SaleType), param.ToString())),
                ["Size"] = ((ad, param) => ad.Size >= decimal.Parse(param.GetProperty("From").ToString()) && ad.Size <= decimal.Parse(param.GetProperty("To").ToString())),
                ["NumBathrooms"] = ((ad, param) => ad.NumBathrooms == int.Parse(param.ToString())),
                ["StructureType"] = ((ad, param) => ad.StructureType == Enum.Parse(typeof(StructureType), param.ToString())),
                ["ResidenceType"] = ((ad, param) => ad.ResidenceType == Enum.Parse(typeof(ResidenceType), param.ToString())),
                ["Furnished"] = ((ad, param) => ad.Furnished == param.ToString())
            };

            Dictionary<string, Func<Advert, dynamic>> orderByDict = new Dictionary<string, Func<Advert, dynamic>>
            {
                ["Id"] = (ad => ad.Id.ToString()),
                ["ResidenceType"] = (ad => ad.ResidenceType.ToString()),
                ["SaleType"] = (ad => ad.SaleType.ToString()),
                ["StructureType"] = (ad => ad.StructureType.ToString()),
                ["Title"] = (ad => ad.Title.ToString()),
                ["City"] = (ad => ad.City.ToString()),
                ["Size"] = (ad => ad.Size.ToString()),
                ["Price"] = (ad => ad.Price.ToString()),
                ["NumBedrooms"] = (ad => ad.NumBedrooms.ToString()),
                ["NumBathrooms"] = (ad => ad.NumBathrooms.ToString()),
                ["Furnished"] = (ad => ad.Furnished.ToString()),
                ["YearOfMake"] = (ad => ad.YearOfMake.ToString()),
                ["DateCreated"] = (ad => ad.DateCreated.ToString()),
            };
            
            IEnumerable<Advert> result = ctx.Adverts;

            if(filterArray != null)
            {
                var filters = JsonSerializer.Deserialize<Filter[]>(filterArray);

                foreach (var filter in filters)
                {
                    result = result.Where(ad => filterDict[filter.Name](ad, filter.Param));
                }
            }

            if(orderBy != null)
            {
                if (ascending)
                    result = result.OrderBy(ad => orderByDict[orderBy](ad));
                else
                    result = result.OrderByDescending(ad => orderByDict[orderBy](ad));
            }

            if (adsPerPage == 0)
                adsPerPage = 10;
            if (pageNum == 0)
                pageNum = 1;

            result = result.Take(adsPerPage * pageNum).TakeLast(adsPerPage);

            return result.ToList();
        }

        [HttpPost]
        [Route("get_my_adverts")]
        public ActionResult<IEnumerable<Advert>> GetMyAdverts()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return ctx.Adverts.Where(a => a.OwnerId == userId).ToList();
        }
    }
}