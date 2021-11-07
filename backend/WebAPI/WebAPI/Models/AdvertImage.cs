﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace WebAPI.Models
{
    public class AdvertImage
    {
        public uint AdvertId { get; set; }
        public string ImageUrl { get; set; }
    }
}
