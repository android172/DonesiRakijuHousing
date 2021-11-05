﻿using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using WebAPI.Helpers;
using WebAPI.Models;
using WebAPI.Services;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace WebAPI.Controllers
{
    [AllowAnonymous]
    [Route("api/[controller]")]
    [ApiController]
    public class LoginController : ControllerBase
    {

        private readonly SkuciSeDBContext ctx;
        private readonly SkuciSeEmailService ems;
        private readonly IJwtHelper jwtHelper;

        public LoginController(SkuciSeDBContext _ctx, SkuciSeEmailService _ems, IJwtHelper _jwtHelper)
        {
            ctx = _ctx;
            ems = _ems;
            jwtHelper = _jwtHelper;
        }

        [HttpGet]       // DEBUG
        [Route("get_all_users")]
        public ActionResult<DbSet<User>> GetAllUsers()
        {
            return ctx.Users;
        }       ///////////////

        [HttpPost]
        [Route("user_login")]
        public ActionResult<string> UserLogin(string usernameOrEmail, string password)
        {
            if (String.IsNullOrWhiteSpace(usernameOrEmail) || String.IsNullOrWhiteSpace(password))
                return BadRequest("Username or password is blank or empty.");

            if (usernameOrEmail.Equals("debug"))        // DEBUG
            {
                User user = new User { Username = "Debug" };
                string token = jwtHelper.CreateToken(user);
                return Ok(new { token });
            }       ///////////////

            var exists = ctx.Users.Where(u => (u.Username == usernameOrEmail || u.Email == usernameOrEmail) && u.Password == password);

            if (exists.Any())
            {
                User user = exists.FirstOrDefault();
                string token = jwtHelper.CreateToken(user);
                return Ok(new { token });
            }

            return NotFound("User does not exist.");

        }

        [HttpPost]
        [Route("user_register")]
        public ActionResult<string> RegisterUser(string firstName, string lastName, string email, string username, string password)
        {
            Regex imePrezimeReg = new Regex(@"^([ \u00c0-\u01ffa-zA-Z'\-])+$");
            Regex usernameReg = new Regex(@"^[A-Za-z0-9_-]{4,16}$");
            Regex emailReg = new Regex(@"^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$");
            Regex passReg = new Regex(@"^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,32}$");

            if (!(imePrezimeReg.IsMatch(firstName) && imePrezimeReg.IsMatch(lastName) && emailReg.IsMatch(email)
                    && usernameReg.IsMatch(username) && passReg.IsMatch(password)))
                return BadRequest("Regex does not match.");

            User newUser = new User { Username = username, Password = password, FirstName = firstName, LastName = lastName, Email = email, DateCreated = DateTime.Now, Confirmed = false };

            try
            {
                ctx.Users.Add(newUser);
                ctx.SaveChanges();
                ems.SendConfirmationEmail(newUser.Email);

                return Ok("User added.");
            }
            catch (Exception e)
            {
                return StatusCode(500, "Failed to add user.");
            }
        }

        [HttpPost]
        [Route("send_confirmation_email")]
        public ActionResult<string> SendConfirmation(string username)
        {
            User user = ctx.Users.Where(u => u.Username == username && !u.Confirmed).FirstOrDefault();

            if(user == null)
            {
                return BadRequest();
            }

            try
            {
                ems.SendConfirmationEmail(user.Email);
                return Ok();
            }
            catch
            {
                return StatusCode(500);
            }
        }

        [HttpPost]
        [Route("send_pass_reset_email")]
        public ActionResult<string> SendPasswordReset(string username)
        {
            User user = ctx.Users.Where(u => u.Username == username).FirstOrDefault();

            if (user == null)
            {
                return BadRequest();
            }

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
