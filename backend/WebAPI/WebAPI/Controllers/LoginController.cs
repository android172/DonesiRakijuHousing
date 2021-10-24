using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Models;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace WebAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LoginController : ControllerBase
    {

        SkuciSeDBContext ctx;

        public LoginController(SkuciSeDBContext _ctx)
        {
            ctx = _ctx;
        }

        [HttpGet]
        [Route("user_login_name")]
        public ActionResult<User> UserLoginName([FromForm] string username, [FromForm] string password)
        {
            var exists = ctx.Users.Where(u => u.Username == username && u.Password == password);

            if (exists.Any())
                return exists.FirstOrDefault();

            return NotFound("User doesnt exist");

        }

        [HttpGet]
        [Route("user_login_mail")]
        public ActionResult<User> UserLoginMail([FromForm] string email, [FromForm] string password)
        {
            var exists = ctx.Users.Where(u => u.Email == email && u.Password == password);

            if (exists.Any())
                return exists.FirstOrDefault();

            return NotFound("User doesnt exist");

        }

        [HttpPost]
        [Route("user_register")]
        public ActionResult<User> RegisterUser([FromForm] string firstName, [FromForm] string lastName, [FromForm] string email, [FromForm] string username, [FromForm] string password)
        {
            if(email.Contains("@"))
            {
                User newUser = new User { Username = username, Password = password, FirstName = firstName, LastName = lastName, Email = email, DateCreated = DateTime.Now };

                try
                {
                    ctx.Users.Add(newUser);
                    ctx.SaveChanges();

                    return Ok("User Added");
                }
                catch (Exception e)
                {
                    return StatusCode(500, "Failed to add user");
                }
            }

            return StatusCode(400, "Email is not in correct format");
            
        }
    }
}
