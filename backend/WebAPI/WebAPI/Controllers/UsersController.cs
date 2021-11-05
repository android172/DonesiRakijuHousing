using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
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
        private readonly int userId;

        public UsersController(SkuciSeDBContext _ctx, SkuciSeEmailService _ems, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            ems = _ems;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name).Value;
            string temp = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value;
            userId = int.Parse(temp);
        }

        [HttpPost]
        [Route("get_user_info")]
        public ActionResult<object> GetUserInfo()
        {
            if (!LoginController.CheckActiveToken(userId))
                return Unauthorized("Token is not found or active");

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

        //[HttpPost]
        //[Route("user_logout")]
        //public ActionResult<string> UserLogout()
        //{
        //    return 
        //}
    }
}