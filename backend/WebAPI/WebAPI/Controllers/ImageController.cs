using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using WebAPI.Services;
using WebAPI.Models;
using Microsoft.AspNetCore.Http;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using System.Text.Json;

namespace WebAPI.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class ImageController : Controller
    {
        private readonly SkuciSeDBContext ctx;
        private readonly SkuciSeImageService img;
        private readonly uint currentUserId;

        public ImageController(SkuciSeDBContext _ctx, SkuciSeImageService _img, IHttpContextAccessor httpContextAccessor)
        {
            ctx = _ctx;
            img = _img;
            //username = httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.Name)?.Value;
            uint.TryParse(httpContextAccessor.HttpContext.User.FindFirst(ClaimTypes.NameIdentifier)?.Value, out currentUserId);
        }

        [HttpPost]
        [Route("get_user_image")]
        public ActionResult<FileData> GetUserImage(uint userId = 0)
        {
            if (userId == 0) 
                userId = currentUserId;
            try
            {
                return img.GetUserImage(userId);
            }catch(Exception e)
            {
                return StatusCode(500, e);
            }
        }

        [HttpPut]
        [Route("set_user_image")]
        public ActionResult SetUserImage(string image)
        {
            if (image == null) { return BadRequest("Object must not be null!"); }

            try
            {
                FileData imageData = JsonSerializer.Deserialize<FileData>(image);
                try
                {
                    img.SetUserImage(currentUserId, imageData);
                }catch(Exception e)
                {
                    return StatusCode(500, e);
                }
                return Ok();
            }
            catch (Exception e)
            {
                return BadRequest(e);
            }
        }

        [HttpDelete]
        [Route("delete_user_image")]
        public ActionResult DeleteUserImage()
        {
            try
            {
                img.DeleteUserImage(currentUserId);
                return Ok();
            }
            catch (Exception e)
            {
                return StatusCode(500, e);
            }
        }

        [HttpPost]
        [Route("get_advert_images")]
        public ActionResult<List<FileData>> GetAdvertImages(uint advertId)
        {
            try
            {
                return img.GetAdvertImages(advertId);
            }
            catch (Exception e)
            {
                return StatusCode(500, e);
            }
        }

        private ActionResult TryEditAdvert(uint advertId, Action edit)
        {
            if (ctx.Adverts.Where(ad => ad.Id == advertId).FirstOrDefault().OwnerId == currentUserId)
            {
                try
                {
                    edit.Invoke();
                    return Ok();
                }
                catch (Exception e)
                {
                    return StatusCode(500, e);
                }
            }
            else
            {
                return Unauthorized("You can only edit your own adverts!");
            }
        }

        [HttpPut]
        [Route("add_advert_image")]
        public ActionResult AddAdvertImage(uint advertId, string image)
        {
            if (image == null) { return BadRequest("Object must not be null!"); }
            try
            {
                FileData imageData = JsonSerializer.Deserialize<FileData>(image);
                return TryEditAdvert(advertId, () => img.AddAdvertImage(advertId, imageData));
            }
            catch (Exception e)
            {
                return BadRequest(e);
            }
        }

        [HttpPut]
        [Route("add_advert_images")]
        public ActionResult AddAdvertImages(uint advertId, string images)
        {
            if (images == null) { return BadRequest("Object must not be null!"); }
            try
            {
                List<FileData> imageData = JsonSerializer.Deserialize<List<FileData>>(images);
                return TryEditAdvert(advertId, () => img.AddAdvertImages(advertId, imageData));
            }
            catch (Exception e)
            {
                return BadRequest(e);
            }
            
        }

        [HttpDelete]
        [Route("delete_advert_image")]
        public ActionResult DeleteAdvertImage(uint advertId, string imageName = null)
        {
            return TryEditAdvert(advertId, () => img.DeleteAdvertImage(advertId, imageName));
        }

        [HttpDelete]
        [Route("delete_advert_images")]
        public ActionResult DeleteAdvertImages(uint advertId)
        {
            return TryEditAdvert(advertId, () => img.DeleteAdvertImage(advertId));
        }
    }
}
