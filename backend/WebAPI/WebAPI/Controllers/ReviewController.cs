using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Net.Http.Headers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using WebAPI.Helpers;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    public class ReviewController : Controller
    {
        SkuciSeDBContext ctx;

        private readonly string username;
        private readonly uint userId;

        public ReviewController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name)?.Value;
            uint.TryParse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out userId);
        }

        [HttpPost]
        [Route("post_review")]
        public ActionResult<string> PostReview(uint advertId, uint rating, string text)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return Ok("Not ready");
        }

        [HttpPost]
        [Route("get_my_available_reviews")]
        public ActionResult<string> AvailableReviews()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return Ok("Not ready");
        }

        [HttpPost]
        [Route("calculate_advert_rating")]
        public ActionResult<string> CalculateAdvertRating(uint advertId)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return Ok("Not ready");
        }

        [HttpPost]
        [Route("calculate_user_rating")]
        public ActionResult<string> CalculateUserRating()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return Ok("Not ready");
        }
    }
}
