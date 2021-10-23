using System;
using System.Collections.Generic;
using System.Linq;
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

        public UsersController(SkuciSeDBContext _ctx)
        {
            ctx = _ctx;
        }

        [HttpGet("get_user")]
        public ActionResult<User> GetUser([FromForm] uint id)
        {
            return ctx.Users.Find(id);
        }

        [HttpGet("get_all_users")]
        public ActionResult<DbSet<User>> GetUsers()
        {
            return ctx.Users;
        }

        [HttpPost("add_user")]
        public ActionResult<User> AddUser([FromForm] string firstName, [FromForm] string lastName)
        {
            User newUser = new User { FirstName = firstName, LastName = lastName };
            ctx.Users.Add(newUser);
            ctx.SaveChanges();
            return newUser;
        }
    }
}