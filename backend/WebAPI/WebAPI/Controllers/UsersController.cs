using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using WebAPI.Models;

namespace WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        SkuciSeDBContext ctx;
        private readonly string username;
        private readonly int userId;

        public UsersController(SkuciSeDBContext _ctx, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value;
            userId = int.Parse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier).Value);
        }

        [HttpPost]
        [Route("get_user_info")]
        public ActionResult<object> GetUserInfo()
        {
            return ctx.Users.Where(u => u.Username == username).Select(u => new { u.Id, u.Username, u.FirstName, u.LastName, u.Email, u.DateCreated }).FirstOrDefault();
        }

        //[HttpPost]
        //[Route("user_logout")]
        //public ActionResult<string> UserLogout()
        //{
        //    return 
        //}
    }
}