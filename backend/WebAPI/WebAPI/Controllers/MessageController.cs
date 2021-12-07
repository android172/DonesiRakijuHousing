using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Net.Http.Headers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using WebAPI.Helpers;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    public class MessageController : Controller
    {
        SkuciSeDBContext ctx;
        private readonly uint userId;

        public MessageController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            userId = uint.Parse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value);
        }

        [HttpPost]
        [Route("get_chats")]
        public ActionResult<IEnumerable<object>> GetChats() // Returns a list of users who have sent or received messages from the currrent user, and the latest message in the chat
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            var otherUsersWithMessages = ctx.Messages
                .Where(msg => msg.SenderId == userId || msg.ReceiverId == userId)
                .Select(msg => new { OtherUserId = msg.SenderId != userId ? msg.SenderId : msg.ReceiverId, Message = msg } )
                .Distinct();

            var otherUsersWithLatestMessageDate = otherUsersWithMessages
                .GroupBy(o => o.OtherUserId)
                .Select(g => new { OtherUserId = g.Key, MaxDate = g.Max(j => j.Message.SendDate) });

            var otherUsersWithLatestMessage = otherUsersWithMessages
                .Where(m => otherUsersWithLatestMessageDate.Any(o => o.OtherUserId == m.OtherUserId && m.Message.SendDate == o.MaxDate))
                .Select(w => new { w.OtherUserId, w.Message });

            List<object> rms = new List<object>();
            foreach(var rm in otherUsersWithLatestMessage)
            {
                rms.Add(new { User = GetUserDisplay(rm.OtherUserId), Message = rm.Message });
            }

            return rms;
        }

        [HttpPost]
        [Route("get_chat")]
        public ActionResult<IEnumerable<Message>> GetChat(uint otherUserId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            var messages = ctx.Messages
                .Where(m => (m.SenderId == otherUserId && m.ReceiverId == userId) || (m.SenderId == userId && m.ReceiverId == otherUserId))
                .OrderBy(m => m.SendDate).ToList();

            for (int i = 0; i < messages.Count; i++)
            {
                if (messages[i].ReceiverId == userId)
                {
                    messages[i].Seen = true;
                    ctx.Messages.Update(messages[i]);
                }
            }

            ctx.SaveChanges();

            return messages;
        }

        [HttpPost]
        [Route("send_message")]
        public IActionResult SendMessage(uint otherUserId, string content)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            if (otherUserId == userId)
                return BadRequest("You cannot send messages to yourself!");

            var hasMeeting = ctx.Meetings
                .Where(m => m.AgreedOwner && m.AgreedVisitor
                && ((m.VisitorId == otherUserId && ctx.Adverts.Where(a => a.Id == m.AdvertId).FirstOrDefault().OwnerId == userId)
                    || ((m.VisitorId == userId && ctx.Adverts.Where(a => a.Id == m.AdvertId).FirstOrDefault().OwnerId == otherUserId))));

            if (!hasMeeting.Any())
                return BadRequest("You must have an arranged meeting before you are able to chat with the recipient!");

            Message msg = new Message { SenderId = userId, ReceiverId = otherUserId, Content = content, Seen = false, SendDate = DateTime.Now };

            try
            {
                ctx.Messages.Add(msg);
                ctx.SaveChanges();

                return Ok();
            }
            catch
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("check_messages")]
        public ActionResult<int> CheckMessages()
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            return ctx.Messages.Where(m => m.ReceiverId == userId && m.Seen == false).Count();
        }

        [HttpPost]
        [Route("get_user_status")]
        public ActionResult<UserDisplay> GetUserInfo(uint otherUserId)
        {
            if (JwtHelper.TokenUnverified(userId, Request))
                return Unauthorized();

            return GetUserDisplay(otherUserId);
        }

        public UserDisplay GetUserDisplay(uint id)
        {
            UserDisplay display = new UserDisplay();
            display.Id = id;
            display.Online = JwtHelper.CheckActiveToken(id) != null;
            User user = ctx.Users.Find(id);
            display.DisplayName = (user.FirstName + " " + user.LastName).Trim();

            return display;
        }
    }
}
