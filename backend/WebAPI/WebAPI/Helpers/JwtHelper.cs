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
        private static List<(uint, string)> activeTokens = new List<(uint, string)>();
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
                new Claim(ClaimTypes.Name, model.Username),
                //new Claim(JwtRegisteredClaimNames.Sub, model.Username),
                new Claim(ClaimTypes.NameIdentifier, model.Id.ToString())
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

        public static string CheckActiveToken(int userId)
        {
            foreach ((uint, string) t in activeTokens)
            {
                if (t.Item1 == userId)
                {
                    return t.Item2;
                }
            }

            return null;
        }

        public static void AddActiveToken(uint userId, string token)
        {
            string temp = CheckActiveToken(int.Parse(userId.ToString()));

            if (temp != null)
                RemoveToken(int.Parse(userId.ToString()));

            activeTokens.Add((userId, token));
        }

        public static void RemoveToken(int userId)
        {
            foreach ((uint, string) t in activeTokens)
            {
                if (t.Item1 == userId)
                {
                    activeTokens.Remove(t);
                    return;
                }
            }
        }

        public static bool CheckActiveToken(string token)
        {
            foreach ((uint, string) t in activeTokens)
            {
                if (t.Item2 == token)
                {
                    return true;
                }
            }

            return false;
        }
    }
}
