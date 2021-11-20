using Microsoft.AspNetCore.Authorization;
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
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
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

            var result = ctx.Meetings.Where(m => m.AdvertId == advertId && m.VisitorId == userId).FirstOrDefault();

            if (result == null)
                return NotFound("You can't post a review because meeting does not exist.");

            Review newReview = new Review { MeetingId = result.Id, Rating = rating, Text = text };

            try
            {
                ctx.Reviews.Add(newReview);
                ctx.SaveChanges();
                return Ok("Review posted.");
            }
            catch
            {
                return StatusCode(500, "Failed to post a review.");
            }
        }

        [HttpPost]
        [Route("get_my_available_reviews")]
        public ActionResult<IEnumerable<Meeting>> AvailableReviews()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return ctx.Meetings.Where(m => m.VisitorId == userId && m.Concluded == true).ToList();

            
        }

        [HttpPost]
        [Route("calculate_advert_rating")]
        public ActionResult<decimal> CalculateAdvertRating(uint advertId)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            var result = ctx.Meetings.Where(m => m.AdvertId == advertId)
                            .Join(ctx.Reviews, m => m.Id, r => r.MeetingId, (m, r) => r);

            if(result.Any())
                return (decimal)result.Average(r => r.Rating);
            return 0;
        }

        [HttpPost]
        [Route("calculate_user_rating")]
        public ActionResult<decimal> CalculateUserRating(uint idUser)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            var result = ctx.Adverts.Where(ad => ad.OwnerId == idUser)
                            .Join(ctx.Meetings, ad => ad.Id, m => m.AdvertId, (ad, m) => new { ad.Id, m })
                            .Join(ctx.Reviews, j => j.m.Id, r => r.MeetingId, (j, r) => new { j.Id, r })
                            .GroupBy(j => j.Id)
                            .Select(j => new { Advert = j.Key, AverageRating = j.Average(x => x.r.Rating)});

            if (result.Any())
                return (decimal)result.Average(x => x.AverageRating);
            else
                return 0;
        }
    }
}
