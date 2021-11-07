using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Net.Http.Headers;
using WebAPI.Helpers;
using WebAPI.Models;
using WebAPI.Services;

namespace WebAPI.Controllers
{
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
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name).Value;
            string temp = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value;
            userId = uint.Parse(temp);
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
    }
}