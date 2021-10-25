using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.IO;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using WebAPI.Models;

namespace WebAPI.Helpers
{
    public class JwtHelper : IJwtHelper
    {
        private readonly IConfiguration config;
        public JwtHelper(IConfiguration configuration)
        {
            config = configuration;
        }

        public BinaryReader JwtRegisterClaimNames { get; private set; }
        public object SymetricSecurityKey { get; private set; }

        public string CreateToken(User model)
        {
            Claim[] claims = new[]
            {
                new Claim(JwtRegisteredClaimNames.UniqueName, model.Username),
                new Claim(JwtRegisteredClaimNames.Sub, model.Username)
            };

            SymmetricSecurityKey key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(config["Jwt:Secret"]));
            SigningCredentials creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            JwtSecurityToken jst = new JwtSecurityToken(
                issuer: config["Jwt:Issuer"],
                audience: config["Jwt:Audience"],
                claims,
                expires: DateTime.Now.AddMinutes(30),
                signingCredentials: creds
                );

            string token = new JwtSecurityTokenHandler().WriteToken(jst);

            return token;
        }
    }
}
