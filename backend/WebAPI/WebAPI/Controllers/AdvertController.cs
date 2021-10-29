using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AdvertController : Controller
    {
        private readonly SkuciSeDBContext ctx;
        private List<String> ListaGradova = new List<string>() { "Beograd", "Novi Sad", "Niš", "Kragujevac",
                                                                "Priština", "Subotica", "Zrenjanin", "Pančevo",
                                                                "Čačak", "Kruševac", "Kraljevo", "Novi Pazar",
                                                                "Smederevo", "Leskovac", "Užice", "Vranje", "Valjevo",
                                                                "Šabac", "Sombor", "Požarevac", "Pirot", "Zaječar",
                                                                "Kikinda", "Sremska Mitrovica", "Jagodina", "Vršac",
                                                                "Bor", "Prokuplje", "Loznica"};

        public AdvertController(SkuciSeDBContext _ctx)
        {
            ctx = _ctx;
        }

        [HttpGet]
        [Route("get_recent_adverts")]
        public ActionResult<IEnumerable<Advert>> GetRecentAdverts(int numOfAdverts)
        {
            var adverts = ctx.Adverts.OrderBy(ad => ad.DateCreated).Take(numOfAdverts);

            return adverts.ToList();        // CHANGE LATER

        }

        [HttpGet]
        [Route("get_all_cities")]
        public ActionResult<IEnumerable<String>> GetAllCities()
        {
            return ListaGradova;
        }

        [HttpPost]
        [Route("get_advert")]
        public ActionResult<Advert> GetAdvert(int advertId)
        {
            var exists = ctx.Adverts.Where(ad => ad.Id == advertId);

            if (exists.Any())
                return exists.FirstOrDefault();
            else
                return NotFound("Advert doesn't exist");
        }

        //[HttpGet]
        //[Route("search_adverts")]
        //public ActionResult<IEnumerable<Advert>> SearchAdverts()
        //{
    
        //}
    }
}