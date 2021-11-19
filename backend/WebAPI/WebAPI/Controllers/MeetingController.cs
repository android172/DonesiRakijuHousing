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
        [Route("change_user_info")]
        public ActionResult ChangeInfo(string newUsername, string newFirstName, string newLastName)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Regex imePrezimeReg = new Regex(@"^([ \u00c0-\u01ffa-zA-Z'\-])+$");
            Regex usernameReg = new Regex(@"^[A-Za-z0-9_-]{4,16}$");

            User result = ctx.Users.Where(u => u.Id == userId).FirstOrDefault();

            if (newUsername != null)
            {
                if (!usernameReg.IsMatch(newUsername))
                    return BadRequest("Regex does not match.");

                result.Username = newUsername;
            }


            if (newFirstName != null)
            {
                if (!imePrezimeReg.IsMatch(newFirstName))
                    return BadRequest("Regex does not match.");

                result.FirstName = newFirstName;
            }


            if (newLastName != null)
            {
                if (!imePrezimeReg.IsMatch(newLastName))
                    return BadRequest("Regex does not match.");

                result.LastName = newLastName;
            }

            ctx.Users.Update(result);
            ctx.SaveChanges();

            return Ok("Info changed.");
        }

        [HttpPost]
        [Route("arrange_meeting")]
        public ActionResult<string> ArrangeMeeting(uint advertId, DateTime time)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Meeting newMeeting = new Meeting() { AdvertId = advertId, Time = time, VisitorId = userId };

            try
            {
                ctx.Meetings.Add(newMeeting);
                ctx.SaveChanges();
                return Ok("Meeting proposal sent.");
            }
            catch(Exception e)
            {
                return StatusCode(500, "Failed to arrange meeting.");
            }

        }

        [HttpPost]
        [Route("get_my_meetings")]
        public ActionResult<IEnumerable<object>> GetMyMeetings()
        {
            return ctx.Meetings.
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
                Where(m => m.OwnerId == userId || m.m.VisitorId == userId).ToList();
        }

        [HttpPost]
        [Route("confirm_meeting")]
        public ActionResult<string> ConfirmMeeting(uint meetingId)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Meeting meeting = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (meeting == null)
                return NotFound("Meeting does not exist.");

            meeting.AgreedUpon = true;

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
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Meeting result = ctx.Meetings.Where(m => m.Id == meetingId).FirstOrDefault();

            if (result == null)
                return NotFound("Meeting does not exist.");

            if (result.AgreedUpon == true)
                return BadRequest("Meeting time is already agreed upon.");

            if (result.Concluded == true)
                return BadRequest("Meeting has already been concluded.");

            result.Time = newTime;

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
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            return ctx.Meetings.
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
                Where(m => m.OwnerId == userId && m.m.Time <= DateTime.Now).Select(j => j.m).ToList();
        }

        [HttpPost]
        [Route("conclude_meeting")]
        public ActionResult<string> ConcludeMeeting(uint meetingId)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

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
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

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
