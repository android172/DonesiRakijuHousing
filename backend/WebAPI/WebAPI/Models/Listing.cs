using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebAPI.Models
{

    public static class Listing
    {
        public static Func<Advert, object> AdListing =
            advert => new
            {
                Id = advert.Id,
                Price = advert.Price,
                Title = advert.Title,
                City = advert.City,
                Address = advert.Address,
                Size = advert.Size,
                SaleType = advert.SaleType,
                ResidenceType = advert.ResidenceType,
                DateCreated = advert.DateCreated
            };

    }
}