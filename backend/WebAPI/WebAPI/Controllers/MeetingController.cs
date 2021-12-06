using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Net.Http.Headers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using WebAPI.Helpers;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class MeetingController : Controller
    {
        SkuciSeDBContext ctx;

        private readonly string username;
        private readonly uint userId;

        public MeetingController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name)?.Value;
            uint.TryParse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out userId);
        }

        [HttpPost]
        [Route("arrange_meeting")]
        public ActionResult<string> ArrangeMeeting(uint advertId, DateTime time)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            if (DateTime.Now >= time)
                return BadRequest("You can not set a date that has passed.");

            var result = ctx.Meetings.Where(m => m.AdvertId == advertId && m.VisitorId == userId && m.Concluded == false);

            if (result.Any())
                return BadRequest("You already proposed meeting for this advert.");

            Meeting newMeeting = new Meeting() { AdvertId = advertId, Time = time, VisitorId = userId, AgreedVisitor = true, DateCreated = DateTime.Now, AgreedOwner = false, Concluded = false };

            try
            {
                ctx.Meetings.Add(newMeeting);
                ctx.SaveChanges();
                return Ok("Meeting proposal sent.");
            }
            catch
            {
                return StatusCode(500, "Failed to arrange meeting.");
            }

        }

        [HttpPost]
        [Route("get_my_meetings")]
        public ActionResult<IEnumerable<object>> GetMyMeetings()
        {
            JwtHelper.TokenUnverified(userId, Request);

            //return ctx.Meetings.
            //    Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId, ad.Title }).
            //    Where(m => (m.OwnerId == userId || m.m.VisitorId == userId) && m.m.Concluded == false).ToList();

            //var result = ctx.Meetings.
            //        Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId, ad.Title }).
            //        Where(m => (m.OwnerId == userId || m.m.VisitorId == userId) && m.m.Concluded == false).
            //        Select(j => new { OwnerId = j.OwnerId, MeetingData = j.m, OtherUserId = j.OwnerId == userId ? j.m.VisitorId : j.OwnerId, AdvertTitle = j.Title}).
            //        Join(ctx.Users, j => j.OtherUserId, u => u.Id, (j, u) => new { MeetingData = j.MeetingData, OtherUserId = j.OtherUserId, OtherUsername = u.Username, AdvertTitle = j.AdvertTitle, AmIOwner = j.OwnerId == userId ? true : false }).ToList();

            var result = ctx.Meetings.
                    Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId, ad.Title }).
                    Where(m => (m.OwnerId == userId || (m.m.VisitorId == userId && m.m.Time > DateTime.Now)) && m.m.Concluded == false).
                    Select(j => new { OwnerId = j.OwnerId, MeetingData = j.m, OtherUserId = j.OwnerId == userId ? j.m.VisitorId : j.OwnerId, AdvertTitle = j.Title }).
                    Join(ctx.Users, j => j.OtherUserId, u => u.Id, (j, u) => new { MeetingData = j.MeetingData, OtherUserId = j.OtherUserId, OtherUsername = u.Username, AdvertTitle = j.AdvertTitle, AmIOwner = j.OwnerId == userId ? true : false }).ToList();

            return result;
        }

        [HttpPost]
        [Route("confirm_meeting")]
        public ActionResult<string> ConfirmMeeting(uint meetingId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            Meeting meeting = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (meeting == null)
                return NotFound("Meeting does not exist.");


            if (meeting.VisitorId == userId)
                meeting.AgreedVisitor = true;
            else
                meeting.AgreedOwner = true;
            

            try
            {
                ctx.Meetings.Update(meeting);
                ctx.SaveChanges();

                return Ok("Meeting confirmed.");
            }
            catch
            {
                return StatusCode(500, "Failed to confirm meeting.");
            }
        }

        [HttpPost]
        [Route("edit_meeting_proposal")]
        public ActionResult<string> EditMeeting(uint meetingId, DateTime newTime)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

            if (DateTime.Now <= result.Time.AddDays(-1))
                return BadRequest("You can not edit meeting on the day of it.");

            //if (result.AgreedOwner == true && result.AgreedVisitor == true)
            //    return BadRequest("Meeting time is already agreed upon.");

            if (result.Concluded == true)
                return BadRequest("Meeting has already been concluded.");

            result.Time = newTime;
            
            if(result.VisitorId == userId)
            {
                result.AgreedOwner = false;
                result.AgreedVisitor = true;
            }
            else
            {
                result.AgreedOwner = true;
                result.AgreedVisitor = false;
            }

            try
            {
                ctx.Meetings.Update(result);
                ctx.SaveChanges();
                return Ok("Meeting changed proposal sent.");
            }
            catch (Exception e)
            {
                return StatusCode(500, "Failed to change meeting proposal.");
            }
        }

        [HttpPost]
        [Route("cancel_meeting")]
        public ActionResult<string> CancelMeeting(uint meetingId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            var result = ctx.Meetings.Where(m => m.Id == meetingId && m.Concluded == false).
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist or it has been concluded.");

            if (DateTime.Now <= result.m.Time.AddDays(-1))
                return BadRequest("You can not cancel meeting on the day of it.");

            if (result.m.VisitorId != userId && result.OwnerId != userId)
                return BadRequest("You are not part of this meeting and can not cancel it.");

            try
            {
                ctx.Meetings.Remove(result.m);
                ctx.SaveChanges();
                return Ok("Meeting canceled.");
            }
            catch (Exception e)
            {
                return StatusCode(500, "Failed to cancel meeting.");
            }
        }

        //[HttpPost]
        //[Route("get_ended_meetings")]
        //public ActionResult<IEnumerable<Meeting>> GetMyEndedMeetings()
        //{
        //    if (JwtHelper.TokenUnverified(userId, Request))
        //        return Unauthorized();

        //    return ctx.Meetings.
        //        Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
        //        Where(j => j.OwnerId == userId && j.m.Concluded == false && j.m.Time <= DateTime.Now).Select(j => j.m).ToList();
        //}

        [HttpPost]
        [Route("conclude_meeting")]
        public ActionResult<string> ConcludeMeeting(uint meetingId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId && ctx.Adverts.Any(ad => ad.OwnerId == userId)).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

            if (result.Time > DateTime.Now)
                return BadRequest("Meeting has not started.");

            if (result.Concluded == true)
                return BadRequest("Meeting has already been concluded.");

            result.Concluded = true;

            try
            {
                ctx.Meetings.Update(result);
                ctx.SaveChanges();
                return Ok("Meeting concluded.");
            }
            catch (Exception e)
            {
                return StatusCode(500, "Failed to conclude meeting.");
            }
        }

        [HttpPost]
        [Route("delete_meeting")]
        public ActionResult<string> DeleteMeeting(uint meetingId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId && ctx.Adverts.Any(ad => ad.OwnerId == userId) && m.Concluded == false).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist or it has been concluded.");

            if (result.Time > DateTime.Now)
                return BadRequest("Meeting has not started.");

            try
            {
                ctx.Meetings.Remove(result);
                ctx.SaveChanges();
                return Ok("Meeting deleted.");
            }
            catch (Exception e)
            {
                return StatusCode(500, "Failed to delete meeting.");
            }
        }
    }
}
