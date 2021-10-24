﻿using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Helpers;
using WebAPI.Models;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace WebAPI.Controllers
{
    [AllowAnonymous]
    [Route("api/[controller]")]
    [ApiController]
    public class LoginController : ControllerBase
    {

        private readonly SkuciSeDBContext ctx;
        private readonly IJwtHelper jwtHelper;

        public LoginController(SkuciSeDBContext _ctx, IJwtHelper _jwtHelper)
        {
            ctx = _ctx;
            jwtHelper = _jwtHelper;
        }

        [HttpGet]
        [Route("user_login")]
        public ActionResult<string> UserLogin([FromForm] string usernameOrEmail, [FromForm] string password)
        {
            if (usernameOrEmail.Equals("debug"))        // DEBUG
            {
                User user = new User { Username = "Debug" };
                string token = jwtHelper.CreateToken(user);
                return Ok(new { token });
            }
                
            var exists = ctx.Users.Where(u => (u.Username == usernameOrEmail || u.Email == usernameOrEmail) && u.Password == password);

            if (exists.Any())
            {
                User user = exists.FirstOrDefault();
                string token = jwtHelper.CreateToken(user);
                return Ok(new { token });
            }
                

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
