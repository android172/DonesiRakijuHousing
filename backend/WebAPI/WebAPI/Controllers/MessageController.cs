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
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            var chats = ctx.Messages
                .Where(msg => msg.SenderId == userId || msg.ReceiverId == userId)
                .Select(msg => new { UserId = msg.SenderId != userId ? msg.SenderId : msg.ReceiverId })
                .Distinct();

            var chatsWithMessages = ctx.Messages
                .Join(chats,
                    m => true,
                    c => true,
                    (m, c) => new { UserId = c.UserId, Message = m })
                .Where(j => j.Message.ReceiverId == j.UserId || j.Message.ReceiverId == j.UserId);

            var chatsWithLatestMessage = chatsWithMessages
                .Join(
                    chatsWithMessages
                    .GroupBy(j => j.UserId)
                    .Select(g => new { UserId = g.Key, MaxDate = g.Max(j => j.Message.SendDate) }),
                    c1 => c1.UserId,
                    c2 => c2.UserId,
                    (c1, c2) => new { c1.UserId, c1.Message, c2.MaxDate })
                .Where(j => j.Message.SendDate == j.MaxDate)
                .Select(w => new { w.UserId, w.Message });

            return chatsWithLatestMessage.ToList();
        }

        [HttpPost]
        [Route("get_chat")]
        public ActionResult<IEnumerable<object>> GetChat(uint otherUserId)
        {
            if (JwtHelper.VerifyToken(userId, Request))
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
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            if (otherUserId == userId)
                return BadRequest("You cannot send messages to yourself!");

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
        public ActionResult<bool> CheckMessages()
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            var exists = ctx.Messages.Where(m => m.ReceiverId == userId && m.Seen == false).FirstOrDefault();
            if (exists != null)
                return true;
            else 
                return false;
        }

        [HttpPost]
        [Route("get_user_info")]
        public ActionResult<object> GetUserInfo(uint otherUserId)
        {
            if (JwtHelper.VerifyToken(userId, Request))
                return Unauthorized();

            User user = ctx.Users.Find(otherUserId);

            bool online = JwtHelper.CheckActiveToken(otherUserId) != null;

            return new { user.ImageUrl, user.Username, Online = online };
        }
    }
}
