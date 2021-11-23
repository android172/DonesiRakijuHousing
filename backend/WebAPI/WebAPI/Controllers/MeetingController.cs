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
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

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
            JwtHelper.VerifyToken(userId, Request);

            return ctx.Meetings.
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
                Where(m => m.OwnerId == userId || m.m.VisitorId == userId).ToList();
        }

        [HttpPost]
        [Route("confirm_meeting")]
        public ActionResult<string> ConfirmMeeting(uint meetingId)
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            Meeting meeting = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (meeting == null)
                return NotFound("Meeting does not exist.");

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
        [Route("edit_meeting_proposal")]        // PROMENITI
        public ActionResult<string> EditMeeting(uint meetingId, DateTime newTime)
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

            if (result.AgreedOwner == true && result.AgreedVisitor == true)
                return BadRequest("Meeting time is already agreed upon.");

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
        [Route("get_ended_meetings")]
        public ActionResult<IEnumerable<Meeting>> GetMyEndedMeetings()
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            return ctx.Meetings.
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
                Where(j => j.OwnerId == userId && j.m.Concluded == false && j.m.Time <= DateTime.Now).Select(j => j.m).ToList();
        }

        [HttpPost]
        [Route("conclude_meeting")]
        public ActionResult<string> ConcludeMeeting(uint meetingId)
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

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
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

            if (result.Time <= DateTime.Now)
                return BadRequest("Meeting is not done yet.");

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
