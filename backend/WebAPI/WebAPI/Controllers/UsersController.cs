using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Net.Http.Headers;
using WebAPI.Helpers;
using WebAPI.Models;
using WebAPI.Services;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        SkuciSeDBContext ctx;
        SkuciSeEmailService ems;

        private readonly string username;
        private readonly uint userId;

        public UsersController(SkuciSeDBContext _ctx, SkuciSeEmailService _ems, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            ems = _ems;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name)?.Value;
            uint.TryParse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out userId);
        }

        [HttpPost]
        [Route("get_user_info")]
        public ActionResult<object> GetUserInfo()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active");

            return ctx.Users.Where(u => u.Username == username).Select(u => new { u.Id, u.Username, u.FirstName, u.LastName, u.Email, u.DateCreated }).FirstOrDefault();
        }

        [HttpPost]
        [Route("send_pass_reset_email")]
        public ActionResult ResetPassword()
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            User user = ctx.Users.Where(u => u.Username == username).FirstOrDefault();
            try
            {
                ems.SendPasswordResetEmail(user.Email);
                return Ok();
            }
            catch
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("change_email")]
        public ActionResult ChangeEmail(string newEmail)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            Regex emailReg = new Regex(@"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$");

            if (!emailReg.IsMatch(newEmail))
                return BadRequest("Regex does not match.");

            User result = ctx.Users.Where(u => u.Id == userId).FirstOrDefault();

            if(result != null)
            {
                result.Email = newEmail;
                result.Confirmed = false;           // email is not confirmed anymore.
                ctx.Users.Update(result);
                ctx.SaveChanges();
                ems.SendConfirmationEmail(newEmail);
                return Ok("Email changed. Confrim new email");
            }

            return NotFound("User does not exist.");
        }

        [HttpPost]
        [Route("change_password")]
        public ActionResult ChangePassword(string oldPassword, string newPassword)
        {
            string token = JwtHelper.CheckActiveToken(userId);

            if (token == null || !token.Equals(Request.Headers[HeaderNames.Authorization].ToString().Replace("Bearer ", "")))
                return Unauthorized("Token is not active.");

            User result = ctx.Users.Where(u => u.Id == userId && u.Password == oldPassword).FirstOrDefault();

            if (result == null)
                return NotFound("User does not exist or old password is incorrect.");

            Regex passReg = new Regex(@"^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,32}$");

            if (!passReg.IsMatch(newPassword))
                return BadRequest("Regex does not match.");

            result.Password = newPassword;
            ctx.Users.Update(result);
            ctx.SaveChanges();
            return Ok("Password changed.");
        }

        [HttpPost]
        [Route("change_user_info")]
        public ActionResult ChangeInfo(string newUsername, string newFirstName, string newLastName)
        {
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
            //return ctx.Adverts.Where(ad => ad.OwnerId == userId).Join(ctx.Meetings, ad => ad.Id, m => m.AdvertId, (m, ad) => m);
            return ctx.Meetings.
                Join(ctx.Adverts, m => m.AdvertId, ad => ad.Id, (m, ad) => new { m, ad.OwnerId }).
                Where(m => m.OwnerId == userId || m.m.VisitorId == userId).ToList();
        }

        [HttpPost]
        [Route("confirm_meeting")]
        public ActionResult<string> ConfirmMeeting(uint meetingId)
        {
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
    }
}